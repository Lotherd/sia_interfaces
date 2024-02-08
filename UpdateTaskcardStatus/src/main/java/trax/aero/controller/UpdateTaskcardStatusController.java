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
import trax.aero.pojo.I74_Request;
import trax.aero.pojo.I74_Response;


public class UpdateTaskcardStatusController {
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	static Logger logger = LogManager.getLogger("UpdateTaskCardStatus_I74");
	
	public UpdateTaskcardStatusController()
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
	
	public static void sendEmailRequest(ArrayList<I74_Request> request)
	{
		try
		{
			
			String requests = "";
			
			for(I74_Request r : request) {
				
				requests = requests + " Order Number: "  + r.getOrderNumber() + " Status: "  + r.getTRAXStatus() + " Status Category: "  + r.getTRAXStatusCategory() +",";
			}
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			final String toEmail = System.getProperty("UpdateTaskCardStatus_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Interface error encountered in Task Card Status Update");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Requests that failed: " +
					requests
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
	
	public static void sendEmailResponse(I74_Response response, String status, String status_category)
	{
		try
		{
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			final String toEmail = System.getProperty("UpdateTaskCardStatus_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Interface error encountered in Task Card Status Update");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Order Number: " 
					+ response.getOrderNumber() + " Status: "  +status  + " Status Category: "  + status_category 
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
