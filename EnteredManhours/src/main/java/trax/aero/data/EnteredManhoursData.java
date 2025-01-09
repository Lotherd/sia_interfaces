package trax.aero.data;


import java.io.ByteArrayInputStream;
import java.io.StringReader;
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
import java.util.Collections;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import trax.aero.controller.EnteredManhoursController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.MT_TRAX_RCV_I84_4071_RES;
import trax.aero.pojo.MT_TRAX_SND_I84_4071_REQ;
import trax.aero.pojo.OperationsAudit;
import trax.aero.pojo.OperationsREQ;
import trax.aero.pojo.OperationsRES;
import trax.aero.pojo.OpsLineEmail;
import trax.aero.pojo.OrderAudit;
import trax.aero.pojo.OrderREQ;
import trax.aero.pojo.OrderRES;
import trax.aero.utils.DataSourceClient;
import trax.aero.utils.ErrorType;


/*
SELECT REFERENCE_TASK_CARD,TASK_CARD_DESCRIPTION,PRIORITY,WO,TASK_CARD,STATUS,OPS_LINE FROM WO_TASK_CARD WHERE interface_transfer_date IS NULL;
 
 SELECT EXTERNAL_CUST_REF,TASK_CARD_TEXT,MAN_REQUIRE,MAN_HOURS,TASK_CARD_ITEM,TASK_CARD_TEXT_BLOB  FROM WO_TASK_CARD_ITEM WHERE WO = ? AND TASK_CARD = ?; 
 
UPDATE
WO_TASK_CARD
SET
WO_TASK_CARD.INTERFACE_MODIFIED_DATE = sysdate
WHERE
WO_TASK_CARD.INTERFACE_MODIFIED_DATE IS NULL AND
WO_TASK_CARD.REFERENCE_TASK_CARD = ?
 
 
 
SELECT rm.RELATION_CODE,rm.NAME,w.ops_line FROM WO w, WO_TASK_CARD wtc, WO_TASK_CARD_ITEM wtci, WO_ACTUALS wa,  relation_master rm
WHERE wtci.WO = '608670' AND
wtci.task_card = 'DHC-8-3-2150/05' AND
wtci.task_card_item = '1'AND
wtci.wo = wtc.wo and
wtci.task_card = wtc.task_card and
wtci.wo = w.wo and
wtci.wo = wa.wo  AND
wa.employee = rm.relation_code 
 
 */


public class EnteredManhoursData {
	EntityManagerFactory factory;
	EntityManager em;
	String exceuted;
	private Connection con;
	//final String BooleanMaxRecord = System.getProperty("EnteredManhours_BooleanMaxRecord");
	final String MaxRecord = System.getProperty("EnteredManhours_MaxRecord");
	Logger logger = LogManager.getLogger("EnteredManhours_I84");
	//public InterfaceLockMaster lock;
	
