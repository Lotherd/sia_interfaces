package trax.aero.interfaces;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.xml.bind.JAXBException;

import trax.aero.model.InterfaceLockMaster;

import trax.aero.pojo.STAFF;
import trax.aero.pojo.STAFFMasterResponse;

public interface IImportCertificationData {

	public String importCert(STAFF input);
	public STAFFMasterResponse getResponseStaff();
	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	public void unlockTable(String notificationType);
	
}
