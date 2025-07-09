package trax.aero.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;
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

import trax.aero.controller.TaskCardController;
import trax.aero.data.TaskCardData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.TaskCardMaster;
import trax.aero.pojo.TaskCards;

public class RunAble implements Runnable {
	
	//Variables
	TaskCardData data = null;
	
	EntityManagerFactory factory;
	static Logger logger = LogManager.getLogger("TaskCard_I18");
	private static File inputFiles[],inputFolder, copyFolder,errorFolder;
	
	public static List<TaskCards> taskCardsArrayFailure  = null;
	public static List<String> tasklist = null;
	
	private static FilenameFilter filter = new FilenameFilter() 
	{		 
		public boolean accept(File dir, String name) 
		{
			return (name.toLowerCase().endsWith(".xml"));
		}
	};
	
	public RunAble() {
		factory = Persistence.createEntityManagerFactory("ImportDS");
		data = new TaskCardData();
	}
	
	private void insertFile(File file, String outcome) 
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime  currentDateTime = LocalDateTime.now();
		
		File todayFolder = new File(System.getProperty("TaskCard_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())			
			todayFolder.mkdir();
		
		file.renameTo(new File(todayFolder + File.separator
			+ outcome + Calendar.getInstance().getTimeInMillis() + "_" + file.getName()));
		
		logger.info("DONE processing file " + file.getName());
	}
	
	private String insertFileFailed(TaskCardMaster taskCardMaster, String outcome, String fileName) throws JAXBException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		JAXBContext jc = JAXBContext.newInstance(TaskCardMaster.class);
		Marshaller marshall = jc.createMarshaller();
		marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		File compFolder = new File(System.getProperty("TaskCard_failedLoc"));
		if (!compFolder.isDirectory())
		compFolder.mkdir();
		File todayFolder = new File(System.getProperty("TaskCard_failedLoc")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
		todayFolder.mkdir();
	
		File output = new File(todayFolder + File.separator
		+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + fileName);
		marshall.marshal(taskCardMaster, output);
		// file.renameTo(new File(todayFolder + "\\"
		// + outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + file.getName()));
	
		logger.info("DONE processing file " + output.getName());
		return output.getName();
	}
	
	// Method to clone TaskCards
	private TaskCards cloneTaskCard(TaskCards original) {
		TaskCards clone = new TaskCards();
		
		// Copy all basic properties
		clone.setTaskCard(original.getTaskCard());
		clone.setCategory(original.getCategory());
		clone.setDescription(original.getDescription());
		clone.setRevision(original.getRevision());
		clone.setPlanningPlant(original.getPlanningPlant());
		clone.setGroupNo(original.getGroupNo());
		clone.setDeletionIndicator(original.getDeletionIndicator());
		clone.setArea(original.getArea());
		clone.setZone(original.getZone());
		
		
		// Clone lists if they exist
		if (original.getItemList() != null) {
			clone.setItemList(new ArrayList<>(original.getItemList()));
		}
		
		if (original.getPanelList() != null) {
			clone.setPanelList(new ArrayList<>(original.getPanelList()));
		}
		
		
		return clone;
	}

	
	private void process() {
		try 
		{
			//setting up variables
			final String process = System.getProperty("TaskCard_fileLoc");
			inputFolder = new File(process);
			String exectued = "";
			
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
				logger.info("Checking file " + inputFiles[i].toString());
				File file = new File(inputFiles[i].toString());
				boolean shopFail = false;
				try
				{					
					JAXBContext jc = JAXBContext.newInstance(TaskCardMaster.class);

			        Unmarshaller unmarshaller = jc.createUnmarshaller();
			       
			        TaskCardMaster taskCardMaster = (TaskCardMaster) unmarshaller.unmarshal(file);

			        Marshaller marshaller = jc.createMarshaller();
			        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
			        StringWriter sw = new StringWriter();
				    marshaller.marshal(taskCardMaster,sw);
				    //logger.info("Input: " +sw.toString() );
				    
				    TaskCardMaster taskCardMasterFailure = new TaskCardMaster();
					taskCardsArrayFailure = Collections.synchronizedList(new ArrayList<TaskCards>());
					tasklist = Collections.synchronizedList(new ArrayList<String>()); 
					
					//List to clone MCS/SI task cards with S_ prefix 
					List<TaskCards> s_taskCardMaster = new ArrayList<>();
					
					
				    int scheduledPoolSize = 4;
					if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
						scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
					}
					logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
					ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
					
				    logger.info("SIZE " + taskCardMaster.getTaskCards().size());
				    
				    //First loop - Process original task cards and create clones 
				    for(TaskCards taskCards : taskCardMaster.getTaskCards()) {
				    	
				    	// Create clone for MCS/SI without S_ prefix
				    	if((taskCards.getCategory().equalsIgnoreCase("MCS") || taskCards.getCategory().equalsIgnoreCase("SI"))
				    		&& !taskCards.getTaskCard().startsWith("S_")) {
				    		
				    		// Clone the task card
				    		TaskCards s_taskCards = cloneTaskCard(taskCards);
				    		s_taskCards.setTaskCard("S_" + taskCards.getTaskCard());
				    		s_taskCardMaster.add(s_taskCards);
				    		
				    		logger.info("Cloned task card: " + taskCards.getTaskCard() + " -> " + s_taskCards.getTaskCard());
				    	}
				    	
				    	// Process original task card
				    	exectued = "OK";
				    	Worker worker = new Worker(factory);
				    	worker.setInput(taskCards);
				    	scheduledServ.execute(worker);
				    }
				    
				    //Second loop - Process cloned task cards with S_ prefix 
				    for(TaskCards s_taskCards : s_taskCardMaster) {
				    	exectued = "OK";
				    	Worker worker = new Worker(factory);
				    	worker.setInput(s_taskCards);
				    	scheduledServ.execute(worker);
				    }
				 
					
				   scheduledServ.shutdown();
				   scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);	  
				    
				   String fileName = file.getName();
				   
				   insertFile(file,"PROCESSED_");
				   
				   taskCardMasterFailure.setTaskCards(new ArrayList<TaskCards>(taskCardsArrayFailure));
				   if(!tasklist.isEmpty()){
					   boolean shop = false;
					   for(String t: tasklist) {
						   if(t.contains(" S_")) {
							   shop = true;
						   }
					   }
					   TaskCardController.sendEmailSent(new ArrayList<String>(tasklist),shop);
				   }
				   if(!taskCardMasterFailure.getTaskCards().isEmpty()){
				    	exectued = insertFileFailed(taskCardMasterFailure,"FAILURE_",fileName);
				    	for( TaskCards t: taskCardMasterFailure.getTaskCards()) {
							if(t.getCategory().equalsIgnoreCase("MCS") || 
								t.getCategory().equalsIgnoreCase("SI") ) {
								   shopFail = true;
							 }
						}
				    	taskCardMasterFailure = null;
				    	throw new Exception("Failed TaskCards are in File " + exectued);
				    	
				   }
				}
				catch(Exception e)
				{
					TaskCardController.addError(e.toString());
					TaskCardController.sendEmail(file,shopFail);
					//insertFile(file,"FAILURE_");
					logger.info(e.getMessage());
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
			if(data.lockAvailable("I18"))
			{
				data.lockTable("I18");
				process();
				data.unlockTable("I18");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}