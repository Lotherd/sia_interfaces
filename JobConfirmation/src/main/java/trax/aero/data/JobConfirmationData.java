package trax.aero.data;
import java.io.ByteArrayInputStream;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.beanutils.BeanUtils;


import trax.aero.controller.JobConfirmationController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.Inbound;
import trax.aero.pojo.Job;
import trax.aero.pojo.MasterInbound;
import trax.aero.pojo.MasterOutbound;
import trax.aero.pojo.Outbound;
import trax.aero.utils.DataSourceClient;


/*
 ALTER TABLE "WO_ACTUALS"
ADD ("INTERFACE_MODIFIED_DATE" DATE);
 
UPDATE 
WO_ACTUALS      
SET   
WO_ACTUALS.INTERFACE_MODIFIED_DATE = sysdate
WHERE
WO_ACTUALS.INTERFACE_MODIFIED_DATE IS NULL
  
  
  
SELECT  
						WTC.REFERENCE_TASK_CARD as Order_number,
						WTCI.EXTERNAL_CUST_REF as Operation_number, 
						WOA.employee as Personnel_number, 
						WOA.transaction_date as "Date",
						PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.START_HOUR) as StartHour,
						PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.START_MINUTE) as startminute,
						PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.END_HOUR) as endhour,
						PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.END_MINUTE) as endminute,
						PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.hours) as hours,
						PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.minutes) as minutes, 
						WOA.category as category,
						WTCI.WORK_ACCOMPLISHED as Confirmation_text, 
			            WTCI.TASK_CARD_TEXT as Defect_text,
						WOA.STATUS status, 
			            WOA.WO_ACTUAL_TRANSACTION, 
			            WOA.TASK_CARD, 
			            WOA.TASK_CARD_ITEM, 
			            WOA.WO,
                        RM.POSITION
						FROM 
						WO_ACTUALS WOA, 
			            WO_TASK_CARD_ITEM WTCI,
			            WO_TASK_CARD WTC, 
                        RELATION_MASTER RM
						where 
			            WOA.TASK_CARD = WTCI.TASK_CARD AND
			            WOA.TASK_CARD_ITEM = WTCI.TASK_CARD_ITEM AND
			            WOA.WO = WTCI.WO AND
			            WOA.TASK_CARD = WTC.TASK_CARD AND
			            WOA.WO = WTC.WO AND
                        WOA.EMPLOYEE = RM.RELATION_CODE AND 
                        RM.RELATION_TRANSACTION = 'EMPLOYEE' AND
						WOA.INTERFACE_MODIFIED_DATE IS NULL AND
						WOA.trasaction_category = 'LABOR';
  
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



public class JobConfirmationData {

		EntityManagerFactory factory;
		EntityManager em;
		String exceuted;
		private Connection con;
		final String BooleanMaxRecord = System.getProperty("JobConfirmation_BooleanMaxRecord");
		final String MaxRecord = System.getProperty("JobConfirmation_MaxRecord");
		Logger logger = LogManager.getLogger("JobConfirmation_I39I40");
		public InterfaceLockMaster lock;
		
		String xml10pattern = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";
		
		public JobConfirmationData(String mark)
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
				JobConfirmationController.addError(e.toString());
				
			}
			catch (CustomizeHandledException e1) {
				
				JobConfirmationController.addError(e1.toString());
				
			} catch (Exception e) {
				
				JobConfirmationController.addError(e.toString());
				
			}
		}
		
		public JobConfirmationData()
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
				JobConfirmationController.addError(e.toString());
				
			}
			catch (CustomizeHandledException e1) {
				
				JobConfirmationController.addError(e1.toString());
				
			} catch (Exception e) {
				
				JobConfirmationController.addError(e.toString());
				
			}
			factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
			em = factory.createEntityManager();
		}
		
		public Connection getCon() {
			return con;
		}
		
		
		public ArrayList<MasterInbound> getJobConfirmation() throws Exception
		{
			//setting up variables
			exceuted = "OK";
			String time = "hhmm00";
			Integer min = 0;
			Date Date;
			String date,currentDate,datebehind;
			
			String hour;
			String minute;
			
			ArrayList<MasterInbound> MasterInbounds = new ArrayList<MasterInbound>();
			MasterInbound MasterInbound = null;
			
			Inbound Inbound = null;
			ArrayList<Inbound>Inbounds =new ArrayList<Inbound>();
			
			String sql = 
			"SELECT NVL(WTC.REFERENCE_TASK_CARD,w.refurbishment_order) as Order_number, WTCI.OPS_NO as Operation_number, \r\n" + 
			"			WOA.employee as Personnel_number, WOA.transaction_date as \"Date\",PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.START_HOUR) as StartHour,\r\n" + 
			"			PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.START_MINUTE) as startminute, PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.END_HOUR) as endhour,\r\n" + 
			"			PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.END_MINUTE) as endminute, PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.hours) as hours,\r\n" + 
			"			PKG_INTERFACE.GETXMLNUMBERSTRING(WOA.minutes) as minutes, WOA.category as category, WTCI.WORK_ACCOMPLISHED as Confirmation_text, \r\n" + 
			"			WTCI.TASK_CARD_TEXT as Defect_text, WOA.STATUS status, WOA.WO_ACTUAL_TRANSACTION, WOA.TASK_CARD, WOA.TASK_CARD_ITEM, \r\n" + 
			"			WOA.WO,RM.Mechanic_Stamp, WTCI.STATUS as stat\r\n" + 
			"			FROM WO_ACTUALS WOA, WO_TASK_CARD_ITEM WTCI, WO_TASK_CARD WTC, RELATION_MASTER RM, WO W where \r\n" + 
			"			 WOA.TASK_CARD = WTCI.TASK_CARD AND WOA.TASK_CARD_ITEM = WTCI.TASK_CARD_ITEM AND\r\n" + 
			"			WOA.WO = WTCI.WO AND WOA.TASK_CARD = WTC.TASK_CARD AND WOA.WO = WTC.WO AND\r\n" + 
			"             WOA.TASK_CARD_PN = WTC.PN AND WOA.TASK_CARD_SN = WTC.PN_SN  AND WTC.PN = WTCI.TASK_CARD_PN AND \r\n" + 
			"            WTC.PN_SN = WTCI.TASK_CARD_PN_SN AND WTC.AC = WTCI.AC AND \r\n" + 
			"			WOA.EMPLOYEE = RM.RELATION_CODE AND WOA.WO = W.WO AND RM.RELATION_TRANSACTION = 'EMPLOYEE' AND\r\n" + 
			"			WOA.INTERFACE_MODIFIED_DATE IS NULL AND WOA.INTERFACE_MODIFIED_FLAG IS NULL AND WOA.trasaction_category = 'LABOR' AND (WTC.REFERENCE_TASK_CARD IS NOT NULL\r\n" + 
			"			OR 1=( SELECT count(*) FROM WO W WHERE W.WO = WTC.WO AND W.MODULE = 'SHOP' AND W.refurbishment_order is not null \r\n" + 
			"			AND (WTC.non_routine = 'N' OR WTC.non_routine IS NULL)))		";

			
			
			String sqlHeader = "SELECT\r\n" + 
					"    nvl(wtc.reference_task_card, w.refurbishment_order) AS order_number,\r\n" + 
					"    woa.employee                                        AS personnel_number,\r\n" + 
					"    woa.transaction_date                                AS \"Date\",\r\n" + 
					"    pkg_interface.getxmlnumberstring(woa.start_hour)    AS starthour,\r\n" + 
					"    pkg_interface.getxmlnumberstring(woa.start_minute)  AS startminute,\r\n" + 
					"    pkg_interface.getxmlnumberstring(woa.end_hour)      AS endhour,\r\n" + 
					"    pkg_interface.getxmlnumberstring(woa.end_minute)    AS endminute,\r\n" + 
					"    pkg_interface.getxmlnumberstring(woa.hours)         AS hours,\r\n" + 
					"    pkg_interface.getxmlnumberstring(woa.minutes)       AS minutes,\r\n" + 
					"    woa.category                                        AS category,\r\n" + 
					"    woa.status                                          AS \"status\",\r\n" + 
					"    woa.wo_actual_transaction,\r\n" + 
					"    woa.task_card,\r\n" + 
					"    woa.task_card_item,\r\n" + 
					"    woa.wo,\r\n" + 
					"    rm.mechanic_stamp,\r\n" + 
					"    wtc.work_accomplished,\r\n" + 
					"    wtc.reference_task_card,\r\n" + 
					"    w.refurbishment_order,\r\n" + 
					"    woa.task_card_pn,\r\n" + 
					"    woa.TASK_CARD_SN,\r\n" + 
					"    wtc.ac\r\n" + 
					"FROM\r\n" + 
					"    wo_actuals      woa,\r\n" + 
					"    wo_task_card    wtc,\r\n" + 
					"    relation_master rm,\r\n" + 
					"    wo              w\r\n" + 
					"WHERE\r\n" + 
					"        woa.task_card_item = '0'\r\n" + 
					"    AND woa.task_card = wtc.task_card\r\n" + 
					"    AND woa.wo = wtc.wo\r\n" + 
					"    AND woa.employee = rm.relation_code\r\n" + 
					"    AND woa.wo = w.wo\r\n" + 
					"    AND rm.relation_transaction = 'EMPLOYEE'\r\n" + 
					"    AND woa.interface_modified_date IS NULL\r\n" + 
					"    AND woa.interface_modified_flag IS NULL\r\n" + 
					"    AND woa.task_card_pn = wtc.pn\r\n" + 
					"    AND woa.task_card_sn = wtc.pn_sn\r\n" + 
					"    AND woa.trasaction_category = 'LABOR'\r\n" + 
					"    AND ( wtc.reference_task_card IS NOT NULL\r\n" + 
					"          OR 1 = (\r\n" + 
					"        SELECT\r\n" + 
					"            COUNT(*)\r\n" + 
					"        FROM\r\n" + 
					"            wo w\r\n" + 
					"        WHERE\r\n" + 
					"                w.wo = wtc.wo\r\n" + 
					"            AND w.module = 'SHOP'\r\n" + 
					"            AND w.refurbishment_order IS NOT NULL\r\n" + 
					"            AND ( wtc.non_routine = 'N'\r\n" + 
					"                  OR wtc.non_routine IS NULL )\r\n" + 
					"    ) )";
			
			String sqlItem = "SELECT\r\n" + 
					"					    wtci_out.ops_no                                   AS operation_number,\r\n" + 
					"					   (select \r\n" + 
					"                            wtciwc_in.work_accomplished \r\n" + 
					"                        from \r\n" + 
					"                            wo_task_card_item_wrk_acmplshd wtciwc_in\r\n" + 
					"                        where \r\n" + 
					"                            wtciwc_in.wo = wtci_out.wo \r\n" + 
					"                            and wtciwc_in.task_card =  wtci_out.task_card\r\n" + 
					"                            and wtciwc_in.task_card_item =  wtci_out.task_card_item\r\n" + 
					"                            and wtciwc_in.task_card_pn_sn =  wtci_out.task_card_pn_sn\r\n" + 
					"                            and wtciwc_in.ac = wtci_out.ac\r\n" + 
					"					        and wtciwc_in.task_card_pn = wtci_out.task_card_pn\r\n" + 
					"                        order by \r\n" + 
					"                            work_accomplished_line desc \r\n" + 
					"                        FETCH FIRST 1 ROWS ONLY)                       AS confirmation_text,\r\n" + 
					"					    dbms_lob.substr(wtci_out.task_card_text, 4000, 1) AS defect_text,\r\n" + 
					"					    wtci_out.status                                   AS stat,\r\n" + 
					"					    (\r\n" + 
					"					        SELECT\r\n" + 
					"					            COUNT(wtci_in.task_card_item) AS count\r\n" + 
					"					        FROM\r\n" + 
					"					            wo_task_card_item wtci_in\r\n" + 
					"					        WHERE\r\n" + 
					"					                wtci_in.wo = wtci_out.wo\r\n" + 
					"					            AND wtci_in.task_card = wtci_out.task_card\r\n" + 
					"					            AND wtci_in.task_card_pn_sn = wtci_out.task_card_pn_sn\r\n" + 
					"					            AND wtci_in.ac = wtci_out.ac\r\n" + 
					"					            AND wtci_in.task_card_pn =  wtci_out.task_card_pn\r\n" + 
					"					    )                                             AS count\r\n" + 
					"					FROM\r\n" + 
					"					    wo_task_card_item wtci_out\r\n" + 
					"					WHERE\r\n" + 
					"					        wtci_out.wo = ?\r\n" + 
					"					    AND wtci_out.task_card = ?\r\n" + 
					"					    AND wtci_out.task_card_pn_sn = ?\r\n" + 
					"					    AND wtci_out.ac = ?\r\n" + 
					"					    AND wtci_out.task_card_pn = ? ";
			
			
			
			if(Boolean.parseBoolean(BooleanMaxRecord)) {
				sql=  "SELECT *	FROM (" + sql;
				sqlHeader =  "SELECT *	FROM (" + sqlHeader;
				
			}
			
			if(Boolean.parseBoolean(BooleanMaxRecord)) {
				sql= sql + "  ) WHERE ROWNUM <= ?";
				sqlHeader= sqlHeader + "  ) WHERE ROWNUM <= ?";
			}
			
			
			PreparedStatement pstmt1 = null;
			ResultSet rs1 = null;
			PreparedStatement pstmt2 = null;
			ResultSet rs2 = null;
			
			
			
			try 
			{
				pstmt1 = con.prepareStatement(sql);
				
				
				if(Boolean.parseBoolean(BooleanMaxRecord)) {
					pstmt1.setString(1, MaxRecord);
				}
				
				rs1 = pstmt1.executeQuery();

				if (rs1 != null) 
				{
					//logger.info("Finding Job books at Item ");
					while (rs1.next()) // LOOP EACH INV LINE
					{
						logger.info("Processing WO: " + rs1.getString(18));
						Inbound = new Inbound();
						min = 0;
						
						if(rs1.getString(1) != null && !rs1.getNString(1).isEmpty()) {
							Inbound.setOrder_number(rs1.getString(1));
						}
						else {
							Inbound.setOrder_number("");
						}
						
						if(rs1.getString(2) != null && !rs1.getNString(2).isEmpty()) {
							Inbound.setOperation_number(rs1.getString(2));
						}
						else {
							Inbound.setOperation_number("");
						}
						
						
						
						Inbound.setPersonnel_number(rs1.getString(3));
						
						if(rs1.getNString(9) != null && !rs1.getNString(9).isEmpty()) {
							min = Integer.parseInt(rs1.getNString(9));
							min = min * 60;
						}
						if(rs1.getNString(10) != null && !rs1.getNString(10).isEmpty()) {
							min = min + Integer.parseInt(rs1.getNString(10));
						}
						
						
						Inbound.setActual_work(String.valueOf(min));
						Inbound.setUnit_for_work("MIN");
						
						
						
						String activity = ""; 
						
						if(rs1.getString(11) != null &&  rs1.getString(11).equals("REGULAR"))
						{
							if(rs1.getString(19) != null &&  rs1.getString(19).equals("INSPECTOR")) 
							{
								activity = "ENGNT";
							}else 
							{
								activity = "TECNT";
							}
							
						}
						else if(rs1.getString(11) != null &&  rs1.getString(11).equals("OVERTIME")) 
						{
							 if(rs1.getString(19) != null &&  rs1.getString(19).equals("INSPECTOR")) 
							{
								activity = "ENGOT";
							}else
							{
								activity = "TECOT";
							}
						}
						
						Inbound.setActivity_type_for_confirmation(activity);
						
						Date = rs1.getDate(4);

						Format formatter = new SimpleDateFormat("ddMMyy");
						 
						
						date = formatter.format(Date);
						
						currentDate = formatter.format(new Date());
						
						Inbound.setPosting_date(currentDate);
						
						String status = "";
						
						logger.info(rs1.getString(20));				
						if(rs1.getString(20) != null && rs1.getString(20).equalsIgnoreCase("COMPLETED")) {
							status = "X"; 
						}else {
							status = "";
						}
						Inbound.setFinal_confirmation(status);
						
						time = "hhmm00";
						Inbound.setActual_start_date(date);
						if(rs1.getString(5) != null && !rs1.getNString(5).isEmpty()) 
						{
							hour = String.format("%02d", Integer.parseInt(rs1.getString(5)));
						}
						else 
						{
							hour = "00";
						}
						if(rs1.getString(6) != null && !rs1.getNString(6).isEmpty()) 
						{
							minute = String.format("%02d", Integer.parseInt(rs1.getString(6)));	
						}
						else 
						{
							minute = "00";
						}
						time=time.replaceAll("hh", hour);
						time=time.replaceAll("mm", minute);
						Inbound.setActual_start_time(time);
						
						Inbound.setActual_finish_date(date);
						
						
						
						time = "hhmm00";
						
						if(rs1.getString(7) != null && !rs1.getNString(7).isEmpty()) 
						{
							hour = String.format("%02d", Integer.parseInt(rs1.getString(7)));
						}
						else 
						{
							hour = "00";
						}
						if(rs1.getString(8) != null && !rs1.getNString(8).isEmpty()) 
						{
							minute = String.format("%02d", Integer.parseInt(rs1.getString(8)));	
						}
						else 
						{
							minute = "00";
						}
						time=time.replaceAll("hh", hour);
						time=time.replaceAll("mm", minute);
						Inbound.setActual_finish_time(time);
						
						
						
						if(Inbound.getActual_start_time().compareTo(Inbound.getActual_finish_time()) > 0) {
							Calendar c = Calendar.getInstance();
							c.setTime(Date);
					        c.add(Calendar.DATE, -1); 
					        datebehind = formatter.format(c.getTime());
							Inbound.setActual_start_date(datebehind);
						}
						
						if(rs1.getString(12) != null && !rs1.getNString(12).isEmpty()) {
							String text = rs1.getNString(12).replaceAll("\\p{Cntrl}", "");
							text = text.replaceAll("\u0019", "");
							text =text.replaceAll(xml10pattern, "");
							Inbound.setConfirmation_text(text);
						}
						else {
							Inbound.setConfirmation_text("");
						}
						
						if(Inbound.getConfirmation_text() !=null && !Inbound.getConfirmation_text().isEmpty()) {
						
							Inbound.setDefect_text(Inbound.getConfirmation_text());
						
						
						}/*	else if(rs1.getString(13) != null && !rs1.getNString(13).isEmpty()) {
							RTFEditorKit rtfParser = new RTFEditorKit();
							Document document = rtfParser.createDefaultDocument();
							rtfParser.read(new ByteArrayInputStream(rs1.getString(13).getBytes()), document, 0);
							String text = document.getText(0, document.getLength());
							
							Inbound.setDefect_text(text);
						}
						*/
						else {
							Inbound.setDefect_text("");
						}
						
						Inbound.setWO_ActualTransaction(rs1.getNString(15));
						
						//BLANK
						Inbound.setWork_Center("");
						Inbound.setPlant("");
						Inbound.setIndicator_No_remaining_work("");
						Inbound.setAccounting_indicator("");
						Inbound.setGeneral_flag("");
																	
						MasterInbound = new MasterInbound();
						MasterInbound.setJobConfirmationInbounds(Inbound);
						MasterInbounds.add(MasterInbound);	
						
					}
				}
				if(rs1 != null && !rs1.isClosed())
					rs1.close();
				if(pstmt1 != null && !pstmt1.isClosed())
					pstmt1.close();
				
				
				
				pstmt1 = con.prepareStatement(sqlHeader);
				pstmt2 = con.prepareStatement(sqlItem);
				
				if(Boolean.parseBoolean(BooleanMaxRecord)) {
					pstmt1.setString(1, MaxRecord);
				}
				
				rs1 = pstmt1.executeQuery();

				
				
				if (rs1 != null) 
				{
					//logger.info("Finding Job books at Header ");
					while (rs1.next()) // LOOP EACH INV LINE
					{
						
						
						logger.info("Processing WO: " + rs1.getString(15));
						min = 0;
						Inbound = new Inbound();
						Inbounds =new ArrayList<Inbound>();

						Inbound.setOrder_number(rs1.getString(1));
						Inbound.setPersonnel_number(rs1.getString(2));
						
						String activity = ""; 
						
						if(rs1.getString(10) != null &&  rs1.getString(10).equals("REGULAR"))
						{
							if(rs1.getString(16) != null &&  rs1.getString(16).equals("INSPECTOR")) 
							{
								activity = "ENGNT";
							}else 
							{
								activity = "TECNT";
							}
							
						}
						else if(rs1.getString(10) != null &&  rs1.getString(10).equals("OVERTIME")) 
						{
							 if(rs1.getString(16) != null &&  rs1.getString(16).equals("INSPECTOR")) 
							{
								activity = "ENGOT";
							}else
							{
								activity = "TECOT";
							}
						}
						Inbound.setActivity_type_for_confirmation(activity);
						
						Date = rs1.getDate(3);
						Format formatter = new SimpleDateFormat("ddMMyy");
						date = formatter.format(Date);
						currentDate = formatter.format(new Date());
						Inbound.setPosting_date(currentDate);
						
						String status = "";
						
									
						
						
						time = "hhmm00";
						Inbound.setActual_start_date(date);
						if(rs1.getString(4) != null && !rs1.getNString(4).isEmpty()) 
						{
							hour = String.format("%02d", Integer.parseInt(rs1.getString(4)));
						}
						else 
						{
							hour = "00";
						}
						if(rs1.getString(5) != null && !rs1.getNString(5).isEmpty()) 
						{
							minute = String.format("%02d", Integer.parseInt(rs1.getString(5)));	
						}
						else 
						{
							minute = "00";
						}
						time=time.replaceAll("hh", hour);
						time=time.replaceAll("mm", minute);
						Inbound.setActual_start_time(time);
						
						Inbound.setActual_finish_date(date);
						
						time = "hhmm00";
						
						if(rs1.getString(6) != null && !rs1.getNString(6).isEmpty()) 
						{
							hour = String.format("%02d", Integer.parseInt(rs1.getString(6)));
						}
						else 
						{
							hour = "00";
						}
						if(rs1.getString(7) != null && !rs1.getNString(7).isEmpty()) 
						{
							minute = String.format("%02d", Integer.parseInt(rs1.getString(7)));	
						}
						else 
						{
							minute = "00";
						}
						time=time.replaceAll("hh", hour);
						time=time.replaceAll("mm", minute);
						Inbound.setActual_finish_time(time);
						
						
						if(Inbound.getActual_start_time().compareTo(Inbound.getActual_finish_time()) > 0) {
							Calendar c = Calendar.getInstance();
							c.setTime(Date);
					        c.add(Calendar.DATE, -1); 
					        datebehind = formatter.format(c.getTime());
							Inbound.setActual_start_date(datebehind);
						}
						
						Inbound.setWO_ActualTransaction(rs1.getNString(12));
						
						
						
						
						
						
						if(rs1.getNString(8) != null && !rs1.getNString(8).isEmpty()) {
							min = Integer.parseInt(rs1.getNString(8));
							min = min * 60;
						}
						if(rs1.getNString(9) != null && !rs1.getNString(9).isEmpty()) {
							min = min + Integer.parseInt(rs1.getNString(9));
						}
						
						
						if(rs1.getString(17) != null && !rs1.getNString(17).isEmpty()) {
							String text = rs1.getNString(17).replaceAll("\\p{Cntrl}", ""); 
							text = text.replaceAll("\u0019", "");
							text =text.replaceAll(xml10pattern, "");
							Inbound.setConfirmation_text(text);
							
						}
						else {
							Inbound.setConfirmation_text("");
						}
						 
						
						//ITEM
						
						//TODO
						pstmt2.setString(1, rs1.getNString(15));
						pstmt2.setString(2, rs1.getNString(13));
						pstmt2.setString(3, rs1.getNString(21));
						pstmt2.setString(4, rs1.getNString(22));
						pstmt2.setString(5, rs1.getNString(20));
						
						
						rs2 = pstmt2.executeQuery();
						
						if (rs2 != null) 
						{
							while (rs2.next()) // LOOP EACH INV LINE
							{
								
								
								Inbound Inbound2 = new Inbound();
								
								BeanUtils.copyProperties(Inbound2, Inbound);
								
								
								
								
								if(rs2.getString(1) != null && !rs2.getNString(1).isEmpty()) {
									logger.info("Processing Ops no: " + rs2.getString(1) );
									
									Inbound2.setOperation_number(rs2.getString(1));
								}
								else {
									Inbound2.setOperation_number("");
								}
								
								if(rs2.getString(2) != null && !rs2.getNString(2).isEmpty()) {
									String text = rs2.getNString(2).replaceAll("\\p{Cntrl}", ""); 
									text = text.replaceAll("\u0019", "");
									text =text.replaceAll(xml10pattern, "");
									Inbound2.setConfirmation_text(text);
									
								}
								
								
								if(rs2.getString(3) != null && !rs2.getNString(3).isEmpty()) {
									RTFEditorKit rtfParser = new RTFEditorKit();
									Document document = rtfParser.createDefaultDocument();
									rtfParser.read(new ByteArrayInputStream(rs2.getString(3).getBytes()), document, 0);
									String text = document.getText(0, document.getLength());
									text = text.replaceAll("\\p{Cntrl}", ""); 
									text = text.replaceAll("\u0019", "");
									text =text.replaceAll(xml10pattern, "");
									Inbound2.setDefect_text(text);
								}
								else {
									Inbound2.setDefect_text("");
								}
								
								
								
								if(rs2.getString(4) != null && rs2.getString(4).equalsIgnoreCase("COMPLETED")) {
									status = "X"; 
								}else {
									status = "";
								}
								Inbound2.setFinal_confirmation(status);
								
								
								int count = Integer.valueOf(rs2.getString(5));
								
								logger.info("min "+ min);
								
								int work = Math.round(min/count);
								
								logger.info("count "+ count);
								logger.info("work "+ work);
								if(work == 0) {
									work = 1;
								}
								Inbound2.setActual_work(String.valueOf(work));
								Inbound2.setUnit_for_work("MIN");
								
								
								//BLANK
								Inbound2.setWork_Center("");
								Inbound2.setPlant("");
								Inbound2.setIndicator_No_remaining_work("");
								Inbound2.setAccounting_indicator("");
								Inbound2.setGeneral_flag("");
								
																												
								
								Inbounds.add(Inbound2);
															
							}
						}	
						
						if(rs2 != null && !rs2.isClosed())
							rs2.close();
						for(Inbound i: Inbounds) {
							MasterInbound = new MasterInbound();
							MasterInbound.setJobConfirmationInbounds(i);
							MasterInbounds.add(MasterInbound);
						}
					}
					
				}
				if(rs1 != null && !rs1.isClosed())
					rs1.close();
				if(pstmt1 != null && !pstmt1.isClosed())
					pstmt1.close();
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
				
			}
			catch (Exception e) 
	        {
				exceuted = e.toString();
				e.printStackTrace();
				JobConfirmationController.addError(exceuted);
				logger.severe(exceuted);
	            
	            throw new Exception("Issue found");
			}
						
			//logger.info("size "  + MasterInbounds.size());
			
			Set<MasterInbound> s= new HashSet<MasterInbound>();
		    s.addAll(MasterInbounds);         
		    MasterInbounds = new ArrayList<MasterInbound>();
		    MasterInbounds.addAll(s);     
		   // logger.info("size After "  + MasterInbounds.size());
		    
		    String updateSentSql = "UPDATE WO_ACTUALS SET INTERFACE_MODIFIED_FLAG = 'S' WHERE WO_ACTUAL_TRANSACTION = ?";
		    PreparedStatement pstmtUpdate = null;

		    try {
		        pstmtUpdate = con.prepareStatement(updateSentSql);
		        
		        for(MasterInbound masterInbound : MasterInbounds) {
		            pstmtUpdate.setString(1, masterInbound.getJobConfirmationInbounds().getWO_ActualTransaction());
		            pstmtUpdate.executeUpdate();
		        }
		        
		       // logger.info("Marked " + MasterInbounds.size() + " records as sent (S)");
		        
		    } catch (Exception e) {
		        logger.severe("Error updating INTERFACE_MODIFIED_FLAG: " + e.toString());
		        JobConfirmationController.addError(e.toString());
		    } finally {
		        if(pstmtUpdate != null && !pstmtUpdate.isClosed())
		            pstmtUpdate.close();
		    }
		    
			return MasterInbounds;
		}
		
		
		public void markTransaction(MasterOutbound Outbound) throws Exception
		{
			//setting up variables
			exceuted = "OK";
			
			String sqlDate =
			"UPDATE \r\n" + 
			"WO_ACTUALS      \r\n" + 
			"SET   \r\n" + 
			"WO_ACTUALS.INTERFACE_MODIFIED_DATE = sysdate\r\n" + 
			"WHERE\r\n" + 
			"WO_ACTUALS.INTERFACE_MODIFIED_DATE IS NULL AND\r\n" + 
			"WO_ACTUALS.WO_ACTUAL_TRANSACTION = ?";
			
			PreparedStatement pstmt2 = null; 
			pstmt2 = con.prepareStatement(sqlDate);
			try 
			{
				
				for(Outbound outbound : Outbound.getJobConfirmationOutbounds()) 
				{
						pstmt2.setString(1, outbound.getWO_ActualTransaction());
						pstmt2.executeQuery();
					
				}
			}
			catch (Exception e) 
	        {
				exceuted = e.toString();
				JobConfirmationController.addError(exceuted);
	            logger.severe(exceuted);
	            
	            throw new Exception("Issue found");
			}finally {
				
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
				
			}
			
		}
		
		public ArrayList<Job> getInformationFromJob(MasterInbound masterInbound) {
			
			ArrayList<Job> jobs = new ArrayList<Job>();
			String sql = "SELECT WO,TASK_CARD,TASK_CARD_ITEM FROM WO_ACTUALS WHERE wo_actual_transaction = ?";
			PreparedStatement pstmt1 = null;
			ResultSet rs1 = null;
			try 
			{
				pstmt1 = con.prepareStatement(sql);
					
				Inbound i = masterInbound.getJobConfirmationInbounds();
					pstmt1.setString(1, i.getWO_ActualTransaction());
					rs1 = pstmt1.executeQuery();
					if (rs1 != null) 
					{
						while (rs1.next()) // LOOP EACH INV LINE
						{
							Job job = new Job();
							job.setWo(rs1.getString(1));
							job.setTask_Card(rs1.getString(2));
							job.setTask_Card_Item(rs1.getString(3));
							job.setWo_actual_transaction(i.getWO_ActualTransaction());
							job.setOrder_number(i.getOrder_number());
							job.setOperation_number(i.getOperation_number());
							job.setActual_finish_date(i.getActual_finish_date());
							job.setActual_finish_time(i.getActual_finish_time());
							job.setActual_start_date(i.getActual_start_date());
							job.setActual_start_time(i.getActual_start_time());
							job.setPosting_date(i.getPosting_date());
							job.setActual_work(i.getActual_work());
							jobs.add(job);
							
					}
					if(rs1 != null && !rs1.isClosed()) {
						rs1.close();
					}
				}
			}catch (Exception e){
				JobConfirmationController.addError(e.toString());
	            logger.severe(e.toString());
	       }finally {
				try {
				if(pstmt1 != null && !pstmt1.isClosed())
					pstmt1.close();
				
				}catch (Exception e) 
		        {
					JobConfirmationController.addError(e.toString());
		            logger.severe(e.toString());
		        }
			}
			return jobs;
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

		public void markTransactionIn(MasterInbound Inbound) throws Exception
		{
			//setting up variables
			exceuted = "OK";
			
			String sqlDate =
			"UPDATE \r\n" + 
			"WO_ACTUALS      \r\n" + 
			"SET   \r\n" + 
			"WO_ACTUALS.INTERFACE_MODIFIED_DATE = sysdate, \r\n" + 
			"WO_ACTUALS.INTERFACE_MODIFIED_FLAG = 'D' \r\n" + 
			"WHERE\r\n" +  
			"WO_ACTUALS.WO_ACTUAL_TRANSACTION = ?";
			
			PreparedStatement pstmt2 = null; 
			pstmt2 = con.prepareStatement(sqlDate);
			try 
			{
						pstmt2.setString(1, Inbound.getJobConfirmationInbounds().getWO_ActualTransaction());
						pstmt2.executeUpdate();		
			}
			catch (Exception e) 
	        {
				exceuted = e.toString();
				JobConfirmationController.addError(exceuted);
	            logger.severe(exceuted);
	            
	            throw new Exception("Issue found");
			}finally {
				
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
				
			}
			
		}

		public void unMarkTransaction(MasterOutbound Outbound) throws Exception {
			//setting up variables
			exceuted = "OK";
			
			String sqlDate =
			"UPDATE \r\n" + 
			"WO_ACTUALS      \r\n" + 
			"SET   \r\n" + 
			"WO_ACTUALS.INTERFACE_MODIFIED_DATE = null, \r\n" + 
			"WO_ACTUALS.INTERFACE_MODIFIED_FLAG = 'F' \r\n" + 
			"WHERE\r\n" + 
			"WO_ACTUALS.WO_ACTUAL_TRANSACTION = ?";
			
			PreparedStatement pstmt2 = null; 
			pstmt2 = con.prepareStatement(sqlDate);
			try 
			{
				for(Outbound outbound : Outbound.getJobConfirmationOutbounds()) 
				{
						pstmt2.setString(1, outbound.getWO_ActualTransaction());
						pstmt2.executeUpdate();	
				}
			}
			catch (Exception e) 
	        {
				exceuted = e.toString();
				JobConfirmationController.addError(exceuted);
	            logger.severe(exceuted);
	            throw new Exception("Issue found");
			}finally {
				if(pstmt2 != null && !pstmt2.isClosed())
					pstmt2.close();
			}
		}
		
		private static final int MAX_LOOP_ATTEMPTS = 3;
	    private static java.util.Map<String, Integer> loopAttempts = new java.util.concurrent.ConcurrentHashMap<>();
	    
	    public boolean loopMarkTransaction(MasterOutbound Outbound) throws Exception {
	        //setting up variables
	        exceuted = "OK";
	        boolean canRetry = true;
	        
	        try {
	            for(Outbound outbound : Outbound.getJobConfirmationOutbounds()) {
	                String transactionId = outbound.getWO_ActualTransaction();
	                
	                
	                int currentAttempts = loopAttempts.getOrDefault(transactionId, 0);
	                currentAttempts++;
	                
	                logger.info("Transaction " + transactionId + " - Loop attempt: " + currentAttempts + "/" + MAX_LOOP_ATTEMPTS);
	                
	                if (currentAttempts >= MAX_LOOP_ATTEMPTS) {
	                    
	                    String sqlFailed = 
	                    "UPDATE WO_ACTUALS \r\n" + 
	                    "SET INTERFACE_MODIFIED_DATE = sysdate, \r\n" + 
	                    "    INTERFACE_MODIFIED_FLAG = 'F' \r\n" + 
	                    "WHERE WO_ACTUAL_TRANSACTION = ?";
	                    
	                    try (PreparedStatement pstmtFailed = con.prepareStatement(sqlFailed)) {
	                        pstmtFailed.setString(1, transactionId);
	                        pstmtFailed.executeUpdate();
	                    }
	                    
	                   
	                    loopAttempts.remove(transactionId);
	                    canRetry = false;
	                    
	                    logger.warning("Transaction " + transactionId + 
	                                  " exceeded max loop attempts (" + MAX_LOOP_ATTEMPTS + "), marked as FAILED (F)");
	                } else {
	                
	                    String sqlRetry = 
	                    "UPDATE WO_ACTUALS \r\n" + 
	                    "SET INTERFACE_MODIFIED_DATE = null, \r\n" + 
	                    "    INTERFACE_MODIFIED_FLAG = null \r\n" + 
	                    "WHERE WO_ACTUAL_TRANSACTION = ?";
	                    
	                    try (PreparedStatement pstmtRetry = con.prepareStatement(sqlRetry)) {
	                        pstmtRetry.setString(1, transactionId);
	                        pstmtRetry.executeUpdate();
	                    }
	                    
	                   
	                    loopAttempts.put(transactionId, currentAttempts);
	                    
	                    logger.info("Transaction " + transactionId + 
	                               " set to retry (attempt " + currentAttempts + "/" + MAX_LOOP_ATTEMPTS + ")");
	                }
	            }
	        }
	        catch (Exception e) {
	            exceuted = e.toString();
	            JobConfirmationController.addError(exceuted);
	            logger.severe(exceuted);
	            throw new Exception("Issue found in loopMarkTransaction");
	        }
	        
	        return canRetry;
	    }
	    
	    
	    public void cleanupOldAttempts() {
	        if (loopAttempts.size() > 1000) {
	            logger.info("Cleaning up old loop attempts map - size: " + loopAttempts.size());
	            loopAttempts.clear();
	        }
	    }
	    
	    
	    public String getLoopAttemptsStatus() {
	        StringBuilder status = new StringBuilder();
	        status.append("Current loop attempts: ").append(loopAttempts.size()).append(" transactions\n");
	        
	        for (java.util.Map.Entry<String, Integer> entry : loopAttempts.entrySet()) {
	            status.append("Transaction: ").append(entry.getKey())
	                  .append(" - Attempts: ").append(entry.getValue())
	                  .append("/").append(MAX_LOOP_ATTEMPTS).append("\n");
	        }
	        
	        return status.toString();
	    }
		
}
