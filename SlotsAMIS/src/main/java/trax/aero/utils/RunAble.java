package trax.aero.utils;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;



import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import trax.aero.controller.SlotsAMISController;

import trax.aero.data.SlotsAMISData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.AMISItem;



public class RunAble implements Runnable {
	
	//Variables
	SlotsAMISData data = null;
	static Logger logger = LogManager.getLogger("SlotsAIMS_I31");
	String queueUrlFrom = System.getProperty("SlotsAIMS_FromSQS");
	SqsClient sqsClient = null;
	ObjectMapper Obj = null;
	public RunAble() {
		data = new SlotsAMISData("EM");
		 sqsClient = SqsClient.builder().build();
		 Obj = new ObjectMapper();
	}
	
	private void process() {
		
		
		try 
		{
			String exceuted = "OK";
							
			 
			
			
			
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder().maxNumberOfMessages(10).queueUrl(queueUrlFrom).build();
			
			List<Message> messages =  sqsClient.receiveMessage(receiveMessageRequest).messages();
			
	        for (Message m : messages) {
	        	
	        	try {
	        	
	        	exceuted = "OK";
	        	String body = m.body();
        		
        		body = body.replaceAll("\u200B", "").trim();
        		body = body.replaceAll("[\\p{Cf}]", "");
	           	logger.info("Message Body: " + body);
	           	AMISItem request = new AMISItem(); 
	           	try {
	           		request = Obj.readValue(body, AMISItem.class);
	           	}catch(Exception e) {
	           		exceuted ="Parseing JSON ERROR";
	           		logger.severe(exceuted);
	           		logger.severe(e.toString());
	           	}
	           	if(request.getAmis().getOid() != null && !request.getAmis().getOid().isEmpty()) {
	           		logger.info("Oid: " + request.getAmis().getOid()); 
	           	}
			    
			     
			    try 
	            {    
	    				 
	            	exceuted = data.slot(request);
	            	if(exceuted == null || !exceuted.equalsIgnoreCase("OK")) {
	            		exceuted = "Issue found";
	            		throw new Exception("Issue found");
	            	}else {
	            		exceuted = "WO " + data.WO;
	            	}
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			SlotsAMISController.addError(e.toString());
	    			SlotsAMISController.sendEmail(request);
	    		}
	           finally 
	           {   
	        	   
	        	   logger.info("finishing");
	           }
	        	}catch(Exception e){
	        		logger.severe(e.toString());
	        	}
	        	finally {
		        	 DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder().queueUrl(queueUrlFrom).receiptHandle(m.receiptHandle()).build();
				     sqsClient.deleteMessage(deleteMessageRequest);  
		        }
	        } 	
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
		}
	}
	
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I31"))
			{
				data.lockTable("I31");
				process();
				data.unlockTable("I31");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
}