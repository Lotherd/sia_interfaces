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
import trax.aero.pojo.AMISItem;


public class SlotsAMISController {

	static Logger logger = LogManager.getLogger("SlotsAIMS_I31");
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	public SlotsAMISController()
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
	
	public static void sendEmail(AMISItem item)
	{
		try
		{
			GenericEntity<AMISItem> entity = new GenericEntity<AMISItem>(item) {
			};
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			
			final String toEmail = System.getProperty("SlotsAMIS_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			//TODO
			email.setSubject("Slots AMIS interface ran into a Issue in SQS");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Slots AMIS interface " 
					+" has encountered an issue. "	
					+ "OID " + item.getAmis().getOid() +"\n" 
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
		
		public static void sendEmailService(String outcome)
		{
			try
			{
				String fromEmail = System.getProperty("fromEmail");
				String host = System.getProperty("fromHost");
				String port = System.getProperty("fromPort");
				String toEmail = System.getProperty("SlotsAMIS_toEmail");
		        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
				Email email = new SimpleEmail();
				email.setHostName(host);
				email.setSmtpPort(Integer.valueOf(port));
				email.setFrom(fromEmail);
				email.setSubject("Slots AMIS interface ran into a Issue in service");
				for(String emails: emailsList)
		        {
		        	email.addTo(emails);
		        }
				email.setMsg("Input" 
						 
						+" has encountered an issue. "			
						+ "Enter records manually. "
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
