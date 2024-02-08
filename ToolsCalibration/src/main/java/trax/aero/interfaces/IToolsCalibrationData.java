package trax.aero.interfaces;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.ToolsCalibrationMaster;

public interface IToolsCalibrationData {

	public String updateTool(ToolsCalibrationMaster toolsCalibrationMaster);
		
	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	
	public void unlockTable(String notificationType);
	
}
