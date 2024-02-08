package trax.aero.application;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
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





import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.controller.ACController;
import trax.aero.data.ACData;
import trax.aero.interfaces.IACData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.ACMaster;



@Path("/ACFunctionService")
public class Service {
	
	Logger logger = LogManager.getLogger("ACFunction_I72");
	
	@EJB IACData data;
	
	
	@POST
	@Path("/insertXml")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response insertXml(ACMaster input)
	{
		String exceuted = "OK";
		try 
        {    
			JAXBContext jc = JAXBContext.newInstance(ACMaster.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(input, sw);
			
			
			
			
			
				logger.info("Input: " + sw.toString());
				exceuted = data.insertACMaster(input);
				    	
        	
        	if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			ACController.addError(e.toString());
			ACController.sendEmail(input.getAircraftTailNumber());
		}
       finally 
       {   
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_JSON )
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_JSON).build();
    }
	
}