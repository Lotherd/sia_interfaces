package trax.aero.data;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import trax.aero.controller.UpdateTaskcardStatusController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.I74_Request;
import trax.aero.pojo.I74_Response;
import trax.aero.utils.DataSourceClient;


/*
SELECT 
WTC.REFERENCE_TASK_CARD as Order_number,
WTC.COMPLETED_ON,
wtc.completed_on_hour,
wtc.completed_on_minute,
WTC.STATUS as TRAXStatus,
WTC.STATUS_CATEGORY  TRAXStatusCategory,
WTC.REMARKS ReasonForTECO_reversal,
WTC.TASK_CARD TaskCard
FROM
WO_TASK_CARD WTC
WHERE 
WTC.INTERFACE_MODIFIED_DATE IS NULL;
 
UPDATE
WO_TASK_CARD
SET
WO_TASK_CARD.INTERFACE_MODIFIED_DATE = sysdate
WHERE
WO_TASK_CARD.INTERFACE_MODIFIED_DATE IS NULL AND
WO_TASK_CARD.REFERENCE_TASK_CARD = ?
 
 */


public class UpdateTaskcardStatusData {
	EntityManagerFactory factory;
	EntityManager em;
	String exceuted;
	private Connection con;
	
	final String MaxRecord = System.getProperty("UpdateTaskCardStatus_MaxRecord");
	//public InterfaceLockMaster lock;
	Logger logger = LogManager.getLogger("UpdateTaskCardStatus_I74");

	
	public UpdateTaskcardStatusData(String mark)
	{
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
			UpdateTaskcardStatusController.addError(e.toString());
			
		}
		catch (CustomizeHandledException e1) {
			
			UpdateTaskcardStatusController.addError(e1.toString());
			
		} catch (Exception e) {
			
			UpdateTaskcardStatusController.addError(e.toString());
			
		}
			
	}
	
	
	public UpdateTaskcardStatusData()
	{
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
			UpdateTaskcardStatusController.addError(e.toString());
			
		}
		catch (CustomizeHandledException e1) {
			
			UpdateTaskcardStatusController.addError(e1.toString());
			
		} catch (Exception e) {
			
			UpdateTaskcardStatusController.addError(e.toString());
			
		}
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();		
	}
	
	public Connection getCon() {
		return con;
	}
	
	
	public String markTransaction(I74_Response request) throws Exception
	{
		//setting up variables
		exceuted = "OK";
		
		String SQLcode53 =
		"UPDATE WO_TASK_CARD SET WO_TASK_CARD.INTERFACE_MODIFIED_DATE = NULL, WO_TASK_CARD.INTERFACE_STEP = 'D' WHERE WO_TASK_CARD.INTERFACE_MODIFIED_DATE IS NOT NULL AND WO_TASK_CARD.REFERENCE_TASK_CARD = ?";
		
		String SQLcode51 =
				"UPDATE WO_TASK_CARD SET WO_TASK_CARD.INTERFACE_MODIFIED_DATE = NULL, WO_TASK_CARD.INTERFACE_STEP = 'F' WHERE WO_TASK_CARD.INTERFACE_MODIFIED_DATE IS NOT NULL AND WO_TASK_CARD.REFERENCE_TASK_CARD = ?";
				
		
		
		PreparedStatement pstmt1 = null;  

		try 
		{
			String errorCode = request.getErrorCode();
			
			if (errorCode != null && errorCode.equalsIgnoreCase("53")) {
				pstmt1 = con.prepareStatement(SQLcode53);

			}else {
				pstmt1 = con.prepareStatement(SQLcode51);

			}
			
			
			pstmt1.setString(1, request.getOrderNumber());
			pstmt1.executeUpdate();
				
			
		}
		catch (Exception e) 
        {
			UpdateTaskcardStatusController.addError(e.toString());
            
            exceuted = e.toString();
            
            logger.severe(exceuted);
            
		}finally {
			if(pstmt1 != null && !pstmt1.isClosed())
				pstmt1.close();

		}
		
		return exceuted;
		
	}
	
	public ArrayList<I74_Request> getTaskCards() throws Exception
	{
		//setting up variables
		exceuted = "OK";
		String time = "hhmm00";
		Integer min = 0;
		Date Date;
		String currentDate;
		Format formatter = new SimpleDateFormat("yyyyMMdd");
		String hour;
		String minute;
		
		
		ArrayList<I74_Request> list = new ArrayList<I74_Request>();
		
		String sql =
		"SELECT WTC.REFERENCE_TASK_CARD as Order_number,WTC.COMPLETED_ON as CompletedDate,PKG_INTERFACE.GETXMLNUMBERSTRING(wtc.completed_on_hour) as completedHour,"
		+ "PKG_INTERFACE.GETXMLNUMBERSTRING(wtc.completed_on_minute) as completedMinute, WTC.STATUS as TRAXStatus,WTC.STATUS_CATEGORY  TRAXStatusCategory,WTC.REMARKS ReasonForTECO_reversal,"
		+ "WTC.TASK_CARD TaskCard,WTC.WO,WTC.modified_by,WTC.modified_date FROM WO_TASK_CARD WTC WHERE WTC.INTERFACE_MODIFIED_DATE IS NOT NULL AND WTC.INTERFACE_TRANSFERRED_DATE IS NOT NULL AND WTC.INTERFACE_STEP IS NULL AND WTC.REFERENCE_TASK_CARD IS NOT NULL  AND WTC.STATUS IN ('OPEN','CLOSED' , 'CANCEL') AND (WTC.non_routine = 'N' OR WTC.non_routine = 'Y' OR WTC.non_routine IS NULL)";
		
		if(MaxRecord != null && !MaxRecord.isEmpty()) {
			sql=  "SELECT *	FROM ( " + sql;
		}
		
		if(MaxRecord != null && !MaxRecord.isEmpty()) {
			sql= sql + "  )WHERE ROWNUM <= ?";		
		}
				
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;

		try 
		{
			pstmt1 = con.prepareStatement(sql);
			if((MaxRecord != null && !MaxRecord.isEmpty())) {
				pstmt1.setString(1, MaxRecord);
			}
			rs1 = pstmt1.executeQuery();

			if (rs1 != null) 
			{
				while (rs1.next()) // LOOP EACH INV LINE
				{
					 logger.info("Processing WO Task Card: " + rs1.getString(8) + " WO: " + rs1.getString(9) );
					I74_Request Inbound = new I74_Request();
						
					if(rs1.getString(1) != null && !rs1.getNString(1).isEmpty()) {
						Inbound.setOrderNumber(rs1.getString(1));
					}
					else {
						Inbound.setOrderNumber("");
					}
					
					if(rs1.getDate(2) != null) {
						Date = rs1.getDate(2);
						currentDate = formatter.format(Date);
						Inbound.setReferenceDate(currentDate);
					}else {
						Inbound.setReferenceDate("");
					}
					
					if(rs1.getString(3) != null && !rs1.getNString(3).isEmpty()) 
					{
						hour = String.format("%02d", Integer.parseInt(rs1.getString(3)));
					}
					else 
					{
						hour = "00";
					}
					
					if(rs1.getString(4) != null && !rs1.getNString(4).isEmpty()) 
					{
						minute = String.format("%02d", Integer.parseInt(rs1.getString(4)));	
					}
					else 
					{
						minute = "00";
					}
					time=time.replaceAll("hh", hour);
					time=time.replaceAll("mm", minute);
					Inbound.setReferenceTime(time);
					
					if(rs1.getString(5) != null && !rs1.getNString(5).isEmpty()) {
						Inbound.setTRAXStatus(rs1.getString(5));
					}
					else {
						Inbound.setTRAXStatus("");
					}
					
					
					if(rs1.getString(6) != null && !rs1.getNString(6).isEmpty()) {
						Inbound.setTRAXStatusCategory(rs1.getString(6));
					}
					else {
						Inbound.setTRAXStatusCategory("");
					}
					
					if(Inbound.getTRAXStatus().equals("OPEN")) {
						Inbound.setReasonForTECO_reversal("REOPENED BY " +rs1.getString(10) + " Date : "+ rs1.getDate(11).toString());
					}else {
						Inbound.setReasonForTECO_reversal("");
					}
					
					list.add(Inbound);	
					
				}
			}
			
			
		}
		catch (Exception e) 
        {
			UpdateTaskcardStatusController.addError(e.toString());
            
            exceuted = e.toString();
            logger.severe(exceuted);
            throw new Exception("Issue found");
		}finally {
			if(rs1 != null && !rs1.isClosed())
				rs1.close();
			if(pstmt1 != null && !pstmt1.isClosed())
				pstmt1.close();
		}
		return list;
	}
	
	public Map<String,String> getStatus(String orderNumber)
	{
		Map<String,String> map = new HashMap<String,String>();
		
		String sqlDate =
		"SELECT STATUS, STATUS_CATEGORY FROM WO_TASK_CARD WHERE WO_TASK_CARD.REFERENCE_TASK_CARD = ? AND (non_routine = 'N' OR non_routine = 'Y' OR non_routine IS NULL)";
		
		PreparedStatement pstmt2 = null; 
		ResultSet rs1 = null;
		try 
		{
			
				pstmt2 = con.prepareStatement(sqlDate);
				
				pstmt2.setString(1, orderNumber);
				
				rs1 =  pstmt2.executeQuery();
				
				

				if (rs1 != null) 
				{
					while (rs1.next()) // LOOP EACH INV LINE
					{
						map.put("STATUS",rs1.getNString(1));
						if(rs1.getNString(2) != null && !rs1.getNString(2).isEmpty()) {
							map.put("STATUS_CATEGORY",rs1.getNString(2));
						}else {
							map.put("STATUS_CATEGORY","");
						}
						return map; 
						
					}
				
				}
				
			
		}
		catch (Exception e) 
        {
			e.printStackTrace();
		}finally {
			try {
				if(rs1 != null && !rs1.isClosed())
					rs1.close();
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
			} catch (SQLException e) {
					
					e.printStackTrace();
			}
		}
		
		return map;            
		
	}
	
	public Map<String,String> getWoTaskCard(String orderNumber)
	{
		Map<String,String> map = new HashMap<String,String>();
		
		String sqlDate =
		"SELECT WO, TASK_CARD FROM WO_TASK_CARD WHERE WO_TASK_CARD.REFERENCE_TASK_CARD = ? AND (non_routine = 'N' OR non_routine = 'Y' OR non_routine IS NULL)";
		
		PreparedStatement pstmt2 = null; 
		ResultSet rs1 = null;
		try 
		{
			
				pstmt2 = con.prepareStatement(sqlDate);
				
				pstmt2.setString(1, orderNumber);
				
				rs1 =  pstmt2.executeQuery();
				
				

				if (rs1 != null) 
				{
					while (rs1.next()) // LOOP EACH INV LINE
					{
						map.put("WO",rs1.getNString(1));
						if(rs1.getNString(2) != null && !rs1.getNString(2).isEmpty()) {
							map.put("TASK_CARD",rs1.getNString(2));
						}else {
							map.put("TASK_CARD","");
						}
						return map; 
						
					}
				
				}
				
			
		}
		catch (Exception e) 
        {
			e.printStackTrace();
		}finally {
			try {
				if(rs1 != null && !rs1.isClosed())
					rs1.close();
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
			} catch (SQLException e) {
					
					e.printStackTrace();
			}
		}
		
		return map;            
		
	}
	
	public boolean lockAvailable(String notificationType)
	{
		
		//em.getTransaction().begin();
		InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType).getSingleResult();
		em.refresh(lock);
		//logger.info("lock " + lock.getLocked());
		if(lock.getLocked().intValue() == 1)
		{				
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime locked = LocalDateTime.ofInstant(lock.getLockedDate().toInstant(), ZoneId.systemDefault());
			Duration diff = Duration.between(locked, today);
			if(diff.getSeconds() >= lock.getMaxLock().longValue())
			{
				lock.setLocked(new BigDecimal(1));
				insertData(lock);
				return true;
			}
			return false;
		}
		else
		{
			lock.setLocked(new BigDecimal(1));
			insertData(lock);
			return true;
		}
		
	}
	
	private <T> void insertData( T data) 
	{
		try 
		{	
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
				em.merge(data);
			em.getTransaction().commit();
		}catch (Exception e)
		{
			logger.severe(e.toString());
		}
	}
	
	
	public void lockTable(String notificationType)
	{
		em.getTransaction().begin();
		InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType)
				.getSingleResult();
		lock.setLocked(new BigDecimal(1));
		lock.setLockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
		}
		lock.setCurrentServer(address.getHostName());
		em.merge(lock);
		em.getTransaction().commit();
	}
	
	public void unlockTable(String notificationType)
	{
		em.getTransaction().begin();
		InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType)
				.getSingleResult();
		lock.setLocked(new BigDecimal(0));
		lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
	
		em.merge(lock);
		em.getTransaction().commit();
	}



}
