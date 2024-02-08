package trax.aero.utils;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.controller.DowngradeLoopController;

import trax.aero.data.DowngradeLoopData;
import trax.aero.logger.LogManager;

import trax.aero.pojo.MT_TRAX_RCV_I94_4084_RES;
import trax.aero.pojo.MT_TRAX_SND_I94_4084_REQ;




public class Run implements Runnable {
	
	//Variables
	DowngradeLoopData data = null;
	//final String ID = System.getProperty("JobConfirmation_ID");
	//final String Password = System.getProperty("JobConfirmation_Password");
	final String url = System.getProperty("DowngradeLoop_URL");
	final int MAX_ATTEMPTS = 3;
	Logger logger = LogManager.getLogger("DowngradeLoop_I94");
	
	public Run() {
		data = new DowngradeLoopData();
	}
	
	private void process() {
		LoopPoster poster = new LoopPoster();
		ArrayList<MT_TRAX_SND_I94_4084_REQ> requests = new ArrayList<MT_TRAX_SND_I94_4084_REQ>();
		String exceuted = "OK";
		try 
		{
							
			// loop
			requests = data.getLoops();
			boolean success = false;
			
			
			for(MT_TRAX_SND_I94_4084_REQ request : requests) {
				success = false;
				JAXBContext jc = JAXBContext.newInstance(MT_TRAX_SND_I94_4084_REQ.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				marshaller.marshal(request, sw);
				
				logger.info("Output to post: " + sw.toString());
				
				for(int i = 0; i < MAX_ATTEMPTS; i++)
				{
					
					
					
					success = poster.postLoop(request, url);
					if(success)
					{
						break;
					}
				}
				if(!success)
				{
					logger.severe("Unable to send XML with RFO: "+request.getSAPServiceOrderNumber() +" to URL " + url);
					DowngradeLoopController.addError("Unable to send XML with RFO: "+request.getSAPServiceOrderNumber() +" to URL " + url + " MAX_ATTEMPTS: "  +MAX_ATTEMPTS );
					DowngradeLoopController.addError("SVO: " + data.getSAPOrderNumber(request.getSAPServiceOrderNumber()) + ", RFO: " + request.getSAPServiceOrderNumber() 
					+ ", Date: " + new Date().toString()  + ", SHOP WO: " +data.getShopWo(request.getSAPServiceOrderNumber()) +
					", Production WO: " + data.getProdWo(request.getSAPServiceOrderNumber()) );
					
					
					DowngradeLoopController.sendEmail(data.getShopWo(request.getSAPServiceOrderNumber()));	
				}else {
					MT_TRAX_RCV_I94_4084_RES input = null;
													
						try 
				        {    
							String body = poster.getBody();
							StringReader sr = new StringReader(body);				
							jc = JAXBContext.newInstance(MT_TRAX_RCV_I94_4084_RES.class);
					        Unmarshaller unmarshaller = jc.createUnmarshaller();
					        input = (MT_TRAX_RCV_I94_4084_RES) unmarshaller.unmarshal(sr);

					        marshaller = jc.createMarshaller();
					        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
					        sw = new StringWriter();
						    marshaller.marshal(input,sw);
						    logger.info("Input: " + sw.toString());
						    if(input.getErrorCode().equalsIgnoreCase("53")) {
								data.markTransaction(input);
							}else {
								data.markTransaction(input);
								exceuted = ("SVO: " + input.getSAPOrderNumber() + ", RFO: " + input.getSAPServiceOrderNumber() 
								+ ", Date: " + new Date().toString()  + ", SHOP WO: " +data.getShopWo(input.getSAPServiceOrderNumber()) +
								", Production WO: " + data.getProdWo(input.getSAPServiceOrderNumber()) );
								
								logger.severe(exceuted);
								DowngradeLoopController.addError(exceuted);
								
								exceuted = ("Received acknowledgement with Error Code: " + input.getErrorCode() 
								+", Status Message: "+input.getRemarks()) ;
								
								logger.severe(exceuted);
								DowngradeLoopController.addError(exceuted);
								
								exceuted = "Issue found";
								DowngradeLoopController.sendEmailACK(data.getShopWo(input.getSAPServiceOrderNumber()));
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				       finally 
				       {
				    	   /*
				    	   try 
							{
								if(data.getCon() != null && !data.getCon().isClosed())
									data.getCon().close();
							} 
							catch (SQLException e) 
							{ 
								e.printStackTrace();
							}
							*/
				    	   logger.info("finishing");
				       }
						    
						    
						    
						    
						    
						 
					
					logger.info("POST status: " + String.valueOf(success) +" to URL: " + url);
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
			if(data.lockAvailable("I94"))
			{
				data.lockTable("I94");
				process();
				data.unlockTable("I94");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}