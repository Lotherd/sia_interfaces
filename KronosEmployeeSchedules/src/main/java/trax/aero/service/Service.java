package trax.aero.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import trax.aero.controller.KronosController;

import trax.aero.dao.WorkScheduleDao;
import trax.aero.logger.LogManager;
import trax.aero.dao.KronosData;
import trax.aero.dao.LocationSiteCapacityDao;







@Path("/KronosService")
public class Service {
	
	Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	
	@GET
	@Path("/setGroup")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setGroup(@QueryParam("costCenter") String costCenter, @QueryParam("group") String group )
	{
		KronosData data = new KronosData();
		
		String exceuted = "OK";
		                              
		try 
        {    		 
        	exceuted = data.setGroup(costCenter,group);
		}
		catch(Exception e)
		{
			exceuted = e.toString();
			KronosController.addError(e.toString());
			KronosController.sendEmailService(exceuted);
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
	
	@GET
	@Path("/deleteGroup")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteGroup(@QueryParam("group") String group )
	{
		KronosData data = new KronosData();
		
		String exceuted = "OK";
		                              
		try 
        {    		 
        	data.deleteGroup(group);
		}
		catch(Exception e)
		{
			exceuted = e.toString();
			logger.severe(e.toString());
			KronosController.addError(e.toString());
			KronosController.sendEmailService(exceuted);
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
	
	@GET
	@Path("/getGroup")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroup(@QueryParam("costCenter") String costCenter )
	{
		KronosData data = new KronosData();
		
		String exceuted = "OK";
		
		String group = null;
		                              
		try 
        {    		 
			group = data.getGroupByCostCenter(costCenter);
        	if(group == null ) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			KronosController.addError(e.toString());
			KronosController.sendEmailService(exceuted);
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
		
		
	   return Response.ok(group,MediaType.APPLICATION_JSON).build();
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