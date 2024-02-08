package trax.aero.dao;

import java.sql.BatchUpdateException;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.codehaus.plexus.util.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
//import org.python.modules.synchronize;

import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.LocationSiteCapacity;
import trax.aero.pojo.Schedule;
import trax.aero.util.DataSourceClient;
import trax.aero.util.ErrorType;
import trax.aero.util.Validation;


public class LocationSiteCapacityDao 
{
	private ArrayList<LocationSiteCapacity> lscList;
	private Connection con;
	Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	
	public Connection getCon() {
		return con;
	}


	public LocationSiteCapacityDao()
	{
		logger.info( "Stablishing the connection...");
		/*	PropertyFile.ReadProperty();
			this.con = DBManager.getOracleConnection(PropertyFile.getDatabase(), PropertyFile.getUser(), PropertyFile.getPassword());*/
			try 
			{
				if(this.con == null || this.con.isClosed())
				{
					this.con = DataSourceClient.getConnection();
					logger.info("The connection was stablished successfully with status: " + String.valueOf(!this.con.isClosed()));
				}
			} 
			catch (SQLException e) 
			{
				logger.info("An SQLException occured getting the status of the connection");
				e.printStackTrace();
			}
			catch (CustomizeHandledException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
	}
	
	public void closeAllCiclesBeforeTheDateRange(DateTime date, String dayControl)
	{
		try
		{
			String newDayControl = "";
			String qry = "";
			PreparedStatement ps = null;
			ArrayList<LocationSiteCapacity> list = new ArrayList<LocationSiteCapacity>(getTheLastAndBeforeEmployeeWithIncompleteCycleOutsideOfTheDateRange(date, dayControl));
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			if(dayControl.equals("999"))
			{
				newDayControl= "001";
			}else
				if(dayControl.equals("001"))
				{
					newDayControl= "999";
				}
				else
					newDayControl = "555";
							
			for(LocationSiteCapacity item : list)
			{					
				if(!dayControl.equals("555"))
				{	
					if(dayControl.equals("999"))
					{
						if(item.getDayControl().equals("001"))
							qry = " delete from location_site_capacity ";
						else
							if(item.getDayControl().equals("555"))
								qry = "update location_site_capacity set day_control='"+dayControl+"'";
					}
					else
						if(dayControl.equals("001"))
						{
							if(item.getDayControl().equals("999"))
								qry = " delete from location_site_capacity ";
							else
								if(item.getDayControl().equals("555"))
									qry = "update location_site_capacity set day_control='"+dayControl+"'";
						}
						
					DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
					String str = item.getStartWorkDate().toString(fmt);	
					String str1 = item.getWorkDate().toString(fmt);	
					
					qry += " WHERE DAY_CONTROL='"+item.getDayControl()+"' and  START_WORK_DATE=TO_DATE('"+str+"','DD-MON-YY HH24:MI:SS') and hour="+item.getHour()+" and minute="+item.getMinute()+" and site='"+item.getSite()+"' and location ='"+item.getLocation()+"' and WORK_DATE =to_date('"+str1+"','DD-MON-YY HH24:MI:SS') and employee='"+item.getEmployee()+"' and \"GROUP\"='"+item.getGroup()+"'";
				
					try
					{
						ps = con.prepareStatement(qry);
						ps.executeUpdate();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					finally 
					{
						if(ps != null && !ps.isClosed())
							ps.close();
					}
				}
			}
		} 
		catch (CustomizeHandledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<LocationSiteCapacity> getTheLastAndBeforeEmployeeWithIncompleteCycleOutsideOfTheDateRange(DateTime date, String dayControl) throws CustomizeHandledException, SQLException
	{
		ArrayList<LocationSiteCapacity> list = new ArrayList<LocationSiteCapacity>();
		String qry = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy");
		String strDate = date.toString(fmt);
					
		strDate += " ";
		if(date.getHourOfDay() < 9)
			strDate += "0";
		
		strDate += date.getHourOfDay()+":00:00";
		
		if(dayControl.equals("001"))
		{//EARLY
			qry =   " SELECT EARLYeMP.*, LSC.DAY_CONTROL, LSC.START_WORK_DATE, lsc.hour, lsc.minute, lsc.site, lsc.location, lsc.\"GROUP\" " + 
					" FROM " + 
					"  ( " + 
					"    SELECT " + 
					"      EMPLOYEE, MIN(pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE)) AS wDate " + 
					"    FROM " + 
					"      location_site_capacity " + 
					"    WHERE pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE )>TO_DATE('"+strDate+"','DD-MON-YY HH24:MI:SS') " + 
					"    GROUP BY " + 
					"      EMPLOYEE " + 
					"  ) EARLYeMP " + 
					" INNER JOIN LOCATION_SITE_CAPACITY LSC ON LSC.EMPLOYEE=EARLYeMP.EMPLOYEE AND EARLYeMP.wDate=pkg_application_function.getCombinedDate(LSC.work_date , LSC.HOUR , MINUTE) AND LSC.DAY_CONTROL<>'"+dayControl+"' ";
		}
		else
			if(dayControl.equals("999"))
			{//LATE
				qry =   " SELECT LATESTeMP.*, LSC.DAY_CONTROL, LSC.START_WORK_DATE, lsc.hour, lsc.minute, lsc.site, lsc.location, lsc.\"GROUP\"  " +
						" FROM " +
						" ( " + 
						"   SELECT " + 
						"     EMPLOYEE, MAX(pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE)) AS wDate " + 
						"   FROM " + 
						"     location_site_capacity " + 
						"   WHERE pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE )<TO_DATE('"+strDate+"','DD-MON-YY HH24:MI:SS') " + 
						"   GROUP BY " + 
						"     EMPLOYEE " +
						" ) LATESTeMP " +
						" INNER JOIN LOCATION_SITE_CAPACITY LSC ON LSC.EMPLOYEE=LATESTeMP.EMPLOYEE AND LATESTeMP.wDate=pkg_application_function.getCombinedDate(LSC.work_date , LSC.HOUR , MINUTE) AND LSC.DAY_CONTROL<>'"+dayControl+"' ";
			}
		//logger.info(qry);
		
		if(con == null || con.isClosed())
		{
			con = DataSourceClient.getConnection();
			logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
		}
		try
		{
			ps = con.prepareStatement(qry);
			rs = ps.executeQuery();
			while(rs.next())
			{
				LocationSiteCapacity temp = new LocationSiteCapacity();
				temp.setEmployee(rs.getString("employee"));
				temp.setGroup(rs.getString("group"));
				temp.setSite(rs.getString("site"));
				temp.setLocation(rs.getString("location"));
				temp.setDayControl(rs.getString("day_control"));
				temp.setWorkDate(new DateTime(rs.getDate("wDate").getTime()));
				temp.setStartWorkDate(new DateTime(rs.getDate("START_WORK_DATE").getTime()));
				temp.setHour(rs.getInt("hour"));
				temp.setMinute(rs.getInt("minute"));
				
				list.add(temp);				
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}finally{
			if(rs != null && !rs.isClosed())
				rs.close();
			if(ps != null && !ps.isClosed())
				ps.close();
		}
		return list;
	}
	
	public void deleteRowsByEmployeeStartAndEndDate(String employee, String group, DateTime startDate, DateTime endDate) throws CustomizeHandledException
	{
		logger.info("Removing location site capacity... employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate);
		
		PreparedStatement ps = null;
				
		try
		{
			logger.info("Preparing the connection and the data to delete the employee..." + employee);
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}			
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy");
			String strStart = startDate.toString(fmt);
			String strEnd = endDate.toString(fmt);	
						
			strStart += " ";
			if(startDate.getHourOfDay() < 9)
				strStart += "0";
			
			strStart += startDate.getHourOfDay()+":00:00";
			
			strEnd += " ";
			if(endDate.getHourOfDay() < 9)
				strEnd += "0";
			
			strEnd += endDate.getHourOfDay()+":"+endDate.getMinuteOfHour()+":"+endDate.getSecondOfMinute();
			
	/*	String query =  " delete  " + 
						" from location_site_capacity l " + 
						" where l.employee = '"+employee+"' and " + 
						" pkg_application_function.getCombinedDate(l.work_date , l.hour , l.minute) between TO_DATE('"+strStart+"','DD-MON-YY HH24:MI:SS') and TO_DATE('"+strEnd+"','DD-MON-YY HH24:MI:SS') ";

	*/	
			
			String query =  " delete  " + 
					" from location_site_capacity l " + 
					" where pkg_application_function.getCombinedDate(l.work_date , l.hour , l.minute) >= TO_DATE('"+strStart+"','DD-MON-YY HH24:MI:SS') and pkg_application_function.getCombinedDate(l.work_date , l.hour , l.minute) <= TO_DATE('"+strEnd+"','DD-MON-YY HH24:MI:SS') ";
			logger.info(query);
			ps = con.prepareStatement(query);
			
			ps.executeQuery();
			
		//	logger.info("The employee " + employee + " has been removed...");
			logger.info("The employees has been removed...");
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurrer trying to delete the employee with the values: employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate);
		throw new CustomizeHandledException("A SQLException occurred executing the query to delete the employee by start and end date. " + "\n error: " + ErrorType.BAD_REQUEST + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to delete the employee with the values: employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate + ". Some values could be null....");
			throw new CustomizeHandledException("A NullPointerException occurred executing the query  to delete the employee by start and end date because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.info("An unexpected error ocurred trying to delete the employee with the values: employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate);
		throw new CustomizeHandledException("An Exception occurred executing the query  to delete the employee by start and end date. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurred trying to close the statement");
			throw new CustomizeHandledException("Log file path: " + "\nA SQLException ocurred trying to close the statement of the query. " + "\n error: " + ErrorType.BAD_REQUEST + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
			}
		}
	}
	
	
	public void deleteRowsByEmployee(String employee, String group, DateTime startDate, DateTime endDate) throws CustomizeHandledException
	{
		logger.info("Removing location site capacity... employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate);
		
		PreparedStatement ps = null;
				
		try
		{
			logger.info("Preparing the connection and the data to delete the employee..." + employee);
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}			
			
			DateTime initialStartDT = new DateTime(startDate.getMillis());
			if(startDate.isBefore(initialStartDT)) {
				startDate = initialStartDT;						
			}
			
			DateTime initialEndDT = new DateTime(endDate.getMillis());
			if(endDate.isAfter(initialEndDT)) {
				endDate = initialEndDT;						
			}
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy");
			String strStart = startDate.toString(fmt);
			String strEnd = endDate.toString(fmt);	
						
			strStart += " ";
			if(startDate.getHourOfDay() < 9)
				strStart += "0";
			
			strStart += startDate.getHourOfDay()+":00:00";
			
			strEnd += " ";
			if(endDate.getHourOfDay() < 9)
				strEnd += "0";
			
			strEnd += endDate.getHourOfDay()+":"+endDate.getMinuteOfHour()+":"+endDate.getSecondOfMinute();
			
			
			
	/*	String query =  " delete  " + 
						" from location_site_capacity l " + 
						" where l.employee = '"+employee+"' and " + 
						" pkg_application_function.getCombinedDate(l.work_date , l.hour , l.minute) between TO_DATE('"+strStart+"','DD-MON-YY HH24:MI:SS') and TO_DATE('"+strEnd+"','DD-MON-YY HH24:MI:SS') ";

	*/	
			
			String query =  " delete  " + 
					" from location_site_capacity l " + 
					" where l.employee = '"+employee+"' and pkg_application_function.getCombinedDate(l.work_date , l.hour , l.minute) >= TO_DATE('"+strStart+"','DD-MON-YY HH24:MI:SS') and pkg_application_function.getCombinedDate(l.work_date , l.hour , l.minute) <= TO_DATE('"+strEnd+"','DD-MON-YY HH24:MI:SS') ";
			logger.info(query);
			ps = con.prepareStatement(query);
			
			ps.executeQuery();
			
		//	logger.info("The employee " + employee + " has been removed...");
			logger.info("The employees has been removed...");
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurrer trying to delete the employee with the values: employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate);
		throw new CustomizeHandledException("A SQLException occurred executing the query to delete the employee by start and end date. " + "\n error: " + ErrorType.BAD_REQUEST + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to delete the employee with the values: employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate + ". Some values could be null....");
			throw new CustomizeHandledException("A NullPointerException occurred executing the query  to delete the employee by start and end date because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.info("An unexpected error ocurred trying to delete the employee with the values: employee = " + employee + "; group = " + group + "; start date = " + startDate + "; end date = " + endDate);
		throw new CustomizeHandledException("An Exception occurred executing the query  to delete the employee by start and end date. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurred trying to close the statement");
			throw new CustomizeHandledException("Log file path: " + "\nA SQLException ocurred trying to close the statement of the query. " + "\n error: " + ErrorType.BAD_REQUEST + "\nemployee: " + "data: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
			}
		}
	}
	
	
	public void setDayControl(boolean isFirst, LocationSiteCapacity lsc, DateTime dtTemp, int initialMinute, double manHour, DateTime endDateTime)
	{
		if(isFirst)
		{
			lsc.setDayControl("001");
			isFirst = false;logger.info("isFisrt"+isFirst);
			lsc.setMinute(dtTemp.getMinuteOfHour());//The minute field will be != 0 only the first time
			initialMinute = lsc.getMinute();					
			manHour = ((60 - lsc.getMinute())/ 60f);
		}
		else
			if(dtTemp.getHourOfDay() == endDateTime.getHourOfDay())
			{
				lsc.setDayControl("999");
				lsc.setMinute(0);
				if(initialMinute > 0 && endDateTime.getMinuteOfHour() == 0)
				{
					//lsc.setMinute(60 - initialMinute);							
					manHour = (1 - ((60 - initialMinute)/ 60f));
				}
				else
					if(endDateTime.getMinuteOfHour() > 0)
					{
						manHour = 1 - ((60 - endDateTime.getMinuteOfHour())/ 60f);
					}
					else
					{
						manHour = (1f);
					}
			//	manHour = (workHours - summatoryWorkHours);
			}
			else
				{
					lsc.setDayControl("555");
					manHour = ((60 - lsc.getMinute())/ 60f);
				}logger.info("isFisrtEnd"+isFirst);
	}
	
	public void fillingLocationSiteCapacity(LocationSiteCapacity lsc, Schedule sched, DateTime dtTemp, final boolean isFirst, int initialMinute, DateTime endDateTime, DateTime startDateTime, double manHour )
	{
		lsc.setLocation(sched.getLocation());
		lsc.setSite(sched.getSite());
		
		//Extracting the Date from the DateTime
		/*SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		String workDateSgring = sdFormat.format(new java.util.Date(dtTemp.getMillis()));*/
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		String workDateSgring = dtTemp.toString(fmt);

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime workDate = formatter.parseDateTime(workDateSgring);
		
		lsc.setWorkDate(workDate);
		lsc.setEmployee(sched.getEmployee());
		
		//develop the logic
		lsc.setMonth(dtTemp.getMonthOfYear());
		lsc.setYear(dtTemp.getYearOfEra());
		
	/*	if(dtTemp.getHourOfDay() == 0)
			dtTemp = dtTemp.plusHours(1);
		*/	
		lsc.setHour(dtTemp.getHourOfDay());
		lsc.setMinute(0);				
		lsc.setCreatedBy("TRAXIFACE");
		
		//develop the logic
		lsc.setBreaks("");
		lsc.setBreakDescription("");		
				
		setDayControl(isFirst, lsc, dtTemp, initialMinute, manHour, endDateTime);
		logger.info("isfirst2 "+isFirst);					
		lsc.setManHours(manHour);
		
	//	DateTime startWorkDate = new DateTime(sched.getEmpstartdt().getTime());
		DateTime startWorkDate = new DateTime(startDateTime.getMillis());
		lsc.setStartWorkDate(startWorkDate);
		
		lsc.setGroup(sched.getGroup());				
		lsc.setHeadCount(0);
		lsc.setFactor(0);	
	}
	
	public void fillData( Schedule sched, java.util.Date startDate, java.util.Date endDate) throws CustomizeHandledException
	{
		java.util.Date throwStartD = null, throwEndD = null;
		LocationSiteCapacity lsc = null;

		logger.info("Preparing the data ... employee = " + sched.getEmployee() + "; group = " + sched.getGroup() + "; location = " + sched.getLocation() + "; site = " + sched.getSite() + "; start date = " + sched.getEmpstartdt() + "; end date = " + sched.getEmpenddt());
		
		lscList = new ArrayList<LocationSiteCapacity>();
		try
		{
			DateTime startDateTime = new DateTime(sched.getEmpstartdt().getTime());
			DateTime endDateTime = new DateTime(sched.getEmpenddt().getTime());
			
			logger.info(endDateTime.toString());
			
			//Added 03/13/2019 for Kathie
			DateTime initialStartDT = new DateTime(startDate.getTime());
			if(startDateTime.isBefore(initialStartDT))
				startDateTime = initialStartDT;						
			//End of Added 03/13/2019 for Kathie
			
			//Added 03/27/2019 for Kathie
			DateTime initialEndDT = new DateTime(endDate.getTime());
			if(endDateTime.isAfter(initialEndDT))
				endDateTime = initialEndDT;						
			//End of Added 03/27/2019 for Kathie
			
			if(startDateTime != null)
				SequenceCompleted001To999ToStart(sched, startDateTime);

			throwStartD = new java.util.Date(startDateTime.getMillis());
			throwEndD = new java.util.Date(endDateTime.getMillis());
												
			@SuppressWarnings("unused")
			double summatoryWorkHours = 0.0f;
						
			boolean isFirst = true;
			
			/*if(startDateTime.getHourOfDay() == 0)
				startDateTime = startDateTime.plusHours(1);*/
			DateTime dtTemp = startDateTime;
			int initialMinute = 0;
			
			//This variable is used when the date of the record 001 plus 1 hr is mayor than the end date of the same record
			//i.e.: date of the record 001: 03/20 00:04
			//i.e.: end date of the record 001: 03/20 00:40
			//i.e.: date of the record 001 plus 1hr: 03/20 01:04
			//i.e.: 03/20 01:04 > 03/20 00:40
			//then the value of this variable will be true
			boolean specialCases = false;
			
			while( dtTemp.isBefore(endDateTime)  || ((dtTemp.getDayOfMonth() == endDateTime.getDayOfMonth()) && (dtTemp.getHourOfDay() == endDateTime.getHourOfDay())))
			{
				lsc = new LocationSiteCapacity();
				
				double manHour = 0.0f;
								
				lsc.setLocation(sched.getLocation());
				lsc.setSite(sched.getSite());
				
				//Extracting the Date from the DateTime
				/*SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
				String workDateSgring = sdFormat.format(new java.util.Date(dtTemp.getMillis()));*/
				DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
				String workDateSgring = dtTemp.toString(fmt);

				DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
				DateTime workDate = formatter.parseDateTime(workDateSgring);
				
				lsc.setWorkDate(workDate);
				lsc.setEmployee(sched.getEmployee());
				
				//develop the logic
				lsc.setMonth(dtTemp.getMonthOfYear());
				lsc.setYear(dtTemp.getYearOfEra());

				lsc.setHour(dtTemp.getHourOfDay());
				lsc.setMinute(0);				
				lsc.setCreatedBy("TRAXIFACE");
				lsc.setModifiedBy("TRAXIFACE");
				
				//develop the logic
				lsc.setBreaks("");
				lsc.setBreakDescription("");		
						
				if(isFirst)
				{
					lsc.setDayControl("001");
					isFirst = false;logger.info("isFisrt"+isFirst);
					lsc.setMinute(dtTemp.getMinuteOfHour());//The minute field will be != 0 only the first time
					initialMinute = lsc.getMinute();	
					
			//		manHour = ((60 - lsc.getMinute())/ 60f);
		/*last added			*/
					Period diff = new Period(dtTemp,endDateTime);
					if(diff.getHours() < 1)
						specialCases = true;
					else
						specialCases = false;		/*end last added*/
				}
				else
					if(dtTemp.getHourOfDay() == endDateTime.getHourOfDay())
					{
						lsc.setDayControl("999");
						lsc.setMinute(0);
											
						lsc.setMinute(endDateTime.getMinuteOfHour());
					//	manHour = 1 - ((60 - lsc.getMinute())/ 60f);
						/*original
							if(endDateTime.getMinuteOfHour() > 0)
								manHour = 1 - ((60 - endDateTime.getMinuteOfHour())/ 60f);
							//	manHour = endDateTime.getMinuteOfHour()/100f;
							else
								if(endDateTime.getMinuteOfHour() == 0 && startDateTime.getMinuteOfHour() > 0)
									manHour = (0f);
								else
									manHour = (1f);*/
					//	manHour = (workHours - summatoryWorkHours);
					}
					else
						{
							lsc.setDayControl("555");
					//		manHour = ((60 - lsc.getMinute())/ 60f);
						}
				
				manHour = ((60 - lsc.getMinute())/ 60f);
				
				if(lsc.getDayControl().equals("999") )
				{
					manHour = (1 - manHour) ;
					lsc.setMinute(endDateTime.getMinuteOfHour());
				}
				
				lsc.setManHours(manHour);
				
			//	DateTime startWorkDate = new DateTime(sched.getEmpstartdt().getTime());
				DateTime startWorkDate = new DateTime(startDateTime.getMillis());
				lsc.setStartWorkDate(startWorkDate);
				
				lsc.setGroup(sched.getGroup());				
				lsc.setHeadCount(0);
				lsc.setFactor(0);	
				
				lscList.add(lsc);
				
				summatoryWorkHours += manHour;
				dtTemp = dtTemp.plusHours(1);
			
			}
			
		
			
			if(specialCases && !lsc.getDayControl().equals("999"))
			{				
				LocationSiteCapacity lscSpecialCases = new LocationSiteCapacity();
				
				lscSpecialCases.setDayControl("999");
//				lscSpecialCases.setMinute(0);
											
				lscSpecialCases.setLocation(lsc.getLocation());
				lscSpecialCases.setSite(lsc.getSite());
				
				if(dtTemp.isBefore(endDateTime))
				{
					lscSpecialCases.setWorkDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dtTemp.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))));
					lscSpecialCases.setHour(dtTemp.getHourOfDay());
					lscSpecialCases.setMonth(dtTemp.getMonthOfYear());
					lscSpecialCases.setYear(dtTemp.getYear());
					lscSpecialCases.setMinute(0);
				}
				else
					{
					dtTemp= dtTemp.minusHours(1);
					lscSpecialCases.setWorkDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dtTemp.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))));
					lscSpecialCases.setHour(dtTemp.getHourOfDay());
					lscSpecialCases.setMonth(dtTemp.getMonthOfYear());
					lscSpecialCases.setYear(dtTemp.getYear());
					lscSpecialCases.setMinute(dtTemp.plusMinutes(1).getMinuteOfHour());
						/*lscSpecialCases.setWorkDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(endDateTime.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))));
						lscSpecialCases.setHour(endDateTime.getHourOfDay());
						lscSpecialCases.setMonth(endDateTime.getMonthOfYear());
						lscSpecialCases.setYear(endDateTime.getYear());
						lscSpecialCases.setMinute(endDateTime.getMinuteOfHour());*/
					}
				lscSpecialCases.setManHours(1-((60 - lsc.getMinute())/ 60f));
				
			//	lscSpecialCases.setWorkDate(lsc.getWorkDate());
				lscSpecialCases.setEmployee(lsc.getEmployee());
