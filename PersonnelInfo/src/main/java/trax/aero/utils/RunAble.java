package trax.aero.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import trax.aero.Encryption.PGPEncryption;
import trax.aero.controller.PersonalInfoController;
import trax.aero.data.PersonalInfoData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.EmployeeInfo;



public class RunAble implements Runnable {
	
	//Variables
	Logger logger = LogManager.getLogger("PersonalInfo_I28");
	PersonalInfoData data = null;
	EntityManagerFactory factory;
	private static File inputFiles[],inputFolder  ;
	public static List<EmployeeInfo> employeesFailure  = null;
	
	private static FilenameFilter filter = new FilenameFilter() 
	{		 
		public boolean accept(File dir, String name) 
		{
			return (name.toLowerCase().endsWith(".csv"));
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
		factory = Persistence.createEntityManagerFactory("ImportDS");
		data = new PersonalInfoData(factory);
	}
	
	private String insertFile(File file, String outcome) 
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
	
	
	
	private String insertFileFailed(ArrayList<EmployeeInfo> employeeFailure, String outcome, String fileName) throws JAXBException, IOException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		

		
		
        List<String[]> data = new ArrayList<String[]>();
        String[] header = {"Win2KID","Staff ID","Full Name","First Name","Last Name","Location","Location - Site(Mail Code – Station)",
        		"Location - Site(Mail Code - Office Location)","Location - Site(Mail Code – Lobby)","Location - Site(Mail Code – Level)",
        		"Department Code","Department","Division Code","Division","Job title","Phone","Mobile Number","Email","Age",
        		"Date Hired","Date Terminated","Company","Company code Description","Cost Center","Job Role","Job Role Description",
        		"Job Level","Job Level Description","Employee Sub Group","Employee Sub Group Description","Employee Status"};
        data.add(header);
        
        for(EmployeeInfo e : employeeFailure) {
        	String[] arr = {"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
        	if(e.getEmployeeWin2KID() != null && !e.getEmployeeWin2KID().isEmpty()) {
        		arr[0] = e.getEmployeeWin2KID();
        	}
        	if(e.getStaffID() != null && !e.getStaffID().isEmpty()) {
        		arr[1] = e.getStaffID();
        	}
        	if(e.getFullName() != null && !e.getFullName().isEmpty()) {
        		arr[2] = e.getFullName();
        	}
        	if(e.getFirstName() != null && !e.getFirstName().isEmpty()) {
        		arr[3] = e.getFirstName();
        	}
        	if(e.getLastName() != null && !e.getLastName().isEmpty()) {
        		arr[4] = e.getLastName();
        	}
        	if(e.getLocation() != null && !e.getLocation().isEmpty()) {
        		arr[6] = e.getLocation();
        	}
        	
        	if(e.getDepartmentCode() != null && !e.getDepartmentCode().isEmpty()) {
        		arr[10] = e.getDepartmentCode();
        	}
        	if(e.getDepartment() != null && !e.getDepartment().isEmpty()) {
        		arr[11] = e.getDepartment();
        	}
        	if(e.getDivisionCode() != null && !e.getDivisionCode().isEmpty()) {
        		arr[12] = e.getDivisionCode();
        	}
        	if(e.getDivision() != null && !e.getDivision().isEmpty()) {
        		arr[13] = e.getDivision();
        	}
        	if(e.getJobTitle() != null && !e.getJobTitle().isEmpty()) {
        		arr[14] = e.getJobTitle();
        	}
        	if(e.getPhone() != null && !e.getPhone().isEmpty()) {
        		arr[15] = e.getPhone();
        	}
        	if(e.getMobileNumber() != null && !e.getMobileNumber().isEmpty()) {
        		arr[16] = e.getMobileNumber();
        	}
        	if(e.getEmail() != null && !e.getEmail().isEmpty()) {
        		arr[17] = e.getEmail();
        	}
        	if(e.getAge() != null && !e.getAge().isEmpty()) {
        		arr[18] = e.getAge();
        	}
        	if(e.getDateHired() != null && !e.getDateHired().isEmpty()) {
        		arr[19] = e.getDateHired();
        	}
        	if(e.getDateTerminated() != null && !e.getDateTerminated().isEmpty()) {
        		arr[20] = e.getDateTerminated();
        	}
        	if(e.getCompany() != null && !e.getCompany().isEmpty()) {
        		arr[21] = e.getCompany();
        	}
        	if(e.getCompanyCodeDescription() != null && !e.getCompanyCodeDescription().isEmpty()) {
        		arr[22] = e.getCompanyCodeDescription();
        	}
        	if(e.getCostCenter() != null && !e.getCostCenter().isEmpty()) {
        		arr[23] = e.getCostCenter();
        	}
        	if(e.getJobRole() != null && !e.getJobRole().isEmpty()) {
        		arr[24] = e.getJobRole();
        	}
        	if(e.getJobRoleDescription() != null && !e.getJobRoleDescription().isEmpty()) {
        		arr[25] = e.getJobRoleDescription();
        	}
        	if(e.getEmployeeSubGroupDescription() != null && !e.getEmployeeSubGroupDescription().isEmpty()) {
        		arr[28] = e.getEmployeeSubGroupDescription();
        	}
        	if(e.getEmployeeStatus() != null && !e.getEmployeeStatus().isEmpty()) {
        		arr[30] = e.getEmployeeStatus();
        	}
        	
        	data.add(arr);
        }
        
		File compFolder = new File(System.getProperty("PersonalInfo_compFiles"));
		if (!compFolder.isDirectory())
		compFolder.mkdir();
		File todayFolder = new File(System.getProperty("PersonalInfo_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
		todayFolder.mkdir();
	
		File output = new File(todayFolder + File.separator
		+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + fileName);
		
        FileWriter outputfile = new FileWriter(output);
  

        CSVWriter writer = new CSVWriter(outputfile, ',',
                                         CSVWriter.NO_QUOTE_CHARACTER,
                                         CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                         CSVWriter.DEFAULT_LINE_END);
		
        writer.writeAll(data);
        writer.close();
        outputfile.close();
        
        logger.info("DONE processing file " + output.getName());
		return output.getName();
	}
	private void process() {
		try 
		{
			//setting up variables
			final String process = System.getProperty("PersonalInfo_fileLoc");
			inputFolder = new File(process);
			String exectued = "OK",outcome = "PROCESSED_";
			ArrayList<EmployeeInfo> employees = new ArrayList<EmployeeInfo>();
			
			int itr;
			EmployeeInfo employee = null;
			
			
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
				File file = new File(inputFiles[i].toString());
				try {
					try {
						logger.info("keyFile " + PGPEncryption.getEncryptionfile());
						PGPEncryption.decryptFile(file.getAbsolutePath(),PGPEncryption.getEncryptionfile() ,PGPEncryption.getEncryptionpassphrase().toCharArray() ,  file.getName().substring(0, file.getName().indexOf(".")) + ".csv" ,process );
							
						file.delete();	
					
					}catch(Exception e){
						PersonalInfoController.addError(e.toString());
						outcome = "FAILURE_";
						throw new Exception("Failed to read file");
						
					}
				}catch(Exception e) {
					PersonalInfoController.addError(e.toString());
					String output = insertFile(file,outcome);
					PersonalInfoController.addError("Failed File "  + output);
					PersonalInfoController.sendEmailFile(file);
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
					
			for (int i = 0; i < inputFiles.length; i++)
			{
				outcome = "PROCESSED_";
				logger.info("Checking file " + inputFiles[i].toString());
				File file = new File(inputFiles[i].toString());
				try
				{	
					
					
					
					//logger.info("Input: "+ FileUtils.readFileToString(file, StandardCharsets.UTF_8));
					
					FileReader filereader = new FileReader(file);
					
					 CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
					
					 CSVReader csvReader = new CSVReaderBuilder(filereader)
                             .withCSVParser(parser)
                             .withSkipLines(1)
                             .build();
					 
					 
					 try {
						 List<String[]> allData = csvReader.readAll();
						 
						 for (String[] row : allData) {
							 itr = 0;
							 employee = new EmployeeInfo();
					            for (String cell : row) {	            	
					            	 
					                if(itr == 0) {
					                	employee.setEmployeeWin2KID(cell);
					            	}
					            	if(itr == 1) {
					            		employee.setStaffID(cell);
					            	}
					            	if(itr == 2) {
					            		employee.setFullName(cell);
					            	}
					            	if(itr == 3) {
					            		employee.setFirstName(cell);
					            	}
					            	if(itr == 4) {
					            		employee.setLastName(cell);
					            	}
					            	if(itr == 6) {
					            		employee.setLocation(cell);
					            	}
					            	if(itr == 10) {
					            		employee.setDepartmentCode(cell);
					            	}
					            	if(itr == 11) {
					            		employee.setDepartment(cell);
					            	}
					            	if(itr == 12) {
					            		employee.setDivisionCode(cell);
					            	}
					            	if(itr == 13) {
					            		employee.setDivision(cell);
					            	}
					            	if(itr == 14) {
					            		employee.setJobTitle(cell);
					            	}
					            	if(itr == 15) {
					            		employee.setPhone(cell);
					            	}
					            	if(itr == 16) {
					            		employee.setMobileNumber(cell);
					            	}
					            	if(itr == 17) {
					            		employee.setEmail(cell);
					            	}
					            	if(itr == 18) {
					            		employee.setAge(cell);
					            	}
					            	if(itr == 19) {
					            		employee.setDateHired(cell);
					            	}
					            	if(itr == 20) {
					            		employee.setDateTerminated(cell);
					            	}
					            	if(itr == 21) {
					            		
					            		employee.setCompany(cell);
					            	}
					            	if(itr == 22) {
					            		employee.setCompanyCodeDescription(cell);
					            	}
					            	if(itr == 23) {
					            		employee.setCostCenter(cell);
					            	}
					            	if(itr == 24) {
					            		employee.setJobRole(cell);
					            	}
					            	if(itr == 25) {
					            		employee.setJobRoleDescription(cell);
					            	}
					            	if(itr == 28) {
					            		employee.setEmployeeSubGroupDescription(cell);
					            	}
					            	if(itr == 30) {
					            		employee.setEmployeeStatus(cell);
					            	}
					            	
					            	

					                itr++;				                
					            }
					            
					            employees.add(employee);
					           
					        }   
						 
						 	 
					 }catch(Exception e){
						 PersonalInfoController.addError(e.toString());
						 
						 outcome = "FAILURE_";
						 throw new Exception("Failed to read file");
					 }finally {
						 csvReader.close();
						 filereader.close();
					 }
					
					 
					employeesFailure = Collections.synchronizedList(new ArrayList<EmployeeInfo>());
				
				 	int scheduledPoolSize = 4;
				 	if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
						scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
					}
					logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
					ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
					
				    logger.info("SIZE " + employees.size());
					 
			       for(EmployeeInfo e: employees) {
			    	   exectued = "OK";
			    	   Worker worker = new Worker(factory);
			    	   worker.setInput(e);
			    	   scheduledServ.execute(worker);
			       }
			       
			       scheduledServ.shutdown();
			   	   scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);	 			        			        
			   	   String fileName = file.getName(); 

				   if(!employeesFailure.isEmpty()){
				    	exectued = insertFileFailed(new ArrayList<EmployeeInfo>(employeesFailure),"FAILURE_",fileName);
				    	employeesFailure = null;
				    	throw new Exception("Failed Employees are in File " + exectued);
				   }
				   
				}
				catch(Exception e)
				{
					e.printStackTrace();
					logger.severe(e.toString());
					PersonalInfoController.addError(e.toString());
					PersonalInfoController.sendEmailFile(file);
					//insertFile(file,"FAILURE_");
					
					
				}finally {
					 
					insertFile(file,outcome);
					
				}
			}
			
		}
		catch(Throwable e)
		{
			logger.severe(e.toString());
		}
	}
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I28"))
			{
				data.lockTable("I28");
				process();
				data.unlockTable("I28");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}