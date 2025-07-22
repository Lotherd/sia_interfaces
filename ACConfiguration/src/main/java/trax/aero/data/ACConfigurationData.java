package trax.aero.data;
  
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import trax.aero.controller.ACConfigurationController;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.NotePad;
import trax.aero.model.NotePadPK;
import trax.aero.model.PnInventoryDetail;
import trax.aero.model.PnMaster;
import trax.aero.model.Wo;
import trax.aero.pojo.Equipment;
import trax.aero.pojo.MT_TRAX_SND_I51_4072;
import trax.aero.pojo.TopFunctionalLocation;

public class ACConfigurationData {
	
	
	long lastBatch = 99999;
	EntityManagerFactory factory;
	@PersistenceContext(unitName = "ACConfigDS")	public EntityManager em;
	String exceuted = "OK";
	public boolean sentToDatabase = false;
	Logger logger = LogManager.getLogger("ACConfiguration_I51");
	//public InterfaceLockMaster lock;
	
	public ACConfigurationData(EntityManagerFactory factory2){
		
		factory = factory2;
		em = factory.createEntityManager();

	}
	
	public List<MT_TRAX_SND_I51_4072> getAC() {
		
		
		List<Wo> wos = null;
		ArrayList<MT_TRAX_SND_I51_4072> SND = new ArrayList<MT_TRAX_SND_I51_4072>();
		int i = 0;
		
		LocalDate dateBefore1Days = LocalDate.now(ZoneId.of(System.getProperty("ACConfiguration_ZoneId"))).plusDays(1);

		
		Date date = new Date();
		
		date = Date.from(dateBefore1Days.atStartOfDay(ZoneId.of(System.getProperty("ACConfiguration_ZoneId"))).toInstant());
		logger.info("Getting WOs that are starting on "  + dateBefore1Days.toString());
		try
		{
			//TODO
			wos = (List<Wo>) em.createQuery("select w from Wo w where TO_CHAR(w.scheduleStartDate, 'DD-MON-YYYY') = TO_CHAR(:date, 'DD-MON-YYYY') and w.ac is not null "
					+ "and w.previousInterfaceTransaction is null")
					.setParameter("date", date,TemporalType.DATE)
					.getResultList();
			
		}
		catch(Exception e)
		{
			logger.severe(e.getMessage() );
		}
		for(Wo w : wos) {
			em.refresh(w);
			if(w.getAc() != null && !w.getAc().isEmpty() && !w.getAc().equalsIgnoreCase("          ") && !w.getModule().equalsIgnoreCase("SHOP")) {
				MT_TRAX_SND_I51_4072 out = new MT_TRAX_SND_I51_4072();
				logger.info("WO: " + String.valueOf(w.getWo()) +" AC: "+ w.getAc() +" i:"+i);
				out.setAircraftTailNumber(w.getAc());
				SND.add(out);  
				
				try {
				    w.setPreviousInterfaceTransaction(new BigDecimal(51));
				    insertData(w);
				    logger.info("Marked WO " + w.getWo() + " as processed");
				}
				catch(Exception e) {
				    logger.severe("Failed to mark WO " + w.getWo() + ": " + e.getMessage());
				}
				
				i++;
			}
		}
			
		return SND;
	}
	
	
	
