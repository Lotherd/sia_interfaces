package trax.aero.application;




import java.io.StringWriter;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

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

import trax.aero.controller.ACConfigurationController;
import trax.aero.data.ACConfigurationData;

import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I51_ACK_4073;

import trax.aero.utils.ProcessRun;
import trax.aero.utils.Run;




@Path("/ACConfigurationService")
public class Service {
	
	Logger logger = LogManager.getLogger("ACConfiguration_I51");
	private ScheduledExecutorService scheduledServ;
	ProcessRun process = null;
	
	@POST
	@Path("/receiveData")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response getXml(MT_TRAX_RCV_I51_ACK_4073 input)
	{
		
		try 
        {    
			
						
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_RCV_I51_ACK_4073.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(input, sw);
						 
			EntityManagerFactory factory = Persistence.createEntityManagerFactory("ACConfigDS");
			ACConfigurationData data = new ACConfigurationData(factory); 	 
			
			if(data.lockAvailable("I51_REST"))
			{
				logger.info("Input: " + sw.toString());
				data.lockTable("I51_REST");
				process = new ProcessRun();
				process.setInput(input);
				
				if (scheduledServ == null) {
					int scheduledPoolSize = 1;
					logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
					this.scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
				}
				scheduledServ.execute(process);
				
				data.unlockTable("I51_REST");
			}		
			
    	   logger.info("finishing");
       }catch(Exception e){
    	   e.printStackTrace();
    	   logger.severe(e.toString());
       }
	   return Response.accepted().build();
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