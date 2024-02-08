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

import trax.aero.controller.MaterialLocationController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.MT_TRAX_RCV_I95_4081_RES;
import trax.aero.pojo.MT_TRAX_SND_I95_4081_REQ;
import trax.aero.pojo.OrderREQ;
import trax.aero.utils.DataSourceClient;


/*
 ALTER TABLE "WO_TASK_CARD"
ADD ("INTERFACE_MODIFIED_DATE" DATE);
 
UPDATE WO_TASK_CARD SET WO_TASK_CARD.INTERFACE_MODIFIED_DATE = sysdate WHERE WO_TASK_CARD.INTERFACE_MODIFIED_DATE IS NULL
  
  
  
SELECT W.refurbishment_Order ORDERNUMBER,PID.PN MATERIALNUMBER,PID.SN SERIALNUMBER, "
			+ "(SELECT LBM.BIN FROM LOCATION_BIN_MASTER LBM WHERE LBM.SITE = W.SITE FETCH FIRST 1 ROWS ONLY) \"FROM\",\r\n" + 
			"(SELECT LBM.BIN FROM LOCATION_BIN_MASTER LBM WHERE LBM.SITE = W.SITE FETCH FIRST 1 ROWS ONLY) as RECEIVING,\r\n" + 
			"PID.LEGACY_LOCATION PLANT,WSD.PN_QTY QUANTITY,PID.LEGACY_BATCH BATCH,\r\n" + 
			"(SELECT COH.CUSTOMER FROM CUSTOMER_ORDER_HEADER COH WHERE ORDER_TYPE = 'W/O' AND ORDER_NUMBER = nvl(W.NH_WO,W.WO) FETCH FIRST 1 ROWS ONLY) as CUSTOMER,\r\n" + 
			"W.WO,W.refurbishment_Order, "
			+ "(SELECT LBM.BIN FROM LOCATION_BIN_MASTER LBM \r\n" + 
			"WHERE LBM.SITE = ( SELECT WO.SITE FROM WO , LOCATION_MASTER LM  WHERE WO.WO = W.WO AND WO.LOCATION = LM.LOCATION\r\n" + 
			"AND  W.INTERFACE_SAP_TRANSFERRED_FLAG = 'Y' AND LM.INVENTORY_QUARANTINE = 'Y' FETCH FIRST 1 ROWS ONLY) FETCH FIRST 1 ROWS ONLY) AS RECEIVING_Q  \r\n" + 
			"FROM WO W, wo_shop_detail WSD, PN_INVENTORY_DETAIL PID\r\n" + 
			"WHERE W.WO = WSD.WO AND  WSD.PN = PID.PN AND PID.BATCH = WSD.BATCH AND W.refurbishment_Order IS NOT NULL AND W.INTERFACE_SAP_TRANSFERRED_DATE IS NOT NULL  
select 
max(wo_actual_transaction) 
from 
wo_actuals 
where 
wo = ? and 
0 = (select count(*) from wo_actuals where wo = ? and status <> 'CLOSED')  
order by 
wo_actual_transaction desc  
  
  
 */



public class MaterialLocationData {

		Logger logger = LogManager.getLogger("MaterialLocation_I95");
		EntityManagerFactory factory;
		EntityManager em;
		String exceuted;
		private Connection con;
		
		final String MaxRecord = System.getProperty("MaterialLocation_MaxRecord");
		//public InterfaceLockMaster lock;
		
