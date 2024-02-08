package trax.aero.data;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import trax.aero.controller.CarryForwardController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.interfaces.ICarryForwardData;
import trax.aero.logger.LogManager;
import trax.aero.model.BlobTable;
import trax.aero.model.BlobTablePK;
import trax.aero.model.FormMaster;
import trax.aero.model.FormResponseHeader;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.WoTaskCard;
import trax.aero.pojo.ADDL_ATTACHMENT;
import trax.aero.pojo.CFI_ATTACHMENT;
import trax.aero.pojo.CarryForward;
import trax.aero.pojo.MT_TRAX_RCV_I74_4070_RES;
import trax.aero.pojo.MT_TRAX_SND_I74_4070_REQ;
import trax.aero.utils.CarryForwardPoster;
import trax.aero.utils.DataSourceClient;
import trax.aero.utils.ErrorType;



/*
SELECT
APTH.PN,
APTH.SN,
APTH.POSITION,
APTH.QTY,
PID.LEGACY_BATCH,
PID.VENDOR_LOT,
APTH.schedule_category,
APTH.REMOVE_AS_SERVICEABLE
FROM
AC_PN_TRANSACTION_HISTORY APTH,
PN_INVENTORY_DETAIL PID,
WO_TASK_CARD WTC
WHERE 
APTH.WO = WTC.WO
AND APTH.TASK_CARD = WTC.TASK_CARD 
AND APTH.PN = PID.PN
AND APTH.SN = PID.SN
AND APTH.BATCH =  PID.BATCH 
AND APTH.TRANSACTION = ?
 
 
 */

@Stateless(name="CarryForwardData" , mappedName="CarryForwardData")
public class CarryForwardData implements ICarryForwardData {

	@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
	
			String exceuted;
			
			private Connection con;
			final String url = System.getProperty("CarryForward_URL");
			Logger logger = LogManager.getLogger("CarryForward_I80");
			public InterfaceLockMaster lock;
			
			public CarryForwardData()
			{
				
			}
			
			public Connection getCon() {
				return con;
			}
	
