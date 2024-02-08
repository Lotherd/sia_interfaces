package trax.aero.application;


import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import trax.aero.controller.CarryForwardController;
import trax.aero.interfaces.ICarryForwardData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.CarryForward;
import trax.aero.pojo.MT_TRAX_RCV_I74_4070_RES;
import trax.aero.pojo.MultipartBody;

@Path("/CarryForwardService")
public class Service {
	
	Logger logger = LogManager.getLogger("CarryForward_I80");
	
	@EJB ICarryForwardData data;
	
	@POST
	@Path("/CarryForwardButton")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response CarryForwardButton(CarryForward b)
	{
		String fianl = "{\n\"status\": \"OK\", \n\"statusCode\": \"200\"\n}";
		String exceuted = "OK";
		
		try 
        {   
        	exceuted = data.Button(b);
        	if(exceuted == null || !exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}else {
        		exceuted = fianl;
        	}
		}
		catch(Exception e)
		{
			CarryForwardController.addError(e.toString());
			CarryForwardController.sendEmail();
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
			}
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("/printFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response  CarryForwardPrint(
			@MultipartForm MultipartBody body)
	{
		String fianl = "{\n\"status\": \"OK\", \n\"statusCode\": \"200\"\n}";
		String exceuted = "OK";
		try 
        {   
			
	           	logger.info("Input: " + body.json.toString());
	          
	           	
	        	exceuted = data.print(body.json.getWo(), body.json.getTask_card(), IOUtils.toByteArray(body.file),
	        			body.json.getForm_No(), body.json.getForm_Line());
	        	
	        
        	
        	
        	if(exceuted == null || !exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}else {
        		exceuted = fianl;
        	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			CarryForwardController.addError(e.toString());
			CarryForwardController.sendEmail();
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
			}
    	   logger.info("finishing");
       }
           	
           	
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	
	@POST
	@Path("/markTransaction")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
	public Response markTransaction(MT_TRAX_RCV_I74_4070_RES request)
	{
		String exceuted = "OK";
		try 
        {   
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_RCV_I74_4070_RES.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(request, sw);
			
			logger.info("Input: " + sw.toString());
			
			
			if(request.getErrorCode().equalsIgnoreCase("53")) {
				data.markTransaction(request);
				
			}else {
				data.markTransaction(request);
				CarryForwardController.addError("Received acknowledgement with Error Code: " + request.getErrorCode() +" ,Remarks: "+request.getRemarks() +", Order: "+request.getOrderNumber()  ) ;
				CarryForwardController.sendEmailInbound(request);
				exceuted = "Issue Found";
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
				e.printStackTrace();
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