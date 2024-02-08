package trax.aero.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.GenericEntity;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import trax.aero.logger.LogManager;
import trax.aero.pojo.Import;


public class ImportClockOnOffController {

	static Logger logger = LogManager.getLogger("ImportClockOnOff_I29");

	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	public ImportClockOnOffController()
	{
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static String getError() {
		return errors;
	}
	
	public static void sendEmail(Import message)
	{
		try
		{
			
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
		
			final String toEmail = System.getProperty("ImportClockOnOff_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Import Clock On Off interface did not insert JSON correctly.");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Import Clock On Off interface " 
					+"has encountered an issue. "	+ 
					"SEQ NO " + message.getMessage().getTa().getSeqNo().toString() +"\n" 
					+ "Issues found at:\n"  
					+errors);
			email.send();
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			logger.severe("Email not found");
			
		}
		finally
		{
			errors = "";
		}
	}
	
}