	public void checkForNotSentEquipment(ArrayList<Equipment> sents , String topfunctionallocation) {
	
		ArrayList<PnInventoryDetail> notSentToDatabase = new ArrayList<PnInventoryDetail>();
		boolean exist;
		List<PnInventoryDetail> pnindets = null;
		
		
		
		exist = true;
		try 
		{
			pnindets = (List<PnInventoryDetail>) em.createQuery("select p from PnInventoryDetail p where p.installedAc = :ac")
					.setParameter("ac", topfunctionallocation)
					.getResultList();
				
		}catch(Exception exception) {
			logger.severe(exception.toString() );
			return;
		}
		for(PnInventoryDetail pid : pnindets) {
			exist = false;
			for(Equipment e : sents) {
				
				if((pid.getVendorLot() != null && !pid.getVendorLot().isEmpty()  && pid.getVendorLot().equalsIgnoreCase(e.getEquipmentNumber()))
					&&	(pid.getPn() != null && !pid.getPn().isEmpty()  && pid.getPn().equalsIgnoreCase(e.getPartNumber()))	
					) {
						if(pid.getSn() != null && !pid.getSn().isEmpty()  
						&& e.getSerialNumber() != null && !e.getSerialNumber().isEmpty()) {
							if(pid.getSn().equalsIgnoreCase(e.getSerialNumber()) ) {
								exist = true;
								break;
							}	
						}else if ((pid.getSn() == null || pid.getSn().isEmpty()) 
								&& (e.getSerialNumber() == null || e.getSerialNumber().isEmpty())) {
							exist = true;
							break;
						}
				}
				
			}
			if(!exist) {
				notSentToDatabase.add(pid);
			}
		
		}
		
		
		if(!notSentToDatabase.isEmpty()) {
			for(PnInventoryDetail pnindet : notSentToDatabase) {
				
				
				pnindet.setInstalledAc(null);
				pnindet.setInstalledPosition(null);
				pnindet.setInstalledDate(null);
				
				logger.info("REMOVING EQUIPMENT: " + pnindet.getVendorLot()  +
				" PN: "	+	pnindet.getPn() + " SN: " + pnindet.getSn()	 + " BATCH: " + pnindet.getBatch() 	
				+ " FROM AC: " + topfunctionallocation);
				
				insertData(pnindet);
				
			}
		}
		
}
	
	
	

