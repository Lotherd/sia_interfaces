package trax.aero.start;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import trax.aero.logger.LogManager;
import trax.aero.utils.Run;

@Startup
@Singleton
public class Start
{

	private ScheduledExecutorService scheduledServ;
	Run timer = null;
	Logger logger = LogManager.getLogger("JobConfirmation_I39I40");

	
	@PostConstruct
	public void start()
	{
		timer = new Run();
		
		if (scheduledServ == null) {
			int scheduledPoolSize = 1;
			logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
			this.scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
		}
		scheduledServ.scheduleAtFixedRate(timer, 30, Long.parseLong(System.getProperty("JobConfirmation_interval")), TimeUnit.SECONDS);
	
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
