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
import trax.aero.pojo.CarryForward;


import trax.aero.pojo.MT_TRAX_RCV_I74_4070_RES;
import trax.aero.pojo.Print;



public class CarryForwardController {

	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	static Logger logger = LogManager.getLogger("CarryForward_I80");
	
	public CarryForwardController()
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
	
	public static void sendEmailInbound(MT_TRAX_RCV_I74_4070_RES log)
	{
		try
		{
			
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
		
			final String toEmail = System.getProperty("CarryForward_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Error encountered in Carry Forward interface");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Carry Forward interface " 
					+"has encountered an issue. "	+ 
					"Message Code: " + log.getErrorCode() +"\n" +
					"Message text: " + log.getRemarks() +"\n" +
					"Order Number: " + log.getOrderNumber() +"\n"
					+ "Issues found at:\n"  
					+errors);
			email.send();
		}
		catch(Exception e)
		{
			logger.info(e.toString());
			logger.info("Email not found");
			
		}
		finally
		{
			errors = "";
		}
		
		
	}
		
		public static void sendEmail()
		{
			try
			{
				
				
				String fromEmail = System.getProperty("fromEmail");
				String host = System.getProperty("fromHost");
				String port = System.getProperty("fromPort");
			
				final String toEmail = System.getProperty("CarryForward_toEmail");
				
				ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
				
				Email email = new SimpleEmail();
				email.setHostName(host);
				email.setSmtpPort(Integer.valueOf(port));
				email.setFrom(fromEmail);
				email.setSubject("Error encountered in Carry Forward interface");
				for(String emails: emailsList)
		        {
					email.addTo(emails);
		        }
				email.setMsg("Carry Forward interface " 
						+"has encountered an issue. "
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
