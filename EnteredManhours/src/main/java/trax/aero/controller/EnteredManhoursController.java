package trax.aero.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I84_4071_RES;
import trax.aero.pojo.MT_TRAX_SND_I84_4071_REQ;
import trax.aero.pojo.OperationsRES;
import trax.aero.pojo.OpsLineEmail;
import trax.aero.pojo.OrderREQ;
import trax.aero.pojo.OrderRES;


public class EnteredManhoursController {
	
	static String errors = "";
	static Logger logger = LogManager.getLogger("EnteredManhours_I84");
	
	static String fromEmail = System.getProperty("fromEmail");
	static String host = System.getProperty("fromHost");
	static String port = System.getProperty("fromPort");
	static String toEmail = System.getProperty("EnteredManhours_toEmail");
	
	public EnteredManhoursController()
	{
		
	}
	
	public static void addError(String error) {
		logger.severe(error);
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static String getError() {
		return errors;
	}
	
	public static void sendEmailRequest(ArrayList<MT_TRAX_SND_I84_4071_REQ> arrayReq)
	{
		try
		{
			
			String requests = "";
			
			for(MT_TRAX_SND_I84_4071_REQ req : arrayReq) {
				for(OrderREQ r : req.getOrder()) {
					requests = requests + " ( Task Card: "  + r.getTaskCard() +", WO: " +r.getWO() +"),";
				}
			}
			
			
			
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("EnteredManhours Interface encountered a Error");
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
	
	public static void sendEmailResponse(MT_TRAX_RCV_I84_4071_RES response)
	{
		try
		{
			String responses = "";
			
			for(OrderRES r : response.getOrder()) {
				
				responses = responses + " SAPOrderNumber: "  + r.getSAPOrderNumber() +",";
			}
			
			
			
			
			
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("EnteredManhours Interface encountered a Error");
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			email.setMsg("Responses that failed: " +
					responses 
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
	
	
	
	public static void sendEmailOpsLine(String OperationNumber, OrderRES order, OpsLineEmail opsLineEmail )
	{
		try
		{
			String date = new Date().toString();
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			if(opsLineEmail.getFlag() != null && !opsLineEmail.getFlag().isEmpty() && ( opsLineEmail.getFlag().equalsIgnoreCase("Y") || opsLineEmail.getFlag().equalsIgnoreCase("I"))) {		
				email.setSubject("Failure to update Entered Man Hours WO: " + order.getWO() + " Task Card: " + order.getTaskCard());
				
				email.setMsg("TRAX WO Number: " + order.getWO() +
						 " ,TRAX Task Card Number: " + order.getTaskCard() + 
						 " ,Date & Time of Transaction: " + date +
						 " ,SVO Number: " + order.getSAPOrderNumber() +
						" ,Staff ID: " + opsLineEmail.getRelationCode() +
						 " ,Name: " + opsLineEmail.getName()
						+ " Issues found at:\n"  
						+errors);
				
			}else {
				email.setSubject("Failure to update Order Details WO: " + order.getWO() + " Task Card: " + order.getTaskCard());
				
				email.setMsg("TRAX WO Number: " + order.getWO() +
						 " ,TRAX Task Card Number: " +order.getTaskCard() + 
						 " ,Date & Time of Transaction: " + date +
						 " ,SVO Number: " + order.getSAPOrderNumber() + 
						 " ,Operation Numbers: " + OperationNumber +
						" ,Staff ID: " + opsLineEmail.getRelationCode() +
						 " ,Name: " + opsLineEmail.getName()
						+ " Issues found at:\n"  
						+ errors);
			}
			for(String emails: emailsList)
	        {
				if(opsLineEmail.getEmail() == null || opsLineEmail.getEmail().isEmpty() || opsLineEmail.getEmail().equalsIgnoreCase("ERROR")) {
					email.addTo(emails);
				}else {
					email.addTo(opsLineEmail.getEmail());
				}
	        }
			
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
			
	        ArrayList<String>  emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("EnteredManhours Interface encountered a Error");
			for(String emails: emailsList)
	        {
	        	email.addTo(emails);
	        }
			email.setMsg("Input" 
					 
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