			public String Button(CarryForward b) throws Exception
			{
				//setting up variables
				exceuted = "OK";
				boolean isOkay = true;
				boolean success = true;
				
				CarryForwardPoster poster = new CarryForwardPoster();

				try 
				{
					MT_TRAX_SND_I74_4070_REQ out = new MT_TRAX_SND_I74_4070_REQ();
					try {
						
						throwValidate(b);
					}
					catch (Exception e)
					{
						exceuted = e.toString();
						CarryForwardController.addError(exceuted);				
						logger.severe(exceuted);
						isOkay = false;
					}

					
					if(isOkay) {
						try {
							out = convertButtonToOutbound(b);
						}catch (Exception e) {
							exceuted = e.toString();
							CarryForwardController.addError(e.toString());
							logger.severe(exceuted);		
							isOkay = false;
						}
					}
					
					if(isOkay) {
						
						JAXBContext jc = JAXBContext.newInstance(MT_TRAX_SND_I74_4070_REQ.class);
						Marshaller marshaller = jc.createMarshaller();
						marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
						StringWriter sw = new StringWriter();
						marshaller.marshal(out, sw);
						
						logger.info("Ouput: " + sw.toString());
						
						success = poster.postCarryForward(out, url);
						
						if(!success)
						{
							exceuted = "Unable to send Outbound with Order: " + out.getOrderNumber() + " to URL " + url;
							logger.severe(exceuted);
							CarryForwardController.addError(exceuted); 
						}else {
							logger.severe("POST status: " + String.valueOf(success) + " Order: " + out.getOrderNumber());
							MT_TRAX_RCV_I74_4070_RES input = null;
							
							try 
					        {    
								String body = poster.getBody();
								StringReader sr = new StringReader(body);				
								jc = JAXBContext.newInstance(MT_TRAX_RCV_I74_4070_RES.class);
						        Unmarshaller unmarshaller = jc.createUnmarshaller();
						        input = (MT_TRAX_RCV_I74_4070_RES) unmarshaller.unmarshal(sr);

						        marshaller = jc.createMarshaller();
						        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
						        sw = new StringWriter();
							    marshaller.marshal(input,sw);
							    logger.info("Input: " + sw.toString());
							
							
							    if(input.getErrorCode().equalsIgnoreCase("53")) {
									//markTransaction(input);
								}else {
									markTransaction(input);
									CarryForwardController.addError("Received acknowledgement with Error Code: " + input.getErrorCode() +" ,Remarks: "+input.getRemarks() +", Order: "+input.getOrderNumber()  ) ;
									CarryForwardController.sendEmailInbound(input);
									exceuted = "Issue Found";
								}
							}
							catch(Exception e)
							{
								logger.severe(e.toString());
								exceuted = 	e.toString();
							}
							logger.info("finishing");
						
						
						
						
						}
					}
				}
				catch (Exception e) 
		        {
					exceuted = e.toString();
					CarryForwardController.addError(exceuted); 
		           logger.severe(exceuted);
		           
				}finally {
					
				}
				return exceuted;
			}
			
			

			
			
			
			
			
			private void throwValidate(CarryForward b)throws Exception {
				
				String isOkay = "OK";
				
				if(b.getWO()!= null && b.getWO().isEmpty()) {
					isOkay = "Error: WO is null or empty";
					throw new Exception(isOkay);
				}
				if(b.getTASK_CARD() != null && b.getTASK_CARD().isEmpty()) {
					isOkay = "Error: Task Card Item is null or empty";
					throw new Exception(isOkay);
				}
			}

			
			
			
			
	
			private MT_TRAX_SND_I74_4070_REQ convertButtonToOutbound(CarryForward item) throws Exception
			{
				String time = "hhmm00" , hour ,currentDate ,minute, blob_NO  ;
				
				
				Date Date;
				Format formatter = new SimpleDateFormat("yyyyMMdd");

				
				logger.info("Getting WO Task Card infromation from WO " + item.getWO() + " Item " + item.getTASK_CARD());
				
				String sql = "SELECT REFERENCE_TASK_CARD,COMPLETED_ON,PKG_INTERFACE.GETXMLNUMBERSTRING(completed_on_hour) as completedHour,PKG_INTERFACE.GETXMLNUMBERSTRING(completed_on_minute) as completedMinute,STATUS, \r\n" + 
						"STATUS_CATEGORY,REMARKS,BLOB_NO FROM WO_TASK_CARD WHERE WO = ? AND TASK_CARD = ?";
				
				String sqlStatus =
						"UPDATE WO_TASK_CARD SET STATUS = 'CANCEL', STATUS_CATEGORY = 'CFWD' WHERE WO = ? AND TASK_CARD = ?";
				String sqlblob ="select blob_item from blob_table where blob_no = ?";		
				
				MT_TRAX_SND_I74_4070_REQ out = new MT_TRAX_SND_I74_4070_REQ();
				
				
				PreparedStatement pstmt1 = null; 
				PreparedStatement pstmt2 = null; 
				PreparedStatement pstmt3 = null; 
				ResultSet rs1 = null;
				ResultSet rs3 = null;
				pstmt1 = con.prepareStatement(sql);
				pstmt2 = con.prepareStatement(sqlStatus);
				pstmt3 = con.prepareStatement(sqlblob);
				try 
				{
					pstmt1.setString(1, item.getWO());
					pstmt1.setString(2, item.getTASK_CARD());
					rs1 = pstmt1.executeQuery();

					if (rs1 != null) 
					{
						while (rs1.next())
						{
							logger.info("Processing " + rs1.getString(1) +" " +item.getWO() +" "+  item.getTASK_CARD());
							
//							pstmt2.setString(1, item.getWO());
//							pstmt2.setString(2, item.getTASK_CARD());
//							pstmt2.executeQuery();
							
							if(rs1.getString(1) != null && !rs1.getNString(1).isEmpty()) {
								out.setOrderNumber(rs1.getString(1));
							}
							else {
								out.setOrderNumber("");
							}
							
							if(rs1.getDate(2) != null) {
								Date = rs1.getDate(2);
								currentDate = formatter.format(Date);
								out.setReferenceDate(currentDate);
							}else {
								out.setReferenceDate("");
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
							out.setReferenceTime(time);
							
							if(rs1.getString(5) != null && !rs1.getNString(5).isEmpty()) {
								out.setTRAXStatus(rs1.getString(5));
							}
							else {
								out.setTRAXStatus("");
							}
							
							
							if(rs1.getString(6) != null && !rs1.getNString(6).isEmpty()) {
								out.setTRAXStatusCategory(rs1.getString(6));
							}
							else {
								out.setTRAXStatusCategory("");
							}
							
							if(out.getTRAXStatus().equals("OPEN")) {
								out.setReasonForTECO_reversal(rs1.getString(7));
							}else {
								out.setReasonForTECO_reversal("");
							}
							
							
							
							
							
							if(rs1.getString(8) != null && !rs1.getNString(8).isEmpty()) {
								blob_NO = rs1.getString(8);
								pstmt3.setString(1, blob_NO);
								rs3 = pstmt3.executeQuery();
								logger.info(" blob_NO " + blob_NO);
								ArrayList<ADDL_ATTACHMENT> aDDL_ATTACHMENTs = new ArrayList<ADDL_ATTACHMENT>();
								if (rs3 != null) 
								{
									while (rs3.next())
									{
										if(rs3.getBlob(1) != null ) {
											Blob blob = rs3.getBlob(1);
											int blobLength = (int) blob.length();  
											byte[] blobAsBytes = blob.getBytes(1, blobLength);
											logger.info(" blobAsBytes " + blobAsBytes);
											ADDL_ATTACHMENT aDDL_ATTACHMENT = new ADDL_ATTACHMENT();
											aDDL_ATTACHMENT.setZADDATT(blobAsBytes);
											aDDL_ATTACHMENTs.add(aDDL_ATTACHMENT);
											blob.free();
										}
									}
								}	
								if(rs3 != null && !rs3.isClosed())
									rs3.close();
								
								
								
								
								out.setADDL_ATTACHMENT(aDDL_ATTACHMENTs);
								
								
								
								
							}
							
							
							
						}
					}
							
					
					
				}
				catch (Exception e) 
		        {
					exceuted = e.toString();
					CarryForwardController.addError(exceuted);
		            logger.severe(exceuted);	            
		            throw new Exception("Issue found");
				}finally {
					
					if(pstmt1 != null && !pstmt1.isClosed())
						pstmt1.close();
					if(pstmt2 != null && !pstmt2.isClosed())
						pstmt2.close();
					if(pstmt3 != null && !pstmt3.isClosed())
						pstmt3.close();
					if(rs1 != null && !rs1.isClosed())
						rs1.close();	
				}
				return out;
			
			}
			
			

	
		 
		 
		 
		 public void markTransaction(MT_TRAX_RCV_I74_4070_RES Inbound) throws Exception
			{
				//setting up variables
				exceuted = "OK";
				
				String sqlDate =
				"UPDATE WO_TASK_CARD SET INTERFACE_SAP_DATE = sysdate WHERE INTERFACE_SAP_DATE IS NULL AND REFERENCE_TASK_CARD = ?";
				
				PreparedStatement pstmt2 = null; 
				pstmt2 = con.prepareStatement(sqlDate);
				try 
				{
				
							logger.severe("Mark: " + Inbound.getOrderNumber());
							pstmt2.setString(1, Inbound.getOrderNumber());
							
							pstmt2.executeQuery();
					
				}
				catch (Exception e) 
		        {
					exceuted = e.toString();
					CarryForwardController.addError(exceuted);
		            logger.severe(exceuted);		           
		            
		            throw new Exception("Issue found");
				}finally {
					
					if(pstmt2 != null && !pstmt2.isClosed())
						pstmt2.close();
					
				}
				
			}

		public String print(String wo,String task_card , byte[] bs, String formNo, String formLine) throws Exception {
			//setting up variables
			exceuted = "OK";
			boolean isOkay = true;
			boolean success = true;
			
			CarryForwardPoster poster = new CarryForwardPoster();

			try 
			{
				if(this.con == null || this.con.isClosed())
				{
					this.con = DataSourceClient.getConnection();
					logger.info("The connection was stablished successfully with status: " + String.valueOf(!this.con.isClosed()));
				}
				
				
				MT_TRAX_SND_I74_4070_REQ out = new MT_TRAX_SND_I74_4070_REQ();
								
				if(isOkay) {
					try {
						out = convertPrintToOutbound(wo, task_card,bs);
						setAttachmentLink(bs,wo, task_card, formNo, formLine);
						
					}catch (Exception e) {
						exceuted = e.toString();
						CarryForwardController.addError(e.toString());
						logger.severe(exceuted);		
						isOkay = false;
					}
				}
				
				if(isOkay) {
					
					JAXBContext jc = JAXBContext.newInstance(MT_TRAX_SND_I74_4070_REQ.class);
					Marshaller marshaller = jc.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					StringWriter sw = new StringWriter();
					marshaller.marshal(out, sw);
					
					//logger.info("Ouput: " + sw.toString());
					
					success = poster.postCarryForward(out, url);

					if(!success)
					{
						exceuted = "Unable to send Outbound with Order: " + out.getOrderNumber() + " to URL " + url;
						logger.severe(exceuted);
						CarryForwardController.addError(exceuted); 
					}else {
						logger.severe("POST status: " + String.valueOf(success) + " Order: " + out.getOrderNumber());
						MT_TRAX_RCV_I74_4070_RES input = null;
						
						try 
				        {    
							String body = poster.getBody();
							StringReader sr = new StringReader(body);				
							jc = JAXBContext.newInstance(MT_TRAX_RCV_I74_4070_RES.class);
					        Unmarshaller unmarshaller = jc.createUnmarshaller();
					        input = (MT_TRAX_RCV_I74_4070_RES) unmarshaller.unmarshal(sr);

					        marshaller = jc.createMarshaller();
					        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
					        sw = new StringWriter();
						    marshaller.marshal(input,sw);
						    logger.info("Input: " + sw.toString());
						
						
						    if(input.getErrorCode().equalsIgnoreCase("53")) {
								markTransaction(input);
							}else {
								markTransaction(input);
								CarryForwardController.addError("Received acknowledgement with Error Code: " + input.getErrorCode() +" ,Remarks: "+input.getRemarks() +", Order: "+input.getOrderNumber()  ) ;
								CarryForwardController.sendEmailInbound(input);
								exceuted = "Issue Found";
							}
						}
						catch(Exception e)
						{
							logger.severe(e.toString());
							exceuted = 	e.toString();
						}
						logger.info("finishing");
					
					
					
					
					}
				}
			}
			catch (Exception e) 
	        {
				exceuted = e.toString();
				CarryForwardController.addError(exceuted); 
	           logger.severe(exceuted);
	           
			}finally {
				
			}
			return exceuted;
			
			
		}

		private MT_TRAX_SND_I74_4070_REQ convertPrintToOutbound(String wo , String task_card, byte[] bs) throws Exception {
				String time = "hhmm00" , hour ,currentDate ,minute, blob_NO  ;
				
				
				Date Date;
				Format formatter = new SimpleDateFormat("yyyyMMdd");

				
				logger.info("Getting WO Task Card infromation from WO " + wo + " Item " + task_card);
				
				String sql = "SELECT REFERENCE_TASK_CARD,COMPLETED_ON,PKG_INTERFACE.GETXMLNUMBERSTRING(completed_on_hour) as completedHour,PKG_INTERFACE.GETXMLNUMBERSTRING(completed_on_minute) as completedMinute,STATUS, \r\n" + 
						"STATUS_CATEGORY,REMARKS,BLOB_NO FROM WO_TASK_CARD WHERE WO = ? AND TASK_CARD = ?";
				
				String sqlStatus =
						"UPDATE WO_TASK_CARD SET STATUS = 'CANCEL', STATUS_CATEGORY = 'CFWD' WHERE WO = ? AND TASK_CARD = ?";
				String sqlblob ="select blob_item from blob_table where blob_no = ?";		
				
				MT_TRAX_SND_I74_4070_REQ out = new MT_TRAX_SND_I74_4070_REQ();
				
				
				PreparedStatement pstmt1 = null; 
				PreparedStatement pstmt2 = null; 
				PreparedStatement pstmt3 = null; 
				ResultSet rs1 = null;
				ResultSet rs3 = null;
				pstmt1 = con.prepareStatement(sql);
				pstmt2 = con.prepareStatement(sqlStatus);
				pstmt3 = con.prepareStatement(sqlblob);
				try 
				{
					logger.info("Chaning Stauts to Cancel WO: " + wo + " TASK CARD: " + task_card);
					
//					pstmt2.setString(1, wo);
//					pstmt2.setString(2, task_card);
//					pstmt2.executeQuery();
//					
					pstmt1.setString(1, wo);
					pstmt1.setString(2, task_card);
					rs1 = pstmt1.executeQuery();

					if (rs1 != null) 
					{
						while (rs1.next())
						{
							logger.info("Processing Order Number: " + rs1.getString(1) + " WO: " + wo + " TASK CARD: " + task_card);
							
							
							
							if(rs1.getString(1) != null && !rs1.getNString(1).isEmpty()) {
								out.setOrderNumber(rs1.getString(1));
							}
							else {
								out.setOrderNumber("");
							}
							
							if(rs1.getDate(2) != null) {
								Date = rs1.getDate(2);
								currentDate = formatter.format(Date);
								out.setReferenceDate(currentDate);
							}else {
								out.setReferenceDate("");
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
							out.setReferenceTime(time);
							
							if(rs1.getString(5) != null && !rs1.getNString(5).isEmpty()) {
								out.setTRAXStatus(rs1.getString(5));
							}
							else {
								out.setTRAXStatus("");
							}
							
							
							if(rs1.getString(6) != null && !rs1.getNString(6).isEmpty()) {
								out.setTRAXStatusCategory(rs1.getString(6));
							}
							else {
								out.setTRAXStatusCategory("");
							}
							
							if(out.getTRAXStatus().equals("OPEN")) {
								out.setReasonForTECO_reversal(rs1.getString(7));
							}else {
								out.setReasonForTECO_reversal("");
							}
							
							
							
							
							
							if(rs1.getString(8) != null && !rs1.getNString(8).isEmpty()) {
								blob_NO = rs1.getString(8);
								pstmt3.setString(1, blob_NO);
								rs3 = pstmt3.executeQuery();
								logger.info(" blob_NO " + blob_NO);
								ArrayList<ADDL_ATTACHMENT> aDDL_ATTACHMENTs = new ArrayList<ADDL_ATTACHMENT>();
								if (rs3 != null) 
								{
									while (rs3.next())
									{
										if(rs3.getBlob(1) != null ) {
											Blob blob = rs3.getBlob(1);
											int blobLength = (int) blob.length();  
											byte[] blobAsBytes = blob.getBytes(1, blobLength);
											//logger.info(" blobAsBytes " + blobAsBytes);
											ADDL_ATTACHMENT aDDL_ATTACHMENT = new ADDL_ATTACHMENT();
											aDDL_ATTACHMENT.setZADDATT(blobAsBytes);
											aDDL_ATTACHMENTs.add(aDDL_ATTACHMENT);
											blob.free();
										}
									}
								}	
								if(rs3 != null && !rs3.isClosed())
									rs3.close();
								
								out.setADDL_ATTACHMENT(aDDL_ATTACHMENTs);
								
								
								

								
							}
							
							CFI_ATTACHMENT cfi = new CFI_ATTACHMENT();
							cfi.setZCFIATT(bs);
							ArrayList<CFI_ATTACHMENT> cfis = new ArrayList<CFI_ATTACHMENT>();								
							cfis.add(cfi);
							out.setCFI_ATTACHMENT(cfis);
							
							
							
						}
					}
							
					
					
				}
				catch (Exception e) 
		        {
					e.printStackTrace();
					exceuted = e.toString();
					CarryForwardController.addError(exceuted);
		            logger.severe(exceuted);	            
		            throw new Exception("Issue found");
				}finally {
					
					if(pstmt1 != null && !pstmt1.isClosed())
						pstmt1.close();
					if(pstmt2 != null && !pstmt2.isClosed())
						pstmt2.close();
					if(pstmt3 != null && !pstmt3.isClosed())
						pstmt3.close();
					if(rs1 != null && !rs1.isClosed())
						rs1.close();	
				}
				return out;
			
			}
		
		
		
		
		private void setAttachmentLink( byte[] input,String wo, String task_card, String formNo, String formLine ) {
			boolean existBlob = false;
			BlobTable blob = null;
			
			if(formNo == null) {
		    	return;
		    }
			
			WoTaskCard woTaskCard =  getWoTaskCard(formNo);
			
			String fileName = getFileName(formNo, formLine);
		    if(woTaskCard == null) {
		    	return;
		    }
			
			
				try 
				{
					blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
							.setParameter("bl", woTaskCard.getBlobNo().longValue())
							.setParameter("des",fileName )
							.getSingleResult();
					existBlob = true;
				}
				catch(Exception e)
				{
					
					BlobTablePK pk = new BlobTablePK();
					blob = new BlobTable();
					blob.setCreatedDate(new Date());
					blob.setCreatedBy("TRAX_IFACE");
					blob.setId(pk);
					
					blob.setPrintFlag("YES");
					
					blob.getId().setBlobLine(getLine(woTaskCard.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
				}
				
				
				blob.setDocType(fileName.substring(0, 3));
				
					
				
				
				blob.setModifiedBy("TRAX_IFACE");
				blob.setModifiedDate(new Date());
				blob.setBlobItem(input);
				blob.setBlobDescription(fileName);
				blob.setCustomDescription(fileName);
				
				
				
				if(!existBlob && woTaskCard.getBlobNo() == null) {
					try {
						blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
						woTaskCard.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
					} catch (Exception e1) {
						
					}
				}else if(woTaskCard.getBlobNo() != null){
					blob.getId().setBlobNo(woTaskCard.getBlobNo().longValue());
				}
				
				logger.info("INSERTING WO TASK CARD: " + wo + " " + task_card);
				insertData(woTaskCard);
				
				logger.info("INSERTING blob: " + blob.getId().getBlobNo() + " Line: " + blob.getId().getBlobLine());
				insertData(blob);
				
				return;
		}
		
		
		//insert generic data from model objects
		private <T> void insertData( T data) 
		{
			try 
			{	
				em.merge(data);
				em.flush();

			}catch (Exception e)
			{
				exceuted = "insertData has encountered an Exception: "+e.toString();
				CarryForwardController.addError(exceuted);
				logger.severe(e.toString());
			}
		}
		
		
		private long getLine(BigDecimal no, String table_line, String table, String table_no)
		{		
			long line = 0;
			String sql = " SELECT  MAX("+table_line+") FROM "+table+" WHERE "+table_no+" = ?";
			try
			{
				logger.info(no.toString());
				Query query = em.createNativeQuery(sql);
				query.setParameter(1, no);  
			
				BigDecimal dec = (BigDecimal) query.getSingleResult(); 
				line = dec.longValue();
				line++;
			}
			catch (Exception e) 
			{
				line = 1;
			}
			
			return line;
		}
		private BigDecimal getTransactionNo(String code)
		{		
			try
			{
				BigDecimal acctBal = (BigDecimal) em.createNativeQuery("SELECT pkg_application_function.config_number ( ? ) "
						+ " FROM DUAL ").setParameter(1, code).getSingleResult();
							
				return acctBal;			
			}
			catch (Exception e) 
			{
				logger.severe("An unexpected error occurred getting the sequence. " + "\nmessage: " + e.toString());
			}
			
			return null;
			
		}
		
		
		private WoTaskCard getWoTaskCard(String formNo) {
			WoTaskCard woTaskCard = null;
			
			try {
				woTaskCard = em.createQuery("SELECT w FROM WoTaskCard w WHERE w.formNo = :formNo ", WoTaskCard.class)
							.setParameter("formNo", new BigDecimal(formNo))
							.getSingleResult();
					
					return woTaskCard;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		
		
		private String getFileName(String formNo, String formLine)
		{
			String fileName ="_" + formNo + "_" + formLine + ".pdf";
			
			System.out.println("Searching for form " + formNo + " and line " + formLine);
			try
			{
				FormResponseHeader header = (FormResponseHeader) 
						em.createQuery("Select f from FormResponseHeader f where f.id.formNo = :formNo "
								+ "and f.id.formLine = :line")
						.setParameter("formNo", Long.parseLong(formNo))
						.setParameter("line", Long.parseLong(formLine))
						.getSingleResult();
				
				FormMaster master = (FormMaster) 
						em.createQuery("Select f from FormMaster f where f.formId = :formId")
						.setParameter("formId", header.getFormId().longValue())
						.getSingleResult();
				
				if("Supplementary Job Carried Forward Item".equalsIgnoreCase(master.getFormTitle()))
				{
					return "CFI" + fileName;
				}
				else
				{
					return "SDC" + fileName;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return fileName;
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
			
			
			
}
