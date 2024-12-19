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

import javax.ejb.EJB;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBException;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;

import trax.aero.controller.ImportWarehouseController;
import trax.aero.data.ImportWarehouseData;
import trax.aero.interfaces.IImportWarehouseData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_BATCH;

public class RunAble implements Runnable {
	
	//Variables
	boolean isPostOn = false;
    boolean isBatchOn =false;
    
    
    public boolean isPostOn() {
        return isPostOn;
    }
    public void setPostOn(boolean isPostOn) {
        this.isPostOn = isPostOn;
    }
    
    public boolean isBatchOn() {
        return isBatchOn;
    }
    public void setBatchOn(boolean isBatchOn) {
        this.isBatchOn = isBatchOn;
    }
    Logger logger = LogManager.getLogger("ImportWarehouse_I46");
    
	
	//private Worker worker = null;
	//private ScheduledExecutorService scheduledServ;
	
	public static List<MT_TRAX_RCV_I46_4077_BATCH> warehousesFailure;
	
	private static File inputFiles[],inputFolder;
	private static FilenameFilter filter = new FilenameFilter() 
	{		 
		public boolean accept(File dir, String name) 
		{
			return (name.toLowerCase().endsWith(".csv"));
		}
	};
	
	@EJB IImportWarehouseData data;
	
	
	public RunAble(IImportWarehouseData data)
	{
		this.data = data;
	}
	
	private void insertFile(File file, String outcome) 
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime  currentDateTime = LocalDateTime.now();
		
