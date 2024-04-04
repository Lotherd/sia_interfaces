package trax.aero.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import trax.aero.Encryption.PGPEncryption;
import trax.aero.controller.KronosController;
import trax.aero.dao.KronosData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Schedule;
import trax.aero.pojo.WorkSchedule;


public class RunAble implements Runnable {
	
	//Variables
	KronosData data = null;
	Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	private static File inputFiles[],inputFolder, copyFolder,errorFolder;
	ArrayList<String> offDay = new ArrayList<>(Arrays.asList("IGNO","REST","OFFD"));
	
	
	private static FilenameFilter filter = new FilenameFilter() 
	{		 
		public boolean accept(File dir, String name) 
		{
			return (name.toLowerCase().endsWith(".dat"));
		}
	};
	private static FilenameFilter filterEN = new FilenameFilter() 
	{		 
		public boolean accept(File dir, String name) 
		{
			return (name.toLowerCase().endsWith(".pgp"));
		}
	};
	public RunAble() {
		data = new KronosData();
	}
	
	private String insertFile(File file, String outcome) 
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime  currentDateTime = LocalDateTime.now();
		
		File todayFolder = new File(System.getProperty("Kronos_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())			
			todayFolder.mkdir();
		
		file.renameTo(new File(todayFolder + File.separator
			+ outcome + Calendar.getInstance().getTimeInMillis() + "_" + file.getName()));
		
		logger.info("DONE processing file " + file.getName());
		return file.getName();
	}
	
	
	private String insertFileEncrypt(File file, String outcome) 
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime  currentDateTime = LocalDateTime.now();
		
		File todayFolder = new File(System.getProperty("PersonalInfo_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())			
			todayFolder.mkdir();
		
		
		
		File output =  new File(todayFolder + File.separator
			+ outcome + Calendar.getInstance().getTimeInMillis() + "_" + file.getName());
		
		try {
			FileUtils.copyFile(file, output);
		} catch (IOException e) {
			
		}
		file.delete();
		
		
		logger.info("DONE processing file " + file.getName() );
		
		return output.getName();
	}
	
	private String insertFileFailed( String errorData, String outcome, String fileName) throws JAXBException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		File compFolder = new File(System.getProperty("Kronos_compFiles"));
		if (!compFolder.isDirectory())
		compFolder.mkdir();
		File todayFolder = new File(System.getProperty("Kronos_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
		todayFolder.mkdir();
	
		//File output = new File();
		String output = todayFolder + File.separator
				+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + fileName;
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(todayFolder + File.separator
					+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + fileName));
		    writer.write(errorData);
		} catch (IOException e) {
			logger.severe(e.toString());
		}finally {
			 try
			    {
			        if ( writer != null)
			        writer.close( );
			    }
			    catch ( IOException e)
			    {
			    	logger.severe(e.getMessage());
			    }
		}
	   
	
		logger.info("DONE processing file " + output);
		return output;
	}
	
	private void process() {
		
		//setting up variables
				final String process = System.getProperty("Kronos_fileLoc");
				inputFolder = new File(process);
				String exectued = "",fileNmae = "",outcome = "PROCESSED_";
				WorkSchedule sched = null;
				BufferedReader in = null;
				Schedule sch = null;
				String result[];
				ArrayList<Schedule> schs_WORKSCHEDULEDATA = new ArrayList<Schedule>();
				ArrayList<Schedule> schs_ATTENDANCEABSENCEDATA = new ArrayList<Schedule>();
				List<String> list_WORKSCHEDULEDATA = new ArrayList<String>();
				List<String> list_ATTENDANCEABSENCEDATA = new ArrayList<String>();
				File file = null;
					
				try 
				{		
					
					if (inputFolder.isDirectory())
					{
						inputFiles = inputFolder.listFiles(filterEN);
					}
					else
					{
						logger.severe("Path: " + inputFolder.toString() + " is not a directory or does not exist");
						throw new Exception("Path: " + inputFolder.toString() + " is not a directory or does not exist");
					}
							
					for (int i = 0; i < inputFiles.length; i++)
					{
						outcome = "PROCESSED_";
						logger.info("Decrypting file " + inputFiles[i].toString());
						file = new File(inputFiles[i].toString());
						try {
							try {
								logger.info("keyFile " + PGPEncryption.getEncryptionfile());
								
								PGPEncryption.decryptFile(file.getAbsolutePath(),PGPEncryption.getEncryptionfile() ,PGPEncryption.getEncryptionpassphrase().toCharArray() ,  file.getName().substring(0, file.getName().indexOf("."))+ ".dat",process );
									
								file.delete();	
							
							}catch(Exception e){
								KronosController.addError(e.toString());
								outcome = "FAILURE_";
								throw new Exception("Failed to read file");
								
							}
						}catch(Exception e) {
							KronosController.addError(e.toString());
							String output = insertFileEncrypt(file,outcome);
							
							KronosController.addError("Failed File "  + output);
							KronosController.sendEmail(file.getName());
							logger.severe(e.toString());
						}
					}
					
					
					
					
					
					
					
						//logic taken from AIMS_Flight_Interface
						if (inputFolder.isDirectory())
						{
							inputFiles = inputFolder.listFiles(filter);
						}
						else
						{
							logger.severe("Path: " + inputFolder.toString() + " is not a directory or does not exist");
							throw new Exception("Path: " + inputFolder.toString() + " is not a directory or does not exist");
						}
						
						try
						{		
							for (int i = 0; i < inputFiles.length; i++)
							{
								logger.info("Checking file " + inputFiles[i].toString());
								 file = new File(inputFiles[i].toString());
													
									in = new BufferedReader(new FileReader(file));
									String str;
									boolean workSchedule = false, attendanceabsence = false;
									
									while((str  = in.readLine()) != null) {
										if(str.contains("WORKSCHEDULEDATA")) {
											workSchedule = true;
											fileNmae = file.getName();
											logger.info("FOUND WORKSCHEDULEDATA");
											
										}
										if(workSchedule) {
											
											
											list_WORKSCHEDULEDATA.add(str);
										}
										if(str.contains("ATTENDANCEABSENCEDATA")) {
											attendanceabsence = true;
											logger.info("FOUND ATTENDANCEABSENCEDATA");
										}
										if(attendanceabsence) {
											list_ATTENDANCEABSENCEDATA.add(str);
										}
										
									}
									in.close();
									insertFile(file,"PROCESSED_");
			
								
							}
							try {
								if(!list_WORKSCHEDULEDATA.isEmpty()) {
									logger.info("PROCESSING WORKSCHEDULEDATA");
									
									Date startdatetime = null ,  enddatetime = null;
									
									Calendar cal = null;
									
									cal = Calendar.getInstance();
									Calendar calCurrent = Calendar.getInstance();
									calCurrent.setTime(cal.getTime());
									
									
									Integer ignoreDay = new Integer(7);
									if(System.getProperty("Kronos_ignoreDay") != null && !System.getProperty("Kronos_ignoreDay").isEmpty()) {
										ignoreDay = Integer.parseInt(System.getProperty("Kronos_ignoreDay"));
									}
									
									cal.add(Calendar.DATE, -ignoreDay);
									
									
									try {
										 startdatetime = new SimpleDateFormat("yyyyMMddkkmmss").parse(list_WORKSCHEDULEDATA.get(1).substring(10, 17) + list_WORKSCHEDULEDATA.get(1).substring(31, 37));
										 enddatetime = new SimpleDateFormat("yyyyMMddkkmmss").parse(list_WORKSCHEDULEDATA.get(1).substring(10, 17) + list_WORKSCHEDULEDATA.get(1).substring(37, 43));;
									}catch(Exception e){
										
									}
									for(String s  : list_WORKSCHEDULEDATA) {
										sch = new Schedule();
										result = new String[2];
										
										if(s.contains("WORKSCHEDULEDATA") || s.equalsIgnoreCase("99")) {
											
										}else {
										
											
											try {
												String empstartdt = s.substring(10, 18) + s.substring(31, 37);
												String empenddt = s.substring(10, 18) + s.substring(37, 43);
												
												DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

												
												Date startdt = Date.from( LocalDateTime.parse(empstartdt,formatter).atZone(ZoneId.systemDefault()).toInstant());  
												Date enddt = Date.from(LocalDateTime.parse(empenddt,formatter).atZone(ZoneId.systemDefault()).toInstant());  
												
												if(enddt.before(startdt)) {
													enddt =  new DateTime(enddt).plusDays(1).toDate();
												}
												
												if(startdt .before(cal.getTime()) || enddt.before( cal.getTime())) {
													logger.info("Ignore Schedule as its -"+ignoreDay+" from current Date " + calCurrent.getTime()
													+ " ,caluclated Date " + cal.getTime()
													+" ,startdt " + startdt + " ,enddt " + enddt + " ,Employee " + s.substring(2, 10));
													continue;
												}
												
												 //Date startdt = new SimpleDateFormat("yyyyMMddHHmmss").parse(empstartdt);  
												 //Date enddt = new SimpleDateFormat("yyyyMMddHHmmss").parse(empenddt);  
												 
												 if (startdatetime.compareTo(startdt) > 0) {
													 startdatetime = startdt;
												 }
										         if (enddatetime.compareTo(enddt) < 0) {
										            	enddatetime = enddt;
										         }
									         	sch.setEmpstartdt(startdt);
												sch.setEmpenddt(enddt);
											}catch(Exception e) {
												
											}
											
											
											sch.setEmployee(s.substring(2, 10));
											
											
											try {
												String costCenter = data.getCostCenterByEmployee(sch.getEmployee());
												String group = data.getGroupByCostCenterEmployee(costCenter);
												result = data.getLocationSiteByGroup(group);
												sch.setCostCenter(costCenter);
												sch.setLocation(result[0]);
												sch.setSite(result[1]);
												sch.setGroup(group);
											}catch(Exception e){
												logger.severe(e.toString());
											}
											//sch.setLocation("MIA");
											//sch.setSite("MIA");
											//sch.setGroup("A1");
											
											
											
											if(offDay.contains(s.substring(26,30))) {
											
											}else {
												schs_WORKSCHEDULEDATA.add(sch);
											}
											
											
											
										}
									}
									
									sched = new WorkSchedule();
									sched.setStartdatetime(startdatetime);
									sched.setEnddatetime(enddatetime);
									
									sched.setSchedules(schs_WORKSCHEDULEDATA);
									schs_WORKSCHEDULEDATA = null;
									list_WORKSCHEDULEDATA = null;
									exectued = data.insertEmployeeSchedules(sched);
									
								}
							}catch(Exception e){
								
								KronosController.addError(e.toString());
								exectued = KronosController.getError();
								insertFileFailed(exectued,"FAILURE_",fileNmae);
								KronosController.sendEmail(file.getName());
								logger.severe(e.getMessage());
							}
							
							try {
								
								if(!list_ATTENDANCEABSENCEDATA.isEmpty()) {
									logger.info("PROCESSING ATTENDANCEABSENCEDATA");
									for(String s  : list_ATTENDANCEABSENCEDATA) {
										sch = new Schedule();
										result = new String[3];
										
										if(s.contains("ATTENDANCEABSENCEDATA") || s.equalsIgnoreCase("99")) {
											
										}else {
											sch.setEmployee(s.substring(0, 8));
					
											try {
												String empstartdt = s.substring(12,20) + s.substring(28,34);
												
												String endTime = "";
												if (s.substring(34,40).equalsIgnoreCase("000000")) {
													endTime = "235959";
												}else {
													endTime = s.substring(34,40);
												}
												String empenddt = s.substring(20,28) + endTime;
												
												DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");

												Date startdt = Date.from( LocalDateTime.parse(empstartdt,formatter).atZone(ZoneId.systemDefault()).toInstant());  
												Date enddt = Date.from(LocalDateTime.parse(empenddt,formatter).atZone(ZoneId.systemDefault()).toInstant());  
												 
												 //Date enddt = new SimpleDateFormat("ddMMyyyyHHmmss").parse(empenddt);  
												
												sch.setEmpstartdt(startdt);
												sch.setEmpenddt(enddt);
											}catch(Exception e) {
											}
											
											schs_ATTENDANCEABSENCEDATA.add(sch);
										}	
									}	
									data.deleteEmployeeSchedules(schs_ATTENDANCEABSENCEDATA);
								}
								
							}catch(Exception e){
								
								KronosController.addError(e.toString());
								exectued = KronosController.getError();
								KronosController.sendEmail(file.getName());
								logger.severe(e.getMessage());
							}	
						}catch(Exception e){
							
							KronosController.addError(e.toString());
							exectued = KronosController.getError();
							insertFileFailed(exectued,"FAILURE_",fileNmae);
							KronosController.sendEmail(file.getName());
							logger.severe(e.getMessage());
						}
					}catch(Throwable e)
					{
						logger.severe(e.getMessage());
					}
	}
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I32I33I34"))
			{
				data.lockTable("I32I33I34");
				process();
				data.unlockTable("I32I33I34");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
			
	}
}