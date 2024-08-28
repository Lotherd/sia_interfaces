package trax.aero.data;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.joda.time.DateTime;
import trax.aero.utils.ErrorType;
import trax.aero.controller.SlotsAMISController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.AcMaster;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.JournalEntriesExpenditure;
import trax.aero.model.JournalEntriesExpenditurePK;
import trax.aero.model.LocationMaster;
import trax.aero.model.LocationSite;
import trax.aero.model.NotePad;
import trax.aero.model.NotePadPK;
import trax.aero.model.SystemTranCode;
import trax.aero.model.Wo;
import trax.aero.pojo.AMISItem;
import trax.aero.pojo.Amis;
import trax.aero.utils.DataSourceClient;

public class SlotsAMISData {

			Long woNumber;
			EntityManagerFactory factory;
			EntityManager em;
			String exceuted;
			public String WO;
			private Connection con;
			Logger logger = LogManager.getLogger("SlotsAIMS_I31");
			public InterfaceLockMaster lock;
			
			public SlotsAMISData()
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
					SlotsAMISController.addError(e.toString());
					
				}
				catch (CustomizeHandledException e1) {
					
					SlotsAMISController.addError(e1.toString());
					
				} catch (Exception e) {
					
					SlotsAMISController.addError(e.toString());
					
				}
				
			}
			
						
			public SlotsAMISData(String string) {
				factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
				em = factory.createEntityManager();
				
			}


			public Connection getCon() {
				return con;
			}
			
	
			public String slot(AMISItem item) throws Exception
			{
				//setting up variables
				exceuted = "OK";
				
				try 
				{
					insertSlot(item);
				}
				catch (Exception e) 
		        {
					SlotsAMISController.addError(e.toString());
		            e.printStackTrace();
		            em.getTransaction().rollback();
		            exceuted = e.toString();
				}
				finally
				{
					//clean up 
					em.clear();

				}
				return exceuted;
			}
			

			private void insertSlot(AMISItem item) {
				//setting up variables
				
				boolean existWO = false;
				boolean existNote = false;
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss",Locale.ENGLISH);	
				SimpleDateFormat formatterModDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");	
				SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
				SimpleDateFormat formatHours = new SimpleDateFormat("HH");
				SimpleDateFormat formatMinutes = new SimpleDateFormat("mm");

						
				Wo wo = null;
				NotePad notepad = null;
					
				//check if object has min values
				if(item.getAmis() != null || checkMinValue(item.getAmis())) 
				{
					
					try 
					{
						wo = em.createQuery("Select w From Wo w where w.cosl = :exRef", Wo.class)
								.setParameter("exRef", item.getAmis().getOid())
								.getSingleResult();
						existWO = true;
					}
					catch(Exception e)
					{
						wo = new Wo();
						wo.setCreatedDate(new Date());
						wo.setCreatedBy("TRAX_IFACE");
						
						//EMRO fields to create basic object

						wo.setGlCompany(System.getProperty("profile_company"));
						wo.setOrderType("W/O");
						wo.setModule("PRODUCTION");
						wo.setPaperChecked("NO");
						wo.setAuthorization("Y");
						wo.setNrReqItem("N");
						wo.setRestrictActual("N");
						wo.setNrAllow("YES");
						wo.setExcludeMhPlanner("N");
						wo.setThirdPartyWo("Y");
						
						wo.setExpenditure(setExpenditure("General"));
						
					}
					
					wo.setModifiedBy("TRAX_IFACE");
					wo.setModifiedDate(new Date());
					wo.setCosl(item.getAmis().getOid());
					wo.setCustomerWo(item.getAmis().getCustomer());
					
					
					if (getAC(item.getAmis().getACReg()) == null) 
					{
						exceuted = "Can not insert/update/delete OID: "+ item.getAmis().getOid() +" as AC does not exist";
						logger.severe(exceuted);
						SlotsAMISController.addError(exceuted);
						return;
					}else {
						wo.setAc(item.getAmis().getACReg());
					}
					
					if (getCategory(item.getAmis().getCheckType()) == null) 
					{
						exceuted = "Can not insert/update/delete OID: "+ item.getAmis().getOid() +" as Category does not exist";
						logger.severe(exceuted);
						SlotsAMISController.addError(exceuted);
						return;
					}else {
						wo.setWoCategory(item.getAmis().getCheckType());
					}
					
					
					
					
					
					wo.setOpsLine(item.getAmis().getLine());
					String site = getSiteOps(wo.getOpsLine());
					
					wo.setSite(site);
					wo.setLocation(setLocation(site));
					
					try {
						Date start = formatterDate.parse(item.getAmis().getPlannedStart());
						Date end = formatterDate.parse(item.getAmis().getPlannedEnd());
						
						
						wo.setScheduleStartDate(start);
						wo.setScheduleStartHour(new BigDecimal((formatHours.format(formatter.parse(item.getAmis().getPlannedStart())))));
						wo.setScheduleStartMinute(new BigDecimal(formatMinutes.format(formatter.parse(item.getAmis().getPlannedStart()))));
						
						wo.setActualStartDate(start);
						wo.setActualStartHour(new BigDecimal(formatHours.format(formatter.parse(item.getAmis().getPlannedStart()))));
						wo.setActualStartMinute(new BigDecimal(formatMinutes.format(formatter.parse(item.getAmis().getPlannedStart()))));
						
						
						
						wo.setScheduleOrgCompletionDate(end);
						wo.setScheduleOrgCompletionHour(new BigDecimal(formatHours.format(formatter.parse(item.getAmis().getPlannedEnd()))));
						wo.setScheduleOrgCompletionMinute(new BigDecimal(formatMinutes.format(formatter.parse(item.getAmis().getPlannedEnd()))));
						
						wo.setScheduleCompletionDate(end);
						wo.setScheduleCompletionHour(new BigDecimal(formatHours.format(formatter.parse(item.getAmis().getPlannedEnd()))));
						wo.setScheduleCompletionMinute(new BigDecimal(formatMinutes.format(formatter.parse(item.getAmis().getPlannedEnd()))));
						
						
					} catch (ParseException e1) {
						
						exceuted = "Can not insert/update/delete OID: "+ item.getAmis().getOid() +" as ERROR: Planned Start or Planned End Date is incorrect format";
						logger.severe(exceuted);
						SlotsAMISController.addError(exceuted);
						return;
					}
					
					if(item.getAmis().getConfirmationStatus().equalsIgnoreCase("1") ) {
						wo.setStatus("OPEN");
					}
					if(item.getAmis().getConfirmationStatus().equalsIgnoreCase("0") ) {
						wo.setStatus("SLOT");
					}
					
					if(item.getAmis().getCheckDescription() != null && !item.getAmis().getCheckDescription().isEmpty()) 
					{
						wo.setWoDescription(item.getAmis().getCheckDescription());
					}
					
					if(item.getAmis().getRemarks() != null && !item.getAmis().getRemarks().isEmpty()) 
					{
							try 
							{
								notepad = em.createQuery("Select n from NotePad n where n.id.notes = :not", NotePad.class)
										.setParameter("not", wo.getNotes().longValue())
										.getSingleResult();
								existNote = true;
							}
							catch(Exception e)
							{
								
								NotePadPK pk = new NotePadPK();
								notepad = new NotePad();
								notepad.setCreatedDate(new Date());
								notepad.setCreatedBy("TRAX_IFACE");
								notepad.setId(pk);
								
								notepad.getId().setNotesLine(1);
								notepad.setPrintNotes("YES");
								
								try {
									notepad.getId().setNotes(((getTransactionNo("NOTES").longValue())));
									wo.setNotes(new BigDecimal(notepad.getId().getNotes()));
								} catch (Exception e1) {
									
								}
								
								//EMRO fields to create basic object
							}
							notepad.setModifiedBy("TRAX_IFACE");
							notepad.setModifiedDate(new Date());
							notepad.setNotesText(item.getAmis().getRemarks());
							
							
						
					}
					
					
					wo.setModifiedBy("TRAX_IFACE");
					wo.setModifiedDate(new Date());
					
					
					if(!existWO) {
						try {
							wo.setWo(getTransactionNo("WOSEQ").longValue());
							
						} catch (Exception e1) {
							
						}
					}
					
					
					WO = String.valueOf(wo.getWo());
					
					if(item.getAmis().getAction().equalsIgnoreCase("I")) {
						
						if(notepad !=null) {
							logger.info("INSERTING NOTE: " + notepad.getId().getNotes());
							insertData(notepad);
						}
						
						logger.info("INSERTING OID: " + item.getAmis().getOid() + " WO: " + wo.getWo());
						
						
						insertData(wo);
						woNumber = wo.getWo();
						
					}else if(item.getAmis().getAction().equalsIgnoreCase("D") ) {
						if((wo.getWorkStarted() !=null && wo.getWorkStarted().equalsIgnoreCase("Y") && item.getAmis().getConfirmationStatus().equalsIgnoreCase("1"))) {
							exceuted = "Can not Delete OID: "+ item.getAmis().getOid() +" as ERROR: Work has started and Confirmed";
							logger.severe(exceuted);
							SlotsAMISController.addError(exceuted);
							return;
						}else{
							if(notepad !=null) {
								logger.info("DELETE NOTE: " + notepad.getId().getNotes()+ " WO: " + wo.getWo());
								deleteData(notepad);
							}
							
							logger.info("DELETE OID: " + item.getAmis().getOid()+ " WO: " + wo.getWo());
							deleteData(wo);
						}
						
					}else if(item.getAmis().getAction().equalsIgnoreCase("U")) {
						
						
							if(notepad !=null) {
								logger.info("UPDATING NOTE: " + notepad.getId().getNotes()+ " WO: " + wo.getWo());
								insertData(notepad);
							}
							
							logger.info("UPDATING OID: " + item.getAmis().getOid()+ " WO: " + wo.getWo());
							insertData(wo);
							
						
					
					}else {
						exceuted = "Can not insert/update/delete OID: "+ item.getAmis().getOid() +" as ERROR: ACTION is incorrect format";
						logger.severe(exceuted);
						SlotsAMISController.addError(exceuted);
						return;
					}

				}else 
				{
					exceuted = "Can not insert/update/delete OID: "+ item.getAmis().getOid() +" as ERROR: Amis is null or item does not have minimum values";
					logger.severe(exceuted);
					SlotsAMISController.addError(exceuted);
					return;
				}
				
				
			}
			
			
			

			
			private String getSiteOps(String opsLine) {
				
				String group = "";
				
				String sql = " Select SITE FROM OPS_LINE_EMAIL_MASTER where OPS_LINE = ?";
				
					
				
				try
				{
					Query query = em.createNativeQuery(sql);
					query.setParameter(1, opsLine);  
				
					group = (String) query.getSingleResult(); 
					
				}
				catch (Exception e) 
				{
					logger.info(e.getMessage());
					logger.severe("An Exception occurred executing the query to get the site. " + "\n error: " + e.toString());
				}
				finally 
				{
					
				}
				return group;
			}

			
			
			
			
			
			public String setSite(String site, String opsLine, String email) throws Exception{
				String Exceuted = "OK";
				String query = "INSERT INTO OPS_LINE_EMAIL_MASTER (OPS_LINE, EMAIL,SITE) VALUES (?, ?, ?)";
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
					ps.setString(2, email);
					ps.setString(3, site);
					ps.executeUpdate();
				
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to set the site opsLine. " + "\n error: " + e.toString() );
					throw new Exception("An Exception occurred executing the query to set the site opsLine. " + "\n error: " + e.toString());
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
			
			public String deleteSite( String opsline) throws Exception{
				String Exceuted = "OK";
				String query = "DELETE OPS_LINE_EMAIL_MASTER where OPS_LINE = ?";	
				PreparedStatement ps = null;
					
				try
				{	
					if(con == null || con.isClosed())
					{
						con = DataSourceClient.getConnection();
						logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
					}
					
					ps = con.prepareStatement(query);
					ps.setString(1, opsline);
					ps.executeUpdate();		
					
					
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to delete the site . " + "\n error: " + e.toString());
					throw new Exception("An Exception occurred executing the query to delete the site. " + "\n error: " + e.toString());
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
			
			
			public String getSite( String opsline) throws Exception{
				
				ArrayList<String> groups = new ArrayList<String>();
				
				String query = "", group = "";
				if(opsline != null && !opsline.isEmpty()) {
					query = " Select OPS_LINE, site, EMAIL FROM OPS_LINE_EMAIL_MASTER where OPS_LINE = ?";
				}else {
					query = " Select OPS_LINE, site, EMAIL FROM OPS_LINE_EMAIL_MASTER";
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
					if(opsline != null && !opsline.isEmpty()) {
						ps.setString(1, opsline);
					}
					
					ResultSet rs = ps.executeQuery();		
					
					if (rs != null) 
					{
						while (rs.next()) 
						{
							
						groups.add("Ops Line: "+rs.getString(1) + " Site: " +rs.getString(2) + " Email: " +rs.getString(3) );

						}
					}
					rs.close();
					
					
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to get the site opsLine. " + "\n error: " + e.toString());
					throw new Exception("An Exception occurred executing the query to get the site opsLine. " + "\n error: " +  e.toString());
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
					e.printStackTrace();
					exceuted = "insertData has encountered an Exception: "+e.toString();
					SlotsAMISController.addError(exceuted);
					logger.severe(exceuted);
				}
			}
			
			//delete generic data from model objects
			private <T> void deleteData( T data) 
			{
				try 
				{	
					if(!em.getTransaction().isActive())
						em.getTransaction().begin();
						em.remove(data);
					em.getTransaction().commit();
				}catch (Exception e)
				{
					exceuted = "insertData has encountered an Exception: "+e.toString();
					SlotsAMISController.addError(exceuted);
					logger.severe(exceuted);
				}
			}
			
			private boolean checkMinValue(Amis amis) {
				
				if( amis.getACReg() == null || amis.getACReg().isEmpty()) {
					
					return false;
				}
					
				if( amis.getCustomer() == null || amis.getCustomer().isEmpty()) {
					return false;
				}
					
				if( amis.getCheckType() == null || amis.getCheckType().isEmpty()) {
					return false;
				}
					
				if( amis.getPlannedStart() == null || amis.getPlannedStart().isEmpty()) {
					return false;
				}
					
				if( amis.getPlannedEnd() == null || amis.getPlannedEnd().isEmpty()) {
					return false;
				}
					
				if( amis.getAction() == null || amis.getAction().isEmpty()) {
					return false;
				}
					
				if( amis.getOid() == null || amis.getOid().isEmpty()) {
					return false;
				}
					
				if( amis.getConfirmationStatus() == null || amis.getConfirmationStatus().isEmpty()) {
					return false;
				}
				if( amis.getLine() == null || amis.getLine().isEmpty()) {
					return false;
				}
					
					
				return true;
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
			
			// get ac from ac master used to check if ac exist
			private String getAC(String AC) {
				try
				{	
					AcMaster acMaster = em.createQuery("Select a From AcMaster a where a.id.ac = :airC", AcMaster.class)
					.setParameter("airC", AC)
					.getSingleResult();
							
					return acMaster.getAc();
				}
				catch (Exception e)
				{
					
				}
				return " " ;
			}
			
			// get category from SystemTranCode used to check if category exist
			private String getCategory(String Category) {
				try
				{	
					SystemTranCode systemTranCode = em.createQuery("Select s From SystemTranCode s where s.id.systemCode = :cat and s.id.systemTransaction = :systran", SystemTranCode.class)
					.setParameter("cat", Category)
					.setParameter("systran", "WOCATEGORY")
					.getSingleResult();
							
					return systemTranCode.getId().getSystemCode();
				}
				catch (Exception e)
				{
					
				}
				return null;
			}


			public String updateOpsLine(String site, String opsLine) throws Exception {
				
				String Exceuted = "OK";
				String query = "UPDATE OPS_LINE_EMAIL_MASTER SET SITE = ? WHERE OPS_LINE = ?";
				PreparedStatement ps = null;	
				
				try
				{
					if(con == null || con.isClosed())
					{
						con = DataSourceClient.getConnection();
						logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
					}
					
					ps = con.prepareStatement(query);
					ps.setString(1, site);
					ps.setString(2, opsLine);
					ps.executeUpdate();
				
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to update the site opsLine. " + "\n error: " + e.toString() );
					throw new Exception("An Exception occurred executing the query to update the site opsLine. " + "\n error: " + e.toString());
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
			
			private String setExpenditure(String string) {
				JournalEntriesExpenditure journalEntriesExpenditure = null;
				try
				{
					journalEntriesExpenditure = em.createQuery("SELECT j FROM JournalEntriesExpenditure j WHERE j.id.categoryCode = :code AND  j.id.transaction = :tra AND j.id.category = :cat", JournalEntriesExpenditure.class)
					.setParameter("code",string)
					.setParameter("tra", "WIP")
					.setParameter("cat", "EXPENDITURE")
					.getSingleResult();
					
					
					return journalEntriesExpenditure.getId().getCategoryCode();
				}
				catch (Exception e)
				{
					journalEntriesExpenditure = new JournalEntriesExpenditure();
					JournalEntriesExpenditurePK pk = new JournalEntriesExpenditurePK();
					journalEntriesExpenditure.setId(pk);
					journalEntriesExpenditure.setModifiedBy("TRAX_IFACE");
					journalEntriesExpenditure.setModifiedDate(new Date());
					journalEntriesExpenditure.setCreatedBy("TRAX_IFACE");
					journalEntriesExpenditure.setCreatedDate(new Date());
					
					journalEntriesExpenditure.getId().setCategoryCode("DEFAULT");
					journalEntriesExpenditure.getId().setTransaction("WIP");
					journalEntriesExpenditure.getId().setCategory("EXPENDITURE");
					
					journalEntriesExpenditure.getId().setClass_("LABOR");
					
					journalEntriesExpenditure.setJournalDescription("DEFAULT");
					journalEntriesExpenditure.setExpenditureUse("PRODUCTION");
					
					
					logger.info("INSERTING CODE: " + journalEntriesExpenditure.getId().getCategoryCode());
					insertData(journalEntriesExpenditure);
				}
				return journalEntriesExpenditure.getId().getCategoryCode();
			}
			
			private String setLocation(String site) {
				List<LocationSite> locationsites = null;
				try
				{
					locationsites = em.createQuery("SELECT l FROM LocationSite l WHERE l.id.site = :sit", LocationSite.class)
					.setParameter("sit", site)
					.getResultList();
					
					for(LocationSite ls : locationsites) {
						if(getLocationType(ls.getId().getLocation())) {
							return ls.getId().getLocation();
						}
						
				}
					
				}catch (Exception e)
				{
					e.printStackTrace();
					logger.info(e.toString());
				}
				return "";
			}
			
			
			private boolean getLocationType(String loc) {
				LocationMaster locationMaster = null;
				try
				{
					locationMaster = em.createQuery("SELECT l FROM LocationMaster l WHERE l.location = :loc", LocationMaster.class)
					.setParameter("loc", loc)
					.getSingleResult();
					if(locationMaster.getMaintenanceFacility() !=null && !locationMaster.getMaintenanceFacility() .isEmpty() && locationMaster.getMaintenanceFacility() .equalsIgnoreCase("Y")){
						return true;
					}else{
						return false;
					}
				}catch (Exception e)
				{
					e.printStackTrace();
					logger.info(e.toString());
					return false;
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
