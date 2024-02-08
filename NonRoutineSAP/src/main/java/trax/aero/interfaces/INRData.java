package trax.aero.interfaces;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.xml.bind.JAXBException;

import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.DTTRAXI37I38ACK4069;

public interface INRData {

	public String sendNRData() throws JAXBException ;
	
	public void closeNr(DTTRAXI37I38ACK4069 orders);
	
	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	public void unlockTable(String notificationType);
	
}