//				lscSpecialCases.setMonth(dtTemp.getMonthOfYear());
//				lscSpecialCases.setYear(dtTemp.getYear());
//				lscSpecialCases.setHour(dtTemp.getHourOfDay());
				
			//	lscSpecialCases.setHour(lsc.getHour()+1);
				lscSpecialCases.setCreatedBy(lsc.getCreatedBy());
				lscSpecialCases.setModifiedBy(lsc.getModifiedBy());
				lscSpecialCases.setStartWorkDate(lsc.getStartWorkDate());
				lscSpecialCases.setGroup(lsc.getGroup());					
				
				lscList.add(lscSpecialCases);
				specialCases = false;
			}

			
			if(initialEndDT != null)
			{
				SequenceCompleted001To999ToEnd(sched, initialEndDT);
			}
		} 
		catch (NullPointerException npe) 
		{		
			Validation.throwValidateScheduleDates(throwStartD, throwEndD);
			logger.info("An error ocurrer while was preparing the values: employee = " + sched.getEmployee() + "; group = " + sched.getGroup() + "; location = " + sched.getLocation() + "; site = " + sched.getSite() + "; start date = " + sched.getEmpstartdt() + "; end date = " + sched.getEmpenddt() + ". Some values could be null....");
			
		throw new CustomizeHandledException("Exception name: NullPointerException,\nSome values are null when filling the fields. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + sched.toString() + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{				
			Validation.throwValidateScheduleDates(throwStartD, throwEndD);
logger.info("An error ocurrer while was preparing the values: employee = " + sched.getEmployee() + "; group = " + sched.getGroup() + "; location = " + sched.getLocation() + "; site = " + sched.getSite() + "; start date = " + sched.getEmpstartdt() + "; end date = " + sched.getEmpenddt());
			throw new CustomizeHandledException("An Exception occurred filling the fields. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" + sched.toString() + "\n}" + "\nmessage: " + e.getMessage());
		}
	}
	
	public void addEmployeeByScheduleAndStartAndEndDate(ArrayList<LocationSiteCapacity> param) throws CustomizeHandledException
	{
		logger.info("Preparing the data ...");
		LocationSiteCapacity error = null;
		String query = 	" insert into location_site_capacity (location, site, work_date, employee, manhours, month, year, hour, minute, created_by, created_date, modified_by, modified_date, break, day_control, start_work_date, \"GROUP\", break_description, headcount, factor) " + 
						" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, ?, sysdate, ?, ?, TO_DATE(?,'DD-MON-YY HH24:MI:SS'), ?, ?, ?, ?) ";
		
		PreparedStatement ps = null;		
		this.lscList = param;
		
		try
		{
			logger.info("Preparing the connection and the data to insert the schedule...");
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
							
			ps = con.prepareStatement(query);
			
			String data = "An SQLException occurred executing trying to insert the schedules. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: ";//{" + this.lscToString() + "\n}" + "\nmessage: " + sqlicve.getMessage();
			boolean isOkay = true;
			for(LocationSiteCapacity item : param)
			{
				error = item;
				try {
					ps.setString(1, item.getLocation());
					ps.setString(2, item.getSite());
					ps.setDate(3, new java.sql.Date(item.getWorkDate().getMillis()));
					ps.setString(4, item.getEmployee());
					ps.setDouble(5, item.getManHours());			
					ps.setInt(6, item.getMonth());
					ps.setInt(7, item.getYear());
					ps.setInt(8, item.getHour());
					ps.setInt(9, item.getMinute());
					ps.setString(10, item.getCreatedBy());
					ps.setString(11, item.getModifiedBy());
					
					ps.setString(12, item.getBreaks());
					ps.setString(13, item.getDayControl());
	
					DateTime date = item.getStartWorkDate();
					DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
					String str = date.toString(fmt);	
					
					ps.setString(14, str);				
					ps.setString(15, item.getGroup());
					ps.setString(16, item.getBreakDescription());
					ps.setInt(17, item.getHeadCount());
					ps.setInt(18, item.getFactor());
					
					ps.executeUpdate();
					logger.info("update");
				}
				catch (SQLException sqle) {
					logger.info("An error ocurrer trying to insert the schedules...");
					data += "{ " + item.toString() + "\nmessage: " + sqle.getMessage()+ "}\n" ;
					isOkay = false;
				}
				//ps.addBatch();
			}
			
			//ps.executeBatch();
			if(!isOkay)
			{
				throw new SQLException(data + "]");
			}
			logger.info("The schedules have been updated...");
		}
		catch (BatchUpdateException bue)
		{
			logger.info("An error ocurrer trying to insert the schedules... Unique constraint (ACA.P_LOCATION_SITE_SCHEDULE) violated");
			throw new CustomizeHandledException("A BatchUpdateException ocurred trying to insert the schedules... Unique constraint (ACA.P_LOCATION_SITE_SCHEDULE) violated. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" + this.lscToString() + "\n}" + "\nmessage: " + bue.getMessage());
		}
		catch (SQLIntegrityConstraintViolationException sqlicve) 
		{
			logger.info("An error ocurrer trying to insert the schedules...");
			//sqlicve.printStackTrace();
			throw new CustomizeHandledException("An SQLIntegrityConstraintViolationException occurred executing trying to insert the schedules. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + this.lscToString() + "\n}" + "\nmessage: " + sqlicve.getMessage());
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurrer trying to insert the schedules...");
			throw new CustomizeHandledException("An SQLException trying to insert the schedules. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + this.lscToString() + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to insert the schedules...");
			throw new CustomizeHandledException("A NullPointerException trying to insert the schedules because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + this.lscToString() + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.info("An error ocurrer trying to insert the schedules");
			throw new CustomizeHandledException("An Exception trying to insert the schedules. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" + this.lscToString() + "\n}" + "\nmessage: " + ExceptionUtils.getStackTrace(e));
		}
		finally 
		{
			try 
			{
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurrer trying to close the statement");
				throw new CustomizeHandledException("A SQLException trying to insert the schedules. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + this.lscToString() + "\n}" + "\nmessage: " + e.getMessage());
			}
		}				
	} 
	
	public void addLocationSiteCapacity(Schedule sched, java.util.Date startDate, Date endDate) throws CustomizeHandledException
	{		
		fillData(sched, startDate, endDate);
		
		synchronized (lscList) 
		{
			addEmployeeByScheduleAndStartAndEndDate(lscList);
		}
	}
	
	public HashMap<String, String> getLocationSiteCapacityByEmployee(String employee, DateTime dateTime) throws CustomizeHandledException
	{		
		logger.info("Preparing the data ... employee = " + employee + "; date = " + dateTime);
		java.sql.Date d = new java.sql.Date(dateTime.getMillis());

		String query = " SELECT location, site FROM LOCATION_SITE_CAPACITY where employee = '"+employee+"' and to_char(TO_DATE(WORK_DATE, 'DD-MON-YY HH24:MI:SS'), 'DD-MON-YY') = to_char(to_date('"+d+"','YYYY-MM-DD'), 'DD-MON-YY') and rownum = 1 ";
			
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashMap<String, String> result = new HashMap<String, String>();
		try
		{
			logger.info("Preparing the connection and the data to get the site and location...");
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			rs = ps.executeQuery();		
			
			if(rs.next())
			{
				result.put("location", rs.getString("location"));
				result.put("site", rs.getString("site"));
				logger.info("Getting the location and site...");
			}
			
			
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurred trying to execute the query with the values: employee = " + employee + "; date = " + dateTime);
			throw new CustomizeHandledException("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to get the location and site with the values: employee = " + employee + "; date = " + dateTime + ". Some values could be null....");
			throw new CustomizeHandledException("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.info("An error ocurrer trying to insert or update the amployee and the group with the values: employee = " + employee + "; date = " + dateTime);
			throw new CustomizeHandledException("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(rs != null && !rs.isClosed())
					rs.close();
				
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurrer trying to close the statement");
				throw new CustomizeHandledException("A SQLException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
			}
		}
		
		return result;
	}
	
	public void removeRowInLocationSiteCapacity(ResultSet rs) throws CustomizeHandledException, SQLException
	{
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
		fmt = DateTimeFormat.forPattern("dd-MMM-yy");
		String workDate = new DateTime(rs.getDate("work_date")).toString(fmt);
		fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
		String createdDate = new DateTime(rs.getDate("created_date")).toString(fmt);
		String startWorkDate = new DateTime(rs.getDate("start_work_date")).toString(fmt);
	/*		
		String query = " insert into location_site_capacity (day_control,location ,site,work_date,employee,manhours,month,year,hour,minute,created_by,created_date,modified_by,modified_date,start_work_date,\"GROUP\",headcount,factor ) "
				+ " select '999', location ,site,work_date,employee,manhours,month,year,hour,minute+1,created_by,created_date,modified_by,modified_date,start_work_date,\"GROUP\",headcount,factor " + 
				" from location_site_capacity " + 
				" where location = '"+rs.getString("location")+"' and site = '"+rs.getString("site")+"' and work_date = TO_DATE('"+workDate+"','DD-MON-YY HH24:MI:SS') and employee = '"+rs.getString("employee")+"' "
						+ " and manhours = "+rs.getString("MANHOURS")+" and month = "+rs.getInt("month")+" and year = "+rs.getInt("year")+" and hour = "+rs.getInt("hour")+" "
						+ " and minute = "+rs.getInt("minute")+" and created_by = '"+rs.getString("created_by")+"' and created_date = TO_DATE('"+createdDate+"','DD-MON-YY HH24:MI:SS') "
								+ " and start_work_date = TO_DATE('"+startWorkDate+"','DD-MON-YY HH24:MI:SS') and \"GROUP\" = '"+rs.getString("group")+"' and headcount = "+rs.getInt("headcount")+" and factor = "+rs.getInt("factor");
		*/
		
		String query = " delete from location_site_capacity lsc where exists (select * " + 
				" from location_site_capacity l" + 
				" where l.location = '"+rs.getString("location")+"' and l.location=lsc.location and l.site = '"+rs.getString("site")+"' and l.site=lsc.site and l.work_date = TO_DATE('"+workDate+"','DD-MON-YY HH24:MI:SS') and l.work_date=lsc.work_date and l.employee = '"+rs.getString("employee")+"' "
						+ " and l.employee=lsc.employee and l.manhours = "+rs.getString("MANHOURS")+" and l.manhours=lsc.manhours and l.month = "+rs.getInt("month")+" and l.month=lsc.month and l.year = "+rs.getInt("year")+" and l.year=lsc.year and l.hour = "+rs.getInt("hour")+" "
						+ " and l.hour=lsc.hour and l.minute = "+rs.getInt("minute")+" and l.minute=lsc.minute and l.created_by = '"+rs.getString("created_by")+"' and l.created_by=lsc.created_by and l.created_date = TO_DATE('"+createdDate+"','DD-MON-YY HH24:MI:SS') "
								+ " and l.created_date=lsc.created_date and l.start_work_date = TO_DATE('"+startWorkDate+"','DD-MON-YY HH24:MI:SS') and l.start_work_date=lsc.start_work_date and l.\"GROUP\" = '"+rs.getString("group")+"' and l.\"GROUP\"=lsc.\"GROUP\" and l.day_control='001' and l.day_control=lsc.day_control ) ";
		
		logger.info(query);
		PreparedStatement ps = null;
		if(rs != null)
			try
			{
				ps = con.prepareStatement(query);
			/*	ps.setString(1, rs.getString("location"));
				ps.setString(2, rs.getString("site"));
			
				
				
				logger.info("workDate--------------------"+workDate);
				ps.setString(3, workDate);
				
				ps.setString(4, rs.getString("employee"));
				ps.setString(5, rs.getString("MANHOURS"));
				ps.setInt(6, rs.getInt("month"));
				ps.setInt(7, rs.getInt("year"));
				ps.setInt(8, rs.getInt("hour"));
				ps.setInt(9, rs.getInt("minute"));
				ps.setString(10, rs.getString("created_by"));
				
				
				ps.setString(11, createdDate);
				
				ps.setString(12, startWorkDate);
				
				ps.setString(13, rs.getString("group"));
				ps.setInt(14, rs.getInt("headcount"));
				ps.setInt(15, rs.getInt("factor"));
				*/
				ps.executeUpdate();
			}
			catch (Exception e) 
			{
				logger.info("An error ocurrer trying to remove the row of the schedule");
				throw new CustomizeHandledException("An Exception occurred executing the query to remove the row of the schedule. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + rs.getString("employee").toString() + "\n}" + "\nmessage: " + e.getMessage());
			}finally {
				if(rs != null && !rs.isClosed())
					rs.close();
				
				if(ps != null && !ps.isClosed())
					ps.close();
			}
	}
	
	
	/**
	 * 
	 * @return 
	 * @throws CustomizeHandledException 
	 */
	public boolean SequenceCompleted001To999ToStart(Schedule sch, DateTime date) throws CustomizeHandledException
	{
		boolean exist = false;
		DateTime d1 = date.plusHours(-1);
		String d2 = date.toString(DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss"));
		/*String query = " select * from location_site_capacity where employee = '"+sch.getEmployee()+"' and \"GROUP\" = '"+sch.getGroup()
		+"' and pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE) BETWEEN TO_DATE('"+d1.toString(DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss"))+"' ,'DD-MON-YY HH24:MI:SS') AND TO_DATE('"+d2+"' ,'DD-MON-YY HH24:MI:SS')  order by pkg_application_function.getCombinedDate(work_date , HOUR , 0 ) asc ";
	last version, works perfect, but this one is before the change they want	*/
		String query = " select * from location_site_capacity where employee = '"+sch.getEmployee()+"' and \"GROUP\" = '"+sch.getGroup()
		+"' and pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE) < TO_DATE('"+d2+"' ,'DD-MON-YY HH24:MI:SS') order by pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE ) DESC ";
		
		
		logger.info(query);
		PreparedStatement ps = null, ps1 = null;
		ResultSet rs = null;
		try
		{
			if(con == null || con.isClosed())
			{
				this.con = DataSourceClient.getConnection();
				logger.info( "The connection was stablished successfully with status: " + String.valueOf(!this.con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
		
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
			
			rs = ps.executeQuery();
			
			if(rs != null && rs.next())
			{			
					logger.info("the last one was: hour"+rs.getInt("hour"));
					if(!rs.getString("day_control").equals("999"))
					{
						if(rs.getString("day_control").equals("001"))
						{
							removeRowInLocationSiteCapacity(rs);
						}
						else
						{
						query =   " update LOCATION_SITE_CAPACITY set day_control = '999', modified_by = 'TRAXIFACE', modified_date = sysdate"
								+ " where location = ? and site = ? and work_date = TO_DATE(?,'DD-MON-YY HH24:MI:SS') and employee = ? and manhours = ? and month = ? and year = ? and hour = ? and minute = ? and created_by = ? and created_date = TO_DATE(?,'DD-MON-YY HH24:MI:SS') and start_work_date = TO_DATE(?,'DD-MON-YY HH24:MI:SS') and \"GROUP\" = ? and headcount = ? and factor = ?  ";
						
						String qrys1 = "update LOCATION_SITE_CAPACITY set day_control = '999', modified_by = 'TRAXIFACE', modified_date = sysdate" 
								 + " where location = '"+rs.getString("location")+"' and site = '"+rs.getString("site")+"' and work_date = TO_DATE('"+new DateTime(rs.getDate("work_date")).toString(DateTimeFormat.forPattern("dd-MMM-yy"))+"','DD-MON-YY HH24:MI:SS') and employee = '"+rs.getString("employee")+"' "
								 + " and manhours = "+rs.getString("MANHOURS")+" and month = "+rs.getInt("month")+" and year = "+rs.getInt("year")+" and hour = "+rs.getInt("hour") +" and minute = "+rs.getInt("minute")+" and created_by = 'TRAXIFACE' "
								 + " and created_date = TO_DATE('"+new DateTime(rs.getDate("created_date")).toString(DateTimeFormat.forPattern("dd-MMM-yy kk:mm:ss"))+"','DD-MON-YY HH24:MI:SS') and start_work_date = TO_DATE('"+new DateTime(rs.getDate("start_work_date")).toString(DateTimeFormat.forPattern("dd-MMM-yy kk:mm:ss"))+"','DD-MON-YY HH24:MI:SS') and \"GROUP\" = '"+rs.getString("group")+"' and headcount = "+rs.getInt("headcount")+" and factor = "+rs.getInt("factor");
						
						if(con == null || con.isClosed())
						{
							con = DataSourceClient.getConnection();
							logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
						}
						ps1 = con.prepareStatement(query);
						
						ps1.setString(1, rs.getString("location"));
						ps1.setString(2, rs.getString("site"));
					
						fmt = DateTimeFormat.forPattern("dd-MMM-yy");
						String workDate = new DateTime(rs.getDate("work_date")).toString(fmt);
						logger.info("workDate--------------------"+workDate);
						ps1.setString(3, workDate);
						
						ps1.setString(4, rs.getString("employee"));
						ps1.setString(5, rs.getString("MANHOURS"));
						ps1.setInt(6, rs.getInt("month"));
						ps1.setInt(7, rs.getInt("year"));
						ps1.setInt(8, rs.getInt("hour"));
						ps1.setInt(9, rs.getInt("minute"));
						ps1.setString(10, rs.getString("created_by"));
						
						fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
						String createdDate = new DateTime(rs.getDate("created_date")).toString(fmt);
						ps1.setString(11, createdDate);
						
						String startWorkDate = new DateTime(rs.getDate("start_work_date")).toString(fmt);
						ps1.setString(12, startWorkDate);
						
						ps1.setString(13, rs.getString("group"));
						ps1.setInt(14, rs.getInt("headcount"));
						ps1.setInt(15, rs.getInt("factor"));
						
						ps1.executeUpdate();
						}
					}
				}
				exist = true;
				
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurrer trying to complete the sequence from 001 to 999 in the begining of the main date range of an employee with the values: employee = " + sch.getEmployee() + "; group = " + sch.getGroup() + "; start date = " + date + ";");
			throw new CustomizeHandledException("A SQLException occurred trying to complete the sequence from 001 to 999 in the begining of the main date range " + "\n error: " + ErrorType.BAD_REQUEST + "\nemployee: " + "data: {\nemployee: " + sch.getEmployee() + "\n,group: " + sch.getGroup() + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to complete the sequence from 001 to 999 in the begining of the main date range of an employee with the values: employee = " + sch.getEmployee() + "; group = " + sch.getGroup() + "; start date = " + date + ";");
			throw new CustomizeHandledException("A NullPointerException occurred trying to complete the sequence from 001 to 999 in the begining of the main date range because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "\nemployee: " + "data: {\nemployee: " + sch.getEmployee() + "\n,group: " + sch.getGroup() + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.info("An error ocurrer trying to complete the sequence from 001 to 999 in the begining of the main date range of an employee with the values: employee = " + sch.getEmployee() + "; group = " + sch.getGroup() + "; start date = " + date + ";");
			throw new CustomizeHandledException("An Exception occurred trying to complete the sequence from 001 to 999 in the begining of the main date range. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "\nemployee: " + "data: {\nemployee: " + sch.getEmployee() + "\n,group: " + sch.getGroup() + "\n}" + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(rs != null && !rs.isClosed())
				{
					rs.close();
				}
				if(ps != null && !ps.isClosed())
				{
					ps.close();
				}
				
				if(ps1 != null && !ps1.isClosed())
					ps1.close();
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
			throw new CustomizeHandledException("A SQLException trying to close the connection on location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + e.getMessage());
			}
		}
		
		return exist;
	}

	public boolean SequenceCompleted001To999ToEnd(Schedule sch, DateTime date) throws CustomizeHandledException
	{
		boolean exist = false;
		
		//String query = 	  " select * from location_site_capacity where employee = ? and \"GROUP\" = ? and start_work_date > TO_DATE(?,'DD-MON-YY HH24:MI:SS')  and rownum=1 order by pkg_application_function.getCombinedDate(work_date , HOUR , 0 ) asc ";
		String query = 	  " select * from location_site_capacity where employee = ? and \"GROUP\" = ? and pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE ) between TO_DATE(?,'DD-MON-YY HH24:MI:SS') and TO_DATE(?,'DD-MON-YY HH24:MI:SS') and rownum=1 order by pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE ) asc ";
		logger.info( " select * from location_site_capacity where employee = '"+sch.getEmployee()+"' and \"GROUP\" = '"+sch.getGroup()+"' and pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE) > TO_DATE('"+date.toString(DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss"))+"','DD-MON-YY HH24:MI:SS') and rownum=1 order by pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE ) asc ");
		
		
		
		DateTimeFormatter fmt1 = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
		String str1 = date.toString(fmt1);

		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
				    ResultSet.CONCUR_READ_ONLY);
			
			ps.setString(1, sch.getEmployee());
			ps.setString(2, sch.getGroup());
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
			String str = date.toString(fmt);	
			
			ps.setString(3, date.toString(fmt));
			ps.setString(4, date.plusHours(1).toString(fmt));
			
			String qryShow = "------------------"+"select * from location_site_capacity where employee = '"+sch.getEmployee()+"' and \"GROUP\" = '"+sch.getGroup()
			+"' and pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE ) between TO_DATE('"+date.toString(fmt)+"','DD-MON-YY HH24:MI:SS') and TO_DATE('"+date.plusHours(1).toString(fmt)+"','DD-MON-YY HH24:MI:SS')  and rownum=1 order by pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE ) asc"+"--------------------";
		//	MyLogger.logger.info(qryShow);
			
			
			rs = ps.executeQuery();
			
			while(rs != null && rs.next())
			{				
					if(!rs.getString("day_control").equals("001"))
					{
						query =   " update LOCATION_SITE_CAPACITY set day_control = '001', modified_by = 'TRAXIFACE', modified_date = sysdate"
								+ " where location = '"+rs.getString("location")+"' and site = '"+rs.getString("site")+"' and work_date = TO_DATE('"+new DateTime(rs.getDate("work_date")).toString(fmt)+"','DD-MON-YY HH24:MI:SS') and employee = '"+rs.getString("employee")+"' "
								+ " and manhours = '"+rs.getString("MANHOURS")+"' and month = "+rs.getInt("month")+" and year = "+rs.getInt("year")+" and hour = "+rs.getInt("hour") +" and minute = "+rs.getInt("minute")+" and created_by = 'TRAXIFACE' "
								+ " and created_date = TO_DATE('"+new DateTime(rs.getDate("created_date")).toString(fmt)+"','DD-MON-YY HH24:MI:SS') and start_work_date = TO_DATE('"+new DateTime(rs.getDate("start_work_date")).toString(fmt)+"','DD-MON-YY HH24:MI:SS') and \"GROUP\" = '"+rs.getString("group")+"' and headcount = "+rs.getInt("headcount")+" and factor = "+rs.getInt("factor") ;
						
						if(con == null || con.isClosed())
						{
							con = DataSourceClient.getConnection();
							logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
						}
						
						ps = con.prepareStatement(query);
					/*	
						ps.setString(1, rs.getString("location"));
						ps.setString(2, rs.getString("site"));
					//	ps.setDate(3, rs.getDate("work_date"));
						
						fmt = DateTimeFormat.forPattern("dd-MMM-yy");
						String workDate = new DateTime(rs.getDate("work_date")).toString(fmt);	
						ps.setString(3, workDate);
						MyLogger.logger.info("workDate--------------------"+workDate);
						
						ps.setString(4, rs.getString("employee"));
						ps.setString(5, rs.getString("MANHOURS"));
						ps.setInt(6, rs.getInt("month"));
						ps.setInt(7, rs.getInt("year"));
						ps.setInt(8, rs.getInt("hour"));
						ps.setInt(9, rs.getInt("minute"));
						ps.setString(10, rs.getString("created_by"));
						
						fmt = DateTimeFormat.forPattern("dd-MMM-yy kk:mm:ss");
						String createdDate = new DateTime(rs.getDate("created_date")).toString(fmt);
						ps.setString(11, createdDate);
						MyLogger.logger.info("createdDate--------------------"+createdDate);
						
						String startWorkDate = new DateTime(rs.getDate("start_work_date")).toString(fmt);
						ps.setString(12, startWorkDate);
						
						MyLogger.logger.info("startWorkDate--------------------"+startWorkDate);
						
						ps.setString(13, rs.getString("group"));
						ps.setInt(14, rs.getInt("headcount"));
						ps.setInt(15, rs.getInt("factor"));
						*/
						ps.executeUpdate();
						
					}
				}
				exist = true;
				
			//}
		}
		catch (SQLException sqle) 
		{
			logger.info( "An error ocurrer trying to complete the sequence from 001 to 999 in the end of the main date range of an employee with the values: employee = " + sch.getEmployee() + "; group = " + sch.getGroup() + "; start date = " + date + ";");
			throw new CustomizeHandledException("A SQLException occurred trying to complete the sequence from 001 to 999 in the end of the main date range " + "\n error: " + ErrorType.BAD_REQUEST + "\nemployee: " + "data: {\nemployee: " + sch.getEmployee() + "\n,group: " + sch.getGroup() + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
		logger.info("An error ocurrer trying to complete the sequence from 001 to 999 in the end of the main date range with the values: employee = " + sch.getEmployee() + "; group = " + sch.getGroup() + "; start date = " + date + ". Some values could be null....");
		throw new CustomizeHandledException("A NullPointerExceptionoccurred trying to complete the sequence from 001 to 999 in the end of the main date range because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "\nemployee: " + "data: {\nemployee: " + sch.getEmployee() + "\n,group: " + sch.getGroup() + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.info("An unexpected error ocurred trying to complete the sequence from 001 to 999 in the end of the main date range of an employee with the values: employee = " + sch.getEmployee() + "; group = " + sch.getGroup() + "; start date = " + date + "; ");
			throw new CustomizeHandledException("An Exception occurred trying to complete the sequence from 001 to 999 in the end of the main date range. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "\nemployee: " + "data: {\nemployee: " + sch.getEmployee() + "\n,group: " + sch.getGroup() + "\n}" + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(rs != null && !rs.isClosed())
				{
					rs.close();
				}
				
				
				if(ps != null && !ps.isClosed())
				{
					ps.close();
				}
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				throw new CustomizeHandledException("A SQLException trying to close the connection on location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + e.getMessage());
			}
		}
		
		return exist;
	}
	/**
	 * Converting Object to Json
	 * 
	 * @return String
	 */	
	public String lscToString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n").append("[");
		
		for(LocationSiteCapacity item : this.lscList)
			sb.append("\n").append("{" + item.toString() + "\n}");
		
		sb.append("\n").append("]");
        
		return sb.toString();	    
	}

/**
 * This is to close the cycle putting 999
 * @param startDT
 * @param dayControl
 * @throws CustomizeHandledException 
 * @throws SQLException 
 */
	public void closeCyclesBeforeTheDateRange(DateTime startDT, String dayControl) throws SQLException, CustomizeHandledException {
		//LATE
		
		ArrayList<LocationSiteCapacity> list = new ArrayList<LocationSiteCapacity>();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy");
		String strDate = startDT.toString(fmt);
					
		strDate += " ";
	/*	if(startDT.getHourOfDay() < 9)
			strDate += "0";
		
		strDate += startDT.getHourOfDay()+":00:00";*/
		strDate += startDT.getHourOfDay()+":"+startDT.getMinuteOfHour()+":00";
		
	    String qry = " SELECT LATESTeMP.*, LSC.DAY_CONTROL, LSC.START_WORK_DATE, lsc.hour, lsc.minute, lsc.site, lsc.location, lsc.\"GROUP\"  " +
					" FROM " +
					" ( " + 
					"   SELECT " + 
					"     EMPLOYEE, MAX(pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE)) AS wDate " + 
					"   FROM " + 
					"     location_site_capacity " + 
					"   WHERE pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE )<TO_DATE('"+strDate+"','DD-MON-YY HH24:MI:SS') " + 
					"   GROUP BY " + 
					"     EMPLOYEE " +
					" ) LATESTeMP " +
					" INNER JOIN LOCATION_SITE_CAPACITY LSC ON LSC.EMPLOYEE=LATESTeMP.EMPLOYEE AND LATESTeMP.wDate=pkg_application_function.getCombinedDate(LSC.work_date , LSC.HOUR , LSC.MINUTE) AND LSC.DAY_CONTROL<>'"+dayControl+"' ";
		
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    
	    if(con == null || con.isClosed())
		{
			con = DataSourceClient.getConnection();
			logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
		}
		try
		{
			ps = con.prepareStatement(qry);
			rs = ps.executeQuery();
			while(rs.next())
			{
				LocationSiteCapacity temp = new LocationSiteCapacity();
				temp.setEmployee(rs.getString("employee"));
				temp.setGroup(rs.getString("group"));
				temp.setSite(rs.getString("site"));
				temp.setLocation(rs.getString("location"));
				temp.setDayControl(rs.getString("day_control"));
				temp.setWorkDate(new DateTime(rs.getDate("wDate").getTime()));
				temp.setStartWorkDate(new DateTime(rs.getDate("START_WORK_DATE").getTime()));
				temp.setHour(rs.getInt("hour"));
				temp.setMinute(rs.getInt("minute"));
				
				list.add(temp);				
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}finally {
			if(rs != null && !rs.isClosed())
			{
				rs.close();
			}
			if(ps != null && !ps.isClosed())
			{
				ps.close();
			}
		}
		
		for(LocationSiteCapacity item : list)
		{		
			if(!item.getDayControl().equals("999"))
			{
				if(item.getDayControl().equals("001"))
					qry = " delete from location_site_capacity ";
				else
					if(item.getDayControl().equals("555"))
						qry = "update location_site_capacity set day_control='"+dayControl+"'";
										
				fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
				String str = item.getStartWorkDate().toString(fmt);	
				String str1 = item.getWorkDate().toString(fmt);	
					
				qry += " WHERE DAY_CONTROL='"+item.getDayControl()+"' and  START_WORK_DATE=TO_DATE('"+str+"','DD-MON-YY HH24:MI:SS') and hour="+item.getHour()+" and minute="+item.getMinute()+" and site='"+item.getSite()+"' and location ='"+item.getLocation()+"' and pkg_application_function.getCombinedDate(WORK_DATE, HOUR, MINUTE) =to_date('"+str1+"','DD-MON-YY HH24:MI:SS') and employee='"+item.getEmployee()+"' and \"GROUP\"='"+item.getGroup()+"'";
				
				try
				{
					ps = con.prepareStatement(qry);
					ps.executeUpdate();
				}
				catch (Exception e) {e.printStackTrace();
				}finally {
					if(ps != null && !ps.isClosed())
					{
						ps.close();
					}
				}
			}
		}
		
	}
	
	/**
	 * This is to open the cycle putting 001
	 * @param startDT
	 * @param dayControl
	 * @throws CustomizeHandledException 
	 * @throws SQLException 
	 */
		public void openCyclesBeforeTheDateRange(DateTime startDT, String dayControl) throws SQLException, CustomizeHandledException {
			//LATE
			
			ArrayList<LocationSiteCapacity> list = new ArrayList<LocationSiteCapacity>();
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy");
			String strDate = startDT.toString(fmt);
						
			strDate += " ";
		/*	if(startDT.getHourOfDay() < 9)
				strDate += "0";
			
			strDate += startDT.getHourOfDay()+":00:00";
			*/
			strDate += startDT.getHourOfDay()+":"+startDT.getMinuteOfHour()+":00";
		    String qry =   " SELECT EARLYeMP.*, LSC.DAY_CONTROL, LSC.START_WORK_DATE, lsc.hour, lsc.minute, lsc.site, lsc.location, lsc.\"GROUP\" " + 
							" FROM " + 
							"  ( " + 
							"    SELECT " + 
							"      EMPLOYEE, MIN(pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE)) AS wDate " + 
							"    FROM " + 
							"      location_site_capacity " + 
							"    WHERE pkg_application_function.getCombinedDate(work_date , HOUR , MINUTE )>TO_DATE('"+strDate+"','DD-MON-YY HH24:MI:SS') " + 
							"    GROUP BY " + 
							"      EMPLOYEE " + 
							"  ) EARLYeMP " + 
							" INNER JOIN LOCATION_SITE_CAPACITY LSC ON LSC.EMPLOYEE=EARLYeMP.EMPLOYEE AND EARLYeMP.wDate=pkg_application_function.getCombinedDate(LSC.work_date , LSC.HOUR , LSC.MINUTE) AND LSC.DAY_CONTROL<>'"+dayControl+"' ";
		
		    if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
		    PreparedStatement ps = null;
		    ResultSet rs = null;
		    ResultSet r = null;
			try
			{
				ps = con.prepareStatement(qry);
				rs = ps.executeQuery();
				while(rs.next())
				{
					LocationSiteCapacity temp = new LocationSiteCapacity();
					temp.setEmployee(rs.getString("employee"));
					temp.setGroup(rs.getString("group"));
					temp.setSite(rs.getString("site"));
					temp.setLocation(rs.getString("location"));
					temp.setDayControl(rs.getString("day_control"));
					temp.setWorkDate(new DateTime(rs.getDate("wDate").getTime()));
					temp.setStartWorkDate(new DateTime(rs.getDate("START_WORK_DATE").getTime()));
					temp.setHour(rs.getInt("hour"));
					temp.setMinute(rs.getInt("minute"));
					
					list.add(temp);				
				}
				
			} catch (SQLException e) 
			{
				e.printStackTrace();
			}finally {
				if(rs != null && !rs.isClosed())
				{
					rs.close();
				}
				if(ps != null && !ps.isClosed())
				{
					ps.close();
				}
			}
			
			for(LocationSiteCapacity item : list)
			{			
				if(!item.getDayControl().equals("001"))
				{
					if(item.getDayControl().equals("999"))
						qry = " delete from location_site_capacity ";
					else
						if(item.getDayControl().equals("555"))
							qry = "update location_site_capacity set day_control='"+dayControl+"'";
											
					fmt = DateTimeFormat.forPattern("dd-MMM-yy HH:mm:ss");
					String str = item.getStartWorkDate().toString(fmt);	
					String str1 = item.getWorkDate().toString(fmt);	
						
					qry += " WHERE DAY_CONTROL='"+item.getDayControl()+"' and  START_WORK_DATE=TO_DATE('"+str+"','DD-MON-YY HH24:MI:SS') and hour="+item.getHour()+" and minute="+item.getMinute()+" and site='"+item.getSite()+"' and location ='"+item.getLocation()+"' and pkg_application_function.getCombinedDate(WORK_DATE, HOUR, MINUTE) =to_date('"+str1+"','DD-MON-YY HH24:MI:SS') and employee='"+item.getEmployee()+"' and \"GROUP\"='"+item.getGroup()+"'";
					
					try
					{
						ps = con.prepareStatement(qry);
						r = ps.executeQuery();
					}
					catch (Exception e) {e.printStackTrace();
					}finally {
						if(r != null && !r.isClosed())
						{
							r.close();
						}
						if(ps != null && !ps.isClosed())
						{
							ps.close();
						}
					}
				}
			}
			
		}
}