	public EnteredManhoursData(String mark)
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
			EnteredManhoursController.addError(e.toString());
			
		}
		catch (CustomizeHandledException e1) {
			EnteredManhoursController.addError(e1.toString());
		} catch (Exception e) {
			EnteredManhoursController.addError(e.toString());
			
		}
		
		
		
	}
	
	
	public EnteredManhoursData()
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
			EnteredManhoursController.addError(e.toString());
			
		}
		catch (CustomizeHandledException e1) {
			EnteredManhoursController.addError(e1.toString());
		} catch (Exception e) {
			EnteredManhoursController.addError(e.toString());
			
		}
		
		factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
		em = factory.createEntityManager();
		
	}
	
	public Connection getCon() {
		return con;
	}
	
	
	public String markTransaction(MT_TRAX_RCV_I84_4071_RES request) throws Exception
	{
		//setting up variables
		exceuted = "OK";
		
		String sqlDate =
		"UPDATE WO_TASK_CARD SET INTERFACE_TRANSFERRED_DATE = sysdate, INTERFACE_FLAG = null WHERE INTERFACE_TRANSFERRED_DATE IS NULL AND TASK_CARD = ? AND WO = ?";
		
		String sqlOps = "UPDATE WO_TASK_CARD_ITEM SET OPS_NO = ? WHERE TASK_CARD = ? AND WO = ? AND TASK_CARD_ITEM =?";
				
		
		PreparedStatement pstmt2 = null; 
		
		PreparedStatement pstmt3 = null; 

		try 
		{
			
				pstmt2 = con.prepareStatement(sqlDate);
				pstmt3 = con.prepareStatement(sqlOps);
				
				for(OrderRES r : request.getOrder()) {
					if(r.getOperations() != null && !r.getOperations().isEmpty()) {
						for(OperationsRES o : r.getOperations()) {
							pstmt2.setString(1, r.getTaskCard());
							pstmt2.setString(2, r.getWO());
							pstmt2.executeQuery();
							
							
							if(o.getOperationNumber() != null && !o.getOperationNumber().isEmpty()) {
								pstmt3.setString(1,o.getOperationNumber());
								pstmt3.setString(2, r.getTaskCard());
								pstmt3.setString(3, r.getWO());
								pstmt3.setString(4, o.getTRAXItemNumber());
								pstmt3.executeQuery();
							}
							
							if(!r.getErrorCode().equalsIgnoreCase("53")) {										
								exceuted = " Request SAPOrderNumber: " + r.getSAPOrderNumber() + " ,ErrorCode: " + r.getErrorCode() + " ,Remarks: " + r.getRemarks() + " ,TRAXItemNumber: " + o.getTRAXItemNumber() + " ,OperationNumber: " + o.getOperationNumber();
								EnteredManhoursController.addError(exceuted);    			           
							}
						}
					}else {
						pstmt2.setString(1, r.getTaskCard());
						pstmt2.setString(2, r.getWO());
						pstmt2.executeQuery();
						
						
						
						if(!r.getErrorCode().equalsIgnoreCase("53")) {
							exceuted = " Request SAPOrderNumber: " + r.getSAPOrderNumber() + " ,ErrorCode: " + r.getErrorCode() + " ,Remarks: " + r.getRemarks();
							EnteredManhoursController.addError(exceuted);    			           
						}
						
						
					}
				}
				
			
		}
		catch (Exception e) 
        {
			e.printStackTrace();
			exceuted = e.toString();
			EnteredManhoursController.addError(exceuted);
			logger.severe(exceuted);
            
            
		}finally {
			if(pstmt2 != null && !pstmt2.isClosed())
				pstmt2.close();
			if(pstmt3 != null && !pstmt3.isClosed())
				pstmt3.close();
		}
		
		return exceuted;
		
	}
	
	public ArrayList<MT_TRAX_SND_I84_4071_REQ> getTaskCards() throws Exception
	{
		//setting up variables
		exceuted = "OK";
		
		
		ArrayList<MT_TRAX_SND_I84_4071_REQ> list = new ArrayList<MT_TRAX_SND_I84_4071_REQ>();
		ArrayList<OperationsREQ> oplist = new ArrayList<OperationsREQ>();
		ArrayList<OrderREQ> orlist = new ArrayList<OrderREQ>();
		
		
		
		String sqlTaskCard =
		"SELECT\r\n" + 
		"    reference_task_card,\r\n" + 
		"    task_card_description,\r\n" + 
		"    priority,\r\n" + 
		"    wo,\r\n" + 
		"    task_card,\r\n" + 
		"    status,\r\n" + 
		"    (\r\n" + 
		"        SELECT\r\n" + 
		"            w.refurbishment_order\r\n" + 
		"        FROM\r\n" + 
		"            wo w\r\n" + 
		"        WHERE\r\n" + 
		"                w.wo = wo_task_card.wo\r\n" + 
		"            AND w.module = 'SHOP'\r\n" + 
		"            AND wo_task_card.interface_flag IS NOT NULL\r\n" + 
		"            AND ( wo_task_card.non_routine = 'N'\r\n" + 
		"                  OR wo_task_card.non_routine IS NULL )\r\n" + 
		"    ) AS rfo,\r\n" + 
		"    pn,\r\n" + 
		"    pn_sn,\r\n" + 
		"    ac\r\n" + 
		"FROM\r\n" + 
		"    wo_task_card\r\n" + 
		"WHERE\r\n" + 
		"    interface_transferred_date IS NULL\r\n" + 
		"    AND ( reference_task_card IS NOT NULL\r\n" + 
		"          OR 1 = (\r\n" + 
		"        SELECT\r\n" + 
		"            COUNT(*)\r\n" + 
		"        FROM\r\n" + 
		"            wo w\r\n" + 
		"        WHERE\r\n" + 
		"                w.wo = wo_task_card.wo\r\n" + 
		"            AND w.module = 'SHOP'\r\n" + 
		"            AND wo_task_card.interface_flag IS NOT NULL\r\n" + 
		"            AND w.refurbishment_order IS NOT NULL\r\n" + 
		"            AND ( wo_task_card.non_routine = 'N'\r\n" + 
		"                  OR wo_task_card.non_routine IS NULL )\r\n" + 
		"    ) )\r\n" + 
		"    AND ( non_routine = 'N'\r\n" + 
		"          OR non_routine = 'Y'\r\n" + 
		"          OR non_routine IS NULL )";
		
		if(MaxRecord != null && !MaxRecord.isEmpty()) {
			sqlTaskCard=  "SELECT *	FROM (" + sqlTaskCard;
		}
		
		if(MaxRecord != null && !MaxRecord.isEmpty()) {
			sqlTaskCard= sqlTaskCard + "  )WHERE ROWNUM <= ?";		
		}
		
		String sqlItem = " SELECT OPS_NO,TASK_CARD_TEXT,MAN_REQUIRE,(MAN_HOURS * MAN_REQUIRE) ,TASK_CARD_ITEM,(INSPECTOR_MAN_HOURS * INSPECTOR_MAN_REQUIRE ),(DUAL_INSPECTOR_MAN_HOURS * DUAL_INSPECTOR_MAN_REQUIRE)  FROM WO_TASK_CARD_ITEM WHERE WO = ? AND TASK_CARD = ? " + 
				" AND TASK_CARD_PN = ? AND TASK_CARD_PN_SN = ? AND AC = ?";
		
		//EO = TASK CARD AND ORDER_NUMBER = WO 
		String sqlItemAudit = "SELECT INTD.xml_document,INTA.TRANSACTION  FROM INTERFACE_AUDIT INTA, INTERFACE_DATA INTD \r\n" + 
				"WHERE INTA.TRANSACTION = INTD.TRANSACTION AND INTA.ORDER_NUMBER = ? AND INTA.EO = ? \r\n" + 
				"AND INTA.TRANSACTION_METHOD = 'DELETE' AND INTA.TRANSACTION_OBJECT = 'TRAX_84_ITEM_XML' AND INTA.MESSAGE_WAS_SENT = 'N'" ;
		
		
		String sqlWork = " SELECT SUM( REPORTED_HOURS), SUM(  REPORTED_MINUTES) FROM WO_ACTUALS WHERE TASK_CARD  = ? AND WO = ?  AND TRASACTION_CATEGORY = 'LABOR'  ";
		
		//EO = TASK CARD AND ORDER_NUMBER = WO 
		String sqlTaskAudit = "SELECT INTD.xml_document,INTA.TRANSACTION  FROM INTERFACE_AUDIT INTA, INTERFACE_DATA INTD \r\n" + 
				"WHERE INTA.TRANSACTION = INTD.TRANSACTION  \r\n" + 
				"AND INTA.TRANSACTION_METHOD = 'DELETE' AND INTA.TRANSACTION_OBJECT = 'TRAX_84_XML'\r\n" + 
				"AND INTA.MESSAGE_WAS_SENT = 'N'";
		
		String sqlAudit = "UPDATE INTERFACE_AUDIT SET MESSAGE_WAS_SENT = 'Y' WHERE TRANSACTION = ?";
		
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;

		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		
		PreparedStatement pstmt3 = null;
		ResultSet rs3 = null;
		
		PreparedStatement pstmt4 = null;
		ResultSet rs4 = null;
		
		PreparedStatement pstmt5 = null;
		ResultSet rs5 = null;
		
		PreparedStatement pstmt6 = null; 
		
		try 
		{
			pstmt1 = con.prepareStatement(sqlTaskCard);
			pstmt2 = con.prepareStatement(sqlItem);
			pstmt3 = con.prepareStatement(sqlWork);
			pstmt4 = con.prepareStatement(sqlItemAudit);
			pstmt5 = con.prepareStatement(sqlTaskAudit);
			pstmt6 = con.prepareStatement(sqlAudit);
			
			
			if(MaxRecord != null && !MaxRecord.isEmpty()) {
				pstmt1.setString(1, MaxRecord);
			}
			
			
			rs1 = pstmt1.executeQuery();

			if (rs1 != null) 
			{
				while (rs1.next()) // LOOP EACH LINE
				{
					logger.info("Processing WO Task Card: " + rs1.getString(5) +" , WO:" + rs1.getString(4));
					MT_TRAX_SND_I84_4071_REQ req = new MT_TRAX_SND_I84_4071_REQ();
					orlist = new ArrayList<OrderREQ>();
					req.setOrder(orlist);
					OrderREQ Inbound = new OrderREQ();
					oplist = new ArrayList<OperationsREQ>();
					Inbound.setOperations(oplist);
						
					if(rs1.getString(1) != null && !rs1.getNString(1).isEmpty()) {
						Inbound.setSAPOrderNumber(rs1.getString(1));
					}
					else {
						Inbound.setSAPOrderNumber("");
					}
					
					if(rs1.getString(2) != null && !rs1.getNString(2).isEmpty()) {
						Inbound.setDescription(rs1.getString(2));
					}
					else {
						Inbound.setDescription("");
					}
					if(rs1.getString(3) != null && !rs1.getNString(3).isEmpty()) {
						Inbound.setPriority(rs1.getString(3));
					}
					else {
						Inbound.setPriority("");
					}
					
					
					
					Inbound.setWO(rs1.getString(4));
					Inbound.setTaskCard(rs1.getString(5));
					
					if(rs1.getString(7) != null && !rs1.getNString(7).isEmpty()) {
						logger.info("Using RFO: " + rs1.getString(7) + " ,WO Task Card: " + rs1.getString(5) +" , WO:" + rs1.getString(4));
						
						Inbound.setSAPOrderNumber(rs1.getString(7));
					}
					
					pstmt2.setString(1, Inbound.getWO());
					pstmt2.setString(2, Inbound.getTaskCard());
					pstmt2.setString(3, rs1.getString(8));
					pstmt2.setString(4, rs1.getString(9));
					pstmt2.setString(5, rs1.getString(10));
					
					rs2 = pstmt2.executeQuery();

					if (rs2 != null) 
					{
						while (rs2.next()) // LOOP EACH LINE
						{
							logger.info("Processing WO Task Card Item: " + rs2.getString(5));
							OperationsREQ InboundItem = new OperationsREQ();
					
							if(rs2.getString(1) != null && !rs2.getNString(1).isEmpty()) {
								InboundItem.setOperationNumber(rs2.getString(1));
							}
							else {
								InboundItem.setOperationNumber("");
							}
							
							if(rs2.getString(2) != null && !rs2.getNString(2).isEmpty()) {
								RTFEditorKit rtfParser = new RTFEditorKit();
								Document document = rtfParser.createDefaultDocument();
								rtfParser.read(new ByteArrayInputStream(rs2.getNString(2).getBytes()), document, 0);
								String text = document.getText(0, document.getLength());
								
								
								InboundItem.setOperationDescription(text);
							}
							else {
								InboundItem.setOperationDescription("");
							}
							
							Integer work = 0;
							
							if(rs2.getString(4) != null && !rs2.getNString(4).isEmpty()) {
								work = work + new BigDecimal(rs2.getString(4)).intValue();
							}
							if(rs2.getString(6) != null && !rs2.getNString(6).isEmpty()) {
								work = work + new BigDecimal(rs2.getString(6)).intValue();
							}							
							if(rs2.getString(7) != null && !rs2.getNString(7).isEmpty()) {
								work = work + new BigDecimal(rs2.getString(7)).intValue();
							}
							if(!work.equals(0)) {						
								InboundItem.setWork(work.toString());
							}else {
								InboundItem.setWork("");
							}
							
							
							InboundItem.setDeletionFlag("");
							
							
							
							
							if(rs2.getString(5) != null && !rs2.getString(5).isEmpty()) {
								InboundItem.setTRAXItemNumber(rs2.getString(5));
							}
							else {
								InboundItem.setTRAXItemNumber("");
							}
							
							Integer hours = 0;
							Integer min = 0;
							
							pstmt3.setString(1, Inbound.getTaskCard());
							pstmt3.setString(2, Inbound.getWO());
							
							rs3 = pstmt3.executeQuery();

							if (rs3 != null) 
							{
								while (rs3.next()) // LOOP EACH LINE
								{
									if(rs3.getString(1) != null && !rs3.getString(1).isEmpty()) {
										hours = hours + new BigDecimal(rs3.getString(1)).intValue();
									}
									if(rs3.getString(2) != null && !rs3.getString(2).isEmpty()) {
										min = min +  + new BigDecimal(rs3.getString(2)).intValue();
									}								
								}
							}
							if(rs3 != null && !rs3.isClosed())
								rs3.close();
							
							String manHours = "hh.mm";
							String hour = "00";
							String minute = "0";
							
							if(hours.intValue() != 0 ) {
								hour= hours.toString();
							}
							if(min.intValue() !=0) {
								minute = min.toString();
							}
							manHours=manHours.replaceAll("hh", hour);
							manHours=manHours.replaceAll("mm", minute);
							if(InboundItem.getTRAXItemNumber().equalsIgnoreCase("1")) {
								InboundItem.setEnteredManHours(manHours);
							}else {
								InboundItem.setEnteredManHours("");
							}
							
							
							boolean match = false;
							for (OperationsREQ item : Inbound.getOperations()) {
								if(item.getOperationNumber() != null && !item.getOperationNumber().isEmpty()
									&& InboundItem.getOperationNumber() != null && !InboundItem.getOperationNumber().isEmpty()
									&& item.getOperationNumber().equalsIgnoreCase(InboundItem.getOperationNumber())) {
									
									//BigDecimal man = new BigDecimal(InboundItem.getEnteredManHours());
									//BigDecimal newMan = new BigDecimal( item.getEnteredManHours()).add(man);
									//item.setEnteredManHours(newMan.toString());
									
									if(InboundItem.getWork() != null &&  !InboundItem.getWork().isEmpty() && (item.getWork() != null &&  !item.getWork().isEmpty())) {
										BigDecimal newWork = new BigDecimal(item.getWork()).add(new BigDecimal(InboundItem.getWork()));
										item.setWork(newWork.toString());
									}else if((InboundItem.getWork() != null &&  !InboundItem.getWork().isEmpty()) && (item.getWork() == null ||  item.getWork().isEmpty())){
										item.setWork(InboundItem.getWork());
									}
									
									match= true;									
								}							
							}
							if(match) {
								continue;
							}else {
								Inbound.getOperations().add(InboundItem);
							}
						}
					}
					if(rs2 != null && !rs2.isClosed())
						rs2.close();
					
					
					pstmt4.setString(1, Inbound.getWO());
					pstmt4.setString(2, Inbound.getTaskCard());
					
					rs4 = pstmt4.executeQuery();

					if (rs4 != null) 
					{
						while (rs4.next()) // LOOP EACH LINE
						{
							logger.info("Processing WO Task Card Item audit delete transaction: " + rs4.getString(2));
							OperationsREQ InboundItem = new OperationsREQ();
							OperationsAudit InboundItemAudit = new OperationsAudit();
							
							String xml = null;
							
							if(rs4.getSQLXML(1) != null) {
								xml = rs4.getSQLXML(1).getString();
								xml = xml.replaceAll("\u200B", "").trim();
								xml = xml.replaceAll("[\\p{Cf}]", "");
								
							}
							if(xml == null) {
								continue;
							}else {
								StringReader sr = new StringReader(xml);				
								JAXBContext jc = JAXBContext.newInstance(OperationsAudit.class);
						        Unmarshaller unmarshaller = jc.createUnmarshaller();
						        InboundItemAudit = (OperationsAudit) unmarshaller.unmarshal(sr);
						        

							}
							
							pstmt6.setString(1, rs4.getString(2));
							
							pstmt6.executeQuery();
							
							if(InboundItemAudit.getOperationNumber() != null && !InboundItemAudit.getOperationNumber().isEmpty()) {
								InboundItem.setOperationNumber(InboundItemAudit.getOperationNumber());
							}
							else {
								InboundItem.setOperationNumber("");
							}
							
							if(InboundItemAudit.getOperationDescription() != null && !InboundItemAudit.getOperationDescription().isEmpty()) {
								RTFEditorKit rtfParser = new RTFEditorKit();
								Document document = rtfParser.createDefaultDocument();
								rtfParser.read(new ByteArrayInputStream(InboundItemAudit.getOperationDescription().getBytes()), document, 0);
								String text = document.getText(0, document.getLength());
								
								
								InboundItem.setOperationDescription(text);
							}
							else {
								InboundItem.setOperationDescription("");
							}
													
							if(InboundItemAudit.getWork() != null && !InboundItemAudit.getWork().isEmpty() ) {						
								InboundItem.setWork(InboundItemAudit.getWork());
							}else {
								InboundItem.setWork("");
							}
							
							InboundItem.setDeletionFlag("X");
							
							if(InboundItemAudit.getTRAXItemNumber() != null && !InboundItemAudit.getTRAXItemNumber().isEmpty()) {
								InboundItem.setTRAXItemNumber(InboundItemAudit.getTRAXItemNumber());
							}
							else {
								InboundItem.setTRAXItemNumber("");
							}
							
							Integer hours = 0;
							Integer min = 0;
							
							pstmt3.setString(1, Inbound.getTaskCard());
							pstmt3.setString(2, Inbound.getWO());
							
							rs3 = pstmt3.executeQuery();

							if (rs3 != null) 
							{
								while (rs3.next()) // LOOP EACH LINE
								{
									if(rs3.getString(1) != null && !rs3.getString(1).isEmpty()) {
										hours = hours + new BigDecimal(rs3.getString(1)).intValue();
									}
									if(rs3.getString(2) != null && !rs3.getString(2).isEmpty()) {
										min = min +  + new BigDecimal(rs3.getString(2)).intValue();
									}								
								}
							}
							if(rs3 != null && !rs3.isClosed())
								rs3.close();
							
							String manHours = "hh.mm";
							String hour = "00";
							String minute = "0";
							
							if(hours.intValue() != 0 ) {
								hour= hours.toString();
							}
							if(min.intValue() !=0) {
								minute = min.toString();
							}
							manHours=manHours.replaceAll("hh", hour);
							manHours=manHours.replaceAll("mm", minute);
							
							if(InboundItem.getTRAXItemNumber().equalsIgnoreCase("1")) {
								InboundItem.setEnteredManHours(manHours);
							}else {
								InboundItem.setEnteredManHours("");
							}							
							Inbound.getOperations().add(InboundItem);
						}
					}
					if(rs4 != null && !rs4.isClosed())
						rs4.close();
					
					Collections.sort(Inbound.getOperations());
					
					req.getOrder().add(Inbound);
					list.add(req);
						
					
				}
			}
			
			rs5 = pstmt5.executeQuery();

			if (rs5 != null) 
			{
				while (rs5.next()) // LOOP EACH LINE
				{
					MT_TRAX_SND_I84_4071_REQ req = new MT_TRAX_SND_I84_4071_REQ();
					orlist = new ArrayList<OrderREQ>();
					req.setOrder(orlist);
					OrderREQ Inbound = new OrderREQ();
					OrderAudit orAudit = new OrderAudit();
					
					
					oplist = new ArrayList<OperationsREQ>();
					Inbound.setOperations(oplist);
					
					
					
					String xml = null;
					
					if(rs5.getSQLXML(1) != null) {
						xml = rs5.getSQLXML(1).getString();
						xml = xml.replaceAll("\u200B", "").trim();
						xml = xml.replaceAll("[\\p{Cf}]", "");
						
					}
					if(xml == null) {
						continue;
					}else {
						StringReader sr = new StringReader(xml);				
						JAXBContext jc = JAXBContext.newInstance(OrderAudit.class);
				        Unmarshaller unmarshaller = jc.createUnmarshaller();
				        orAudit = (OrderAudit) unmarshaller.unmarshal(sr);


					}
					
					
					if(orAudit.getDescription() != null && !orAudit.getDescription().isEmpty()) {
						Inbound.setDescription(orAudit.getDescription());
					}
					else {
						Inbound.setDescription("");
					}
					if(orAudit.getPriority() != null && !orAudit.getPriority().isEmpty()) {
						Inbound.setPriority(orAudit.getPriority());
					}
					else {
						Inbound.setPriority("");
					}
					
					
					
					Inbound.setWO(orAudit.getWO());
					Inbound.setTaskCard(orAudit.getTaskCard());
					
					if(orAudit.getSAPOrderNumber() != null && !orAudit.getSAPOrderNumber().isEmpty()) {
						
						Inbound.setSAPOrderNumber(orAudit.getSAPOrderNumber());
					}else {
						continue;
					}
					logger.info("Processing AUDIT WO Task Card: " + orAudit.getTaskCard() +" , WO:" + orAudit.getWO());
					
					
					pstmt4.setString(1, Inbound.getWO());
					pstmt4.setString(2, Inbound.getTaskCard());
					
					rs4 = pstmt4.executeQuery();

					if (rs4 != null) 
					{
						while (rs4.next()) // LOOP EACH LINE
						{
							logger.info("Processing WO Task Card Item audit delete transaction: " + rs4.getString(2));
							OperationsREQ InboundItem = new OperationsREQ();
							OperationsAudit InboundItemAudit = new OperationsAudit();
							
							xml = null;
							
							if(rs4.getSQLXML(1) != null) {
								xml = rs4.getSQLXML(1).getString();
								xml = xml.replaceAll("\u200B", "").trim();
								xml = xml.replaceAll("[\\p{Cf}]", "");
								
							}
							if(xml == null) {
								continue;
							}else {
								StringReader sr = new StringReader(xml);				
								JAXBContext jc = JAXBContext.newInstance(OperationsAudit.class);
						        Unmarshaller unmarshaller = jc.createUnmarshaller();
						        InboundItemAudit = (OperationsAudit) unmarshaller.unmarshal(sr);
						        

							}
							
							pstmt6.setString(1, rs4.getString(2));
							
							pstmt6.executeQuery();
							
							if(InboundItemAudit.getOperationNumber() != null && !InboundItemAudit.getOperationNumber().isEmpty()) {
								InboundItem.setOperationNumber(InboundItemAudit.getOperationNumber());
							}
							else {
								InboundItem.setOperationNumber("");
							}
							
							if(InboundItemAudit.getOperationDescription() != null && !InboundItemAudit.getOperationDescription().isEmpty()) {
								RTFEditorKit rtfParser = new RTFEditorKit();
								Document document = rtfParser.createDefaultDocument();
								rtfParser.read(new ByteArrayInputStream(InboundItemAudit.getOperationDescription().getBytes()), document, 0);
								String text = document.getText(0, document.getLength());
								
								
								InboundItem.setOperationDescription(text);
							}
							else {
								InboundItem.setOperationDescription("");
							}
													
							if(InboundItemAudit.getWork() != null && !InboundItemAudit.getWork().isEmpty() ) {						
								InboundItem.setWork(InboundItemAudit.getWork());
							}else {
								InboundItem.setWork("");
							}
							
							InboundItem.setDeletionFlag("X");
							
							if(InboundItemAudit.getTRAXItemNumber() != null && !InboundItemAudit.getTRAXItemNumber().isEmpty()) {
								InboundItem.setTRAXItemNumber(InboundItemAudit.getTRAXItemNumber());
							}
							else {
								InboundItem.setTRAXItemNumber("");
							}
							
							Integer hours = 0;
							Integer min = 0;
							
							pstmt3.setString(1, Inbound.getTaskCard());
							pstmt3.setString(2, Inbound.getWO());
							
							rs3 = pstmt3.executeQuery();

							if (rs3 != null) 
							{
								while (rs3.next()) // LOOP EACH LINE
								{
									if(rs3.getString(1) != null && !rs3.getString(1).isEmpty()) {
										hours = hours + new BigDecimal(rs3.getString(1)).intValue();
									}
									if(rs3.getString(2) != null && !rs3.getString(2).isEmpty()) {
										min = min +  + new BigDecimal(rs3.getString(2)).intValue();
									}								
								}
							}
							if(rs3 != null && !rs3.isClosed())
								rs3.close();
							
							String manHours = "hh.mm";
							String hour = "00";
							String minute = "0";
							
							if(hours.intValue() != 0 ) {
								hour= hours.toString();
							}
							if(min.intValue() !=0) {
								minute = min.toString();
							}
							manHours=manHours.replaceAll("hh", hour);
							manHours=manHours.replaceAll("mm", minute);
							
							if(InboundItem.getTRAXItemNumber().equalsIgnoreCase("1")) {
								InboundItem.setEnteredManHours(manHours);
							}else {
								InboundItem.setEnteredManHours("");
							}							
							Inbound.getOperations().add(InboundItem);
						}
					}
					if(rs4 != null && !rs4.isClosed())
						rs4.close();
					
					
					pstmt6.setString(1, rs5.getString(2));
					
					pstmt6.executeQuery();
					Collections.sort(Inbound.getOperations());
					
					req.getOrder().add(Inbound);
					list.add(req);
					
				}
			}	
			
			
			
		}
		catch (Exception e) 
        {
			e.printStackTrace();
			exceuted = e.toString();
			EnteredManhoursController.addError(e.toString());
            
            
            logger.severe(exceuted);
            
            throw new Exception("Issue found");
		}finally {
			if(rs1 != null && !rs1.isClosed())
				rs1.close();
			if(pstmt1 != null && !pstmt1.isClosed())
				pstmt1.close();
			if(pstmt2 != null && !pstmt2.isClosed())
				pstmt2.close();
			if(pstmt3 != null && !pstmt3.isClosed())
				pstmt3.close();
			if(pstmt4 != null && !pstmt4.isClosed())
				pstmt4.close();
			if(pstmt5 != null && !pstmt5.isClosed())
				pstmt5.close();
			if(rs5 != null && !rs5.isClosed())
				rs5.close();
			if(pstmt6 != null && !pstmt6.isClosed())
				pstmt6.close();
		}
		return list;
	}
	
	
	
	public String setOpsLine(String opsLine, String email) throws Exception{
		String Exceuted = "OK";
		
		String query = "INSERT INTO OPS_LINE_EMAIL_MASTER (OPS_LINE, \"EMAIL\") VALUES (?, ?)";
		
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.severe("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			ps.setString(1, opsLine);
			ps.setString(2, email);
			
			ps.executeUpdate();
		
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.severe("An error ocurrer trying to close the statement");
				
			}
		}
		
		
		return Exceuted;
		
		
	}
	
	public String deleteOpsLine( String opsLine) throws Exception{
		String Exceuted = "OK";
		

		
		String query = "DELETE OPS_LINE_EMAIL_MASTER where \"OPS_LINE\" = ?";
		
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			ps.setString(1, opsLine);
			ps.executeUpdate();		
			
			
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.severe("An error ocurrer trying to close the statement");
				
			}
		}
		
		
		return Exceuted;
		
		
		
	}
	
	
	public String getemailByOpsLine( String opsLine) throws Exception{
		
		ArrayList<String> groups = new ArrayList<String>();
		
		String query = "", group = "";
		if(opsLine != null && !opsLine.isEmpty()) {
			query = " Select \"EMAIL\", OPS_LINE FROM OPS_LINE_EMAIL_MASTER where OPS_LINE = ?";
		}else {
			query = " Select \"EMAIL\", OPS_LINE FROM OPS_LINE_EMAIL_MASTER";
		}
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			if(opsLine != null && !opsLine.isEmpty()) {
				ps.setString(1, opsLine);
			}
			
			ResultSet rs = ps.executeQuery();		
			
			if (rs != null) 
			{
				while (rs.next()) 
				{
					
				groups.add("OPS_LINE: "+rs.getString(2) + " EMAIL: " +rs.getString(1) );

				}
			}
			rs.close();
			
			
		}
		catch (SQLException sqle) 
		{
			logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
			throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
		}
		catch (NullPointerException npe) 
		{
			logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
			throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.severe("An error ocurrer trying to close the statement");
				
			}
		}
		
		for(String g : groups) {
			group = group + g +"\n";
			
		}
		
		return group;
		
	}
	

	
	
	
	
	
	public String getemailByOnlyOpsLine( String opsLine){
					
			String email = "ERROR";
			
			String query = " Select \"EMAIL\" FROM OPS_LINE_EMAIL_MASTER where OPS_LINE = ?";
			
			PreparedStatement ps = null;
				
			
			try
			{
				if(con == null || con.isClosed())
				{
					con = DataSourceClient.getConnection();
					logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
				}
				
				ps = con.prepareStatement(query);
				if(opsLine != null && !opsLine.isEmpty()) {
					ps.setString(1, opsLine);
				}
				
				ResultSet rs = ps.executeQuery();		
				
				if (rs != null) 
				{
					while (rs.next()) 
					{
						email = rs.getString(1);
					}
				}
				rs.close();
			}
			catch (Exception e) 
			{
				email = "ERROR";
			}
			finally 
			{
				try 
				{
					if(ps != null && !ps.isClosed())
						ps.close();
				} 
				catch (SQLException e) 
				{ 
					logger.severe("An error ocurrer trying to close the statement");
				}
			}
			
			return email;
			
		}
	


	public OpsLineEmail getOpsLineStaffName(String wo, String taskCard, String item){
		
		String query = "";
		OpsLineEmail OpsLineEmail =  new OpsLineEmail();
		
		query = "SELECT rm.RELATION_CODE,rm.NAME,w.ops_line,wtc.INTERFACE_FLAG\r\n" + 
				"FROM WO w, WO_TASK_CARD wtc, WO_TASK_CARD_ITEM wtci, WO_ACTUALS wa,  relation_master rm \r\n" + 
				"WHERE w.WO = ? AND wtc.task_card = ? AND wtci.task_card_item = ? AND w.wo = wtc.wo and wtc.task_card = wtci.task_card and  wtc.wo = wtci.wo and wtc.task_card = wtci.task_card AND w.wo = wa.wo and wa.task_card = wtc.task_card and wa.task_card_item = wtci.task_card_item  AND wa.employee = rm.relation_code";
		
		PreparedStatement ps = null;
			
		
		try
		{
			
			
			if(con == null || con.isClosed())
			{
				con = DataSourceClient.getConnection();
				logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
			}
			
			ps = con.prepareStatement(query);
			
			ps.setString(1, wo);
			ps.setString(2, taskCard);
			ps.setString(3, item);
			
			ResultSet rs = ps.executeQuery();		
			
			if (rs != null) 
			{
				while (rs.next()) 
				{
					if(rs.getString(1) != null && !rs.getString(1).isEmpty()) {
						OpsLineEmail.setRelationCode(rs.getString(1));
					}else {
						OpsLineEmail.setRelationCode("");
					}
					
					if(rs.getString(2) != null && !rs.getString(2).isEmpty()) {
						OpsLineEmail.setName(rs.getString(2));
					}else {
						OpsLineEmail.setName("");
					}
					
					if(rs.getString(3) != null && !rs.getString(3).isEmpty()) {
						OpsLineEmail.setOpsLine(rs.getString(3));
					}else {
						OpsLineEmail.setOpsLine("");
					}
					
					OpsLineEmail.setEmail(getemailByOnlyOpsLine(OpsLineEmail.getOpsLine()));
					
					if(rs.getString(4) != null && !rs.getString(4).isEmpty()) {
						OpsLineEmail.setFlag(rs.getString(4));
					}else {
						OpsLineEmail.setFlag("");
					}
				}
			}
			rs.close();
		}
		
		catch (Exception e) 
		{
			logger.severe(e.toString());
			OpsLineEmail.setOpsLine("");
			OpsLineEmail.setName("");
			OpsLineEmail.setRelationCode("");
			OpsLineEmail.setFlag("");
		}
		finally 
		{
			try 
			{
				if(ps != null && !ps.isClosed())
					ps.close();
			} 
			catch (SQLException e) 
			{ 
				logger.severe("An error ocurrer trying to close the statement");
			}
		}
		
		return OpsLineEmail;
		
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
