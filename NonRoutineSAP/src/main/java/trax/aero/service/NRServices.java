package trax.aero.service;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import trax.aero.data.NRData;
import trax.aero.interfaces.INRData;

import trax.aero.logger.LogManager;
import trax.aero.pojo.DTTRAXI37I384068;
import trax.aero.pojo.DTTRAXI37I38ACK4069;
import trax.aero.pojo.OrderHeader;

@Path("/NRServices")
public class NRServices {
	
	Logger logger = LogManager.getLogger("NR_I37I38");
	
	@EJB INRData nrd;
	
	@POST
	@Path("/testPost")  
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response testPost()
	{
		return Response.ok().build();
		
	}
	
	
	@POST
	@Path("/recieveResponse")
	@Consumes(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	@Produces(MediaType.APPLICATION_XML + ";charset=UTF-8" )
	public Response recieveResponse(DTTRAXI37I38ACK4069 data)
	{
		
		try
		{
			JAXBContext jc = JAXBContext.newInstance(DTTRAXI37I38ACK4069.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(data, sw);
			
			logger.info("Input: " + sw.toString());
			
			
			nrd.closeNr(data);				
			
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			e.printStackTrace();
		}finally {
			
		}
		logger.info("finishing");
    
		return Response.ok("OK",MediaType.APPLICATION_XML + ";charset=UTF-8").build();
		
	}
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_XML )
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_XML).build();
    }
	

}
