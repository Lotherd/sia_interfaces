package trax.aero.util;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import trax.aero.data.EcData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Ec;
import trax.aero.pojo.Hierarchy;


public class TimerExecutor implements Runnable {
	
	EcData data = null;
	EntityManagerFactory factory;
	
	public volatile static boolean flag = true;
	public static volatile  String error = "";
	public EmailSender emailer = null;
	
	public TimerExecutor()
	{
		factory = Persistence.createEntityManagerFactory("ImportDS");
		data = new EcData(factory);
		emailer = new EmailSender(System.getProperty("PN_toEmail"));
	}
	Logger logger = LogManager.getLogger("EC_I19");
	
	private void process() {
		try
		{
			JAXBContext jc = JAXBContext.newInstance(Hierarchy.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			
			File folder = new File(System.getProperty("EC_fileLoc"));

			File[] files = folder.listFiles();
			
			if(files != null)
			{
				for(File xml : files)
				{
					if(xml.exists() && xml.getName().toLowerCase().endsWith(".xml"))
					{
						
						logger.info("Processing file " + xml.getName());
						Hierarchy ecsData = (Hierarchy) unmarshaller.unmarshal(xml);
						
						int scheduledPoolSize = 4;
					 	if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
							scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
						}
						logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
						ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
						
					    logger.info("SIZE " + ecsData.getData().size());
					
						
					    flag = true;
						for(Ec ec : ecsData.getData()) {
							Worker worker = new Worker(factory);
					    	worker.setInput(ec);
					    	scheduledServ.execute(worker);
						}
						scheduledServ.shutdown();
					   	scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
						
						if(flag)
						{
							insertFile(xml, "SUCCESS");
						}
						else
						{
							String errorFile;
							errorFile = insertFile(xml, "FAILURE");
							error = error.concat("Error File: " +errorFile );
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
			
			logger.severe(e.toString());
		}
	}
	
	public void run()
	{
		try {
			if(data.lockAvailable("I19"))
			{
				data.lockTable("I19");
				process();
				data.unlockTable("I19");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String insertFile(File file, String outcome)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		File compFolder = new File(System.getProperty("EC_compFiles"));
		if (!compFolder.isDirectory())
			compFolder.mkdir();
		File todayFolder = new File(System.getProperty("EC_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
			todayFolder.mkdir();

		file.renameTo(new File(todayFolder + File.separator
				+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + file.getName()));
		
		logger.info("DONE processing file " + outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + file.getName());
		
		return outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + file.getName();
	}
	
}