	public boolean insertACData(Equipment input , String topfunctionallocation) {
		String result = "OK";
		
		boolean existPnInventoryDetail = false;
		NotePad notepad = null;
		
		PnInventoryDetail pnindet = null;
		if(input != null  && checkMinValue(input)) 
		{
			String partNumber_Tool ;
			partNumber_Tool = input.getPartNumber().replaceAll("\"", "IN");
			partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
			if(!partNumber_Tool.contains(":"))
			{
				partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
			}
			input.setPartNumber(partNumber_Tool);
			
			
			if(input.getSerialNumber() != null && !input.getSerialNumber().isEmpty()) {
				try 
				{
					pnindet = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :eqNum and p.sn = :ser", PnInventoryDetail.class)
							.setParameter("eqNum", input.getPartNumber())
							.setParameter("ser", input.getSerialNumber())
							.getSingleResult();
					existPnInventoryDetail = true;
				}
				catch(Exception e)
				{
					pnindet = new PnInventoryDetail();
					pnindet.setCreatedDate(new Date());
					pnindet.setCreatedBy("TRAX_IFACE");
					
					//EMRO fields to create basic object
					pnindet.setQtyAvailable(new BigDecimal(1));
					pnindet.setQtyReserved(new BigDecimal(0));
					pnindet.setQtyInTransfer(new BigDecimal(0));
					pnindet.setQtyPendingRi(new BigDecimal(0));
					pnindet.setQtyUs(new BigDecimal(0));
					pnindet.setUnitCost(new BigDecimal(0));
					pnindet.setCurrencyExchangeRate(new BigDecimal(1));
					pnindet.setQtyInRental(new BigDecimal(0));
					pnindet.setSecondaryCost(new BigDecimal(0));
					pnindet.setSecondaryCurrencyExchange(new BigDecimal(1));
					
					pnindet.setKitNo(new BigDecimal(0));
					pnindet.setBlobNo(new BigDecimal(0));
					pnindet.setDocumentNo(new BigDecimal(0));
					pnindet.setFilingSequence(new BigDecimal(0));
					pnindet.setNoOfTagPrint(new BigDecimal(0));
					pnindet.setSosHour(new BigDecimal(0));
					pnindet.setSlot(new BigDecimal(0));
					pnindet.setNlaPosition(" ");
					
					pnindet.setInventoryType("MAINTENANCE");	
					try
					{
						String company = System.getProperty("profile_company");
						pnindet.setGlCompany(company);
					}
					catch(Exception e1) {
						result = "Can not insert/update AC: "+ input.getFunctionalLocation() +" as ERROR: Company could not be found";
						logger.severe(result);
						ACConfigurationController.addError(result);
						return false;
					}
					
					
					pnindet.setCondition("NEW");
					//gl
					
				}
				
			}else {
				
				try 
				{
					pnindet = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :eqNum and p.sn is null and p.location is null", PnInventoryDetail.class)
							.setParameter("eqNum", input.getPartNumber())
							.getSingleResult();
					existPnInventoryDetail = true;
				}
				catch(Exception e)
				{
					pnindet = new PnInventoryDetail();
					pnindet.setCreatedDate(new Date());
					pnindet.setCreatedBy("TRAX_IFACE");
					
					//EMRO fields to create basic object
					pnindet.setQtyAvailable(new BigDecimal(1));
					pnindet.setQtyReserved(new BigDecimal(0));
					pnindet.setQtyInTransfer(new BigDecimal(0));
					pnindet.setQtyPendingRi(new BigDecimal(0));
					pnindet.setQtyUs(new BigDecimal(0));
					pnindet.setUnitCost(new BigDecimal(0));
					pnindet.setCurrencyExchangeRate(new BigDecimal(1));
					pnindet.setQtyInRental(new BigDecimal(0));
					pnindet.setSecondaryCost(new BigDecimal(0));
					pnindet.setSecondaryCurrencyExchange(new BigDecimal(1));
					
					pnindet.setKitNo(new BigDecimal(0));
					pnindet.setBlobNo(new BigDecimal(0));
					pnindet.setDocumentNo(new BigDecimal(0));
					pnindet.setFilingSequence(new BigDecimal(0));
					pnindet.setNoOfTagPrint(new BigDecimal(0));
					pnindet.setSosHour(new BigDecimal(0));
					pnindet.setSlot(new BigDecimal(0));
					pnindet.setNlaPosition(" ");
					
					pnindet.setInventoryType("MAINTENANCE");	
					try
					{
						String company = System.getProperty("profile_company");
						pnindet.setGlCompany(company);
					}
					catch(Exception e1) {
						result = "Can not insert/update AC: "+ input.getFunctionalLocation() +" as ERROR: Company could not be found";
						logger.severe(result);
						ACConfigurationController.addError(result);
						return false;
					}
					
					
					pnindet.setCondition("NEW");
					//gl
					
				}
			}
			
			
			pnindet.setModifiedBy("TRAX_IFACE");
			pnindet.setModifiedDate(new Date());
			
			pnindet.setVendorLot(input.getEquipmentNumber());
			if(input.getPartNumber() != null && !input.getPartNumber().isEmpty()) {
				try 
				{
					PnMaster pnmas = em.createQuery("Select p From PnMaster p where p.id.pn = :partn", PnMaster.class)
							.setParameter("partn", input.getPartNumber())
							.getSingleResult();
				}
				catch(Exception e) {
					result = "Can not insert/update AC: "+ input.getFunctionalLocation() +" PN: "+input.getPartNumber()+" as ERROR: pn could not be found";
					logger.severe(result);
					ACConfigurationController.addError(result);
					return false; 
				}
				pnindet.setPn(input.getPartNumber());
			}
			
			
			pnindet.setFUNCTIONALLOCATION(input.getFunctionalLocation());
			
			if(input.getSuperiorEquipment() != null && !input.getSuperiorEquipment().isEmpty()) 
			{
				pnindet.setSUPERIOREQUIPMENT(input.getSuperiorEquipment());
			}
			
			
			if(input.getSerialNumber() != null && !input.getSerialNumber().isEmpty()) 
			{
				pnindet.setSn(input.getSerialNumber());
			}
			
			if(input.getPosition() != null && !input.getPosition().isEmpty()) 
			{
				pnindet.setInstalledPosition(input.getPosition());
			}
			
		
			try {
				pnindet.setInstalledDate(new SimpleDateFormat("yyyyMMdd").parse(input.getValidFrom()));
			} catch (ParseException e2) {
				result = "Can not insert/update AC: "+ input.getFunctionalLocation() +" Date: "+input.getValidFrom()+" as ERROR: Date is in wrong format";
				logger.severe(result);
				ACConfigurationController.addError(result);
				return false;
			}
			pnindet.setInstalledHour(new BigDecimal(0));
			pnindet.setInstalledMinute(new BigDecimal(0));
			pnindet.setInstalledAc(topfunctionallocation);
			
			

			if(input.getDescription() != null && !input.getDescription().isEmpty()) 
	        {
	            try {
	                
	                notepad = null;
	                try {
	                    if (pnindet.getNotes() != null) {
	                        Query query = em.createQuery("SELECT n FROM NotePad n WHERE n.id.notes = :notes");
	                        query.setParameter("notes", pnindet.getNotes().longValue());
	                        List<NotePad> notepads = query.getResultList();
	                        if (!notepads.isEmpty()) {
	                            notepad = notepads.get(0);
	                        }
	                    }
	                } catch (Exception e) {
	                    logger.info("No existing note found, creating new one");
	                }
	                
	              
	                if (notepad == null) {
	                    NotePadPK pk = new NotePadPK();
	                    notepad = new NotePad();
	                    notepad.setCreatedDate(new Date());
	                    notepad.setCreatedBy("TRAX_IFACE");
	                    notepad.setId(pk);
	                    notepad.setPrintNotes("YES");
	                    
	                    try {
	                        notepad.getId().setNotes(getTransactionNo("NOTES").longValue());
	                    } catch (Exception e1) {
	                        logger.severe("Error getting transaction number: " + e1.toString());
	                        e1.printStackTrace();
	                    }
	                    notepad.getId().setNotesLine(getLine(new BigDecimal(notepad.getId().getNotes()), "notes_line", "NOTE_PAD", "NOTES"));
	                    pnindet.setNotes(new BigDecimal(notepad.getId().getNotes()));
	                }
	                
	                notepad.setModifiedBy("TRAX_IFACE");
	                notepad.setModifiedDate(new Date());
	                
	               
	                EntityTransaction tx = null;
	                Connection conn = null;
	                PreparedStatement ps = null;
	                
	                try {
	                    tx = em.getTransaction();
	                    if (!tx.isActive()) {
	                        tx.begin();
	                    }
	                    
	                   
	                    if (notepad.getNotesText() == null) {
	                        Query insertQuery = em.createNativeQuery(
	                            "INSERT INTO NOTE_PAD (NOTES, NOTES_LINE, CREATED_BY, CREATED_DATE, " +
	                            "MODIFIED_BY, MODIFIED_DATE, PRINT_NOTES) " +
	                            "VALUES (?, ?, ?, ?, ?, ?, ?)"
	                        );
	                        insertQuery.setParameter(1, notepad.getId().getNotes());
	                        insertQuery.setParameter(2, notepad.getId().getNotesLine());
	                        insertQuery.setParameter(3, notepad.getCreatedBy());
	                        insertQuery.setParameter(4, new java.sql.Date(notepad.getCreatedDate().getTime()));
	                        insertQuery.setParameter(5, notepad.getModifiedBy());
	                        insertQuery.setParameter(6, new java.sql.Date(notepad.getModifiedDate().getTime()));
	                        insertQuery.setParameter(7, notepad.getPrintNotes());
	                        
	                        insertQuery.executeUpdate();
	                    }
	                    
	                  
	                    conn = em.unwrap(Connection.class);
	                    ps = conn.prepareStatement(
	                        "UPDATE NOTE_PAD SET NOTES_TEXT = ? WHERE NOTES = ? AND NOTES_LINE = ?"
	                    );
	                    
	                    
	                    ps.setString(1, input.getDescription());
	                    ps.setLong(2, notepad.getId().getNotes());
	                    ps.setLong(3, notepad.getId().getNotesLine());
	                    
	                    ps.executeUpdate();
	                    
	                    if (!tx.getRollbackOnly()) {
	                        tx.commit();
	                    }
	                    
	                    logger.info("INSERTING NOTE: " + notepad.getId().getNotes());
	                } catch (Exception e) {
	                    if (tx != null && tx.isActive()) {
	                        tx.rollback();
	                    }
	                    logger.severe("Error inserting/updating note: " + e.toString());
	                    e.printStackTrace();
	                    result = "Error inserting note: " + e.toString();
	                    ACConfigurationController.addError(result);
	                } finally {
	                    if (ps != null) {
	                        try {
	                            ps.close();
	                        } catch (Exception e) {
	                            // Ignore
	                        }
	                    }
	                }
	            } catch (Exception e) {
	                logger.severe("Error processing note: " + e.toString());
	                e.printStackTrace();
	                result = "Error processing note: " + e.toString();
	                ACConfigurationController.addError(result);
	            }
	        }
			
			if(!existPnInventoryDetail) {
				try {
					long batch;
					//batch =	getBatch(getCon(),lastBatch);
					batch = getTransactionNo("BATCH").longValue();
					pnindet.setBatch(batch);
					pnindet.setGoodsRcvdBatch(new BigDecimal(batch));
					lastBatch = batch;
				} catch (Exception e1) {
					logger.severe("Error getting batch number: " + e1.toString());
	                e1.printStackTrace();
					
				}
			}
			
			
			
			
			
			
			logger.info("INSERTING EQUIPMENT: " + pnindet.getVendorLot() +   
					" , PN: "	+	pnindet.getPn() + " , SN: " + pnindet.getSn()		
					+ " , INSERTING AC: " + input.getFunctionalLocation() + ", BATCH: " + pnindet.getBatch() );
			
			
			insertData(pnindet);
			
			

			
			sentToDatabase = true;
			
		}else 
		{
			result = "Can not insert/update AC: "+ input.getFunctionalLocation() +" as ERROR: Equipment is null or does not have minimum values";
			logger.severe(result);
			ACConfigurationController.addError(result);
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
			ACConfigurationController.addError(exceuted);
			logger.severe(e.toString());
			e.printStackTrace();
		}
	}
	
	
	
	
	private boolean checkMinValue(Equipment RCV) {
		
		if( RCV.getEquipmentNumber() == null || RCV.getEquipmentNumber().isEmpty()) {
			
			return false;
		}
			
		if( RCV.getPartNumber() == null || RCV.getPartNumber().isEmpty()) {
			return false;
		}
			
		if( RCV.getValidFrom() == null || RCV.getValidFrom().isEmpty()) {
			return false;
		}
			
		if( RCV.getValidTo() == null || RCV.getValidTo().isEmpty()) {
			return false;
		}
			
		if( RCV.getFunctionalLocation() == null || RCV.getFunctionalLocation().isEmpty()) {
			return false;
		}
			
		
			
	
			
		return true;
	}
	
	public boolean checkTopFunctionalLocation(TopFunctionalLocation topFunctionalLocation) {
		
		if( topFunctionalLocation.getTopFunctionalLocation() == null || topFunctionalLocation.getTopFunctionalLocation().isEmpty()) {
			return false;
		}
		
		if( topFunctionalLocation.getEquipment() == null || topFunctionalLocation.getEquipment().isEmpty()) {
			return false;
		}
		
		return true;
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
				insertData(lock,"lock");
				return true;
			}
			return false;
		}
		else
		{
			lock.setLocked(new BigDecimal(1));
			insertData(lock,"lock");
			return true;
		}
		
	}
	
	private <T> void insertData( T data,String name) 
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
			logger.severe(e.getMessage());
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
		//em.lock(lock, LockModeType.NONE);
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
		//em.lock(lock, LockModeType.NONE);
		em.merge(lock);
		em.getTransaction().commit();
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
	
}
