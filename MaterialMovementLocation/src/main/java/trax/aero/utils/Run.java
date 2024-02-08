package trax.aero.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.net.HttpURLConnection;
import java.net.URL;


import trax.aero.controller.MaterialLocationController;
import trax.aero.data.MaterialLocationData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I95_4081_RES;
import trax.aero.pojo.MT_TRAX_SND_I95_4081_REQ;



public class Run implements Runnable {
	
	//Variables
	MaterialLocationData data = null;
	//final String ID = System.getProperty("JobConfirmation_ID");
	//final String Password = System.getProperty("JobConfirmation_Password");
	final String url = System.getProperty("MaterialLocation_URL");
	final int MAX_ATTEMPTS = 3;
	Logger logger = LogManager.getLogger("MaterialLocation_I95");
	
	public Run() {
		data = new MaterialLocationData();
	}
	
	private void process() {
		LocationPoster poster = new LocationPoster();
		ArrayList<MT_TRAX_SND_I95_4081_REQ> requests = new ArrayList<MT_TRAX_SND_I95_4081_REQ>();
		String exceuted = "OK";
		try 
		{
							
			// loop
			requests = data.getMaterials();
			boolean success = false;
			
			
			for(MT_TRAX_SND_I95_4081_REQ request : requests) {
				success = false;
				
				JAXBContext jc = JAXBContext.newInstance(MT_TRAX_SND_I95_4081_REQ.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				marshaller.marshal(request, sw);
				
				logger.info("Ouput: " + sw.toString());
				
				for(int i = 0; i < MAX_ATTEMPTS; i++)
				{
					success = poster.postLocation(request, url);
					if(success)
					{
						break;
					}
				}
				if(!success)
				{
					logger.severe("Unable to send XML with Order Number: "+request.getOrder().getOrderNumber() +" to URL " + url);
					MaterialLocationController.addError("Unable to send XML with Order Number: "+request.getOrder().getOrderNumber() +" to URL " + url + " MAX_ATTEMPTS: "  +MAX_ATTEMPTS );
				}else {
					
					
					MT_TRAX_RCV_I95_4081_RES input = null;
					
					try 
			        {    
						String body = poster.getBody();
						StringReader sr = new StringReader(body);				
						jc = JAXBContext.newInstance(MT_TRAX_RCV_I95_4081_RES.class);
				        Unmarshaller unmarshaller = jc.createUnmarshaller();
				        input = (MT_TRAX_RCV_I95_4081_RES) unmarshaller.unmarshal(sr);

				        marshaller = jc.createMarshaller();
				        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
				        sw = new StringWriter();
					    marshaller.marshal(input,sw);
					    logger.info("Input: " + sw.toString());
					    if(input.getSuccessErrorLog().getStatusErrorCode().equalsIgnoreCase("53")) {
							data.markTransaction(input);
						}else {
							data.markTransaction(input);
							exceuted = "Issue Found";					
							String wo =  data.getWO(input.getOrder().getOrderNumber());
							String rfo =  data.getRFO(wo);
							logger.severe("Received acknowledgement with Error Code: " + input.getSuccessErrorLog().getStatusErrorCode() +", Status Message: "+input.getSuccessErrorLog().getStatusMessage() + ", WO: " +wo + ", RFO: " + rfo);
							data.markTransaction(input);
							MaterialLocationController.sendEmailACK("Received acknowledgement with Error Code: " + input.getSuccessErrorLog().getStatusErrorCode() +", Status Message: "+input.getSuccessErrorLog().getStatusMessage() + ", WO: " +wo + ", RFO: " + rfo,wo) ;
						}			        	
					}
					catch(Exception e)
					{
						throw new Exception("Issue found");
					}
			       finally 
			       {   
			    	   logger.info("finishing");
			       }   
					logger.info("POST status: " + String.valueOf(success) +" to URL: " + url);
				}
			}
			if(!MaterialLocationController.getError().isEmpty()) {
				 throw new Exception("Issue found");
			}
				
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
			MaterialLocationController.addError(e.toString());
			MaterialLocationController.sendEmail(exceuted);
		}
	}
	
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I95"))
			{
				data.lockTable("I95");
				process();
				data.unlockTable("I95");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}