package trax.aero.application;



import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.controller.DowngradeLoopController;

import trax.aero.data.DowngradeLoopData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I94_4084_RES;
import trax.aero.pojo.MT_TRAX_SND_I94_4084_REQ;



@Path("/DowngradeLoopService")
public class Service {
		
	Logger logger = LogManager.getLogger("DowngradeLoop_I94");
	
	@GET
	@Path("/getLoops")
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response getLoops()
	{
		
		DowngradeLoopData data = new DowngradeLoopData();
		String exceuted = "OK";
		ArrayList<MT_TRAX_SND_I94_4084_REQ> request = new ArrayList<MT_TRAX_SND_I94_4084_REQ>();
		try 
        {        	 
			request = data.getLoops();
			if(!DowngradeLoopController.getError().isEmpty()) {
				 throw new Exception("Issue found");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			DowngradeLoopController.addError(e.toString());
			DowngradeLoopController.sendEmail(exceuted);
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
		GenericEntity<ArrayList<MT_TRAX_SND_I94_4084_REQ>> entity = new GenericEntity<ArrayList<MT_TRAX_SND_I94_4084_REQ>>(request) {
		};
	   return Response.ok(entity,MediaType.APPLICATION_XML + ";charset=UTF-8" ).build();
		
		
	}
	
	@POST
	@Path("/markTransaction")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8")
	public Response markTransaction(MT_TRAX_RCV_I94_4084_RES response)
	{
		DowngradeLoopData data = new DowngradeLoopData("mark");
		String exceuted = "OK";
		
		try 
        {
			
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_RCV_I94_4084_RES.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(response, sw);
			
			logger.info("Input: " + sw.toString());
			
			if(response.getErrorCode().equalsIgnoreCase("53")) {
				data.markTransaction(response);
			}else {
				
				exceuted = ("SVO: " + response.getSAPOrderNumber() + ", RFO: " + response.getSAPServiceOrderNumber() 
				+ ", Date: " + new Date().toString()  + ", SHOP WO: " +data.getShopWo(response.getSAPServiceOrderNumber()) +
				", Production WO: " + data.getProdWo(response.getSAPServiceOrderNumber()) );
				
				logger.severe(exceuted);
				DowngradeLoopController.addError(exceuted);
				
				exceuted = ("Received acknowledgement with Error Code: " + response.getErrorCode() 
				+", Status Message: "+response.getRemarks()) ;
				
				logger.severe(exceuted);
				DowngradeLoopController.addError(exceuted);
				
				exceuted = "Issue found";
				DowngradeLoopController.sendEmailACK(data.getShopWo(response.getSAPServiceOrderNumber()));
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