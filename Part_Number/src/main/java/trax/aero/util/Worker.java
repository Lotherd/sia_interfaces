package trax.aero.util;

import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;

import trax.aero.data.PnMasterData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.PartNumber;

public class Worker implements Runnable {

	PnMasterData data = null;
	Logger logger = LogManager.getLogger("PartMaster_I16I50");
	public Worker(EntityManagerFactory factory) {
		data = new PnMasterData(factory);
	}
	
	private PartNumber input =null;
	
	private Boolean exceuted = true;
	
	public void run() 
	{
		setExceuted(true);
		
		try {
			
			if(!data.savePnMaster(input))
			{
				TimerExecutor.failedList.add(input);
				logger.severe("Failure on part " + input.getPn());
				
			}
			if(data.error.length() > 0)
			{
				TimerExecutor.error = TimerExecutor.error.concat(data.error);
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

	public PartNumber getInput() {
		return input;
	}

	public void setInput(PartNumber inputs) {
		this.input = inputs;
	}
	
	public Boolean getExceuted() {
		return exceuted;
	}

	public void setExceuted(Boolean exceuted) {
		this.exceuted = exceuted;
	}

}
