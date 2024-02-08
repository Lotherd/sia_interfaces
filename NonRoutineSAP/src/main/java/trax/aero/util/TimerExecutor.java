package trax.aero.util;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import trax.aero.interfaces.INRData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.DTTRAXI37I384068;

public class TimerExecutor  implements Runnable{

	
	
	Logger logger = LogManager.getLogger("NR_I37I38");
	
	@EJB INRData data;
	
	
	public TimerExecutor(INRData data)
	{
		this.data = data;
	}
	
	private void process() {
		// TODO Auto-generated method stub
		
				try
				{
					//logger.info("Searching for Non Routines");
					JAXBContext jc = JAXBContext.newInstance(DTTRAXI37I384068.class);
					Unmarshaller unmarshaller = jc.createUnmarshaller();
					//File folder = new File(System.getProperty("NR_fileLoc"));

					//File[] files = folder.listFiles();
					
					String success = data.sendNRData();
							
				}
				catch(Exception e)
				{
					e.printStackTrace();
					logger.severe(e.toString());
					
				}
	}
	
	@Override
	public void run() {
		try {
			if(data.lockAvailable("I37I38"))
			{
				data.lockTable("I37I38");
				process();
				data.unlockTable("I37I38");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
