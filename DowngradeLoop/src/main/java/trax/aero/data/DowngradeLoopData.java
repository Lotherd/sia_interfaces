package trax.aero.data;

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
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import trax.aero.controller.DowngradeLoopController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.MT_TRAX_SND_I94_4084_REQ;
import trax.aero.pojo.MT_TRAX_RCV_I94_4084_RES;
import trax.aero.utils.DataSourceClient;


/*
ALTER TABLE "WO"
ADD ("INTERFACE_SAP_TRANSFER_DATE" DATE);
 
UPDATE 
WO      
SET   
WO.INTERFACE_SAP_TRANSFER_DATE = sysdate
WHERE
WO.refurbishment_order = ? AND w.MODULE = 'SHOP'
  
  
  
SELECT w.refurbishment_order FROM WO w where w.INTERFACE_SAP_TRANSFER_DATE IS NULL AND w.MODULE = 'SHOP'
  
*/



public class DowngradeLoopData {

		Logger logger = LogManager.getLogger("DowngradeLoop_I94");
		EntityManagerFactory factory;
		EntityManager em;
		String exceuted;
		private Connection con;
		
		final String MaxRecord = System.getProperty("DowngradeLoop_MaxRecord");
		//public InterfaceLockMaster lock;
		
		public DowngradeLoopData(String mark)
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
				logger.severe("An error occured getting the status of the connection");
				DowngradeLoopController.addError(e.toString());
				
			}
			catch (CustomizeHandledException e1) {
				DowngradeLoopController.addError(e1.toString());
				logger.severe(e1.toString());
			} catch (Exception e) {
				DowngradeLoopController.addError(e.toString());
				logger.severe(e.toString());
			}
			
		}
		
		public DowngradeLoopData()
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
				logger.severe("An error occured getting the status of the connection");
				DowngradeLoopController.addError(e.toString());
				
			}
			catch (CustomizeHandledException e1) {
				DowngradeLoopController.addError(e1.toString());
				logger.severe(e1.toString());
			} catch (Exception e) {
				DowngradeLoopController.addError(e.toString());
				logger.severe(e.toString());
			}
			factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
			em = factory.createEntityManager();
		}
		
		public Connection getCon() {
			return con;
		}
		
		
		public ArrayList<MT_TRAX_SND_I94_4084_REQ> getLoops() throws Exception
		{
			//setting up variables
			exceuted = "OK";
			
			ArrayList<MT_TRAX_SND_I94_4084_REQ> requests = new ArrayList<MT_TRAX_SND_I94_4084_REQ>();
			
			
			
			
			String sql = 
			"SELECT w.refurbishment_order FROM WO w where w.INTERFACE_SAP_TRANSFER_DATE IS NULL AND w.refurbishment_order IS NOT NULL AND w.MODULE = 'SHOP'";

			if((MaxRecord != null && !MaxRecord.isEmpty())) {
				sql= sql + " AND ROWNUM <= ?";		
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
					while (rs1.next()) 
					{
						logger.info("Processing RFO: " + rs1.getString(1));
						MT_TRAX_SND_I94_4084_REQ request = new MT_TRAX_SND_I94_4084_REQ();
						
						if(rs1.getString(1) != null && !rs1.getNString(1).isEmpty()) {
							request.setSAPServiceOrderNumber(rs1.getString(1));
						}
						else {
							request.setSAPServiceOrderNumber("");
						}
						requests.add(request);	
						
					}
				}
				
				
			}
			catch (Exception e) 
	        {
				DowngradeLoopController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
	            throw new Exception("Issue found");
			}finally {
				if(rs1 != null && !rs1.isClosed())
					rs1.close();
				if(pstmt1 != null && !pstmt1.isClosed())
					pstmt1.close();
			}
			
			return requests;
		}
		
		
		public void markTransaction(MT_TRAX_RCV_I94_4084_RES response) throws Exception
		{
			//setting up variables
			exceuted = "OK";
			
			String sqlDate ="UPDATE WO SET WO.INTERFACE_SAP_TRANSFER_DATE = sysdate WHERE WO.refurbishment_order = ? AND WO.MODULE = 'SHOP'";
			
			PreparedStatement pstmt2 = null; 
			pstmt2 = con.prepareStatement(sqlDate);
			try 
			{	
				logger.info("Marking RFO: " + response.getSAPOrderNumber());
				pstmt2.setString(1, response.getSAPOrderNumber());
				pstmt2.executeQuery();
			}
			catch (Exception e) 
	        {
				DowngradeLoopController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
	            throw new Exception("Issue found");
			}finally {
				
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
				
			}
			
		}

		public String getShopWo(String sapServiceOrderNumber) {
			String sqlDate ="SELECT WO FROM WO WHERE refurbishment_order = ? AND MODULE = 'SHOP'";
			
			PreparedStatement pstmt2 = null; 
			
			try 
			{	
				pstmt2 = con.prepareStatement(sqlDate);
				pstmt2.setString(1, sapServiceOrderNumber);
				ResultSet rs = pstmt2.executeQuery();
				if (rs != null) 
				{
					while (rs.next()) 
					{
						
						return rs.getString(1);

					}
				}
				rs.close();
				
			}
			catch (Exception e) 
	        {
				DowngradeLoopController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
	            
			}finally {
				
				try {
					if(pstmt2 != null && !pstmt2.isClosed())
						pstmt2.close();
				} catch (SQLException e) {
					
				}
				
			}
			
			return "";
		}

		public String getProdWo(String SAPServiceOrderNumber) {
			String sqlDate ="SELECT WO FROM WO WHERE refurbishment_order = ? AND MODULE = 'SHOP'";
			
			PreparedStatement pstmt2 = null; 
			
			try 
			{	
				pstmt2 = con.prepareStatement(sqlDate);
				pstmt2.setString(1, SAPServiceOrderNumber);
				ResultSet rs = pstmt2.executeQuery();
				if (rs != null) 
				{
					while (rs.next()) 
					{
						
						return rs.getString(1);

					}
				}
				rs.close();
				
			}
			catch (Exception e) 
	        {
				DowngradeLoopController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
	            
			}finally {
				
				try {
					if(pstmt2 != null && !pstmt2.isClosed())
						pstmt2.close();
				} catch (SQLException e) {
					
				}
				
			}
			
			return "";
		}
		
		
		public String getSAPOrderNumber(String SAPServiceOrderNumber) {
			String sqlDate ="SELECT DISTINCT WTC.REFERENCE_TASK_CARD FROM WO W, WO_TASK_CARD WTC WHERE W.WO = WTC.WO AND W.refurbishment_order = ?";
			
			PreparedStatement pstmt2 = null; 
			
			try 
			{	
				pstmt2 = con.prepareStatement(sqlDate);
				pstmt2.setString(1, SAPServiceOrderNumber);
				ResultSet rs = pstmt2.executeQuery();
				if (rs != null) 
				{
					while (rs.next()) 
					{
						
						return rs.getString(1);

					}
				}
				rs.close();
				
			}
			catch (Exception e) 
	        {
				DowngradeLoopController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
	            
			}finally {
				
				try {
					if(pstmt2 != null && !pstmt2.isClosed())
						pstmt2.close();
				} catch (SQLException e) {
					
				}
				
			}
			
			return "";
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
		
		
		public void lockTable(String notificationType)
		{
			em.getTransaction().begin();
			InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType).getSingleResult();
			lock.setLocked(new BigDecimal(1));
			//logger.info("lock " + lock.getLocked());
			
			lock.setLockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			InetAddress address = null;
			try {
				address = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				
				logger.info(e.getMessage());
				//e.printStackTrace();
			}
			lock.setCurrentServer(address.getHostName());
			//em.lock(lock, LockModeType.NONE);
			em.merge(lock);
			em.getTransaction().commit();
		}
		
		public void unlockTable(String notificationType)
		{
			em.getTransaction().begin();
			
			InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType).getSingleResult();
			lock.setLocked(new BigDecimal(0));
			//logger.info("lock " + lock.getLocked());
			
			lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			//em.lock(lock, LockModeType.NONE);
			em.merge(lock);
			em.getTransaction().commit();
		}
		
}
