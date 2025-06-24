package trax.aero.application;


import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.controller.JobConfirmationController;
import trax.aero.data.JobConfirmationData;
import trax.aero.logger.LogManager;
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
        try {
            JAXBContext jc = JAXBContext.newInstance(MasterOutbound.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(request, sw);
            
            logger.info("Input: " + sw.toString());
            
            String status = request.getSuccess_errorLog().getIDOC_Status();
            String message = request.getSuccess_errorLog().getStatusMessage().toLowerCase();
            
           
           /* boolean condition = status.equalsIgnoreCase("51") 
                    && (message.contains("already being processed") 
                    || message.contains("is locked by"));*/
            
           
            if (status.equalsIgnoreCase("53")) {
                data.markTransaction(request);
                logger.info("SUCCESS: Transaction processed successfully");
            }
            
           /* else if (condition) {
                boolean canRetry = data.loopMarkTransaction(request);
                
                if (canRetry) {
                    logger.info("TEMPORARY ERROR: Transaction set for retry - " + message);
                } else {
                    
                    String emailMessage = "Transaction exceeded retry limit - IDOC Status: " + status +
                                         ", IDOC Number: " + request.getSuccess_errorLog().getIDOC_Number() +
                                         ", Status Error Code: " + request.getSuccess_errorLog().getStatus_ErrorCode() + 
                                         ", Status Message: " + request.getSuccess_errorLog().getStatusMessage();
                    
                    JobConfirmationController.sendEmailACK(emailMessage);
                    logger.warning("TEMPORARY ERROR: Max attempts reached, transaction marked as failed");
                }
            }*/
         
            else {
                data.unMarkTransaction(request);
                
                String emailMessage = "Received acknowledgement with IDOC Status: " + status +
                                     ", IDOC Number: " + request.getSuccess_errorLog().getIDOC_Number() +
                                     ", Status Error Code: " + request.getSuccess_errorLog().getStatus_ErrorCode() + 
                                     ", Status Message: " + request.getSuccess_errorLog().getStatusMessage();
                
                JobConfirmationController.sendEmailACK(emailMessage);
                logger.warning("PERMANENT ERROR: Transaction marked as failed (F)");
            }
        }
        catch(Exception e) {
            logger.severe(e.toString());
        }
        finally {
            try {
                if(data.getCon() != null && !data.getCon().isClosed())
                    data.getCon().close();
            }
            catch (SQLException e) {
                logger.severe(e.toString());
            }
            logger.info("finishing");
        }
        return Response.ok("OK", MediaType.APPLICATION_XML + ";charset=UTF-8").build();
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