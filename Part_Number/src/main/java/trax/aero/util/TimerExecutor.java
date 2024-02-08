package trax.aero.util;

import java.io.File;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.data.PnMasterData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.PartMaster;
import trax.aero.pojo.PartNumber;

public class TimerExecutor implements Runnable {
	
	
	PnMasterData data = null;
	EntityManagerFactory factory;
	EmailSender emailer = null;
	public static volatile  String error = "";
	public static List<PartNumber> failedList;
	
	public TimerExecutor()
	{
		factory = Persistence.createEntityManagerFactory("ImportDS");
		data = new PnMasterData(factory);
		emailer = new EmailSender(System.getProperty("PN_toEmail"));
	}
	Logger logger = LogManager.getLogger("PartMaster_I16I50");
	
	private void process() {
		try
		{
			
			JAXBContext jc = JAXBContext.newInstance(PartMaster.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			File folder = new File(System.getProperty("PN_fileLoc"));
			
			File[] files = folder.listFiles();
			
			if(files != null)
			{
				for(File xml : files)
				{
					if(xml.exists() && xml.getName().toLowerCase().endsWith(".xml"))
					{
						
						logger.info("Processing file " + xml.getName());
						PartMaster partMaster = (PartMaster) unmarshaller.unmarshal(xml);
						
						PartMaster failed = new PartMaster();
						failedList = Collections.synchronizedList(new ArrayList<PartNumber>());
										
						if(partMaster == null)
						{
							logger.severe("ERROR INPUT IS NULL");
						}else {
							
							
							int scheduledPoolSize = 4;
							if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
								scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
							}
							logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
							ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
								
							logger.info("SIZE " + partMaster.getParts().size());
							
							for(PartNumber part: partMaster.getParts()) {
								Worker worker = new Worker(factory);
						    	 worker.setInput(part);
						    	 scheduledServ.execute(worker);
							}
							scheduledServ.shutdown();
							scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);	  
							
							failed.setParts(new ArrayList<PartNumber>(failedList));
							
						}
						
						
						insertFile(xml, "PROCESSED");
						String fileName = xml.getName();
						
						if(failed.getParts().size() > 0)
						{
							error = error.concat("Input File Name: " + fileName + System.lineSeparator() +  System.lineSeparator());
							
							String output = insertFile(failed, "FAILURE", fileName);
							
							error = error.concat("Ouput Failed File Name: " + output + System.lineSeparator() +  System.lineSeparator());
							
						}
						
						
						if(error.length() > 0)
						{
							emailer.sendEmail(error);
							error = "";
						}
					
					}
				}
			}
			

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
		}
	}
	
	public void run()
	{
		try {
			if(data.lockAvailable("I16I50"))
			{
				data.lockTable("I16I50");
				process();
				data.unlockTable("I16I50");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void insertFile(File file, String outcome)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		File compFolder = new File(System.getProperty("PN_compFiles"));
		if (!compFolder.isDirectory())
			compFolder.mkdir();
		File todayFolder = new File(System.getProperty("PN_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
			todayFolder.mkdir();

		file.renameTo(new File(todayFolder + File.separator
				+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + file.getName()));
		
		logger.info("DONE processing file " + file.getName());
	}
	
	private String insertFile(PartMaster parts, String outcome, String fileName) throws JAXBException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		JAXBContext jc = JAXBContext.newInstance(PartMaster.class);
		Marshaller marshall = jc.createMarshaller();
		marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		File compFolder = new File(System.getProperty("PN_compFiles"));
		if (!compFolder.isDirectory())
			compFolder.mkdir();
		File todayFolder = new File(System.getProperty("PN_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
			todayFolder.mkdir();
		
		File output = new File(todayFolder + File.separator
				+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + fileName);
		marshall.marshal(parts, output);
//		file.renameTo(new File(todayFolder + "\\"
//				+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + file.getName()));
		
		logger.info("DONE processing file " + output.getName());
		
		return output.getName();
	}
}
