package trax.aero.utils;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.controller.EnteredManhoursController;

import trax.aero.data.EnteredManhoursData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I84_4071_RES;
import trax.aero.pojo.MT_TRAX_SND_I84_4071_REQ;
import trax.aero.pojo.OperationsRES;
import trax.aero.pojo.OpsLineEmail;
import trax.aero.pojo.OrderREQ;
import trax.aero.pojo.OrderRES;




public class Run implements Runnable {
	
	//Variables
	EnteredManhoursData data = null;
	final String url = System.getProperty("EnteredManhours_URL");
	final int MAX_ATTEMPTS = 3;
	Logger logger = LogManager.getLogger("EnteredManhours_I84");
	
	public Run() {
		data = new EnteredManhoursData();
	}
	
	private void process() {
		Poster poster = new Poster();
		ArrayList<MT_TRAX_SND_I84_4071_REQ> ArrayReq = new ArrayList<MT_TRAX_SND_I84_4071_REQ>();
		//MT_TRAX_SND_I84_4071_REQ ArrayRequestError = new MT_TRAX_SND_I84_4071_REQ();
			String exceuted = "OK";
			try 
			{			
				// loop
				ArrayReq = data.getTaskCards();
				boolean success = false;
				
				if(!ArrayReq.isEmpty()) {
					for(MT_TRAX_SND_I84_4071_REQ ArrayRequest : ArrayReq ) {
						JAXBContext jc = JAXBContext.newInstance(MT_TRAX_SND_I84_4071_REQ.class);
						Marshaller marshaller = jc.createMarshaller();
						marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
						StringWriter sw = new StringWriter();
						marshaller.marshal(ArrayRequest, sw);
						
						logger.info("Ouput: " + sw.toString());
						
							for(int i = 0; i < MAX_ATTEMPTS; i++)
							{
								success = poster.post(ArrayRequest, url);
								if(success)
								{
									String body = poster.getBody();
									boolean lock = true;
									StringReader sr = new StringReader(body);				
									jc = JAXBContext.newInstance(MT_TRAX_RCV_I84_4071_RES.class);
							        Unmarshaller unmarshaller = jc.createUnmarshaller();
							        MT_TRAX_RCV_I84_4071_RES input = (MT_TRAX_RCV_I84_4071_RES) unmarshaller.unmarshal(sr);
							       for(OrderRES r : input.getOrder()) {
								        if(r.getErrorCode() != null && !r.getErrorCode().isEmpty() 
								        	&& r.getErrorCode().equalsIgnoreCase("51")
								        	&&	r.getRemarks() != null && !r.getRemarks().isEmpty() 
								        	&& r.getRemarks().contains("locked") ) {
								        	lock = false;
								        }
							       }
							       if(lock) {
							    	   break;
							       }else {
							    	   Thread.sleep(60000); 
							       }
								}
							}
							if(!success)
							{
								logger.severe("Unable to send XML " +" to URL " + url);
								EnteredManhoursController.addError("Unable to send XML " +" to URL " + url + " MAX_ATTEMPTS: "  +MAX_ATTEMPTS );
							}else {
								MT_TRAX_RCV_I84_4071_RES input = null;
															
								
								
								try 
						        {    
									String body = poster.getBody();
									StringReader sr = new StringReader(body);				
									jc = JAXBContext.newInstance(MT_TRAX_RCV_I84_4071_RES.class);
							        Unmarshaller unmarshaller = jc.createUnmarshaller();
							        input = (MT_TRAX_RCV_I84_4071_RES) unmarshaller.unmarshal(sr);
	
							        marshaller = jc.createMarshaller();
							        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
							        sw = new StringWriter();
								    marshaller.marshal(input,sw);
								    logger.info("Input: " + sw.toString());
								    
								    
						        	exceuted = data.markTransaction(input);
						        	if(!exceuted.equalsIgnoreCase("OK")) {
						        		exceuted = "Issue found";      		
						        		throw new Exception("Issue found");
						        	}
							        
								}
								catch(Exception e)
								{
									logger.severe(e.toString());
									EnteredManhoursController.addError(e.toString());
									if(input !=null) {
									
										for(OrderRES o : input.getOrder()) {
											if(o.getOperations() !=null && !o.getOperations().isEmpty()) {
												for(OperationsRES op : o.getOperations()) {
												
													OpsLineEmail opsLineEmail = data.getOpsLineStaffName(o.getWO(),o.getTaskCard(), op.getTRAXItemNumber());
									        		
													EnteredManhoursController.sendEmailOpsLine(op.getOperationNumber(), o, opsLineEmail);
												}
											}else {
												OpsLineEmail opsLineEmail = data.getOpsLineStaffName(o.getWO(),o.getTaskCard(),"1");
								        		
												EnteredManhoursController.sendEmailOpsLine("", o, opsLineEmail);
											}
										}
									}else {
										EnteredManhoursController.sendEmailService("NULL");
									}
								}
						       finally 
						       {   
						    	   logger.info("finishing");
						       }
								logger.info("POST status: " + String.valueOf(success) +" to URL: " + url);
							}
						}
				}
				if(!EnteredManhoursController.getError().isEmpty()) {
					 throw new Exception("Issue found");
				}
			}
			catch(Throwable e)
			{
				logger.severe(e.toString());
				EnteredManhoursController.addError(e.toString());		
				EnteredManhoursController.sendEmailRequest(ArrayReq);
				
			}
	}
	
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I84"))
			{
				data.lockTable("I84");
				process();
				data.unlockTable("I84");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}