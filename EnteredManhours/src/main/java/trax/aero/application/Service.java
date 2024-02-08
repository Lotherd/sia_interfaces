package trax.aero.application;

import java.io.StringWriter;
import java.sql.SQLException;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.controller.EnteredManhoursController;
import trax.aero.data.EnteredManhoursData;
import trax.aero.logger.LogManager;

import trax.aero.pojo.MT_TRAX_RCV_I84_4071_RES;
import trax.aero.pojo.OperationsRES;
import trax.aero.pojo.OpsLineEmail;
import trax.aero.pojo.OrderRES;


@Path("/EnteredManhoursService")
public class Service {
	
	Logger logger = LogManager.getLogger("EnteredManhours_I84");
	
	@POST
	@Path("/markTransaction")
	@Consumes(MediaType.APPLICATION_XML + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_XML + ";charset=utf-8")
	public Response markTransaction(MT_TRAX_RCV_I84_4071_RES input)
	{
		String exceuted = "OK";
		EnteredManhoursData data = new EnteredManhoursData("mark");
		String staffId , name,email = "ERROR" ;
		try 
        {    
			
			JAXBContext jc = JAXBContext.newInstance(MT_TRAX_RCV_I84_4071_RES.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(input, sw);
			
			logger.info("Input: " + sw.toString());
			
        	exceuted = data.markTransaction(input);
        	if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";      		
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			EnteredManhoursController.addError(e.toString());
			for(OrderRES o : input.getOrder()) {
				if(o.getOperations() !=null && !o.getOperations().isEmpty()) {
					for(OperationsRES op : o.getOperations()) {
					
						OpsLineEmail opsLineEmail = data.getOpsLineStaffName(o.getWO(),o.getTaskCard(), op.getTRAXItemNumber());
		        		
						EnteredManhoursController.sendEmailOpsLine(op.getOperationNumber(), o, opsLineEmail);
					}
				}else {
					OpsLineEmail opsLineEmail = data.getOpsLineStaffName(o.getWO(),o.getTaskCard(),"1");
	        		
					EnteredManhoursController.sendEmailOpsLine(null, o, opsLineEmail);
				}
			}
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
	   return Response.ok(exceuted,MediaType.APPLICATION_XML + ";charset=utf-8").build();
	}
	
	
	@GET
	@Path("/setOpsLine")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setOpsLine(@QueryParam("opsLine") String opsLine, @QueryParam("email") String email )
	{
		EnteredManhoursData data = new EnteredManhoursData();
		
		String exceuted = "OK";
		                              
		try 
        {    		 
        	exceuted = data.setOpsLine(opsLine,email);
		}
		catch(Exception e)
		{
			exceuted = e.toString();
			EnteredManhoursController.addError(e.toString());
			EnteredManhoursController.sendEmailService(exceuted);
			logger.severe(exceuted);
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
	@Path("/deleteOpsLine")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteOpsLine(@QueryParam("opsLine") String opsLine )
	{
		EnteredManhoursData data = new EnteredManhoursData();
		
		String exceuted = "OK";
		                              
		try 
        {    		 
        	data.deleteOpsLine(opsLine);
		}
		catch(Exception e)
		{
			exceuted = e.toString();
			logger.severe(e.toString());
			EnteredManhoursController.addError(e.toString());
			EnteredManhoursController.sendEmailService(exceuted);
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
	@Path("/getEmail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmail(@QueryParam("opsLine") String opsLine )
	{
		EnteredManhoursData data = new EnteredManhoursData();
		
		String exceuted = "OK";
		
		String group = null;
		                              
		try 
        {    		 
			group = data.getemailByOpsLine(opsLine);
        	if(group == null ) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");
        	}
		}
		catch(Exception e)
		{
			EnteredManhoursController.addError(e.toString());
			EnteredManhoursController.sendEmailService(exceuted);
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
    @Produces(MediaType.APPLICATION_XML + ";charset=utf-8")
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_JSON).build();
    }
	
}