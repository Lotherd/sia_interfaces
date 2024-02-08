package trax.aero.application;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import trax.aero.controller.PlannedRFOController;
import trax.aero.data.PlannedRFOData;
import trax.aero.interfaces.IPlannedRFOData;
import trax.aero.logger.LogManager;

import trax.aero.pojo.MT_TRAX_I61;
import trax.aero.pojo.Orders;



@Path("/PlannedRFOService")
public class Service {
	
	Logger logger = LogManager.getLogger("PlannedRFO_I61");
	
	@EJB IPlannedRFOData data;
	
	@POST
	@Path("/insertXml")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response getXml(MT_TRAX_I61 o)
	{
		String exceuted = "OK";
		try 
        {    
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_I61.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(o, sw);
			
			
			//if(data.lockAvailable("I61"))
			//{
				logger.info("Input: " + sw.toString());
			//	data.lockTable("I61");
				 
        		exceuted = data.insertRFO(o);
        		
        	//	data.unlockTable("I61");
        	
		//	}
			
        	if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			PlannedRFOController.addError(e.toString());
			PlannedRFOController.sendEmail(o.getOrders().getRFONumber());
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