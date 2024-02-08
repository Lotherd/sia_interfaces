package trax.aero.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.pojo.PunchClock;
import trax.aero.pojo.PunchClockItem;
import trax.aero.pojo.Schedule;

import trax.aero.pojo.WorkSchedule;




public class Validation 
{
	static public Schedule finalSched;
	static public PunchClockItem finalPunch;
	
	
	static public ArrayList<PunchClockItem> validItems;
	static public String header;
	static public String errorSchedData;
	static public ArrayList<Schedule> validScheds;
	static public boolean isOkayHeader = true;
	static Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	
	static public volatile  boolean isOkay = true;
	static public volatile String errorData;
	public static List<Schedule> valid = null;
	
	
	static public void throwValidateEmployeeWorkSchedule(ArrayList<Schedule> items, WorkSchedule sched) throws CustomizeHandledException, InterruptedException
	{
		errorData = "";
		errorSchedData = "";
		validScheds = new ArrayList<Schedule>();
		 valid = Collections.synchronizedList(new ArrayList<Schedule>()); 
		
		String data = "Error: " + ErrorType.BAD_REQUEST;
		header = "";
		isOkay = true;
		boolean isOkayItem = true;
		isOkayHeader = true;
		
		
		int COUNT = 0;
		
		if(sched == null || sched.getSchedules() == null || sched.getSchedules().isEmpty() || sched.getStartdatetime() == null || sched.getEnddatetime() == null || sched.getSchedules() == null || sched.getSchedules().size() == 0 )
		{
			isOkay = false;
			isOkayHeader = false; 
			header += "\nException(s) occured with Null Entries";
		}
		if(!validateScheduleDates(sched.getStartdatetime(), sched.getEnddatetime()))
		{
			header += "\nException(s) occured during Schedule Date Validation.";
			isOkay = false;
			isOkayHeader = false; 
		}
		
		
		int scheduledPoolSize = 4;
		if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
			scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
		}
		logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
		ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
		
		logger.info("SIZE " + items.size());
		   for(Schedule s : items) {
		    	   
		    	WorkerValidation worker = new WorkerValidation();
		    	worker.setSh(s);
		    	scheduledServ.execute(worker);
		    }
			
