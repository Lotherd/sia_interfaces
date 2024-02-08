package trax.aero.utils;

import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;

import trax.aero.data.ACConfigurationData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Equipment;
import trax.aero.pojo.Equipment;

public class Worker implements Runnable {

	ACConfigurationData data = null;
	Logger logger = LogManager.getLogger("ACConfiguration_I51");
	
	private Equipment input =null;
	
	private String top = "";
	
	public Worker(EntityManagerFactory factory) {
		 data = new ACConfigurationData(factory);
	}
	
	public void run() 
	{
		
		
			try {
				boolean ouput = data.insertACData(input, top);
	    	if(!ouput) {
	    		ProcessRun.exceuted = "Issue";
	    	}
	    	if(data.sentToDatabase) {
	    		ProcessRun.sentToDatabase.add(input);
	    	}
		}
		catch(Exception e)
		{	
			
			e.printStackTrace();
			logger.severe(e.toString());
		}finally {
			if(data.em != null && data.em .isOpen())
				data.em .close();
		}
	}

	public Equipment getInput() {
		return input;
	}

	public void setInput(Equipment input) {
		this.input = input;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}

	
	
	
}
