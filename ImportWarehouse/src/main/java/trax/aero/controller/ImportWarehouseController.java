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
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_RES;

public class ImportWarehouseController {
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	static Logger logger = LogManager.getLogger("ImportWarehouse_I46");
	
	public ImportWarehouseController()
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
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("ImportWarehouse_toEmail");
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Error Encountered in Import Warehouse Quantity Batch Interface");
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
	
	public static void sendEmailRest(MT_TRAX_RCV_I46_4077_RES input)
	{
		

		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("ImportWarehouse_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Error Encountered in Import Warehouse Quantity Realtime Interface");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			
			email.setMsg("Issues found at:\n"  
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
	
	public static void sendEmailPOST()
	{
		

		try
		{
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			String toEmail = System.getProperty("ImportWarehouse_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Import Warehouse Interface interface did not POST XML correctly.");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			
			email.setMsg("Import Warehouse Interface has encountered an issue. "+ "Enter records manually. "
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
