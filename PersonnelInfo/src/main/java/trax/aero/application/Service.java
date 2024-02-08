package trax.aero.application;



import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import trax.aero.controller.PersonalInfoController;
import trax.aero.data.PersonalInfoData;
import trax.aero.logger.LogManager;



@Path("/PersonalInfoService")
public class Service {
	
	EntityManagerFactory factory;
	Logger logger = LogManager.getLogger("PersonalInfo_I28");
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_XML).build();
    }
	
	@GET
	@Path("/setSkill")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSkill(@QueryParam("costCenter") String costCenter, @QueryParam("jobRole") String jobRole,@QueryParam("skill") String skill )
	{
		
		
		String exceuted = "OK";
		logger.info("Input: costCenter: " +costCenter+" jobRole: "+jobRole+" skill: "+skill);                          
		try 
        {    		 
			factory = Persistence.createEntityManagerFactory("ImportDS");
			PersonalInfoData data = new PersonalInfoData(factory);
        	exceuted = data.setSkill(costCenter,jobRole,skill);
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			exceuted = e.toString();
			PersonalInfoController.addError(e.toString());
			PersonalInfoController.sendEmailService(exceuted);
		}
       finally 
       {   
    	   
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/deleteSkill")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteSkill(@QueryParam("costCenter") String costCenter, @QueryParam("jobRole") String jobRole,@QueryParam("skill") String skill  )
	{
		
		logger.info("Input: costCenter: " +costCenter+" jobRole: "+jobRole+" skill: "+skill); 
		String exceuted = "OK";
		                              
		try 
        {    	
			factory = Persistence.createEntityManagerFactory("ImportDS");
			PersonalInfoData data = new PersonalInfoData(factory);
        	data.deleteSkill(costCenter,jobRole,skill);
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			exceuted = e.toString();
			PersonalInfoController.addError(e.toString());
			PersonalInfoController.sendEmailService(exceuted);
		}
       finally 
       {   
    	  
    	   logger.info("finishing");
       }
	   return Response.ok(exceuted,MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/getSkill")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSkill(@QueryParam("jobRole") String jobRole,@QueryParam("costCenter") String costCenter )
	{
		
		logger.info("Input: costCenter: " +costCenter+" jobRole: "+jobRole); 
		String exceuted = "OK";
		
		String group = null;
		                              
		try 
        {   
			factory = Persistence.createEntityManagerFactory("ImportDS");
			PersonalInfoData data = new PersonalInfoData(factory);
			group = data.getSkill(jobRole,costCenter);
        	if(group == null ) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			PersonalInfoController.addError(e.toString());
			PersonalInfoController.sendEmailService(exceuted);
		}
       finally 
       {   
    	   
    	   logger.info("finishing");
       }
		
		
	   return Response.ok(group,MediaType.APPLICATION_JSON).build();
	}
	
}