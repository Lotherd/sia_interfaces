package trax.aero.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Schedule;

public class WorkerValidation implements Runnable{

	Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	
	private Schedule s = null;
	
	public WorkerValidation() {
		
	}
	
	public void run() 
	{
		boolean isOkayItem = true;
		String errorType = "";
		if(s.getGroup() == null || s.getGroup().trim().isEmpty() || s.getEmployee() == null || s.getEmployee().trim().isEmpty() || s.getLocation() == null || s.getLocation().trim().isEmpty() || s.getSite() == null || s.getSite().trim().isEmpty() || s.getEmpstartdt() == null || s.getEmpenddt() == null)
		{
			if(!"".equalsIgnoreCase(errorType))
			{
				errorType +="/";
			}
			if(!Validation.header.contains("\nException(s) occured with Invalid or Missing Schedule Field."))
			{
				Validation.header += "\nException(s) occured with Invalid or Missing Schedule Field.";
			}
			errorType += "Invalid or Missing Schedule Field";
			Validation.isOkay = false;
			isOkayItem = false;
		}
		
		if(s.getSite() == null || s.getSite().isEmpty() || s.getLocation() == null || s.getLocation().isEmpty())
		{
			if(!"".equalsIgnoreCase(errorType))
			{
				errorType +="/";
			}
			errorType += "Invalid Location Site";
			Validation.isOkay = false;
			isOkayItem = false;
			if(!Validation.header.contains("\nException(s) occured during Location/Site Validation.")) {
				Validation.header += "\nException(s) occured during Location/Site Validation.";
			}
		}
		
		if(!validateEmployee(s.getEmployee()))
		{
			if(!Validation.header.contains("\nException(s) occured during Employee Validation.")) {
				Validation.header += "\nException(s) occured during Employee Validation.";
			}
			Validation.isOkay = false;
			isOkayItem = false;
			if(!"".equalsIgnoreCase(errorType))
			{
				errorType +="/";
			}
			errorType += "Invalid Employee";
		}
		
		if(s.getEmpstartdt() == null || s.getEmpenddt() == null ||  !validateScheduleDates(s.getEmpstartdt(), s.getEmpenddt()))
		{
			if(!Validation.header.contains("\nException(s) occured during Schedule Date Validation."))
			{
				Validation.header += "\nException(s) occured during Schedule Date Validation.";
			}
			Validation.isOkay = false;
			isOkayItem = false;
			if(!"".equalsIgnoreCase(errorType))
			{
				errorType +="/";
			}
			errorType += "Invalid Schedule Date";
		}
		if(!"".equalsIgnoreCase(errorType)) 
		{
			Validation.errorData += "{Error Type: "+ errorType +", group: " + s.getGroup() + ", employee: " + s.getEmployee() + ", location: " + s.getLocation() + ", site: " + s.getSite() + ", Empstartdt: " + s.getEmpstartdt() + ", Empenddt:" + s.getEmpenddt() + " }\n";	
		}
		if(isOkayItem) {
			Validation.valid.add(s);
		}
	}
	
	
	private boolean validateEmployee(String empID)
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

	public Schedule getSh() {
		return s;
	}

	public void setSh(Schedule sh) {
		this.s = sh;
	}
	
	public boolean validateScheduleDates(java.util.Date startDate, java.util.Date endDate)
	{
		boolean isOkay = false;
		
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		
		if(start.isBefore(end))
			isOkay = true;
		
		return isOkay;
	}
}
