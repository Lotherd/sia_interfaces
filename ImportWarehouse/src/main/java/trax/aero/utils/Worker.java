package trax.aero.utils;

import java.sql.SQLException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.persistence.EntityManagerFactory;

import trax.aero.controller.ImportWarehouseController;
import trax.aero.data.ImportWarehouseData;
import trax.aero.interfaces.IImportWarehouseData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_BATCH;


public class Worker implements Runnable {

	Logger logger = LogManager.getLogger("ImportWarehouse_I46");
	@EJB IImportWarehouseData data;
	
	
	public Worker(IImportWarehouseData data)
	{
		this.data = data;
	}
	
	private MT_TRAX_RCV_I46_4077_BATCH input =null;
	
	private String exceuted = "";
	
	public void run() 
	{
		setExceuted("OK");
		
			try {
				String ouput = data.ProcessReqestBatch(input);
	    	if(ouput == null || !ouput.equalsIgnoreCase("OK")) {
	    		RunAble.warehousesFailure.add(input); 
	    	}
		}
		catch(Exception e)
		{	
			
			e.printStackTrace();
			logger.severe(e.toString());
		}
	}

	public MT_TRAX_RCV_I46_4077_BATCH getInput() {
		return input;
	}

	public void setInput(MT_TRAX_RCV_I46_4077_BATCH inputs) {
		this.input = inputs;
	}

	public String getExceuted() {
		return exceuted;
	}

	public void setExceuted(String exceuted) {
		this.exceuted = exceuted;
	}

}

