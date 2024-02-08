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

public class TaskCardController {

	
	static String errors = "";
	
	static Logger logger = LogManager.getLogger("TaskCard_I18");
	
	public TaskCardController()
	{
		
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static void sendEmail(File file)
	{
		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("TaskCard_toEmail");
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			//email.setAuthentication("apikey", "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");
			email.setSubject("TaskCard Interface did not receive an XML correctly.");
			for(String emails: emailsList)
	        {
	        	email.addTo(emails);
	        }
			email.setMsg("XML File " 
					+ file.getName()  
					+" has encountered an issue. "			
					+ "Enter records manually. "
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
	
	
	public static void sendEmailSent(ArrayList<String> tasks)
	{
		try
		{
			String taskslist = "";
			
			
			for(String t : tasks){
				taskslist = taskslist + t +"," +System.lineSeparator() + System.lineSeparator();
			}
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("TaskCard_toEmail");
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Task Card Interface : New Task Cards created successfully in TRAX");
			for(String emails: emailsList)
	        {
	        	email.addTo(emails);
	        }
			email.setMsg(
					"Taskcard Count: " + tasks.size() +System.lineSeparator()+ 
					"Taskcards: \n" 
					+ taskslist);
			email.send();
		}
		catch(Exception e)
		{
			logger.info(e.toString());
			logger.info("Email not found");
			
		}
	}
}