		public MaterialLocationData(String mark)
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
				MaterialLocationController.addError(e.toString());
				
			}
			catch (CustomizeHandledException e1) {
				MaterialLocationController.addError(e1.toString());
				logger.severe(e1.toString());
			} catch (Exception e) {
				MaterialLocationController.addError(e.toString());
				logger.severe(e.toString());
			}
		}
		
		public MaterialLocationData()
		{
			factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
			em = factory.createEntityManager();
			
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
				MaterialLocationController.addError(e.toString());
				
			}
			catch (CustomizeHandledException e1) {
				MaterialLocationController.addError(e1.toString());
				logger.severe(e1.toString());
			} catch (Exception e) {
				MaterialLocationController.addError(e.toString());
				logger.severe(e.toString());
			}
		}
		
		public Connection getCon() {
			return con;
		}
		
		
		public ArrayList<MT_TRAX_SND_I95_4081_REQ> getMaterials() throws Exception
		{
			//setting up variables
			exceuted = "OK";
			
			ArrayList<MT_TRAX_SND_I95_4081_REQ> requests = new ArrayList<MT_TRAX_SND_I95_4081_REQ>();
			
			
			
			
			String sql = 
			"SELECT W.refurbishment_Order ORDERNUMBER,PID.PN MATERIALNUMBER,PID.SN SERIALNUMBER, \r\n" + 
			"			W.\"SITE\",PID.LEGACY_LOCATION PLANT,WSD.PN_QTY QUANTITY,PID.LEGACY_BATCH BATCH,W.WO, \r\n" + 
			"			(SELECT LBM.BIN FROM LOCATION_BIN_MASTER LBM,location_site LS,LOCATION_MASTER LM WHERE LBM.SITE = \r\n" + 
			"			    (SELECT e.SITE FROM wo_audit e WHERE e.WO = W.WO AND 2=\r\n" + 
			"			        (SELECT COUNT(DISTINCT p.MODIFIED_DATE) FROM wo_audit p \r\n" + 
			"			            WHERE p.WO = W.WO AND  e.MODIFIED_DATE<=p.MODIFIED_DATE) ORDER BY e.MODIFIED_DATE DESC FETCH FIRST 1 ROWS ONLY) \r\n" + 
			"							AND LBM.SITE = LS.SITE AND LM.LOCATION = LS.LOCATION AND  LM.INVENTORY_QUARANTINE = 'Y' \r\n" + 
			"							AND LBM.LOCATION = LM.LOCATION FETCH FIRST 1 ROWS ONLY) \"FROM_A\",\r\n" + 
			"            (SELECT LBM.BIN FROM LOCATION_BIN_MASTER LBM,location_site LS,LOCATION_MASTER LM \r\n" + 
			"                WHERE LBM.SITE = W.SITE AND LBM.SITE = LS.SITE AND LM.LOCATION = LS.LOCATION \r\n" + 
			"                    AND  LM.INVENTORY_QUARANTINE = 'Y' AND LBM.LOCATION = LM.LOCATION \r\n" + 
			"                        FETCH FIRST 1 ROWS ONLY) \"FROM\",                \r\n" + 
			"            (SELECT LBM.BIN FROM LOCATION_BIN_MASTER LBM,location_site LS,LOCATION_MASTER LM , WO wp \r\n" + 
			"                WHERE LBM.SITE = wp.SITE AND LBM.SITE = LS.SITE AND LM.LOCATION = LS.LOCATION AND wp.wo = w.NH_WO and wp.MODULE = 'PRODUCTION' \r\n" + 
			"                    AND LBM.LOCATION = LM.LOCATION \r\n" + 
			"                        FETCH FIRST 1 ROWS ONLY) \"FROM_P\",                \r\n" + 
			"			(SELECT LBM.BIN FROM LOCATION_BIN_MASTER LBM \r\n" + 
			"			    WHERE LBM.SITE = W.SITE FETCH FIRST 1 ROWS ONLY) as RECEIVING,\r\n" + 
			"			(SELECT COH.CUSTOMER FROM CUSTOMER_ORDER_HEADER COH \r\n" + 
			"			    WHERE ORDER_TYPE = 'W/O' AND ORDER_NUMBER = W.WO FETCH FIRST 1 ROWS ONLY) as CUSTOMER,     \r\n" + 
			"			(SELECT LBM.BIN FROM LOCATION_MASTER LM,location_site LS, LOCATION_BIN_MASTER LBM  \r\n" + 
			"			    WHERE LS.SITE = W.SITE AND LBM.SITE = LS.SITE AND LM.LOCATION = LS.LOCATION AND  LM.INVENTORY_QUARANTINE = 'Y' AND LBM.LOCATION = LM.LOCATION FETCH FIRST 1 ROWS ONLY) AS RECEIVING_Q,\r\n" + 
			"			(SELECT LBM.BIN FROM LOCATION_MASTER LM,location_site LS, LOCATION_BIN_MASTER LBM  \r\n" + 
			"			    WHERE LS.SITE = W.SITE AND LBM.SITE = LS.SITE AND LM.LOCATION = LS.LOCATION AND  LM.INVENTORY_QUARANTINE = 'N' AND LBM.LOCATION = LM.LOCATION FETCH FIRST 1 ROWS ONLY) AS RECEIVING_NQ,\r\n" + 
			"			(SELECT COH.CUSTOMER FROM CUSTOMER_ORDER_HEADER COH, WO wp \r\n" + 
			"			    WHERE COH.ORDER_TYPE = 'W/O' AND COH.ORDER_NUMBER = W.NH_WO AND wp.wo = w.NH_WO and wp.MODULE = 'PRODUCTION' FETCH FIRST 1 ROWS ONLY) as CUSTOMER_P     \r\n" + 
			",(SELECT REMOVE_AS_SERVICEABLE FROM AC_PN_TRANSACTION_HISTORY WHERE PN = PID.PN AND SN =PID.SN  ORDER BY TRANSACTION DESC FETCH FIRST 1 ROWS ONLY ) AS SERVICEABLE   \r\n" +
			"			FROM WO W, wo_shop_detail WSD, PN_INVENTORY_DETAIL PID\r\n" + 
			"			WHERE W.WO = WSD.WO AND  WSD.PN = PID.PN AND PID.BATCH = WSD.BATCH \r\n" + 
			"			AND W.refurbishment_Order IS NOT NULL AND W.INTERFACE_SAP_TRANSFERRED_DATE IS NOT NULL";

			
			String sqlPick = "SELECT PD.PICKLIST REQ,PD.PICKLIST_LINE REQLINE \r\n" + 
					"FROM picklist_distribution PD, PICKLIST_HEADER PH \r\n" + 
					"WHERE  ph.picklist = pd.picklist AND pd.transaction = 'DISTRIBU' and PH.WO = ? AND pd.pn = ?";
			
			if((MaxRecord != null && !MaxRecord.isEmpty())) {
				sql=  "SELECT *	FROM (" + sql;
			}
			
			if((MaxRecord != null && !MaxRecord.isEmpty())) {
				sql= sql + "  )WHERE ROWNUM <= ?";		
			}
			
			//logger.info(sql);
			
			PreparedStatement pstmt1 = null;
			ResultSet rs1 = null;
			
			PreparedStatement pstmt2 = null;
			ResultSet rs2 = null;
			try 
			{
				pstmt1 = con.prepareStatement(sql);
				pstmt2 = con.prepareStatement(sqlPick);
				if((MaxRecord != null && !MaxRecord.isEmpty())) {
					pstmt1.setString(1, MaxRecord);
				}
				
				rs1 = pstmt1.executeQuery();

				if (rs1 != null) 
				{
					while (rs1.next()) 
					{
						logger.info("Processing ORDER NUMBER: " + rs1.getString("ORDERNUMBER"));
						
						MT_TRAX_SND_I95_4081_REQ request = new MT_TRAX_SND_I95_4081_REQ();
						OrderREQ orderREQ = new OrderREQ();
						
						
						if(rs1.getString("ORDERNUMBER") != null && !rs1.getNString("ORDERNUMBER").isEmpty()) {
							orderREQ.setOrderNumber(rs1.getString("ORDERNUMBER"));
						}
						else {
							orderREQ.setOrderNumber("");
						}
						if(rs1.getString("MATERIALNUMBER") != null && !rs1.getNString("MATERIALNUMBER").isEmpty()) {
							
							String pn = rs1.getString("MATERIALNUMBER");
							
							pn = pn.replaceAll("in", "\"");
							pn = pn.replaceAll("ft", "'");
							
							if(pn.contains(":UPLOAD"))
							{
								pn=  pn.substring(0, pn.indexOf(":"));
							}
							
							
							orderREQ.setMaterialNumber(pn);
						}
						else {
							orderREQ.setMaterialNumber("");
						}
						if(rs1.getString("SERIALNUMBER") != null && !rs1.getNString("SERIALNUMBER").isEmpty()) {
							orderREQ.setSerialNumber(rs1.getString("SERIALNUMBER"));
						}
						else {
							orderREQ.setSerialNumber("");
						}
						if(rs1.getString("FROM_A") != null && !rs1.getNString("FROM_A").isEmpty()) {
							orderREQ.setFromStorageLocation(rs1.getString("FROM_A"));
						} else if(rs1.getString("FROM_P") != null && !rs1.getNString("FROM_P").isEmpty()) {
							orderREQ.setFromStorageLocation(rs1.getString("FROM_P"));
						} else if(rs1.getString("FROM") != null && !rs1.getNString("FROM").isEmpty()) {
							orderREQ.setFromStorageLocation(rs1.getString("FROM"));
						}
						else {
							orderREQ.setFromStorageLocation("");
						}
						
						
						
						if("YES".equalsIgnoreCase(rs1.getString("SERVICEABLE"))) {
							
							orderREQ.setReceivingStorageLocation(rs1.getString("RECEIVING_NQ"));
							
						}else if(rs1.getString("RECEIVING_Q") != null && !rs1.getString("RECEIVING_Q").isEmpty()){
							
							orderREQ.setReceivingStorageLocation(rs1.getString("RECEIVING_Q"));
							
						}else {
							
							orderREQ.setReceivingStorageLocation("");
							
						}
						
						if(orderREQ.getReceivingStorageLocation() == null || orderREQ.getReceivingStorageLocation().isEmpty()) {
							orderREQ.setReceivingStorageLocation("");
						}
						
						if(rs1.getString("PLANT") != null && !rs1.getString("PLANT").isEmpty()) {
							orderREQ.setFromPlant(rs1.getString("PLANT"));
						}
						else {
							orderREQ.setFromPlant("");
						}
						if(rs1.getString("QUANTITY") != null && !rs1.getNString("QUANTITY").isEmpty()) {
							orderREQ.setQuantity(rs1.getString("QUANTITY"));
						}
						else {
							orderREQ.setQuantity("");
						}
						if(rs1.getString("BATCH") != null && !rs1.getNString("BATCH").isEmpty()) {
							orderREQ.setBatch(rs1.getString("BATCH"));
						}
						else {
							orderREQ.setBatch("");
						}
						if(rs1.getString("CUSTOMER_P") != null && !rs1.getNString("CUSTOMER_P").isEmpty()) {
							orderREQ.setCustomer(rs1.getString("CUSTOMER_P"));
							
						}else if(rs1.getString("CUSTOMER") != null && !rs1.getNString("CUSTOMER").isEmpty()) {
							orderREQ.setCustomer(rs1.getString("CUSTOMER"));
						}
						else {
							orderREQ.setCustomer("");
						}
						
						
						
						
						pstmt2.setString(1, rs1.getString("WO"));
						pstmt2.setString(2, orderREQ.getMaterialNumber());
						rs2 = pstmt2.executeQuery();

						if (rs2 != null) 
						{
							while (rs2.next()) 
							{
						
								if(rs2.getString(1) != null && !rs2.getNString(1).isEmpty()) {
									orderREQ.setRequistionNumber(rs2.getString(1));
								}
								else {
									orderREQ.setRequistionNumber("");
								}
								if(rs2.getString(2) != null && !rs2.getNString(2).isEmpty()) {
									orderREQ.setRequistionLine(rs2.getString(2));
								}
								else {
									orderREQ.setRequistionLine("");
								}
						
							}
						}
						if(rs2 != null && !rs2.isClosed()) {
							rs2.close();
						}
						
						if(orderREQ.getReceivingStorageLocation() == null || orderREQ.getReceivingStorageLocation().isEmpty()) {
							orderREQ.setReceivingStorageLocation("");
						}
						
						//BLANK
						orderREQ.setMovementType("");
						orderREQ.setSpecialStockIndicator("");
						orderREQ.setRequistionNumber("");
						orderREQ.setRequistionLine("");
						
						
						request.setOrder(orderREQ);
						requests.add(request);	
						
						
					}
				}
				
				
			}
			catch (Exception e) 
	        {
				e.printStackTrace();
				MaterialLocationController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
	            throw new Exception("Issue found");
			}finally {
				if(rs1 != null && !rs1.isClosed())
					rs1.close();
				if(pstmt1 != null && !pstmt1.isClosed())
					pstmt1.close();
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
			}
			return requests;
		}
		
		
		public void markTransaction(MT_TRAX_RCV_I95_4081_RES response) throws Exception
		{
			//setting up variables
			exceuted = "OK";
			
			
			String sqlDate =
			"UPDATE WO SET WO.INTERFACE_SAP_TRANSFERRED_DATE = NULL, WO.INTERFACE_SAP_TRANSFERRED_FLAG = NULL  WHERE WO.INTERFACE_SAP_TRANSFERRED_DATE IS NOT NULL AND WO.refurbishment_Order = ?";
			
			PreparedStatement pstmt2 = null; 
			pstmt2 = con.prepareStatement(sqlDate);
			try 
			{	
				pstmt2.setString(1, response.getOrder().getOrderNumber());
				pstmt2.executeQuery();
			}
			catch (Exception e) 
	        {
				MaterialLocationController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
	            throw new Exception("Issue found");
			}finally {
				
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
				
			}
			
		}
		
		
		
		public String getRFO(String wo) 
		{
			
			String sqlDate =
			"SELECT WO.refurbishment_order FROM WO WHERE WO.WO = ?";
			
			PreparedStatement pstmt2 = null; 
			ResultSet rs1 = null;
			
			try 
			{	
				
				pstmt2 = con.prepareStatement(sqlDate);
				pstmt2.setString(1, wo);
				
				
				rs1 = pstmt2.executeQuery();

				if (rs1 != null) 
				{
					while (rs1.next()) 
					{
						if(rs1.getString(1) !=null) {
							return rs1.getString(1);
						}else {
							return "";
						}
						
					}
				}
				
			}
			catch (Exception e) 
	        {
				
				logger.severe(e.toString());
	            
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
			
			return "";
		
	}
		
		
			public String getWO(String Order) 
			{
				
				String sqlDate =
				"SELECT WO.WO FROM WO WHERE WO.refurbishment_Order = ?";
				
				PreparedStatement pstmt2 = null; 
				ResultSet rs1 = null;
				
				try 
				{	
					
					pstmt2 = con.prepareStatement(sqlDate);
					pstmt2.setString(1, Order);
					
					
					rs1 = pstmt2.executeQuery();

					if (rs1 != null) 
					{
						while (rs1.next()) 
						{
							if(rs1.getString(1) !=null) {
								return rs1.getString(1);
							}else {
								return "";
							}
						}
					}
					
				}
				catch (Exception e) 
		        {
					
					logger.severe(e.toString());
		            
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
