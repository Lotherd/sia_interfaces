package trax.aero.data;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import trax.aero.controller.TaskCardsIngestionController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.NotePad;
import trax.aero.model.NotePadPK;
import trax.aero.model.PnInventoryDetail;
import trax.aero.model.PnMaster;
import trax.aero.pojo.MT_TRAX_RCV_I87_RES;
import trax.aero.pojo.MT_TRAX_SND_I87_REQ;
import trax.aero.utils.DataSourceClient;
import trax.aero.utils.ErrorType;

/*
 select pn,qty,hours,minutes from wo_actuals wa where wa.wo = ? and wa.task_card = ? and wa.task_card_item = '1' and rownum <= 1 order by wa.wo_actual_transaction desc;
select task_card_text from wo_task_card_item wtci where wtci.wo = ? and wtci.task_card = ? and wtci.task_card_item = '1' and rownum <= 1;

 */



public class TaskCardsIngestionData {
	
	private Connection con;
	String ex;
	MT_TRAX_RCV_I87_RES in;
	long lastBatch = 99999;
	EntityManagerFactory factory;
	EntityManager em;
	String exceuted = "OK";
	final String MaxRecord = System.getProperty("TaskCardsIngestion_MaxRecord");
	final String party3 = System.getProperty("TaskCardsIngestion_3PValue");
	Logger logger = LogManager.getLogger("TaskCardsIngestion_I87");
	public InterfaceLockMaster lock;
	
	
	public TaskCardsIngestionData()
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
			TaskCardsIngestionController.addError(e.toString());
			
		}
		catch (CustomizeHandledException e1) {
			
			TaskCardsIngestionController.addError(e1.toString());
			
		} catch (Exception e) {
			
			TaskCardsIngestionController.addError(e.toString());
			
		}
			
	}
	
	public Connection getCon() {
		return con;
	}

	

	public String markTransaction(MT_TRAX_SND_I87_REQ t) throws Exception
	{
		//setting up variables
		exceuted = "OK";
		String sqlDateY =
		"UPDATE WO_TASK_CARD SET INTERFACE_SAP_TRANSFER_DATE = sysdate, REFERENCE_TASK_CARD = ? WHERE TASK_CARD = ? AND WO = ?";
		
		String sqlDate =
				"UPDATE WO_TASK_CARD SET INTERFACE_SAP_TRANSFER_DATE = sysdate WHERE TASK_CARD = ? AND WO = ?";
		
		String sqlCus =
				"SELECT CUSTOMER_TASK_CARD FROM WO_TASK_CARD_CUSTOMER WHERE TASK_CARD = ? AND WO = ?";
		
		
		String sqlItem =
		"UPDATE WO_TASK_CARD_ITEM SET ops_no = ? WHERE TASK_CARD = ? AND WO = ? AND TASK_CARD_ITEM = '1'";
				
		
		PreparedStatement pstmt = null; 
		PreparedStatement pstmt1 = null; 
		PreparedStatement pstmt2 = null; 
		ResultSet rs = null;
		
		try 
		{
				if(t.getSuccessErrorIndicator().equalsIgnoreCase("53")) {
					pstmt = con.prepareStatement(sqlDateY);
					pstmt.setString(1, t.getSAPOrderNumber());
					pstmt.setString(2, t.getTRAXTCNumber());
					pstmt.setString(3, t.getTRAXWO());
					
					pstmt.executeQuery();
					
					pstmt1 = con.prepareStatement(sqlItem);
					pstmt1.setString(1, t.getSAPOperationNumber());
					pstmt1.setString(2, t.getTRAXTCNumber());
					pstmt1.setString(3, t.getTRAXWO());
					pstmt1.executeQuery();
					
				}else {
					exceuted = " Request WO: " + t.getTRAXWO() + " ,Task Card: " + t.getTRAXTCNumber() + " ,Indicator: " + t.getSuccessErrorIndicator() + " ,Remarks: " + t.getRemarks() + " ,OperationNumber: " + t.getSAPOperationNumber() +" ,OrderNumber: " + t.getSAPOrderNumber() ;
					TaskCardsIngestionController.addError(exceuted);
					pstmt = con.prepareStatement(sqlDate);
					pstmt.setString(1, t.getTRAXTCNumber());
					pstmt.setString(2, t.getTRAXWO());
					pstmt.executeQuery();	
				}
				pstmt2 = con.prepareStatement(sqlCus);
				pstmt2.setString(1, t.getTRAXTCNumber());
				pstmt2.setString(2, t.getTRAXWO());
				
				
				
				
				
				rs = pstmt2.executeQuery();
				if (rs != null) 
				{
					while (rs.next()) // LOOP EACH LINE
					{
						if(rs.getString(1) != null && !rs.getString(1).isEmpty() ) {
							logger.info("Processing Customer WO Task Card: " + rs.getString(1));
							pstmt.setString(1, t.getSAPOrderNumber());
							pstmt.setString(2, rs.getString(1));
							pstmt.setString(3, t.getTRAXWO());
							pstmt.executeQuery();
						}
					}
				}
				if(rs != null && !rs.isClosed())
					rs.close();
		}
		catch (Exception e) 
        {
			TaskCardsIngestionController.addError(e.toString());
            exceuted = e.toString();
            
		}finally {
			if(pstmt != null && !pstmt.isClosed())
				pstmt.close();
			if(pstmt1 != null && !pstmt1.isClosed())
				pstmt1.close();
			if(pstmt2 != null && !pstmt2.isClosed())
				pstmt2.close();
		}
		
		return exceuted;
		
	}

	public ArrayList<MT_TRAX_RCV_I87_RES> getTaskCards() throws Exception {
		
		//setting up variables
		exceuted = "OK";
		Float hours;
		Float minutes;
		Float time;
		ArrayList<MT_TRAX_RCV_I87_RES> list = new ArrayList<MT_TRAX_RCV_I87_RES>();
	
		String sqlTaskCard ="SELECT wtc.pn_sn,wtc.pn,wtc.task_card_description,wtc.MPD,wtc.revision,wtc.priority,wtc.TASK_CARD_NUMBERING_SYSTEM,wtc.task_card,wtc.wo,wtc.mod_no,wtc.MRB\r\n" + 
				"FROM WO_TASK_CARD wtc WHERE  wtc.INTERFACE_SAP_TRANSFER_DATE is null AND (wtc.non_routine = 'N' OR wtc.non_routine is null ) AND wtc.wo IN ( select w.wo from wo w where w.wo_category like '%" +party3 +"' and w.rfo_no is null)";
		String sqlTaskCardItem ="select task_card_text from wo_task_card_item wtci where wtci.wo = ? and wtci.task_card = ? and wtci.task_card_item = '1' and rownum <= 1";
		String sqlwork ="select pn,qty,hours,minutes from wo_actuals wa where wa.wo = ? and wa.task_card = ? and wa.task_card_item = '1' and rownum <= 1";
		//logger.info(sqlTaskCard);
		
		
		sqlTaskCard= sqlTaskCard + " order by wtc.task_card_numbering_system desc";	
		
		if(MaxRecord != null && !MaxRecord.isEmpty()) {
			sqlTaskCard=  "SELECT *	FROM (" + sqlTaskCard;
		}
		
		if(MaxRecord != null && !MaxRecord.isEmpty()) {
			sqlTaskCard= sqlTaskCard + "  )WHERE ROWNUM <= ?";		
		}
		
		
		
		
		
		
		
		

		String sqlDate =
				"UPDATE WO_TASK_CARD SET INTERFACE_SAP_TRANSFER_DATE = sysdate WHERE INTERFACE_SAP_TRANSFER_DATE IS NULL AND TASK_CARD = ? AND WO = ?";
				
		PreparedStatement pstmtTC = null;
		ResultSet rstc = null;
		
		PreparedStatement pstmtTCI = null;
		ResultSet rstci = null;
		
		PreparedStatement pstmtWORK = null;
		ResultSet rstw = null;
		
		PreparedStatement pstmtDate = null;
		
				
		try {
				pstmtTC = con.prepareStatement(sqlTaskCard);
				
				pstmtDate = con.prepareStatement(sqlDate);
				
				pstmtWORK = con.prepareStatement(sqlwork);
				
				pstmtTCI = con.prepareStatement(sqlTaskCardItem);
				
				if(MaxRecord != null && !MaxRecord.isEmpty()) {
					pstmtTC.setString(1, MaxRecord);
					}
				rstc = pstmtTC.executeQuery();
				if (rstc != null) 
				{
					while (rstc.next()) // LOOP EACH LINE
					{
						logger.info("Processing WO Task Card: " + rstc.getString(8) +" , WO:" + rstc.getString(9));
						MT_TRAX_RCV_I87_RES Inbound = new MT_TRAX_RCV_I87_RES();	
						
	
						if(rstc.getString(1) != null && !rstc.getNString(1).isEmpty() && !rstc.getNString(1).equalsIgnoreCase("                                   ")) {
							Inbound.setSerialNumber(rstc.getString(1));
						}
						else {
							Inbound.setSerialNumber("");
						}
						
						if(rstc.getString(2) != null && !rstc.getNString(2).isEmpty() && !rstc.getNString(2).equalsIgnoreCase("                                   ")) {
							Inbound.setMaterialNumber(rstc.getString(2));
							Inbound.setMaterialNumber(Inbound.getMaterialNumber().replaceAll("IN", "\""));
							Inbound.setMaterialNumber(Inbound.getMaterialNumber().replaceAll("FT", "'"));
							
							if(Inbound.getMaterialNumber().contains(":UPLOAD"))
							{
								Inbound.setMaterialNumber( Inbound.getMaterialNumber().substring(0, Inbound.getMaterialNumber().indexOf(":")));
							}
														
						}
						else {
							Inbound.setMaterialNumber("");
						}	
						if(rstc.getString(3) != null && !rstc.getNString(3).isEmpty()) {
							Inbound.setOrderShortText(rstc.getString(3));
						}
						else {
							Inbound.setOrderShortText("");
						}
						if(rstc.getString(4) != null && !rstc.getNString(4).isEmpty()) {
								Inbound.setMaintenanceActivityType(rstc.getString(4));
						}
						else {
							Inbound.setMaintenanceActivityType("");
						}
						if(rstc.getString(5) != null && !rstc.getNString(5).isEmpty()) {
							//Inbound.setRevision(rstc.getString(5));
							Inbound.setRevision("");
						}
						else {
							Inbound.setRevision("");
						}
						if(rstc.getString(6) != null && !rstc.getNString(6).isEmpty()) {
							Inbound.setPriority(rstc.getString(6));
						}
						else {
							Inbound.setPriority("");
						}
							
							
						if(rstc.getString(7) != null && !rstc.getNString(7).isEmpty()) {
							Inbound.setRunningNumber(rstc.getString(7));
						}
						else {
							Inbound.setRunningNumber("");
						}	
						
						Inbound.setTRAXTCNumber(rstc.getString(8));
						Inbound.setTRAXWO(rstc.getNString(9));
						
						if(rstc.getString(10) != null && !rstc.getNString(10).isEmpty()) {
							Inbound.setWBS(rstc.getNString(10));
						}else {
							Inbound.setWBS("");
						}
						
						if(rstc.getString(11) != null && !rstc.getNString(11).isEmpty()) {
							Inbound.setMainWorkCenter(rstc.getNString(11));
						}else {
							Inbound.setMainWorkCenter("");
						}
						
						pstmtWORK.setString(1, Inbound.getTRAXWO());
						pstmtWORK.setString(2, Inbound.getTRAXTCNumber());
						rstw = pstmtWORK.executeQuery();
						if (rstw != null) 
							{
								while (rstw.next()) // LOOP EACH LINE
								{
									if(rstw.getString(1) != null && !rstw.getNString(1).isEmpty()) {
										Inbound.setMaterialNumberServiceProduct(rstw.getString(1));
									}
									else {
										Inbound.setMaterialNumberServiceProduct("");
									}	
									if(rstw.getString(2) != null && !rstw.getNString(2).isEmpty()) {
										Inbound.setQTY(rstw.getString(2));
									}
									else {
										Inbound.setQTY("");
									}
									if(rstw.getString(3) != null && !rstw.getNString(3).isEmpty()) {
										hours = Float.parseFloat(rstw.getString(3));
									}
									else {
										hours = null;
									}
									if(rstw.getString(4) != null && !rstw.getNString(4).isEmpty()) {
										minutes = Float.parseFloat(rstw.getString(4));
									}
									else {
										minutes = null;
									}
									if(hours != null && minutes !=null) {				
										time = (float) (hours + (minutes * 1.0/60.0 ));
										Inbound.setWorkInvolved(time.toString());
									}else {
										Inbound.setWorkInvolved("1.0");
									}
								}
								
							
									
							pstmtTCI.setString(1, Inbound.getTRAXWO());
							pstmtTCI.setString(2, Inbound.getTRAXTCNumber());
							rstci = pstmtTCI.executeQuery();
							if (rstci != null) 
							{
									while (rstci.next()) // LOOP EACH LINE
									{
										if(rstci.getString(1) != null && !rstci.getNString(1).isEmpty()) {
											String text = "";
											RTFEditorKit rtfParser = new RTFEditorKit();
											Document document = rtfParser.createDefaultDocument();
											try {
												rtfParser.read(new ByteArrayInputStream(rstci.getString(1).getBytes()), document, 0);
												text = document.getText(0, document.getLength());
											} catch (Exception e) {
											
											}
											
											Inbound.setOperationShortText(text);
										}
										else {
											Inbound.setOperationShortText("");
										}
									}
									
							}
							if(rstci != null && !rstci.isClosed()) {
								rstci.close();
							}
							
							pstmtDate.setString(1, Inbound.getTRAXTCNumber());
							pstmtDate.setString(2, Inbound.getTRAXWO());
							pstmtDate.executeQuery();
							
							list.add(Inbound);	
						}
						
						if(rstw != null && !rstw.isClosed()) {
							rstw.close();
						}
						
					}
				}
				
				if(rstc != null && !rstc.isClosed()) {
					rstc.close();
				}
					
			}
			catch (Exception e) 
		    {
				TaskCardsIngestionController.addError(e.toString());
		       e.printStackTrace();
		        exceuted = e.toString();
		        logger.severe(exceuted);
		        throw new Exception("Issue found");
		    }finally {
				
		    	if(pstmtWORK != null && !pstmtWORK.isClosed()) {
					pstmtWORK.close();
				}
		    	if(pstmtTC != null && !pstmtTC.isClosed()) {
					pstmtTC.close();
				}
		    	if(pstmtTCI != null && !pstmtTCI.isClosed()) {
					pstmtTCI.close();
				}
		    	if(pstmtDate != null && !pstmtDate.isClosed()) {
					pstmtDate.close();
				}
				
				
			}
			return list;
	}
	//insert generic data from model objects
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
			exceuted = "insertData has encountered an Exception: "+e.toString();
			TaskCardsIngestionController.addError(exceuted);
			logger.severe(exceuted);
		}
	}
	
	public boolean lockAvailable(String notificationType)
	{
		
		lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType).getSingleResult();
		if(lock.getLocked().intValue() == 1)
		{				
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime locked = LocalDateTime.ofInstant(lock.getLockedDate().toInstant(), ZoneId.systemDefault());
			Duration diff = Duration.between(locked, today);
			if(diff.getSeconds() >= lock.getMaxLock().longValue())
			{
				
				return true;
			}
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	public int getSeqNo() 
	{		
		logger.info("Finding next seq");
		try
		{
			BigDecimal transaction = (BigDecimal)em.createNativeQuery("select SEQ_TASKCARDS_INGESTION.NextVal "
					+ "FROM DUAL").getSingleResult();		
			return transaction.intValue();			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return 1;
		}
		
	}
	
	public void resetSeqNo() 
	{		
		logger.info("Reset seq");
		try
		{
			em.createNativeQuery("ALTER SEQUENCE SEQ_TASKCARDS_INGESTION RESTART WITH 1 ").getSingleResult();		
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			
		}
		
	}
	
	public void lockTable(String notificationType)
	{
		em.getTransaction().begin();
		lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
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
		em.lock(lock, LockModeType.NONE);
		em.merge(lock);
		em.getTransaction().commit();
	}
	
	public void unlockTable(String notificationType)
	{
		em.getTransaction().begin();
		lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.getSingleResult();
		lock.setLocked(new BigDecimal(0));
		lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
		em.lock(lock, LockModeType.NONE);
		em.merge(lock);
		em.getTransaction().commit();
	}
		
	
}
