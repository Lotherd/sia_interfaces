package trax.aero.application;




import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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

import trax.aero.controller.MaterialMovementController;
import trax.aero.data.MaterialMovementData;
import trax.aero.interfaces.IMaterialMovementData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MaterialMovementMaster;
import trax.aero.pojo.OpsLineEmail;
import trax.aero.utils.SharePointPoster;




@Path("/MaterialMovementService")
public class Service {
	
	Logger logger = LogManager.getLogger("MaterialMovement_I42&I44");
	
	@EJB IMaterialMovementData data;
	
	
	
	@POST
	@Path("/moveMaterial")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response moveMaterial(MaterialMovementMaster input)
	{
		String exceuted = "OK";
		try 
        {    
			 
			JAXBContext jc = JAXBContext.newInstance(MaterialMovementMaster.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(input, sw);
			
			
			
			//if(data.lockAvailable("I42I44"))
			//{
				logger.info("Input: " + sw.toString());
				//data.lockTable("I42I44");
				exceuted = data.updateMaterial(input);
				//data.unlockTable("I42I44");
			//}			
        	
        	if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			MaterialMovementController.addError(e.toString());
			
			OpsLineEmail opsLineEmail = data.getOpsLineStaffName(input);
	        		
			MaterialMovementController.sendEmail(input, opsLineEmail);
		}
       finally 
       {   
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
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