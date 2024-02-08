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

public class ToolsCalibrationController {
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	static Logger logger = LogManager.getLogger("ToolsCalibration_I75");

	
	public ToolsCalibrationController()
	{
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static void sendEmail(String tool)
	{
		try
		{
			
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
		
			final String toEmail = System.getProperty("ToolsCalibration_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Tools Calibration interface did not receive XML correctly.");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("XML with Tool " 
					+ tool  
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
