package trax.aero.interfaces;

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

import javax.xml.bind.JAXBException;

import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.PicklistDistribution;
import trax.aero.outbound.DTTRAXI41ACK4067;
import trax.aero.outbound.Orders;

import trax.aero.util.DataSourceClient;

public interface IMaterialData {

	public String setSite(String site, String recipient) throws Exception;
	
	public String deleteSite( String site) throws Exception;
	
	
	public String getSite( String site) throws Exception;
	
	public String sendComponent() throws JAXBException;

	public void acceptReq(DTTRAXI41ACK4067 reqs);

	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	public void unlockTable(String notificationType);
	
}
