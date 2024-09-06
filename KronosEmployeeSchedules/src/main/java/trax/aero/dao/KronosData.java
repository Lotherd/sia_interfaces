package trax.aero.dao;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import org.joda.time.DateTime;

import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
//import trax.aero.optional.Optional;
import trax.aero.pojo.Schedule;
import trax.aero.pojo.WorkSchedule;
import trax.aero.util.DataSourceClient;
import trax.aero.util.ErrorType;
import trax.aero.util.Validation;
import trax.aero.util.Worker;

public class KronosData {

	EntityManagerFactory factory;
	EntityManager em;
	private Connection con;
	public static volatile  String data = "";
	
	Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	public Connection getCon() {
		return con;
	}
	
	//public InterfaceLockMaster lock;
	
	public KronosData()
	{		
		logger.info("Stablishing the connection...");
		
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
			logger.severe("An error occured getting the status of the connection");
			
		}
		catch (CustomizeHandledException e1) {
			logger.severe(e1.toString());
		}
		
		factory = Persistence.createEntityManagerFactory("KronosDS");
		em = factory.createEntityManager();	
		
	}
	
	
	public String insertEmployeeSchedules(WorkSchedule sched) throws CustomizeHandledException{
		String Exceuted = "";
		  
		logger.info("Starting WORK SCHEDULE...");
		logger.info(java.util.TimeZone.getDefault().getDisplayName());
		logger.info("SIZE: " + sched.getSchedules().size());
		boolean isOkay = true;
		boolean isOkayHeader = true;
		data = "";
		String errorData = "";
		String errorDataHeader = "";
		ArrayList<Schedule> items = sched.getSchedules();
		String finalStatus = "{\n\"status\": \"OK\", \n\"statusCode\": \"200\"\n}";
//		Response a = null;
		try
		{	
			
			synchronized (items)
			{
				//Optional<WorkSchedule> schedImpl = Optional.of(sched);
				
				try {
					Validation.throwValidateEmployeeWorkSchedule(items,sched);
				}
				catch (CustomizeHandledException e)
				{
					isOkay = false;
					errorData = Validation.errorSchedData;
					logger.info("size " + Validation.validScheds.size());
					items = Validation.validScheds;
					isOkayHeader = Validation.isOkayHeader;
					errorDataHeader = e.getMessage();
				}finally {
					  
				}
				
				//
				
//				Validation.throwValidateScheduleEntries(sched);
//				Validation.throwValidateScheduleLocationSite(sched);
//				Validation.throwValidateScheduleDates(schedImpl.get().getStartdatetime(), schedImpl.get().getEnddatetime());
				logger.info("Starting the process...");
				
				

				WorkScheduleDao wsDao = new WorkScheduleDao();						
				LocationSiteCapacityDao lscDao = new LocationSiteCapacityDao();
					
				try 
				{	
					
					if(!isOkayHeader)
					{
						throw new CustomizeHandledException("Invalid Header...\nData: [\n\t" + errorDataHeader + "]");
					}
					
					DateTime startDT = new DateTime(sched.getStartdatetime().getTime());
					DateTime endDT = new DateTime(sched.getEnddatetime().getTime());

					lscDao.deleteRowsByEmployeeStartAndEndDate(null, null, startDT, endDT);
					
					int scheduledPoolSize = 4;
					if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
						scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
					}
					logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
					ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
					
					
					
					for(Schedule item : items)
					{
						Worker worker = new Worker();
				    	worker.setInput(item);
				    	worker.setSched(sched);
				    	worker.setWork(true);
				    	scheduledServ.execute(worker);
				    	Thread.sleep(500);
					}
					
					scheduledServ.shutdown();
				    scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
						
					lscDao.closeCyclesBeforeTheDateRange(startDT, "999");
					lscDao.openCyclesBeforeTheDateRange(endDT, "001");
					if(!"".equalsIgnoreCase(data)) {
						throw new CustomizeHandledException(data);
					}
					
					if(!isOkay)
					{
						throw new CustomizeHandledException("Some Invalid Schedules...\nData: [\n\t" + errorData + "]");
					}
				}
				catch(CustomizeHandledException che) // added due to log 307522 - francis.ventura
				{
					//che.printStackTrace();
					throw new CustomizeHandledException(che.getMessage());
				}
				finally 
				{
					if(lscDao.getCon() != null && !lscDao.getCon().isClosed())
						lscDao.getCon().close();
					
					if(wsDao.getCon() != null && !wsDao.getCon().isClosed())
						wsDao.getCon().close();
				}
				
				logger.info("Ending the process...");
			}
			
//			a = Response.ok(finalStatus, MediaType.APPLICATION_JSON).build();
		}	
		catch(CustomizeHandledException che)
		{
			logger.severe("CustomizeHandledException: " + che.getMessage());
			throw new CustomizeHandledException("Some fields are missing or invalid. Please correct your entries and try again." + "\n error: " + ErrorType.BAD_REQUEST + "\n" + che.getMessage());
		}
		catch(Exception e)
		{
			
			logger.severe(e.toString());
		}
		finally 
		{
			Exceuted = Validation.errorSchedData;
			
			Validation.errorSchedData = "";
			Validation.validScheds.clear();
			logger.info("Finished WORK SCHEDULE...");
		}
		
		return Exceuted;
    
		
	}
	
	
	public void deleteEmployeeSchedules(ArrayList<Schedule> sched) throws CustomizeHandledException{
		
		logger.info("Starting ATTENDANCE ABSENCE...");
		  
		LocationSiteCapacityDao lscDao = new LocationSiteCapacityDao();
		
		ArrayList<Schedule> items = new ArrayList<Schedule>();
		
		for(Schedule e : sched) {
			if(e.getEmployee() != null  &&  !e.getEmployee().isEmpty()  && e.getEmpstartdt() != null && e.getEmpenddt() != null) {
				items.add(e);
			}
			
			
		}
		
		
		try
		{
			try 
			{
				if(!items.isEmpty()) {
					
					int scheduledPoolSize = 4;
					if(System.getProperty("Thread_Count") != null && !System.getProperty("Thread_Count").isEmpty()) {
						scheduledPoolSize = Integer.parseInt(System.getProperty("Thread_Count"));
					}
					logger.info("Creating default Scheduled Executor Service [poolSize =" + String.valueOf(scheduledPoolSize) + "]");
					ScheduledExecutorService scheduledServ = Executors.newScheduledThreadPool(scheduledPoolSize);
					
					for(Schedule item : items)
					{
						Worker worker = new Worker();
				    	worker.setInput(item);
				    	worker.setWork(false);
				    	scheduledServ.execute(worker);
					}
					
					scheduledServ.shutdown();
				    scheduledServ.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				}
			}catch(Exception che) {
				throw new CustomizeHandledException(che.getMessage());
			}finally 
			{
				if(lscDao.getCon() != null && !lscDao.getCon().isClosed())
					lscDao.getCon().close();
			}
		}catch(Exception e) {
			logger.severe("Exception: " + e.toString());
		}
		
		logger.info("Finished ATTENDANCE ABSENCE...");
		
	}
	
	
	
	
	
	public String setGroup(String costCenter, String group) throws Exception{
		String Exceuted = "OK";
		
		String query = "INSERT INTO SITE_GROUP_MASTER (cost_centre, \"GROUP\") VALUES (?, ?)";
		
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			ps.setString(1, costCenter);
			ps.setString(2, group);
			
			ps.executeUpdate();
		
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
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
				logger.severe("An error ocurrer trying to close the statement");
				
			}
		}
		
		
		return Exceuted;
		
		
	}
	
	public String deleteGroup( String group) throws Exception{
		String Exceuted = "OK";
		

		
		String query = "DELETE SITE_GROUP_MASTER where \"GROUP\" = ?";
		
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			ps.setString(1, group);
			ps.executeUpdate();		
			
			
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
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
				logger.severe("An error ocurrer trying to close the statement");
				
			}
		}
		
		
		return Exceuted;
		
		
		
	}
	
	
	public String getGroupByCostCenter( String costcenter) throws Exception{
		
		ArrayList<String> groups = new ArrayList<String>();
		
		String query = "", group = "";
		if(costcenter != null && !costcenter.isEmpty()) {
			query = " Select \"GROUP\", cost_centre FROM SITE_GROUP_MASTER where cost_centre = ?";
		}else {
			query = " Select \"GROUP\", cost_centre FROM SITE_GROUP_MASTER";
		}
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			if(costcenter != null && !costcenter.isEmpty()) {
				ps.setString(1, costcenter);
			}
			
			ResultSet rs = ps.executeQuery();		
			
			if (rs != null) 
			{
				while (rs.next()) 
				{
					
				groups.add("GROUP: "+rs.getString(1) + " Cost Centre: " +rs.getString(2) );

				}
			}
			rs.close();
			
			
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
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
				logger.severe("An error ocurrer trying to close the statement");
				
			}
		}
		
		for(String g : groups) {
			group = group + g +"\n";
			
		}
		
		return group;
		
	}
	
	public String getGroupByCostCenterEmployee( String costcenter) throws Exception{
		
		
		String query = "", group = "";
		query = " Select \"GROUP\" FROM SITE_GROUP_MASTER where cost_centre = ?";
		
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			if(costcenter != null && !costcenter.isEmpty()) {
				ps.setString(1, costcenter);
			}
			
			ResultSet rs = ps.executeQuery();		
			
			if (rs != null) 
			{
				while (rs.next()) 
				{
					
				 return rs.getString(1);

				}
			}
			rs.close();
			
			
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
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
				logger.severe("An error ocurrer trying to close the statement");
				
			}
		}
		return group;
		
	}
	
	
	public String getCostCenterByEmployee(String employee) throws Exception
	{		
		logger.info("Preparing the data ... employee = " + employee);
		
		String costCenter = "";
		
		String query = " SELECT cost_center FROM RELATION_MASTER where relation_code = '"+employee+"' and RELATION_TRANSACTION = 'EMPLOYEE' and rownum = 1 ";
			
		PreparedStatement ps = null;
			
		
		try
		{
			logger.info("Preparing the connection and the data to get the cost center...");
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();		
			
			if(rs.next())
			{
				
				return rs.getString(1);
			}
			
			rs.close();
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
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
				logger.severe("An error ocurrer trying to close the statement");
				throw new Exception("A SQLException occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
			}
		}
		
		return costCenter;
	}
	
	public String[] getLocationSiteByGroup(String group) throws Exception
	{		
		logger.info("Preparing the data ... group = " + group);
		
		String locationSite[] = new String[2];
		
		String query = " SELECT LOCATION,SITE FROM EMPLOYEE_SCHEDULE_GROUP where \"GROUP\" = '"+group+"'";
			
		PreparedStatement ps = null;
			
		
		try
		{
			logger.info("Preparing the connection and the data to get the site, location...");
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			ResultSet rs = ps.executeQuery();		
			
			if(rs.next())
			{
				locationSite[0]= rs.getString(1);
				locationSite[1]= rs.getString(2);
			}
			
			rs.close();
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + e.getMessage());
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
				logger.severe("An error ocurrer trying to close the statement");
				throw new Exception("A SQLException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + e.getMessage());
			}
		}
		
		return locationSite;
	}
	
	
	private <T> void insertData( T data) 
	{
		try 
		{	
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
				em.merge(data);
			em.getTransaction().commit();
		}catch (Exception e)
		{
			logger.severe(e.toString());
		}
	}
	
	public boolean lockAvailable(String notificationType)
	{
		
		//em.getTransaction().begin();
		InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType).getSingleResult();
		em.refresh(lock);
		//logger.info("lock " + lock.getLocked());
		if(lock.getLocked().intValue() == 1)
		{				
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime locked = LocalDateTime.ofInstant(lock.getLockedDate().toInstant(), ZoneId.systemDefault());
			Duration diff = Duration.between(locked, today);
			if(diff.getSeconds() >= lock.getMaxLock().longValue())
			{
				lock.setLocked(new BigDecimal(1));
				insertData(lock);
				return true;
			}
			return false;
		}
		else
		{
			lock.setLocked(new BigDecimal(1));
			insertData(lock);
			return true;
		}
		
	}
	
	
	public void lockTable(String notificationType)
	{
		em.getTransaction().begin();
		InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType).getSingleResult();
		lock.setLocked(new BigDecimal(1));
		//logger.info("lock " + lock.getLocked());
		
		lock.setLockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			
			logger.info(e.getMessage());
			//e.printStackTrace();
		}
		lock.setCurrentServer(address.getHostName());
		//em.lock(lock, LockModeType.NONE);
		em.merge(lock);
		em.getTransaction().commit();
	}
	
	public void unlockTable(String notificationType)
	{
		em.getTransaction().begin();
		
		InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType).getSingleResult();
		lock.setLocked(new BigDecimal(0));
		//logger.info("lock " + lock.getLocked());
		
		lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
		//em.lock(lock, LockModeType.NONE);
		em.merge(lock);
		em.getTransaction().commit();
	}
	
}
