package trax.aero.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.persistence.Persistence;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import trax.aero.logger.LogManager;

public class KronosController {

	static String errors = "";
	static Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	public KronosController()
	{
		
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static String getError() {
		return errors;
	}
	
	public static void sendEmail(String file)
	{
		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("Kronos_toEmail");
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Kronos Interface did not receive an file correctly.");
			for(String emails: emailsList)
	        {
	        	email.addTo(emails);
	        }
			email.setMsg("File " 
					+ file  
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
	
	public static void sendEmailService(String outcome)
	{
		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("Kronos_toEmail");
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Kronos Interface did not receive an file correctly.");
			for(String emails: emailsList)
	        {
	        	email.addTo(emails);
	        }
			email.setMsg("Inpunt" 
					 
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
