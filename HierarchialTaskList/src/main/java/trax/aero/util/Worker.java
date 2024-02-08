package trax.aero.util;

import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;

import trax.aero.data.EcData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Ec;

public class Worker implements Runnable {

	EcData ecData = null;
	Logger logger = LogManager.getLogger("EC_I19");
	public Worker(EntityManagerFactory factory) {
		ecData = new EcData(factory);
	}
	
	private Ec input =null;
	
	private boolean success = false;
	
	public void run() 
	{
		setSuccess(false);
		
		try {
			success = false;
			
			success = ecData.saveEcData(input);
			if(!success) {
				TimerExecutor.flag = false;
			}
			if(ecData.error.length() > 0)
			{
				TimerExecutor.error = TimerExecutor.error.concat(ecData.error);
			}
		}	
		catch(Exception e)
		{	
			e.printStackTrace();
			logger.severe(e.toString());
		}finally {
			if(ecData.em != null && ecData.em .isOpen())
				ecData.em .close();
		}
	}

	public Ec getInput() {
		return input;
	}

	public void setInput(Ec inputs) {
		this.input = inputs;
	}
	
	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean exceuted) {
		this.success = exceuted;
	}

}
