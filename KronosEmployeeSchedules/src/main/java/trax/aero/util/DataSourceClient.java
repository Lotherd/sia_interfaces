package trax.aero.util;

import java.sql.Connection;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;


public class DataSourceClient 
{  	
	static Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	
	public static Connection getConnection() throws CustomizeHandledException 
	{
		Connection connection;	
		Context ctx;
		try 
		{
			ctx = new InitialContext();
		
			DataSource ds = null;//(DataSource)ctx.lookup("ManHoursScheduleTraxDS");
			
			if(System.getProperty("jboss.server.config.dir") != null)
				ds = (DataSource)ctx.lookup("java:/KronosDS");
			else
				ds = (DataSource)ctx.lookup("KronosDS");
			connection = ds.getConnection();
		} catch (Exception e) {
			logger.severe("An error ocurred trying connect to the DataSource: KronosDS");
			 throw new CustomizeHandledException("\nGetting error trying to connect to the datasource. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR);
		}
	    return connection;
	}
}
