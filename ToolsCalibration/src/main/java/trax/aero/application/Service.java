package trax.aero.application;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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

import trax.aero.controller.ToolsCalibrationController;
import trax.aero.data.ToolsCalibrationData;
import trax.aero.interfaces.IToolsCalibrationData;
import trax.aero.logger.LogManager;

import trax.aero.pojo.ToolsCalibrationMaster;



@Path("/ToolsCalibrationService")
public class Service {
	
	Logger logger = LogManager.getLogger("ToolsCalibration_I75");
	
	
	@EJB
	IToolsCalibrationData data;
	
	
	@POST
	@Path("/updateToolCalibration")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response updateToolCalibration(ToolsCalibrationMaster input)
	{
		String exceuted = "OK";
		
		
		
		try 
        {    
			JAXBContext jc = JAXBContext.newInstance(ToolsCalibrationMaster.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(input, sw);
			
			//if(data.lockAvailable("I75"))
			//{
				logger.info("Input: " + sw.toString());
				//data.lockTable("I75");
				exceuted = data.updateTool(input);
			//	data.unlockTable("I75");
			//} 
        	
        	if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
			ToolsCalibrationController.addError(e.toString());
			ToolsCalibrationController.sendEmail(input.getPartNumber());
		}
       finally 
       {   
    	   logger.info("finishing");
    	   try {
				
				
				
			} catch (Exception e) {
				
			}
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