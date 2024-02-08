package trax.aero.application;


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

import trax.aero.controller.ImportClockOnOffController;

import trax.aero.data.ImportClockOnOffData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Import;







@Path("/ImportClockOnOffService")
public class Service {
	
	Logger logger = LogManager.getLogger("ImportClockOnOff_I29");
	
	
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() 
    {    	
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_JSON).build();
    }
	
}