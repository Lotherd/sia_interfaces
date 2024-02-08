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
import trax.aero.pojo.MT_TRAX_RCV_I87_RES;


public class TaskCardsIngestionController {
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	
	static String fromEmail = System.getProperty("fromEmail");
	static String host = System.getProperty("fromHost");
	static String port = System.getProperty("fromPort");
	static String toEmail = System.getProperty("TaskCardsIngestion_toEmail");
	static Logger logger = LogManager.getLogger("TaskCardsIngestion_I87");
	
	
	public TaskCardsIngestionController()
	{
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static void sendEmailFile(File file)
	{
		try
		{
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Task Cards Ingestion Interface did not receive an CSV correctly.");
			for(String emails: emailsList)
	        {
	        	email.addTo(emails);
	        }
			email.setMsg("CSV File " 
					+ file.getName()  
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
	
	
	public static void sendEmailOutFile(ArrayList<MT_TRAX_RCV_I87_RES> taskCardsOut)
	{
		try
		{
			String requests = "";
			
			for(MT_TRAX_RCV_I87_RES t : taskCardsOut) {
				requests = requests + " ( Task Card: "  + t.getTRAXTCNumber() +", WO: " +t.getTRAXWO() +"),\n";
			}
			
			
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Task Cards Ingestion Interface did create an CSV correctly.");
			for(String emails: emailsList)
	        {
	        	email.addTo(emails);
	        }
			email.setMsg("requests: " +  requests
					+"\n Encountered an issue. "			
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
