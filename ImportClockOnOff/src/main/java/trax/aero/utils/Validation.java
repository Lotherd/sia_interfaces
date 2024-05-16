package trax.aero.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import trax.aero.controller.ImportClockOnOffController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Import;
import trax.aero.pojo.Punch;
import trax.aero.pojo.Ta;




public class Validation 
{	
	static Logger logger = LogManager.getLogger("ImportClockOnOff_I29");
	
	static public String validatePunchesEntries(Ta punch)
	{
		String isOkay = "OK";
		
		if( punch.getCostCentre() == null || punch.getCostCentre().isEmpty()) {
			isOkay = "Error: Cost Centre is null or empty";
			ImportClockOnOffController.addError(isOkay);
		}
			
		if( punch.getSeqNo() == null ) {
			isOkay = "Error: Sequence Number is null";
			ImportClockOnOffController.addError(isOkay);
		}
		
		if( punch.getStatus() == null || punch.getStatus().isEmpty()) {
			isOkay = "Error: Status is null or empty";
			ImportClockOnOffController.addError(isOkay);
		}else if(!punch.getStatus().equalsIgnoreCase("N") && !punch.getStatus().equalsIgnoreCase("R")) {
			isOkay ="Status: "+ punch.getStatus() + " Error: Status is not N or R";
			ImportClockOnOffController.addError(isOkay);
		}
		
		if( punch.getMsgType() == null || punch.getMsgType().isEmpty()) {
			isOkay = "Error: Message Type is null or empty";
			ImportClockOnOffController.addError(isOkay);
		}else if(!punch.getMsgType().equalsIgnoreCase("ClockIn") && !punch.getMsgType().equalsIgnoreCase("ClockOut")) {
			isOkay ="Message Type: "+ punch.getMsgType() + " Error: Message Type is not ClockIn or ClockOut";
			ImportClockOnOffController.addError(isOkay);
		}
		
		if((punch.getClkInTime() == null || punch.getClkInTime().isEmpty() || !isValidDate(punch.getClkInTime())) && punch.getMsgType().equalsIgnoreCase("ClockIn")) {
			isOkay = punch.getClkInTime() +  " Error: Clock in Time is null or empty";
			ImportClockOnOffController.addError(isOkay);
		} 
		
		if((punch.getClkOutTime() == null || punch.getClkOutTime().isEmpty() || !isValidDate(punch.getClkOutTime())) && punch.getMsgType().equalsIgnoreCase("ClockOut")) {
			isOkay = punch.getClkInTime() + " Error: Clock Out Time is null or empty";
			ImportClockOnOffController.addError(isOkay);
		} 
		
		if(punch.getStaffNo() != null && !punch.getStaffNo().isEmpty()) {

			PreparedStatement ps = null;
			ResultSet rs = null;
			String query = "SELECT RELATION_CODE FROM RELATION_MASTER WHERE RELATION_CODE = ? AND RELATION_TRANSACTION = 'EMPLOYEE'";
			Connection con = null;
			try {
				if(con == null || con.isClosed())
				{
					con = DataSourceClient.getConnection();
					logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
				}
				
				ps = con.prepareStatement(query);
				ps.setString(1, punch.getStaffNo());
				
				rs = ps.executeQuery();
				
				if(!rs.next())
				{
					isOkay ="Employee: "+punch.getStaffNo()+ " Error: Employee does not exist";
					logger.severe(isOkay);
					ImportClockOnOffController.addError(isOkay);
				}
			} catch(SQLException | CustomizeHandledException sqle){
				logger.severe("An error occured when Validating User: Employee = " + punch.getStaffNo());
				isOkay = "An error occured when Validating User: Employee = " + punch.getStaffNo();
				ImportClockOnOffController.addError(isOkay);
				
			} catch (Exception e) {
				logger.severe("An error occured when Validating User: Employee = " + punch.getStaffNo());
				isOkay = "An error occured when Validating User: Employee = " + punch.getStaffNo();
				ImportClockOnOffController.addError(isOkay);
				
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
		} else {
			isOkay = "Error: Staff number is null or empty";
			ImportClockOnOffController.addError(isOkay);
		}
		
		return isOkay;
	}
	
	static public void throwValidatePunches(Import punch) throws Exception
	{	
		String validate = validatePunchesEntries(punch.getMessage().getTa());
		if(!validate.equalsIgnoreCase("OK"))
		{
			logger.severe(validate);
			throw new Exception(validate );
		}
	}
	
	public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (Exception pe) {
            return false;
        }
        return true;
    }

}
