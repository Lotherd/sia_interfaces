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
import trax.aero.pojo.MaterialMovementMaster;
import trax.aero.pojo.OpsLineEmail;


public class MaterialMovementController {
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	
	static Logger logger = LogManager.getLogger("MaterialMovement_I42&I44");
	
	public MaterialMovementController()
	{
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static void sendEmail(MaterialMovementMaster input, OpsLineEmail opsLineEmail)
	{
		try
		{
			
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
		
			final String toEmail = System.getProperty("MaterialMovement_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Material Movement interface did not receive XML correctly.");
			
			for(String emails: emailsList)
	        {
				if(opsLineEmail.getEmail() == null || opsLineEmail.getEmail().isEmpty() || opsLineEmail.getEmail().equalsIgnoreCase("ERROR")) {
					email.addTo(emails);
				}else {
					email.addTo(opsLineEmail.getEmail());
				}
	        }
			
			
			email.setMsg("XML with Order Number " 
					+ input.getOrderNumber()
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
