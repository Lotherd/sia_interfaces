package trax.aero.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.EmployeeSchedule;
import trax.aero.model.EmployeeScheduleGroup;
import trax.aero.pojo.Group;
import trax.aero.pojo.Schedule;
import trax.aero.util.DataSourceClient;
import trax.aero.util.ErrorType;




public class WorkScheduleDao
{
	private EmployeeScheduleGroup esg;
	private EmployeeSchedule es;
	private Connection con;
	Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	
	public Connection getCon() {
		return con;
	}
	
	public WorkScheduleDao()
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
			logger.info("An error occured getting the status of the connection");
			e.printStackTrace();
		}
		catch (CustomizeHandledException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
	}
	
	public void addEmployeeScheduleGroup(Schedule sched) throws CustomizeHandledException
	{
		logger.info("Inserting employee schedule group..." + sched.getGroup());
		
		esg = new EmployeeScheduleGroup();
		esg.setGroup(sched.getGroup());
		esg.setLocation(sched.getLocation());
		esg.setSite(sched.getSite());
		esg.setCreatedBy("TRAXIFACE");
		esg.setModifiedBy("TRAXIFACE");
		
		
				
		String query = 	" BEGIN " + 
						"  INSERT INTO employee_schedule_group (\"GROUP\", LOCATION, SITE, CREATED_BY, CREATED_DATE, MODIFIED_BY, MODIFIED_DATE) VALUES (?, ?, ?, ?, sysdate, ?, sysdate); " + 
						" EXCEPTION " + 
						"  WHEN DUP_VAL_ON_INDEX THEN " + 
						"    UPDATE employee_schedule_group " + 
						"    SET    LOCATION = ?, site = ?, modified_by = ?, modified_DATE = sysdate " + 
						"    WHERE \"GROUP\" = ?; " + 
						"END; ";
		
		PreparedStatement ps = null;
		
		try
		{
			logger.info("Preparing the connection and the data to insert the group..." + sched.getGroup());
			
			if(this.con == null || this.con.isClosed())
			{
				this.con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!this.con.isClosed()));
			}
			
			
			ps = con.prepareStatement(query);
			
			ps.setString(1, esg.getGroup());
			ps.setString(2, esg.getLocation());
			ps.setString(3, esg.getSite());
			ps.setString(4, esg.getCreatedBy());
			ps.setString(5, esg.getModifiedBy());
			
			ps.setString(6, esg.getLocation());
			ps.setString(7, esg.getSite());
			ps.setString(8, esg.getModifiedBy());
			ps.setString(9, esg.getGroup());
			
			ps.executeUpdate();	
			logger.info("The group " + sched.getGroup() + " has been inserted...");
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurrer trying to insert the group with the values: group = " + esg.getGroup() + "; location = " + esg.getLocation() + "; site = " + esg.getSite() + "; createdBy = " + esg.getCreatedBy());
		throw new CustomizeHandledException("A SQLException occurred executing the query of employee schedule group. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" +sched.toString() + "\n}" + "\nmessage: " + sqle.getMessage() ) ;
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to insert the group with the values: group = " + esg.getGroup() + "; location = " + esg.getLocation() + "; site = " + esg.getSite() + "; createdBy = " + esg.getCreatedBy() + ". Some values could be null....");
			throw new CustomizeHandledException("A NullPointerException occurred executing the query of employee schedule group. Some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + sched.toString() + "\n}"  + "\nmessage: " + npe.getMessage());
			
		}
		catch (Exception e) 
		{
			logger.info("An error ocurrer trying to insert the group with the values: group = " + esg.getGroup() + "; location = " + esg.getLocation() + "; site = " + esg.getSite() + "; createdBy = " + esg.getCreatedBy());
			throw new CustomizeHandledException("An Exception ocurrer trying to insert the group. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" + sched.toString() + "\n}"  + "\nmessage: " + e.getMessage());
		}
		finally 
		{			
			try 
			{
				if (ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurrer trying to close the statement");
				throw new CustomizeHandledException("A SQLException occurred executing the query of employee schedule group. " + "\n error: " + ErrorType.BAD_REQUEST + "\n" + "data: {" + sched.toString() + "\n}"  + "\nmessage: " + e.getMessage());
			}
		}
		logger.info("Inserting employee schedule group.....done..........");
	}

	public void addEmployeeSchedule(Schedule sched) throws CustomizeHandledException
	{
		logger.info("Inserting employee schedule....");
		
		es = new EmployeeSchedule();
		es.setEmployee(sched.getEmployee());
		es.setGroup(sched.getGroup());
		es.setLocation(sched.getLocation());
		es.setSite(sched.getSite());
		es.setCreatedBy("TRAXIFACE");
		es.setModifiedBy("TRAXIFACE");
		
		PreparedStatement ps = null, ps1 = null, ps2 = null;
		ResultSet rs = null;
		try
		{
			logger.info("Checking if the employee already exist on the table..." + sched.getEmployee());
			//The employee already exist on this table?
			String query = " select nvl(employee, '') as empl, nvl(\"GROUP\", '') as gr from employee_schedule where employee = ? ";
			String employee = "", group = "";
			
			ps = con.prepareStatement(query);
			ps.setString(1, sched.getEmployee());
			
			rs = ps.executeQuery();
			if(rs.next())
			{
				logger.info("Getting the group and employee if they already exist...");
				employee = rs.getString(1);
				group = rs.getString(2);
			}
			
			if(employee == null || employee.isEmpty() )
			{
				query = " INSERT INTO employee_schedule (EMPLOYEE, LOCATION, SITE, \"SELECT\", \"GROUP\", CREATED_BY, CREATED_DATE) VALUES (?, ?, ?, ?, ?, ?, sysdate) ";
				
				logger.info("Preparing the connection and the data to insert the employee and the group...");
				
				try
				{
				ps1 = con.prepareStatement(query);
				ps1.setString(1, es.getEmployee());
				ps1.setString(2, es.getLocation());
				ps1.setString(3, es.getSite());
				ps1.setString(4, es.getSelect());
				ps1.setString(5, es.getGroup());
				ps1.setString(6, es.getCreatedBy());
												
				ps1.executeQuery();
				}
				catch(SQLException sqle)
				{
					
				}
				logger.info("The employee and group has been inserted...");
			}
			else
				if(employee != null && !employee.isEmpty() && !group.equals(es.getGroup()))
				{
					query = " UPDATE employee_schedule " + 
							"    SET    \"GROUP\" = ?, LOCATION = ?, site = ?, modified_by = ?, modified_DATE = sysdate " + 
							"    WHERE EMPLOYEE = ? ";
					
					logger.info("Preparing the connection and the data to update the employee and the group...");
					ps2 = con.prepareStatement(query);
										
					ps2.setString(1, es.getGroup());
					ps2.setString(2, es.getLocation());
					ps2.setString(3, es.getSite());
					ps2.setString(4, es.getModifiedBy());
					
					ps2.setString(5, es.getEmployee());					
					
					ps2.executeQuery();
					logger.info("The employee and group has been updated...");
				}
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurrer trying to insert or update the amployee and the group with the values: employee = " + es.getEmployee() + "; group = " + es.getGroup() + "; location = " + es.getLocation() + "; site = " + es.getSite() + "; createdBy = " + es.getCreatedBy() + "; modifiedBy = " + es.getModifiedBy());
			throw new CustomizeHandledException("A SQLException ocurred trying to insert or update the amployee and the group. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + sched.toString() + "\n}" + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to insert or update the amployee and the group with the values: employee = " + es.getEmployee() + "; group = " + es.getGroup() + "; location = " + es.getLocation() + "; site = " + es.getSite() + "; createdBy = " + es.getCreatedBy() + "; modifiedBy = " + es.getModifiedBy() + ". Some values could be null....");
			throw new CustomizeHandledException("A NullPointerException ocurrer trying to insert or update the amployee and the group because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + sched.toString() + "\n}" + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.info("An error ocurrer trying to insert or update the employee and the group with the values: employee = " + es.getEmployee() + "; group = " + es.getGroup() + "; location = " + es.getLocation() + "; site = " + es.getSite() + "; createdBy = " + es.getCreatedBy() + "; modifiedBy = " + es.getModifiedBy());
			throw new CustomizeHandledException("An Exception ocurrer trying to insert or update the employee and the group. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" + sched.toString() + "\n}" + "\nmessage: " + e.getMessage());
		}
		finally 
		{							
			try 
			{
				if (ps != null && !ps.isClosed())
					ps.close();
				
				if (ps1 != null && !ps1.isClosed())
					ps1.close();
				
				if (ps2 != null && !ps2.isClosed())
					ps2.close();
				
				if (rs != null && !rs.isClosed())
					rs.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurrer trying to close the statement");
				throw new CustomizeHandledException("A SQLException ocurred trying to close the statement of the query of employee schedule. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" + sched.toString() + "\n}" + "\nmessage: " + e.getMessage());
			}
		}
		logger.info("Inserting employee schedule.....done.....");
	}
	
	public ArrayList<Group> getWorkScheduleList() throws CustomizeHandledException
	{
		ArrayList<Group> gList = new ArrayList<Group>();
		
		logger.info("Getting employee schedule group list...");
						
		String query = 	" SELECT * FROM employee_schedule_group ";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				Group e = new Group();
				
				e.setGroup(rs.getString("GROUP"));
				e.setDescription(rs.getString("DESCRIPTION"));
				e.setLocation(rs.getString("LOCATION"));
				e.setSite(rs.getString("SITE"));
				
				gList.add(e);
			}
			
			
		}
		catch (SQLException sqle) 
		{
			logger.info("An error ocurrer trying to get the group list with the values.");
		throw new CustomizeHandledException("A SQLException occurred executing the query of employee schedule group list. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "\nmessage: " + sqle.getMessage()) ;
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error ocurrer trying to get the group list with the values. Some values could be null....");
		throw new CustomizeHandledException("A NullPointerException occurred executing the query of employee schedule group list. Some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			
		}
		catch (Exception e) 
		{
			logger.info("An error ocurrer trying to get the group list with the values.");
		throw new CustomizeHandledException("An error ocurrer trying to get the group list with the values. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
		}
		finally 
		{			
			try 
			{
				if (ps != null && !ps.isClosed())
					ps.close();
				
				if (rs != null && !rs.isClosed())
					rs.close();
				
				if (con != null && !con.isClosed())
					con.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurrer trying to close the statement");
			throw new CustomizeHandledException("A SQLException occurred executing the query of employee schedule group. " + "\n error: " + ErrorType.BAD_REQUEST  + "\nmessage: " + e.getMessage());
			}
		}
		logger.info("Getting employee schedule group.....done..........");
	
		
		return gList;
	}
	
	public String getHealthCheck() throws CustomizeHandledException
	{
		
		logger.info("Querying AC Master...");
						
		String query = 	" SELECT AC FROM AC_MASTER WHERE ROWNUM = 1 ";
		
		String ac = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			rs = ps.executeQuery();
			
			while(rs.next())
			{
								
				ac = rs.getString("AC");

			}
			
		}
		catch (SQLException sqle) 
		{
			logger.info("An error occurred trying to get AC.");
		throw new CustomizeHandledException("A SQLException occurred executing the query of employee schedule group list. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "\nmessage: " + sqle.getMessage()) ;
		}
		catch (NullPointerException npe) 
		{
			logger.info("An error occurred trying to get AC. Some values could be null....");
		throw new CustomizeHandledException("A NullPointerException occurred executing the query of employee schedule group list. Some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			
		}
		catch (Exception e) 
		{
			logger.info("An error occurred trying to get AC.");
		throw new CustomizeHandledException("An error ocurrer trying to get the group list with the values. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
		}
		finally 
		{			
			try 
			{
				if (ps != null && !ps.isClosed())
					ps.close();
				
				if (rs != null && !rs.isClosed())
					rs.close();
				
				if (con != null && !con.isClosed())
					con.close();
			} 
			catch (SQLException e) 
			{ 
				logger.info("An error ocurrer trying to close the statement");
			throw new CustomizeHandledException("A SQLException occurred executing the query of employee schedule group. " + "\n error: " + ErrorType.BAD_REQUEST  + "\nmessage: " + e.getMessage());
			}
		}
		logger.info("Querying AC Master.....done..........");
	
		
		return ac;
	}
}
