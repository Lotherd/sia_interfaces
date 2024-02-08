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

import trax.aero.pojo.STAFF;
import trax.aero.pojo.STAFFRequest;


public class ImportCertificationController {
	EntityManagerFactory factory;
	private EntityManager em;
	static String errors = "";
	
	static Logger logger = LogManager.getLogger("ImportCertification_I30_1");
	
	public ImportCertificationController()
	{
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();
	}
	
	public static void addError(String error) {
		errors=errors.concat(error + System.lineSeparator()+ System.lineSeparator());
	}
	
	public static void sendEmail(STAFF input)
	{
		try
		{
			String staff = "";
			for(STAFFRequest i : input.getSTAFF()) {
				staff = staff +" Staff number: "+ i.getStaffNumber()+",\n";
			}
			
			
			String fromEmail = System.getProperty("fromEmail");
			String host = System.getProperty("fromHost");
			String port = System.getProperty("fromPort");
		
			final String toEmail = System.getProperty("ImportCertification_toEmail");
			
			ArrayList<String>  emailsList = new ArrayList<String> (Arrays.asList(toEmail.split(",")));
			
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setFrom(fromEmail);
			email.setSubject("Interface Error encountered during insert/update on staff eSign & stamp");
			
			for(String emails: emailsList)
	        {
				email.addTo(emails);
	        }
			
			
			email.setMsg("JSON with the following Staff: \n" 
					+ staff
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
