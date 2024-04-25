package trax.aero.utils;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.controller.UpdateTaskcardStatusController;

import trax.aero.data.UpdateTaskcardStatusData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.I74_Request;
import trax.aero.pojo.I74_Response;




public class Run implements Runnable {
	
	//Variables
	UpdateTaskcardStatusData data = null;
	//final String ID = System.getProperty("JobConfirmation_ID");
	//final String Password = System.getProperty("JobConfirmation_Password");
	final String url = System.getProperty("UpdateTaskCardStatus_URL");
	final int MAX_ATTEMPTS = 3;
	Logger logger = LogManager.getLogger("UpdateTaskCardStatus_I74");
	
	public Run() {
		data = new UpdateTaskcardStatusData();
	}
	
	private void process() {
		TaskCardPoster poster = new TaskCardPoster();
		ArrayList<I74_Request> ArrayRequest = new ArrayList<I74_Request>();
		ArrayList<I74_Request> ArrayRequestError = new ArrayList<I74_Request>();
			String exceuted = "OK";
			try 
			{
								
				// loop
				ArrayRequest = data.getTaskCards();
				boolean success = false;
				
				if(!ArrayRequest.isEmpty()) {
					for(I74_Request req : ArrayRequest) {
						success = false;
					
						JAXBContext jc = JAXBContext.newInstance(I74_Request.class);
						Marshaller marshaller = jc.createMarshaller();
						marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
						StringWriter sw = new StringWriter();
						marshaller.marshal(req, sw);
						
						logger.info("Ouput: " + sw.toString());
						
						for(int i = 0; i < MAX_ATTEMPTS; i++)
						{
							success = poster.postTaskCard(req, url);
							if(success)
							{
								String body = poster.getBody();
								StringReader sr = new StringReader(body);				
								jc = JAXBContext.newInstance(I74_Response.class);
						        Unmarshaller unmarshaller = jc.createUnmarshaller();
						        I74_Response input = (I74_Response) unmarshaller.unmarshal(sr);
						        if(input.getErrorCode() != null && !input.getErrorCode().isEmpty() 
						        	&& input.getErrorCode().equalsIgnoreCase("51")
						        	&&	input.getRemarks() != null && !input.getRemarks().isEmpty() 
						        	&& input.getRemarks().contains("locked") ) {
						        	Thread.sleep(300000); 
						        	continue;
						        }else {
						        	break;
						        }
						        
							}
							
						}			

						if(!success)
						{
							 logger.severe("Unable to send Order Number: "+req.getOrderNumber() +" to URL " + url);
							UpdateTaskcardStatusController.addError("Unable to send Order Number: "+req.getOrderNumber() +" to URL " + url);
							ArrayRequestError.add(req);
							
						}else {
							I74_Response input = null;
														
							try 
					        {    
								String body = poster.getBody();
								StringReader sr = new StringReader(body);				
								jc = JAXBContext.newInstance(I74_Response.class);
						        Unmarshaller unmarshaller = jc.createUnmarshaller();
						        input = (I74_Response) unmarshaller.unmarshal(sr);

						        marshaller = jc.createMarshaller();
						        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
						        sw = new StringWriter();
							    marshaller.marshal(input,sw);
							    logger.info("Input: " + sw.toString());
							    if(input.getErrorCode() != null && !input.getErrorCode().isEmpty() && input.getErrorCode().equalsIgnoreCase("53")) {
							    	exceuted = data.markTransaction(input);
								}else {
																		
									logger.severe("Received Response with Remarks: " + input.getRemarks() +", Order Number: "+input.getOrderNumber() + ", Error Code: " +input.getErrorCode());
									UpdateTaskcardStatusController.addError("Received Response with Remarks: " + input.getRemarks() +", Order Number: "+input.getOrderNumber() + ", Error Code: " +input.getErrorCode());
									exceuted = data.markTransaction(input);
									exceuted = "Issue found";
								}
					        	if(exceuted == null || !exceuted.equalsIgnoreCase("OK")) {
					        		exceuted = "Issue found";
					        		throw new Exception("Issue found");
					        	}
							}
							catch(Exception e)
							{
								Map<String,String> map = data.getStatus(input.getOrderNumber());
								UpdateTaskcardStatusController.addError(e.toString());
								UpdateTaskcardStatusController.sendEmailResponse(input, map.get("STATUS"),map.get("STATUS_CATEGORY"));
							}
					       finally 
					       {   
					    	   logger.info("finishing");
					       }   
							    
							    
							    
							    
							    
							 logger.info("POST status: " + String.valueOf(success) + " Order Number: "+ req.getOrderNumber());
						}
					}
				}
				
				
				
				if(!UpdateTaskcardStatusController.getError().isEmpty()) {
					 throw new Exception("Issue found");
				}
			}
			catch(Throwable e)
			{
				 logger.severe(e.toString());
				UpdateTaskcardStatusController.addError(e.toString());
				if(!ArrayRequestError.isEmpty()) {			
					UpdateTaskcardStatusController.sendEmailRequest(ArrayRequestError);
				}else{
					UpdateTaskcardStatusController.sendEmailRequest(ArrayRequest);
				}
			}
	}
	
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I74"))
			{
				data.lockTable("I74");
				process();
				data.unlockTable("I74");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	 }
}