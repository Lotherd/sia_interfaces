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
import trax.aero.pojo.Application_Log;
import trax.aero.pojo.IE4N;
import trax.aero.pojo.MT_TRAX_RCV_I43_4076_RES;



public class IE4NController {

	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	static Logger logger = LogManager.getLogger("IE4N_I43");
	
	public IE4NController()
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
	
	public static void sendEmailInbound(Application_Log log)
	{
		try
		{
			
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
		
			final String toEmail = System.getProperty("IE4N_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);


			email.setSubject("IE4N interface did not insert XML correctly.");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("IE4N interface " 
					+"has encountered an issue. "
					+ "Issues found at:"+System.lineSeparator()
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
		
		public static void sendEmailButton(IE4N message)
		{
			try
			{
				
				
				String fromEmail = System.getProperty("fromEmail");
				String host = System.getProperty("fromHost");
				String port = System.getProperty("fromPort");
			
				final String toEmail = System.getProperty("IE4N_toEmail");
				
				ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
				
				Email email = new SimpleEmail();
				email.setHostName(host);
				email.setSmtpPort(Integer.valueOf(port));
				email.setFrom(fromEmail);

				email.setSubject("IE4N interface did not send XML correctly.");
				for(String emails: emailsList)
		        {
					email.addTo(emails);
		        }
				email.setMsg("IE4N interface " 
						+"has encountered an issue. "
						+ "Issues found at:" +System.lineSeparator()  
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
