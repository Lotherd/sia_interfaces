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
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import trax.aero.controller.TaskCardsIngestionController;
import trax.aero.data.TaskCardsIngestionData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I87_RES;
import trax.aero.pojo.MT_TRAX_SND_I87_REQ;

public class RunAble implements Runnable {
	
	//Variables
	
	Logger logger = LogManager.getLogger("TaskCardsIngestion_I87");
	
	TaskCardsIngestionData data = null;
	private static File inputFiles[],inputFolder, outFolder;
	private static FilenameFilter filter = new FilenameFilter() 
	{		 
		public boolean accept(File dir, String name) 
		{
			return (name.toLowerCase().endsWith(".csv"));
		}
	};
	
	public RunAble() {
		data = new TaskCardsIngestionData();
	}
	
	private void insertFileOut(ArrayList<MT_TRAX_RCV_I87_RES> taskCardsOut) throws JAXBException, IOException
	{
		
		int i = 1;
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		File output;

        List<String[]> out = new ArrayList<String[]>();
       
        String folderNumber = "XXX";
        if(System.getProperty("TaskCardsIngestion_folderNumber") !=null && !System.getProperty("TaskCardsIngestion_folderNumber").isEmpty()) {
        	folderNumber = System.getProperty("TaskCardsIngestion_folderNumber");
        }
        
        String[] header = {"Serial", "Material", "FL", "PlanningPlant","ShortText","WorkCenter",
        		"PMACT", "Revision", "Priority", "Mat",
        		"Quan", "Activity","OpDesc", "Running","TC","TO", "WBS"};
        
        out.add(header);
        
        
        
        for(MT_TRAX_RCV_I87_RES t : taskCardsOut) {
        	String[] arr = {t.getSerialNumber(), t.getMaterialNumber(),
        			t.getFunctionalLocation(), t.getPlanningPlant(),
        			t.getOrderShortText(),t.getMainWorkCenter(),
        			t.getMaintenanceActivityType(), t.getRevision(),
        			t.getPriority(),t.getMaterialNumberServiceProduct(),
        			t.getQTY(), t.getWorkInvolved(),
        			t.getOperationShortText(),t.getRunningNumber(),
        			t.getTRAXTCNumber(), t.getTRAXWO(), t.getWBS()};
	        
	        out.add(arr);
        }
        
        
		File outFiles = new File(System.getProperty("TaskCardsIngestion_outFiles"));
		if (!outFiles.isDirectory())
			outFiles.mkdir();
		
		i=data.getSeqNo();
		if(i >= 1000) {
			data.resetSeqNo();
			i = data.getSeqNo();
		}
		output = new File(outFiles + File.separator
				+ "I"+ folderNumber+ "_"+ dtf.format(currentDateTime) + "_" +String.format("%03d", i) +".dat");		
		
				
		
        FileWriter outputfile = new FileWriter(output);
  
        CSVWriter writer = new CSVWriter(outputfile, '#',
                                         CSVWriter.NO_QUOTE_CHARACTER,
                                         CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                         CSVWriter.DEFAULT_LINE_END);
		
        writer.writeAll(out);
        writer.close();
        outputfile.close();
        
        logger.info("DONE processing file " + output.getName());
	}
	
	
	
	private String insertFileMark(ArrayList<MT_TRAX_SND_I87_REQ> TaskCards, String outcome, String fileName) throws JAXBException, IOException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
        List<String[]> in = new ArrayList<String[]>();
        String[] header = {"TRAX_TC_NUMBER", "ZTRAX_WO", "SAP_ORDER_NO", "SAP_OPERATION_NO","SUCCESS_ERROR","REMARKS"};
        in.add(header);
        
   
        for(MT_TRAX_SND_I87_REQ t : TaskCards) {
        	String[]arr = {t.getTRAXTCNumber(), t.getTRAXWO(), 
        			t.getSAPOrderNumber(), t.getSAPOperationNumber(),
        			t.getSuccessErrorIndicator(),t.getRemarks()};
        	
        	in.add(arr);
        }
        
