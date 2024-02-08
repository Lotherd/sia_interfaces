package trax.aero.start;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import trax.aero.interfaces.IImportWarehouseData;
import trax.aero.logger.LogManager;
import trax.aero.utils.RunAble;

@Startup
@Singleton
public class Start
{

	private ScheduledExecutorService scheduledServ;
	RunAble timerPost = null;
	RunAble timerBatch = null;
	Logger logger = LogManager.getLogger("ImportWarehouse_I46");
	@EJB IImportWarehouseData data;
	
	private void loadConcurrencyResources() {
        /*
         * try { scheduledServ = InitialContext.doLookup("concurrent/eMobilityTimer"); }
         * catch (NamingException e) { logger.
         * debug("Resource: concurrent/QuickTurnScheduledExecutor could not be injected (Message: "
         * + e.getMessage() + ")"); }
         */
		if (scheduledServ == null) {
			int scheduledPoolSize = 2;
			logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
			this.scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
		}
    }
	
	private void startTimer() {
		timerPost = new RunAble(data);
		timerBatch = new RunAble(data);
		
		try {
        	timerPost.setPostOn(true);
    		scheduledServ.scheduleAtFixedRate(timerPost, 30, Long.parseLong(System.getProperty("ImportWarehouse_intervalPost")), TimeUnit.SECONDS);
    		
    		timerBatch.setBatchOn(true);
    		scheduledServ.scheduleAtFixedRate(timerBatch, 30, Long.parseLong(System.getProperty("ImportWarehouse_intervalBatch")), TimeUnit.SECONDS);
        } catch (Exception e) {
        	logger.severe(e.getMessage());
            return;
        }
        
    }
	
	
	@PostConstruct
	public void start()
	{
		loadConcurrencyResources();
		startTimer();
	}
	@PreDestroy
	public void stop() 
	{
		if(!scheduledServ.isShutdown()) 
		{
			scheduledServ.shutdown();
		}
	}
	
}
