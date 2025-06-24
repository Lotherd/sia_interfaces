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
import trax.aero.pojo.Inbound;
import trax.aero.pojo.Job;
import trax.aero.pojo.MasterInbound;

public class JobConfirmationController {
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	static Logger logger = LogManager.getLogger("JobConfirmation_I39I40");

	
	public JobConfirmationController()
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
	
	public static void sendEmail(ArrayList<Job> jobs)
	{
		try
		{
			String out = "";
			
			for(Job j : jobs) {
				out = out + " WO:" +j.getWo()+ " Task Card: " +j.getTask_Card()+ " Task Card item number: " +j.getTask_Card_Item()+",\n";
			}
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			final String toEmail = System.getProperty("JobConfirmation_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Error encountered in Job Confirmation interface" );
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Job Confirmation interface " 
					+" has encountered an issue.\n "	
					+out
					+ " Issues found at:\n"  
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
	
	public static void sendEmailACK(String message)
	{
		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			final String toEmail = System.getProperty("JobConfirmation_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Error encountered in Job Confirmation interface" +" WO:" + " Task Card: " + " Task Card item number: ");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Job Confirmation interface " 
					+" has encountered an issue. "			
					+ "Issues found at:\n"  
					+message);
			email.send();
			
			logger.info("Email sent");
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
