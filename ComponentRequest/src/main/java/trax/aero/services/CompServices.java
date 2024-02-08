package trax.aero.services;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import trax.aero.data.MaterialData;
import trax.aero.interfaces.IMaterialData;

import trax.aero.logger.LogManager;
import trax.aero.outbound.DTTRAXI41ACK4067;
import trax.aero.util.EmailSender;




@Path("/CompServices")
public class CompServices {

	Logger logger = LogManager.getLogger("ComponentRequest_I41");

	@EJB IMaterialData md;
	
	@POST
	@Path("/recieveResponse")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response recieveResponse(DTTRAXI41ACK4067 data)
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(DTTRAXI41ACK4067.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(data, sw);
			
			logger.info("Input: " + sw.toString());
			
			md.acceptReq(data);
			
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			e.printStackTrace();
		}
		logger.info("finishing");
		   
		
		return Response.ok("Ok",MediaType.APPLICATION_XML + ";charset=UTF-8").build();
		
	}
	
	@GET
	@Path("/setSite")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSite(@QueryParam("site") String site, @QueryParam("recipient") String recipient )
	{
		
		String exceuted = "OK";
		                              
		try 
        {    		 
        	exceuted = md.setSite(site,recipient);
		}
		catch(Exception e)
		{
			exceuted = e.toString();
			EmailSender emailer = new EmailSender(System.getProperty("CM_toEmail"));
			emailer.sendEmail(exceuted);
			
		}
       finally 
       {   
    	  
			
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/deleteSite")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteSite(@QueryParam("site") String site )
	{
		
		String exceuted = "OK";
		                              
		try 
        {    		 
			md.deleteSite(site);
		}
		catch(Exception e)
		{
			exceuted = e.toString();
			logger.severe(e.toString());
			EmailSender emailer = new EmailSender(System.getProperty("CM_toEmail"));
			emailer.sendEmail(exceuted);
		
		}
       finally 
       {   
    	   
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/getSite")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSite(@QueryParam("site") String site )
	{
		
		String exceuted = "OK";
		
		String group = null;
		                              
		try 
        {    		 
			group = md.getSite(site);
        	if(group == null ) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			EmailSender emailer = new EmailSender(System.getProperty("CM_toEmail"));
			emailer.sendEmail(e.toString());
		
		}
       finally 
       {   
    	   
    	   logger.info("finishing");
       }
		
		
	   return Response.ok(group,MediaType.APPLICATION_JSON).build();
	}
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_XML )
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_XML).build();
    }


}