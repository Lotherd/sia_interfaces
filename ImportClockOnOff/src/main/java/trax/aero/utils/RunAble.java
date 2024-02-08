package trax.aero.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import trax.aero.controller.ImportClockOnOffController;
import trax.aero.data.ImportClockOnOffData;

import trax.aero.logger.LogManager;
import trax.aero.pojo.Import;


public class RunAble implements Runnable {
	
	//Variables
	ImportClockOnOffData data = null;
	ImportClockOnOffData lock = null;
	static Logger logger = LogManager.getLogger("ImportClockOnOff_I29");
	String queueUrlFrom = System.getProperty("ImportClockOnOff_FromSQS");
	String queueUrlTo= System.getProperty("ImportClockOnOff_ToSQS");
    
	SqsClient sqsClient = null;
    
	public RunAble() {
		lock = new ImportClockOnOffData();	
		sqsClient = SqsClient.builder().build();
	}
	
	
	private void process() {
		try 
		{
			String exceuted = "OK";
			
			ObjectMapper Obj = new ObjectMapper();
			
			
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder().maxNumberOfMessages(10).queueUrl(queueUrlFrom).build();
	            
			List<Message> messages =  sqsClient.receiveMessage(receiveMessageRequest).messages();

	        for (Message m : messages) {
	        	try {
	        		
	        		exceuted = "OK";
	        		String body = m.body();
	        		
	        		body = body.replaceAll("\u200B", "").trim();
	        		body = body.replaceAll("[\\p{Cf}]", "");
		           	logger.info("Message Body: " + body);
		           	Import punch  = new Import(); 
		           	
		           	try {
		           		punch = Obj.readValue(body, Import.class);
		           	}catch(Exception e) {
		           		e.printStackTrace();
		           		exceuted ="Parseing JSON ERROR";
		           		logger.severe(exceuted);
		           		logger.severe(e.toString());
		           		
		           	}
		           	if(punch.getMessage().getTa().getStaffNo() != null && !punch.getMessage().getTa().getStaffNo().isEmpty()) {
		           		logger.info("SeqNo: " + punch.getMessage().getTa().getSeqNo().toString()); 
		           	}
				    
				    data = new ImportClockOnOffData();
				    try 
				    {    		 
				       	exceuted = data.clock(punch, queueUrlTo);
				       	if(exceuted == null || !exceuted.equalsIgnoreCase("OK")) {
				           		exceuted = "Issue found";
				           		throw new Exception("Issue found");
				           	}
				    	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    		ImportClockOnOffController.addError(e.toString());
				    		ImportClockOnOffController.sendEmail(punch);
				    	}
				        finally 
				        {   
				           try 
				    		{
				    			if(data.getCon() != null && !data.getCon().isClosed())
				    				data.getCon().close();
				    		} 
				    		catch (SQLException e) 
				    		{ 
				    			logger.severe(e.toString());
				    		}
				           logger.info("finishing");
				        }
	        	}catch(Exception e){
	        		e.printStackTrace();
	        		logger.severe(e.toString());
	        	}finally {
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
			if(lock.lockAvailable("I29"))
			{
				lock.lockTable("I29");
				process();
				lock.unlockTable("I29");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
}