		File compFolder = new File(System.getProperty("TaskCardsIngestion_compFiles"));
		if (!compFolder.isDirectory())
		compFolder.mkdir();
		File todayFolder = new File(System.getProperty("TaskCardsIngestion_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
		todayFolder.mkdir();
	
		File output = new File(todayFolder + File.separator
		+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + fileName);
		
        FileWriter outputfile = new FileWriter(output);
  

        CSVWriter writer = new CSVWriter(outputfile, '#',
                                         CSVWriter.NO_QUOTE_CHARACTER,
                                         CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                         CSVWriter.DEFAULT_LINE_END);
		
        writer.writeAll(in);
        writer.close();
        outputfile.close();
        
        logger.info("DONE processing file " + output.getName());
		return output.getName();
	}
	
	private void process() {
		try 
		{
			
			//setting up variables
			final String process = System.getProperty("TaskCardsIngestion_fileLoc");
			inputFolder = new File(process);
			String exectued = "OK",outcome = "PROCESSED_";
			int itr;
			ArrayList<MT_TRAX_RCV_I87_RES> taskCardsOut = new ArrayList<MT_TRAX_RCV_I87_RES>();
			ArrayList<MT_TRAX_SND_I87_REQ> taskCardsMark = new ArrayList<MT_TRAX_SND_I87_REQ>(); 
			ArrayList<MT_TRAX_SND_I87_REQ> taskCardsMarkFailure = new ArrayList<MT_TRAX_SND_I87_REQ>(); 
			MT_TRAX_SND_I87_REQ taskCard = null;
			
			
			//get Task Card out
			taskCardsOut = data.getTaskCards();
			
				try {
					if(taskCardsOut.size() != 0) {
						
						insertFileOut(taskCardsOut);
						
					}
				}catch(Exception e) {
					TaskCardsIngestionController.addError(e.toString());
					TaskCardsIngestionController.sendEmailOutFile(taskCardsOut);
				}
		
			
			
			//Mark Transaction
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
				try
				{	
					
					
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
							 taskCard = new MT_TRAX_SND_I87_REQ();
					            for (String cell : row) {	            	
					            	 
					                if(itr == 0) {
					                	taskCard.setTRAXTCNumber(cell);
					                	logger.info(taskCard.getTRAXTCNumber() + "\t");
					            	}
					            	if(itr == 1) {
					            		taskCard.setTRAXWO(cell);
					            		logger.info(taskCard.getTRAXWO() + "\t");
					            	}
					            	if(itr == 2) {
					            		taskCard.setSAPOrderNumber(cell);
					            		logger.info(taskCard.getSAPOrderNumber() + "\t");
					            	}
					            	if(itr == 3) {
					            		taskCard.setSAPOperationNumber(cell);
					            		logger.info(taskCard.getSAPOperationNumber() + "\t");
					            	}
					            	if(itr == 4) {
					            		taskCard.setSuccessErrorIndicator(cell);
					            		logger.info(taskCard.getSuccessErrorIndicator() + "\t");
					            	}
					            	if(itr == 5) {
					            		taskCard.setRemarks(cell);
					            		logger.info(taskCard.getRemarks() + "\t");
					            	}
					                itr++;				                
					            }
					            
					            taskCardsMark.add(taskCard);
					            
					        }   
						 
						 	 
					 }catch(Exception e){
						 TaskCardsIngestionController.addError(e.toString());
						 
						 outcome = "FAILURE_";
						 throw new Exception("Failed to read file");
					 }finally {
						 csvReader.close();
						 filereader.close();
						 
					 }
					
			       for(MT_TRAX_SND_I87_REQ taskcard: taskCardsMark) {
			    	   
			    	   exectued = "OK";
			    	   
			    	  
			    	   exectued = data.markTransaction(taskcard);
			    	   
			    	   if(!exectued.equalsIgnoreCase("OK")) {
			    		   taskCardsMarkFailure.add(taskcard);
			    	   }
			       }
			        			        			        
				   String fileName = file.getName(); 
				   
				   if(!taskCardsMarkFailure.isEmpty()){
				    	exectued = insertFileMark(taskCardsMarkFailure,"FAILURE_",fileName);
				    	taskCardsMarkFailure = null;
				    	throw new Exception("Failed Import are in File " + exectued);
				   }
				   
				}
				catch(Exception e)
				{
					TaskCardsIngestionController.addError(e.toString());
					TaskCardsIngestionController.sendEmailFile(file);
					
					logger.severe(e.toString());
				}
				insertFileMark(taskCardsMark,outcome,file.getName());
				file.delete();
			}
		}
		catch(Throwable e)
		{
			logger.severe(e.toString());
		}
	}
	
	public void run() 
	{
		try 
		{
			if(data.lockAvailable("I87"))
			{
				data.lockTable("I87");
				process();
				data.unlockTable("I87");
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	 }
	
	
	
	
}