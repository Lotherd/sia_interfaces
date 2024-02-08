package trax.aero.start;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import trax.aero.logger.LogManager;
import trax.aero.utils.Run;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Startup
@Singleton
public class Start
{

	Logger logger = LogManager.getLogger("ACConfiguration_I51");
	private ScheduledExecutorService scheduledServ;
	Run timer = null;

	@PostConstruct
	public void start()
	{
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(System.getProperty("ACConfiguration_ZoneId")));
		 LocalTime localTime = LocalTime.parse(System.getProperty("ACConfiguration_Time"), DateTimeFormatter.ofPattern("HH:mm"));
		
		ZonedDateTime nextRun = now.withHour(localTime.getHour()).withMinute(localTime.getMinute()).withSecond(0);
		if(now.compareTo(nextRun) > 0)
		    nextRun = nextRun.plusDays(1);

		Duration duration = Duration.between(now, nextRun);
		long initialDelay = duration.getSeconds();
		
		
		timer = new Run();
		
		if (scheduledServ == null) {
			int scheduledPoolSize = 1;
			logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
			this.scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
		}
		scheduledServ.scheduleAtFixedRate(timer,initialDelay,TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	
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
