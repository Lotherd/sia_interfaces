package trax.aero.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import trax.aero.logger.LogManager;

public class DowngradeLoopController {
	
	static Logger logger = LogManager.getLogger("DowngradeLoop_I94");
	
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	public DowngradeLoopController()
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
	
	public static void sendEmail(String job)
	{
		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			final String toEmail = System.getProperty("DowngradeLoop_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Interface Error encountered in delink of Shop WO "+job+  " after max retry");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Downgrade Loop interface " 
					+" has encountered an issue. "			
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
	
	public static void sendEmailACK(String wo)
	{
		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			final String toEmail = System.getProperty("DowngradeLoop_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Interface Error encountered in delink of Shop WO "+wo );
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Downgrade Loop interface " 
					+" has encountered an issue. "			
					+ "Issues found at:\n"  
					+errors
					);
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
