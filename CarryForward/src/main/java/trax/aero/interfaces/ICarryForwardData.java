package trax.aero.interfaces;

import java.sql.Connection;

import trax.aero.pojo.CarryForward;
import trax.aero.pojo.MT_TRAX_RCV_I74_4070_RES;

public interface ICarryForwardData {

	public String Button(CarryForward b) throws Exception;
	 public void markTransaction(MT_TRAX_RCV_I74_4070_RES Inbound) throws Exception
		;
	
	 public String print(String wo,String task_card , byte[] bs, String formNo, String formLine) throws Exception ;
			
			
	 
	public Connection getCon();
	
	public void lockTable(String notificationType);
	
	public void unlockTable(String notificationType);
	
}
