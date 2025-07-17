package trax.aero.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
	
		logger.info("DONE processing file " + output.getName());
		return output.getName();
	}
	
	// Method to clone TaskCards with validation
	private TaskCards cloneTaskCard(TaskCards original) {
		if (original == null) {
			logger.severe("Cannot clone null TaskCards");
			return null;
		}
		
		TaskCards clone = new TaskCards();
		
		// Copy all basic properties with null checks
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
	
	// Method to validate TaskCard before processing
	private boolean isValidTaskCard(TaskCards taskCard) {
		if (taskCard == null) {
			logger.warning("TaskCard is null");
			return false;
		}
		
		if (taskCard.getTaskCard() == null || taskCard.getTaskCard().trim().isEmpty()) {
			logger.warning("TaskCard name is null or empty");
			return false;
		}
		
		if (taskCard.getGroupNo() == null || taskCard.getGroupNo().trim().isEmpty()) {
			logger.warning("TaskCard GroupNo is null or empty for TaskCard: " + taskCard.getTaskCard());
			return false;
		}
		
		if (taskCard.getPlanningPlant() == null || taskCard.getPlanningPlant().trim().isEmpty()) {
			logger.warning("TaskCard PlanningPlant is null or empty for TaskCard: " + taskCard.getTaskCard());
			return false;
		}
		
		if (taskCard.getCategory() == null || taskCard.getCategory().trim().isEmpty()) {
			logger.warning("TaskCard Category is null or empty for TaskCard: " + taskCard.getTaskCard());
			return false;
		}
		
		return true;
	}

	// Method to process TaskCard individually
	private String processTaskCardSafely(TaskCards taskCard, Set<String> processedTaskCards) {
		// Validate TaskCards Before Processing
		if (!isValidTaskCard(taskCard)) {
			logger.severe("Invalid TaskCard detected, skipping...");
			return "INVALID_TASKCARD";
		}
		
		String taskCardKey = taskCard.getTaskCard() + "_" + taskCard.getGroupNo() + "_" + taskCard.getPlanningPlant();
		
		// Verify if TaskCard was already processed
		if (processedTaskCards.contains(taskCardKey)) {
			logger.warning("TaskCard " + taskCardKey + " already processed, skipping...");
			return "SKIPPED";
		}
		
		try {
			logger.info("Processing TaskCard: " + taskCardKey);
			
			// Create a new TaskCardData instance for each TaskCard
			TaskCardData taskCardData = new TaskCardData(factory);
			String result = taskCardData.insertTaskCard(taskCard);
			
			if (!"OK".equals(result)) {
				logger.severe("Error processing TaskCard " + taskCardKey + ": " + result);
				taskCardsArrayFailure.add(taskCard);
			} else {
				logger.info("Successfully processed TaskCard: " + taskCardKey);
				if (taskCardData.newExist) {
					tasklist.add(taskCardData.getTaskList());
				}
				// Mark as processed if it was successful 
				processedTaskCards.add(taskCardKey);
			}
			
			return result;
			
		} catch (Exception e) {
			logger.severe("Exception processing TaskCard " + taskCardKey + ": " + e.getMessage());
			e.printStackTrace();
			taskCardsArrayFailure.add(taskCard);
			return "ERROR: " + e.getMessage();
		}
	}
	
	private void process() {
		try 
		{
			//setting up variables
			final String process = System.getProperty("TaskCard_fileLoc");
			inputFolder = new File(process);
			String executed = "";
			
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
				    
				    TaskCardMaster taskCardMasterFailure = new TaskCardMaster();
					taskCardsArrayFailure = Collections.synchronizedList(new ArrayList<TaskCards>());
					tasklist = Collections.synchronizedList(new ArrayList<String>()); 
					
					// Set to check TaskCards already processed
					Set<String> processedTaskCards = new HashSet<>();
					
					// List of all TaskCards (originals + clones)
					List<TaskCards> allTaskCards = new ArrayList<>();
					
					logger.info("Original TaskCards SIZE: " + taskCardMaster.getTaskCards().size());
					
					// Add TaskCards originals with validation
					for (TaskCards taskCard : taskCardMaster.getTaskCards()) {
						if (isValidTaskCard(taskCard)) {
							allTaskCards.add(taskCard);
						} else {
							logger.warning("Skipping invalid TaskCard from original list");
						}
					}
					
					// Create clones for MCS/SI task cards with no prefix S_
					for(TaskCards taskCards : taskCardMaster.getTaskCards()) {
						if (isValidTaskCard(taskCards) && 
							(taskCards.getCategory().equalsIgnoreCase("MCS") || taskCards.getCategory().equalsIgnoreCase("SI")) &&
							!taskCards.getTaskCard().startsWith("S_")) {
							
							TaskCards s_taskCards = cloneTaskCard(taskCards);
							if (s_taskCards != null && isValidTaskCard(s_taskCards)) {
								s_taskCards.setTaskCard("S_" + taskCards.getTaskCard());
								allTaskCards.add(s_taskCards);
								logger.info("Cloned task card: " + taskCards.getTaskCard() + " -> " + s_taskCards.getTaskCard());
							} else {
								logger.warning("Failed to clone TaskCard: " + (taskCards.getTaskCard() != null ? taskCards.getTaskCard() : "null"));
							}
						}
					}
					
					logger.info("Total TaskCards to process (including clones): " + allTaskCards.size());
					
					// Sequential Processing 
					int successCount = 0;
					int failureCount = 0;
					int skippedCount = 0;
					
					for (int j = 0; j < allTaskCards.size(); j++) {
						TaskCards taskCard = allTaskCards.get(j);
						
						// Additional Validation
						if (!isValidTaskCard(taskCard)) {
							logger.warning("Skipping invalid TaskCard at position " + j);
							skippedCount++;
							continue;
						}
						
						logger.info("Processing TaskCard " + (j + 1) + " of " + allTaskCards.size() + ": " + taskCard.getTaskCard());
						
						String result = processTaskCardSafely(taskCard, processedTaskCards);
						
						if ("OK".equals(result)) {
							successCount++;
						} else if ("SKIPPED".equals(result) || "INVALID_TASKCARD".equals(result)) {
							skippedCount++;
						} else {
							failureCount++;
						}
						
						// Small pause between TaskCard to free resources
						if (j % 10 == 0 && j > 0) {
							try {
								Thread.sleep(100); // 100ms every 10 TaskCards
							} catch (InterruptedException ie) {
								Thread.currentThread().interrupt();
								break;
							}
						}
					}
					
					logger.info("Processing completed. Success: " + successCount + 
								", Failures: " + failureCount + 
								", Skipped: " + skippedCount + 
								", Total: " + allTaskCards.size());
				    
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
				    	executed = insertFileFailed(taskCardMasterFailure,"FAILURE_",fileName);
				    	for( TaskCards t: taskCardMasterFailure.getTaskCards()) {
							if(t.getCategory().equalsIgnoreCase("MCS") || 
								t.getCategory().equalsIgnoreCase("SI") ) {
								   shopFail = true;
							 }
						}
				    	taskCardMasterFailure = null;
				    	throw new Exception("Failed TaskCards are in File " + executed);
				   }
				}
				catch(Exception e)
				{
					TaskCardController.addError(e.toString());
					TaskCardController.sendEmail(file,shopFail);
					logger.severe("Error processing file " + file.getName() + ": " + e.getMessage());
				}					
			}
		}
		catch(Throwable e)
		{
			logger.severe("Critical error in process(): " + e.toString());
			e.printStackTrace();
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