		   scheduledServ.shutdown();
		   scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);	  
		    
		
		
		validScheds = new ArrayList<Schedule>(valid); 
		errorSchedData = errorData;
		if(!isOkay)
		{
			data += header + "\ndata: [ \n";
			data += errorData  + "]\n";
			throw new CustomizeHandledException(data);
		}
	}
	
	static public void throwValidateScheduleDates(java.util.Date startDate, java.util.Date endDate) throws CustomizeHandledException
	{			
		if(!validateScheduleDates(startDate, endDate))
		{
			logger.severe("The startdatetime value it is not before enddatetime value. Please correct your entries and try again...");
			throw new CustomizeHandledException("The startdatetime value it is not before enddatetime value. Please correct your entries and try again." + "\n error: " + ErrorType.BAD_REQUEST + "\n{" + "\n\"startdatetime\":" + new DateTime(startDate.getTime()) + ",\n\"enddatetime\":" + new DateTime(endDate.getTime()));
		}
	}
	
	static public void throwValidateScheduleDatesByList(WorkSchedule sched) throws CustomizeHandledException
	{	
		if(!validateScheduleDatesByList(sched))
		{
			logger.severe("The range of the dates is wrong. Please correct your entries and try again...");
			
			if(finalSched != null)
				throw new CustomizeHandledException("The range of the dates is wrong. Please correct your entries and try again." + "\n error: " + ErrorType.BAD_REQUEST + "\n{" + finalSched.toString() );
			else
				throw new CustomizeHandledException("The range of the dates is wrong. Please correct your entries and try again." + "\n error: " + ErrorType.BAD_REQUEST + "\n{" );
		}
	}
	
	static public void throwValidateScheduleEntries(WorkSchedule sched) throws CustomizeHandledException
	{	
		if(!validateScheduleEntries(sched))
		{
			logger.severe("Some fields are missing or empty. Please correct your entries and try again...");
			throw new CustomizeHandledException("Some fields are missing. Please correct your entries and try again." + "\n error: " + ErrorType.BAD_REQUEST + "\n{" + sched.toString() );
		}
	}
	
	static public void throwValidateScheduleLocationSite(WorkSchedule sched) throws CustomizeHandledException
	{	
		List<Schedule> schedules = sched.getSchedules();
		String data = "An Exception occurred executing the location site validation. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: [ \n";
		boolean isFail = false;
		for(Schedule s : schedules)
		{
			if(s.getSite() == null || s.getSite().isEmpty() || s.getLocation() == null || s.getLocation().isEmpty())
			{
				logger.severe("Location/Site(s) could not be validated. Please correct your entries and try again...");
				data += "{ employee: " + s.getEmployee() + ", group: " + s.getGroup() + ", location: "+ s.getLocation() + ", site:" + s.getSite() + " },\n";
				isFail = true;
			}
		}
		
		if(isFail)
		{
			throw new CustomizeHandledException(data + "]");
		}

	}
	
	
	static public boolean validateScheduleEntries(WorkSchedule sched)
	{
		boolean isOkay = true;
		String errorData = "";
		
		if(sched == null || sched.getSchedules() == null || sched.getSchedules().isEmpty() || sched.getStartdatetime() == null || sched.getEnddatetime() == null || sched.getSchedules() == null || sched.getSchedules().size() == 0 )
		{
			isOkay = false;
		}
		
		if(sched.getSchedules().size() > 0)
		{		
			int i =0;
			
			while(i < sched.getSchedules().size())
			{
				if(sched.getSchedules().get(i).getGroup() == null || sched.getSchedules().get(i).getGroup().trim().isEmpty() || sched.getSchedules().get(i).getEmployee() == null || sched.getSchedules().get(i).getEmployee().trim().isEmpty() || sched.getSchedules().get(i).getLocation() == null || sched.getSchedules().get(i).getLocation().trim().isEmpty() || sched.getSchedules().get(i).getSite() == null || sched.getSchedules().get(i).getSite().trim().isEmpty() || sched.getSchedules().get(i).getEmpstartdt() == null || sched.getSchedules().get(i).getEmpenddt() == null)
				{
					errorData += "{Error Type: Invalid/Missing Schedule Field, group: " + sched.getSchedules().get(i).getGroup() + ", employee: " + sched.getSchedules().get(i).getEmployee() + ", location: " + sched.getSchedules().get(i).getLocation() + ", site: " + sched.getSchedules().get(i).getSite() + ", Empstartdt: " + sched.getSchedules().get(i).getEmpenddt() + ", Empenddt:" + sched.getSchedules().get(i).getEmpenddt() + " }\n";
					isOkay = false;
				}
				i++;
			}
		}
		
		return isOkay;
	}
	
	static public boolean validateScheduleDates(java.util.Date startDate, java.util.Date endDate)
	{
		boolean isOkay = false;
		
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		
		if(start.isBefore(end))
			isOkay = true;
		
		return isOkay;
	}
	
	static public boolean validateScheduleDatesByList(WorkSchedule sched) throws CustomizeHandledException
	{
		boolean isOkay = true;
		
		DateTime start = new DateTime(sched.getStartdatetime().getTime());
		DateTime end = new DateTime(sched.getEnddatetime().getTime());
		
		ArrayList<Schedule> list = new ArrayList<Schedule>(sched.getSchedules());
		int i = 0;
		
		while(i < list.size() && isOkay)
		{
			if(!validateScheduleDates(list.get(i).getEmpstartdt(), list.get(i).getEmpenddt()))
			{
				isOkay = false;
				finalSched = new Schedule(list.get(i));
				
				logger.severe("The empstartdt value it is not before empenddt value. Please correct your entries and try again...");
				throw new CustomizeHandledException("The startdatetime value it is not before enddatetime value. Please correct your entries and try again." + "\n error: " + ErrorType.BAD_REQUEST + "\n{" + finalSched.toString());
			}
			
			DateTime startItem = new DateTime(list.get(i).getEmpstartdt().getTime());
			DateTime endItem = new DateTime(list.get(i).getEmpenddt().getTime());
			
			if((start.isBefore(startItem) || start.isEqual(startItem)) && (end.isEqual(endItem) || end.isAfter(endItem)))
			{
				isOkay = true;
			}	
			else
			{
				isOkay = false;
				finalSched = new Schedule(list.get(i));
			}
			i++;
		}
				
		return isOkay;
	}
	
	static private boolean validateEmployee(String empID)
	{
		boolean isOkay = true;
		if(empID != null || !empID.isEmpty())
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			String query = "SELECT RELATION_CODE FROM RELATION_MASTER WHERE RELATION_CODE = ? AND RELATION_TRANSACTION = 'EMPLOYEE' and rownum = 1 ";
			Connection con = null;
			
			try {
				if(con == null || con.isClosed())
				{
					con = DataSourceClient.getConnection();
					logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
				}
				
				ps = con.prepareStatement(query);
				ps.setString(1, empID);
				
				rs = ps.executeQuery();
				
				if(!rs.next())
				{
					isOkay = false;
				}
			} catch(SQLException | CustomizeHandledException sqle){
				logger.severe("An error occured when Validating User: Employee = " + empID);
				//sqle.printStackTrace();
				isOkay = false;
			} finally {
				try {
					if(con != null && !con.isClosed())
					{
						con.close();
					}
					if(ps != null && !ps.isClosed())
					{ 
						ps.close();
					}
					if(rs != null && !rs.isClosed())
					{
						rs.close();
					}
				} catch(SQLException sql) {
				
				}
			}
			
			return isOkay;
		} else
		{
			return false;
		}
		
	}
	
	

}
