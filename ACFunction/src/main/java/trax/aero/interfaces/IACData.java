package trax.aero.interfaces;

import trax.aero.pojo.ACMaster;

public interface IACData {

	public String insertACMaster(ACMaster aCMaster);
	
	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	public void unlockTable(String notificationType);
	
}
