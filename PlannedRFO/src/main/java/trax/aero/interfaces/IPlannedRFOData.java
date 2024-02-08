package trax.aero.interfaces;

import trax.aero.pojo.MT_TRAX_I61;

public interface IPlannedRFOData {

	public String insertRFO(MT_TRAX_I61 input);
	
		
	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	
	public void unlockTable(String notificationType);
	
}
