package trax.aero.utils;

import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;

import trax.aero.data.TaskCardData;
import trax.aero.logger.LogManager;

import trax.aero.pojo.TaskCards;

public class Worker implements Runnable {

	TaskCardData data = null;
	Logger logger = LogManager.getLogger("TaskCard_I18");
	public Worker(EntityManagerFactory factory) {
		data = new TaskCardData(factory);
	}
	
	private TaskCards input =null;
	
	private String exceuted = "";
	
	public void run() 
	{
		setExceuted("OK");
		
			try {
				String ouput = data.insertTaskCard(input);
	    	if(ouput == null || !ouput.equalsIgnoreCase("OK")) {
	    		RunAble.taskCardsArrayFailure.add(input); 
	    	}
	    	if(data.newExist) {
	    		RunAble.tasklist.add(data.getTaskList());
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

	public TaskCards getInput() {
		return input;
	}

	public void setInput(TaskCards inputs) {
		this.input = inputs;
	}
	
	public String getExceuted() {
		return exceuted;
	}

	public void setExceuted(String exceuted) {
		this.exceuted = exceuted;
	}

}
