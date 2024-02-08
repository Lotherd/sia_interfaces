package trax.aero.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.net.HttpURLConnection;
import java.net.URL;

import trax.aero.controller.ACConfigurationController;
import trax.aero.data.ACConfigurationData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I51_ACK_4073;
import trax.aero.pojo.MT_TRAX_SND_I51_4072;
 

public class Run implements Runnable {
	
	//Variables
	ACConfigurationData data = null;
	//final String ID = System.getProperty("JobConfirmation_ID");
	//final String Password = System.getProperty("JobConfirmation_Password");
	final String url = System.getProperty("ACConfiguration_URL");
	int numberOfTries = 0;
	Logger logger = LogManager.getLogger("ACConfiguration_I51");
	EntityManagerFactory factory;
	
	public Run() {
		factory = Persistence.createEntityManagerFactory("ACConfigDS");
		 data = new ACConfigurationData(factory);
	}
	
	
	private void process() {
		ACPoster poster = new ACPoster();
		List<MT_TRAX_SND_I51_4072> MasterOutbound = new ArrayList<MT_TRAX_SND_I51_4072>();
			String exceuted = "OK";
			try 
			{
				logger.info("getting data" );				
				// loop
				MasterOutbound = data.getAC();
				boolean success = false;
					
				if(!MasterOutbound.isEmpty() ) {
					for(MT_TRAX_SND_I51_4072 o : MasterOutbound) {
						numberOfTries = 0;
						success = false;
						Marshaller marshaller = null;
						JAXBContext jc = JAXBContext.newInstance(MT_TRAX_SND_I51_4072.class);
						marshaller = jc.createMarshaller();
						marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
						StringWriter sw = new StringWriter();
						marshaller.marshal(o, sw);
						
						logger.info("Ouput: " + sw.toString());
						
						for(int i = 0; i < 3; i++) {
							success = poster.postAC(o, url);
							if(!success) {
								numberOfTries++;
							}else if(success){
								 break;
							}
						}
						if(numberOfTries >= 3)
						{
							logger.severe("Unable to send data" + " to URL "+ url);
							ACConfigurationController.addError("Unable to send AC " +o.getAircraftTailNumber() + " to URL "+ url +" Max tries attempted: " +numberOfTries);
							exceuted = "Issue found";
						}
						
					}
				}
				
				if(!ACConfigurationController.getError().isEmpty()) {
					 throw new Exception("Issue found");
				}
				logger.info("finishing..." );	
				
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				ACConfigurationController.addError(e.toString());
				ACConfigurationController.sendEmailPOST();
			}
	}
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I51_POST"))
			{
				data.lockTable("I51_POST");
				process();
				data.unlockTable("I51_POST");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
}