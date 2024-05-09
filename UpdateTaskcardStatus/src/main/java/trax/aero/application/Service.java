package trax.aero.application;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.controller.UpdateTaskcardStatusController;

import trax.aero.data.UpdateTaskcardStatusData;
import trax.aero.logger.LogManager;

import trax.aero.pojo.I74_Request;
import trax.aero.pojo.I74_Response;





@Path("/UpdateTaskCardStatusService")
public class Service {
	
	Logger logger = LogManager.getLogger("UpdateTaskCardStatus_I74");
	
	@POST
	@Path("/markTransaction")
	@Consumes(MediaType.APPLICATION_XML + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_XML + ";charset=utf-8")
	public Response markTransaction(I74_Response input)
	{
		String exceuted = "OK";
		
		UpdateTaskcardStatusData data = new UpdateTaskcardStatusData("mark");
		
		try 
        {    
			
			JAXBContext jc = JAXBContext.newInstance(I74_Response.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(input, sw);
			
			logger.info("Input: " + sw.toString());
			
			 
			
			if(input.getRemarks() != null && !input.getRemarks().isEmpty()) {
				logger.severe("Received Response with Remarks: " + input.getRemarks() +", Order Number: "+input.getOrderNumber());
				UpdateTaskcardStatusController.addError("Received Response with Remarks: " + input.getRemarks() +", Order Number: "+input.getOrderNumber());
				exceuted = "Issue found";
			}else {
				exceuted = data.markTransaction(input);
			}
        	if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			Map<String,String> map = data.getStatus(input.getOrderNumber());
			Map<String,String> mapwtc = data.getWoTaskCard(input.getOrderNumber());
			UpdateTaskcardStatusController.addError(e.toString());
			UpdateTaskcardStatusController.sendEmailResponse(input, map.get("STATUS"),map.get("STATUS_CATEGORY")
			,mapwtc.get("WO"),mapwtc.get("TASK_CARD")	);
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
			e.printStackTrace();
		}
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_XML + ";charset=utf-8").build();
	}
	
	@POST
	@Path("/testPost")  
	@Consumes(MediaType.APPLICATION_XML+ ";charset=utf-8")
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response testPost(I74_Request input)
	{
		logger.info("got something" + input.getOrderNumber());
		return Response.ok().build();
		
	}
	
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_XML + ";charset=utf-8")
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_JSON).build();
    }
	
}