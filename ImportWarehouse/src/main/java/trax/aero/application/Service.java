package trax.aero.application;



import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.controller.ImportWarehouseController;
import trax.aero.data.ImportWarehouseData;
import trax.aero.interfaces.IImportWarehouseData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_RES;
import trax.aero.pojo.MT_TRAX_SND_I46_4077_REQ;
import trax.aero.pojo.MaterialDetails;




@Path("/ImportWarehouseService")
public class Service {
	
	Logger logger = LogManager.getLogger("ImportWarehouse_I46");
	
	@EJB
	IImportWarehouseData data;
	
	@POST
	@Path("/processReqest")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response getXml(MT_TRAX_RCV_I46_4077_RES input)
	{
		String exceuted = "OK";
		try 
        {   
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_RCV_I46_4077_RES.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(input, sw);
			
			
				
			//if(data.lockAvailable("I46_REST"))
			{
				logger.info("Input: " + sw.toString());
				//data.lockTable("I46_REST");
				for(MaterialDetails i :  input.getMaterialDetails()) {
					String output = "OK";
					output = data.ProcessReqest(i);
					if(!output.equalsIgnoreCase("OK")) {
						exceuted = "Issue found";
					}
				}
				//data.unlockTable("I46_REST");
			} 
			
			
        	if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			ImportWarehouseController.addError(e.toString());
			ImportWarehouseController.sendEmailRest(input);
		}
       finally 
       {   
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_XML).build();
	}
	
	@POST
	@Path("/invokeRequest")
	@Consumes(MediaType.APPLICATION_JSON )
	@Produces(MediaType.TEXT_PLAIN)
	public Response invokeRequest(MT_TRAX_SND_I46_4077_REQ input)
	{
		try 
        {   
			logger.info("MaterialNumber: " +input.getMaterialNumber() );
			data.invokeRequest(input); 			
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
		}
       finally 
       {   
    	   logger.info("finishing");
       }
	   return Response.ok().build();
	}
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_XML).build();
    }
	
}