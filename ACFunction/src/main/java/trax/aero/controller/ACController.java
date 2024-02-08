package trax.aero.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import trax.aero.logger.LogManager;

public class ACController {
	EntityManagerFactory factory;
	private EntityManager em;
	static Logger logger = LogManager.getLogger("ACFunction_I72");
	
	static String errors = "";
	public ACController()
	{
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static void sendEmail(String AC)
	{
		
		
		
		try
		{
			String message = "", es = "";
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("AC_Function_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("AC Function interface did not receive XML correctly.");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
				es = es + "Email: " + emails +", ";
	        }
			
			email.setMsg("XML with AC " 
					+ AC  
					+" has encountered an issue. "			
					+ "Enter records manually. "
					+ "Issues found at:\n"  
					+errors);
			
			message = "SENDING EMAIL TO " + es + " FROM: " + fromEmail +" PORT: " + port + " HOST: " + host;
			logger.info(message);
			email.send();
			logger.info("SUCCESS");
		}
		catch(Exception e)
		{
			
			logger.severe(e.getMessage());
			logger.severe(e.toString());
			logger.severe("Email not found");
			
		}
		finally
		{
			errors = "";
		}
	}
}
