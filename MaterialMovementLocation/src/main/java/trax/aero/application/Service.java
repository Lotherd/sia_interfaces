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

import trax.aero.controller.MaterialLocationController;

import trax.aero.data.MaterialLocationData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_SND_I95_4081_REQ;
import trax.aero.pojo.MT_TRAX_RCV_I95_4081_RES;



@Path("/MaterialLocationService")
public class Service {
		
	Logger logger = LogManager.getLogger("MaterialLocation_I95");
	
	@GET
	@Path("/getMaterials")
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response getJobConfirmation()
	{
		
		MaterialLocationData data = new MaterialLocationData();
		String exceuted = "OK";
		ArrayList<MT_TRAX_SND_I95_4081_REQ> request = new ArrayList<MT_TRAX_SND_I95_4081_REQ>();
		try 
        {        	 
			request = data.getMaterials();
			if(!MaterialLocationController.getError().isEmpty()) {
				 throw new Exception("Issue found");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			MaterialLocationController.addError(e.toString());
			MaterialLocationController.sendEmail(exceuted);
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
		GenericEntity<ArrayList<MT_TRAX_SND_I95_4081_REQ>> entity = new GenericEntity<ArrayList<MT_TRAX_SND_I95_4081_REQ>>(request) {
		};
	   return Response.ok(entity,MediaType.APPLICATION_XML + ";charset=UTF-8" ).build();
		
		
	}
	
	@POST
	@Path("/markTransaction")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
	public Response markTransaction(MT_TRAX_RCV_I95_4081_RES response)
	{
		String exceuted = "OK";
		MaterialLocationData data = new MaterialLocationData("mark");
		try 
        {   
			
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_RCV_I95_4081_RES.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(response, sw);
			
			logger.info("Input: " + sw.toString());
			
			if(response.getSuccessErrorLog().getStatusErrorCode().equalsIgnoreCase("53")) {
				data.markTransaction(response);
			}else {
				exceuted = "Issue Found";
				
				
				String wo =  data.getWO(response.getOrder().getOrderNumber());
				String rfo =  data.getRFO(wo);
				
				logger.severe("Received acknowledgement with Error Code: " + response.getSuccessErrorLog().getStatusErrorCode() +", Status Message: "+response.getSuccessErrorLog().getStatusMessage() + ", WO: " +wo + ", RFO: " + rfo);
				data.markTransaction(response);
				MaterialLocationController.sendEmailACK("Received acknowledgement with Error Code: " + response.getSuccessErrorLog().getStatusErrorCode() +", Status Message: "+response.getSuccessErrorLog().getStatusMessage() + ", WO: " +wo + ", RFO: " + rfo,wo) ;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
    @Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_XML + ";charset=UTF-8").build();
    }
	
}