package trax.aero.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import trax.aero.logger.LogManager;

public class EmailSender 
{
	Logger logger = LogManager.getLogger("Techdoc_I20_I26");
	private String toEmail;
	
	public EmailSender(String email)
	{
		toEmail = email;
	}
	
	public void sendEmail(String error, String wo, String ac, boolean shop) 
	{

		try {
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			if(!shop) {
				ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
				for(String toEmails : emailsList)
				{
					email.addTo(toEmails);
				}
			}else {
				ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(System.getProperty("TECH_SHOP_toEmail").split(",")));
				for(String toEmails : emailsList)
				{
					email.addTo(toEmails);
				}
			}



			email.setSubject("ZPRINT Interface with WO#" +wo + " for A/C " + ac);
			
			email.setMsg(error);
			
			email.send();
		} 
		catch (EmailException e) 
		{
			logger.severe(e.toString());
		}

		
	}
	
	public void sendEmail(String error, boolean shop) 
	{

		try {
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			if(shop) {
				ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(System.getProperty("TECH_SHOP_toEmail").split(",")));
				for(String toEmails : emailsList)
				{
					email.addTo(toEmails);
				}
			}else {
				
				ArrayList<String> emailsList = new ArrayList<String>(Arrays.asList(toEmail.split(",")));
				for(String toEmails : emailsList)
				{
					email.addTo(toEmails);
				}
			}



			email.setSubject("Shop Task Card Selection- Missing Enhanced Data");
			String message = "Following task cards are missing enhanced data. Please take necessary steps." 
			+ System.lineSeparator() + error;
			email.setMsg(message);
			
			email.send();
		} 
		catch (EmailException e) 
		{
			logger.severe(e.toString());
		}

		
	}

}
