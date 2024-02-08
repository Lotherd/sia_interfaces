package trax.aero.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import trax.aero.controller.ACConfigurationController;
import trax.aero.data.ACConfigurationData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Equipment;
import trax.aero.pojo.MT_TRAX_RCV_I51_ACK_4073;

public class ProcessRun implements Runnable {

	ACConfigurationData data = null;
	EntityManagerFactory factory;
	Logger logger = LogManager.getLogger("ACConfiguration_I51");
	public static List<Equipment> sentToDatabase = null;
	public volatile static String exceuted = "OK";
	
	public ProcessRun() {
		factory = Persistence.createEntityManagerFactory("ACConfigDS");
		data = new ACConfigurationData(factory);
	}
	
	private MT_TRAX_RCV_I51_ACK_4073 input =null;
	
	public void run() 
	{
			String exceuted = "OK";
		
			try {
				if(input.getTopFunctionalLocation() == null && !data.checkTopFunctionalLocation(input.getTopFunctionalLocation())) {
					exceuted = "Can not insert/update AC: "+ input.getTopFunctionalLocation().getTopFunctionalLocation() +" as ERROR: input is null or does not have minimum values";
					logger.severe(exceuted);
					ACConfigurationController.addError(exceuted);
					throw new Exception("Issue found");
				}
				
				 
				int scheduledPoolSize = 4;
				if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
					scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
				}
				logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
				ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
								
				
				
				logger.info(" SIZE " + input.getTopFunctionalLocation().getEquipment().size());
				

				sentToDatabase =  Collections.synchronizedList(new ArrayList<Equipment>());
					
				for(Equipment e: input.getTopFunctionalLocation().getEquipment()) {
					Worker worker = new Worker(factory);
			    	worker.setInput(e);
			    	worker.setTop(input.getTopFunctionalLocation().getTopFunctionalLocation());
			    	scheduledServ.execute(worker);
			    	   	    	   
			    }
			      
			    scheduledServ.shutdown();
			    scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);	
				
			    data.checkForNotSentEquipment(new ArrayList<Equipment>(sentToDatabase),input.getTopFunctionalLocation().getTopFunctionalLocation());
			    
			    if(ProcessRun.exceuted == null || !ProcessRun.exceuted.equalsIgnoreCase("OK")) {
			    	throw new Exception("Issue found");
			    }
			    
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			logger.severe(e.toString());
			ACConfigurationController.addError(e.toString());
			ACConfigurationController.sendEmail(input.getTopFunctionalLocation().getTopFunctionalLocation());
		}finally {
			logger.info("finishing");
		}
	  
	}

	public MT_TRAX_RCV_I51_ACK_4073 getInput() {
		return input;
	}

	public void setInput(MT_TRAX_RCV_I51_ACK_4073 inputs) {
		this.input = inputs;
	}
}
