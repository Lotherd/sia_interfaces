package trax.aero.application;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

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

import trax.aero.controller.JobConfirmationController;


import trax.aero.data.JobConfirmationData;
import trax.aero.logger.LogManager;

import trax.aero.pojo.MasterInbound;
import trax.aero.pojo.MasterOutbound;



@Path("/JobConfirmationService")
public class Service {
		
	Logger logger = LogManager.getLogger("JobConfirmation_I39I40");
	
	
	
	@POST
	@Path("/markTransaction")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
	public Response markTransaction(MasterOutbound request)
	{
		JobConfirmationData data = new JobConfirmationData("mark");
		try 
        {   
			JAXBContext jc = JAXBContext.newInstance(MasterOutbound.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(request, sw);
			
			logger.info("Input: " + sw.toString());
			
			if(request.getSuccess_errorLog().getIDOC_Status().equalsIgnoreCase("53")) {
				data.markTransaction(request);
			}else {
				data.markTransaction(request);
				JobConfirmationController.sendEmailACK("Received acknowledgement with IDOC Status: " + request.getSuccess_errorLog().getIDOC_Status() +", IDOC Number: "+request.getSuccess_errorLog().getIDOC_Number()+", Status Error Code: "+request.getSuccess_errorLog().getStatus_ErrorCode() + ", Status Message: " + request.getSuccess_errorLog().getStatusMessage() ) ;
			}
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
		}
       finally 
       {
    	   try 
			{
				if(data.getCon() != null && !data.getCon().isClosed())
					data.getCon().close();
			} 
			catch (SQLException e) 
			{ 
				logger.severe(e.toString());
			}
    	   logger.info("finishing");
       }
		return Response.ok("OK",MediaType.APPLICATION_XML + ";charset=UTF-8").build();
	}
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
    public Response healthCheck() 
    {    
		
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_XML + ";charset=UTF-8").build();
    }
	
}