		File todayFolder = new File(System.getProperty("ImportWarehouse_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())			
			todayFolder.mkdir();
		
		boolean result = file.renameTo(new File(todayFolder + File.separator
			+ outcome + Calendar.getInstance().getTimeInMillis() + "_" + file.getName()));
		
		logger.info("DONE processing file " + file.getName() + "Result: " + result);
	}
	
	
	
	private String insertFileFailed(ArrayList<MT_TRAX_RCV_I46_4077_BATCH> warehousesFailure, String outcome, String fileName) throws JAXBException, IOException
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
		

        List<String[]> data = new ArrayList<String[]>();
       
        String[] header = {"Material Number", "Material Description", "Plant", "Available Quantity","Unit of Measure"};
        
        data.add(header);
        
   
        for(MT_TRAX_RCV_I46_4077_BATCH w : warehousesFailure) {
        	
        	String[] arr = {w.getPN(), w.getPNDESCRIPTION(), w.getLOCATION(), w.getQTY(),w.getWEIGHTUNIT()};
        	
        	data.add(arr);
        }
        
		File compFolder = new File(System.getProperty("ImportWarehouse_compFiles"));
		if (!compFolder.isDirectory())
		compFolder.mkdir();
		File todayFolder = new File(System.getProperty("ImportWarehouse_compFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
		todayFolder.mkdir();
	
		File output = new File(todayFolder + File.separator
		+ outcome + "_"+ Calendar.getInstance().getTimeInMillis() + "_" + fileName);
		
        FileWriter outputfile = new FileWriter(output);
  

        CSVWriter writer = new CSVWriter(outputfile, '\t',
                                         CSVWriter.NO_QUOTE_CHARACTER,
                                         CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                         CSVWriter.DEFAULT_LINE_END);
		
        writer.writeAll(data);
        writer.close();
        outputfile.close();
        
        logger.info("DONE processing file " + output.getName());
		return output.getName();
	}
	
	private void batch() throws Exception {
		
		//setting up variables
		final String process = System.getProperty("ImportWarehouse_fileLoc");
		inputFolder = new File(process);
		String exectued = "OK",outcome = "PROCESSED_";
		ArrayList<MT_TRAX_RCV_I46_4077_BATCH> warehouses = new ArrayList<MT_TRAX_RCV_I46_4077_BATCH>();
		warehousesFailure = Collections.synchronizedList(new ArrayList<MT_TRAX_RCV_I46_4077_BATCH>()); 
		int itr;
		MT_TRAX_RCV_I46_4077_BATCH warehouse = null;
		
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
				RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().withSeparator('\t').build();

				
				 CSVReader csvReader = new CSVReaderBuilder(filereader)
                         .withCSVParser(rfc4180Parser)
                         .withSkipLines(1)
                         .build();
				 
				 
				 try {
					 List<String[]> allData = csvReader.readAll();
					 
					 for (String[] row : allData) {
						 itr = 0;
						 warehouse = new MT_TRAX_RCV_I46_4077_BATCH();
				            for (String cell : row) {	            	
				            	 
				                if(itr == 0) {
				                	warehouse.setPN(cell);
				                	//logger.info(warehouse.getPN() + "\t");
				            	}
				            	if(itr == 1) {
				            		warehouse.setPNDESCRIPTION(cell.trim());
				            		//logger.info(warehouse.getPNDESCRIPTION() + "\t");
				            	}
				            	if(itr == 2) {
				            		warehouse.setLOCATION(cell.trim());
				            		//logger.info(warehouse.getLOCATION() + "\t");
				            	}
				            	if(itr == 3) {
				            		warehouse.setQTY(cell.trim());
				            		//logger.info(warehouse.getQTY() + "\t");
				            	}
				            	if(itr == 4) {
				            		warehouse.setWEIGHTUNIT(cell.trim());
				            		//logger.info(warehouse.getWEIGHTUNIT() + "\t");
				            	}
				            	
				            	warehouse.setNotesText(null);
				            	warehouse.setSN(null);
				                itr++;				                
				            }
				            
				            warehouses.add(warehouse);
				            
				        }   
					 
					 	 
				 }catch(Exception e){
					 e.printStackTrace();
					 ImportWarehouseController.addError(e.toString());
					 logger.severe(e.toString());
					 outcome = "FAILURE_";
					 throw new Exception("Failed to read file");
				 }finally {
					 csvReader.close();
					 filereader.close();
				 }
				
				
				 
				 
				int scheduledPoolSize = 4;
				if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
					scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
				}
				logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
				ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
								
				//logger.info("DELETING INVENTORY RECORDS");
				//data.deleteAllInventoryRecords();
				
				logger.info(String.valueOf(warehouses.size()));
		       for(MT_TRAX_RCV_I46_4077_BATCH house: warehouses) {
		    	   
		    	   exectued = "OK";
		    	   Worker worker = new Worker(data);
		    	   worker.setInput(house);
		    	   scheduledServ.execute(worker);
		    	   Thread.sleep(100);
		       }
		      
		       scheduledServ.shutdown();
		       scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);	
		       data.getEm().flush();
			   String fileName = file.getName(); 
			   
	   
			   if(!warehousesFailure.isEmpty()){
			    	exectued = insertFileFailed(new ArrayList<MT_TRAX_RCV_I46_4077_BATCH>(warehousesFailure),"FAILURE_",fileName);
			    	warehousesFailure = null;
			    	throw new Exception("Failed Import are in File " + exectued);
			   }
			   
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ImportWarehouseController.addError(e.toString());
				ImportWarehouseController.sendEmailFile(file);
				//insertFile(file,"FAILURE_");
				
				logger.info(e.toString());
			}finally {
				
				insertFile(file,outcome);
				
			}
		}
	}
	
	
	public void run() 
	{
			try 
			{
				
				if(isBatchOn()) {
					if(data.lockAvailable("I46_BATCH"))
					{
						data.lockTable("I46_BATCH");
						batch();
						data.unlockTable("I46_BATCH");
					}
				}
				if(isPostOn()) {
					if(data.lockAvailable("I46_POST"))
					{
						data.lockTable("I46_POST");
						data.invokePOST();
						data.unlockTable("I46_POST");
					}
				}	
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
			
			
	 }
}