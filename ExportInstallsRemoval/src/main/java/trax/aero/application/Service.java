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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import trax.aero.controller.IE4NController;
import trax.aero.data.IE4NData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Application_Log;
import trax.aero.pojo.IE4N;
import trax.aero.pojo.MT_TRAX_RCV_I43_4076_RES;
import trax.aero.utils.ErrorType;










@Path("/IE4NService")
public class Service {
	
	Logger logger = LogManager.getLogger("IE4N_I43");
	
	@POST
	@Path("/IE4NButton")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response IE4NButton(IE4N b)
	{
		IE4NData data = new IE4NData();
		String fianl = "{\n\"status\": \"OK\", \n\"statusCode\": \"200\"\n}";
		String exceuted = "OK";
		
		try 
        {   
			
			ObjectMapper Obj = new ObjectMapper();
			String json;
			try {
				json = Obj.writeValueAsString(b);
			} catch (JsonProcessingException e) {
				json = "JsonProcessingException";
			}
			
			if(b.getTrans() != null && !b.getTrans().isEmpty() &&
				(b.getTransaction() == null || b.getTransaction().isEmpty()) 
				) {
				b.setTransaction(b.getTrans());
				
							
			}
			
			if(b.getTrans_Item() != null && !b.getTrans_Item().isEmpty() && 
			  (b.getTransaction_Item() == null || b.getTransaction_Item().isEmpty())
							) {
						
				b.setTransaction_Item(b.getTrans_Item());
									
			}		
			
			//if(data.lockAvailable("I43"))
			{
				logger.info("Input: " + json);
				//data.lockTable("I43");
				exceuted = data.Button(b);
				//data.unlockTable("I43");
			}	
        	
        	if(exceuted == null || !exceuted.equalsIgnoreCase("OK")) {
        		//exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}else {
        		exceuted = fianl;
        	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
			ResponseBuilder R = Response.serverError().status(Response.Status.BAD_REQUEST);
		    R.entity((exceuted));
		    R.type(MediaType.TEXT_PLAIN_TYPE);
			IE4NController.addError(e.toString());
			IE4NController.sendEmailButton(b);
			 return R.build();
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
				exceuted = e.toString();
				ResponseBuilder R = Response.serverError().status(Response.Status.BAD_REQUEST);
			    R.entity((e.toString()));
			    R.type(MediaType.TEXT_PLAIN_TYPE);
			    return R.build();
			}
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	
	@POST
	@Path("/markTransaction")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
	public Response markTransaction(MT_TRAX_RCV_I43_4076_RES request)
	{
		String exceuted = "OK";
		IE4NData data = new IE4NData();
		try 
        {   
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_RCV_I43_4076_RES.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(request, sw);
			
			logger.info("Input: " + sw.toString());
			
			String message = "";
		    
		    for(Application_Log l: request.getApplication_Log()) {
			    if(l.getMessage_Type().equalsIgnoreCase("I")) {
			    	logger.info("SETTING MESSAGE TYPE: " +request.getApplication_Log().get(0).getMessage_Type() +" TO W");
			    	l.setMessage_Type("W");
			    }
			    message = message +l.getMessage_Type()+ "-" +l.getMessage_Text()+ "\n";	 
		    }
		    
		   
		    data.markTransaction(request);
		    
		   	
			if(data.containsType(request.getApplication_Log(), "E")){
				
				exceuted = "Received acknowledgement:\n" 
				+ message 
				+"Reference order: "+request.getHeader().getReference_Order() 
				+ " Transaction: " + request.getHeader().getTRAXTRANS() 
				+ " Transaction item: " + request.getHeader().getTRAXITEM();
				
				IE4NController.addError(exceuted ) ;
				IE4NController.sendEmailInbound(request.getApplication_Log().get(0));
				
				
				
			}else if(data.containsType(request.getApplication_Log(), "W")) {
				

				exceuted = "Received acknowledgement:\n" 
						+ message 
						+"Reference order: "+request.getHeader().getReference_Order() 
						+ " Transaction: " + request.getHeader().getTRAXTRANS() 
						+ " Transaction item: " + request.getHeader().getTRAXITEM();
						
						IE4NController.addError(exceuted ) ;
						IE4NController.sendEmailInbound(request.getApplication_Log().get(0));

			}
			
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			exceuted = 	e.toString();
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
		return Response.ok(exceuted,MediaType.APPLICATION_XML + ";charset=UTF-8").build();
	}
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_JSON).build();
    }
	
}