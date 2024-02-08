package trax.aero.data;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.controller.ImportWarehouseController;
import trax.aero.interfaces.IImportWarehouseData;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.NotePad;
import trax.aero.model.NotePadPK;
import trax.aero.model.PnInterchangeable;
import trax.aero.model.PnInventoryDetail;
import trax.aero.model.PnMaster;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_BATCH;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_RES;
import trax.aero.pojo.MT_TRAX_SND_I46_4077_REQ;
import trax.aero.pojo.MaterialDetails;
import trax.aero.utils.ImportPoster;

@Stateless(name="ImportWarehouseData" , mappedName="ImportWarehouseData")
public class ImportWarehouseData implements IImportWarehouseData {
	
	
	String ex;
	MT_TRAX_RCV_I46_4077_RES in;
	long lastBatch = 99999;
	
	@PersistenceContext(unitName = "ImportWarehouseDS")	public EntityManager em;
	
	String exceuted = "OK";
	Logger logger = LogManager.getLogger("ImportWarehouse_I46");
	//public InterfaceLockMaster lock;
	
	

	

	public String ProcessReqestBatch(MT_TRAX_RCV_I46_4077_BATCH i) {
		
		exceuted = "OK";
		
		boolean existPnInventoryDetail = false,existNote = false;
		NotePad notepad = null;
		
		PnInventoryDetail pnindet = null;
		PnMaster pnmas = null;
		
		String partNumber_Tool ;
		partNumber_Tool = i.getPN().replaceAll("\"", "in");
		partNumber_Tool = partNumber_Tool.replaceAll("'", "ft");
		if(!partNumber_Tool.contains(":"))
		{
			partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
		}
		i.setPN(partNumber_Tool);
		
		if(i != null  && checkMinValueBatch(i)) 
		{
			try 
			{
				pnindet = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :eqNum and p.sn is null and p.location = :loc and p.createdBy != :create", PnInventoryDetail.class)
						.setParameter("eqNum", i.getPN())
						.setParameter("loc", i.getLOCATION())
						.setParameter("create", "ISSUEIFACE")
						.getSingleResult();
				existPnInventoryDetail = true;
			}
			catch(Exception e)
			{
				//e.printStackTrace();
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
					String company = (String) this.em.createQuery("select p.profile from ProfileMaster p")
							.getSingleResult();
					pnindet.setGlCompany(company);
				}
				catch(Exception e1) {
					
					exceuted = "Can not insert/update PN: "+ i.getPN() +" as ERROR: Company could not be found";
					logger.severe(exceuted);
					logger.severe(e.toString());
					ImportWarehouseController.addError(exceuted);
					return exceuted;
				}
				
				
				pnindet.setCondition("NEW");
				//gl
				
			}
			pnindet.setModifiedBy("TRAX_IFACE");
			pnindet.setModifiedDate(new Date());
			
			
			if(i.getWEIGHTUNIT() != null && !i.getWEIGHTUNIT().isEmpty()) 
			{
				pnindet.setWeightUnit(i.getWEIGHTUNIT());
			}
			
			if(i.getLOCATION() != null && !i.getLOCATION().isEmpty()) 
			{
				pnindet.setLocation(i.getLOCATION());
			}
			
			pnindet.setPn(i.getPN());
			
			
			
			if(i.getSN() != null && !i.getSN().isEmpty()) 
			{
				pnindet.setSn(i.getSN());
			}
			
			if(i.getQTY() != null && !i.getQTY().isEmpty()) {
				pnindet.setQtyAvailable(new BigDecimal(i.getQTY()));
			}
			
			if(i.getNotesText() != null && !i.getNotesText().isEmpty()) 
			{
				
				
					try 
					{
						notepad = em.createQuery("Select n from NotePad n where n.id.notes = :not", NotePad.class)
								.setParameter("not", pnindet.getNotes())
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
						
						//EMRO fields to create basic object
					}
					notepad.setModifiedBy("TRAX_IFACE");
					notepad.setModifiedDate(new Date());
					notepad.setNotesText(i.getNotesText());
					
					
				
			}
			
			if(!existPnInventoryDetail) {
				try {
					long batch;
					batch = getTransactionNo("BATCH").longValue();
					//batch =	getBatch(getCon(),lastBatch);
					pnindet.setBatch(batch);
					pnindet.setGoodsRcvdBatch(new BigDecimal(batch));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			
			
			if(!existNote) {
				try {
					notepad.getId().setNotes(((getTransactionNo("NOTES").longValue())));
					pnindet.setNotes(new BigDecimal(notepad.getId().getNotes()));
				} catch (Exception e1) {
					
				}
			}
			
			
		
			
			logger.info("INSERTING PN INVENTORY DETAIL: " + pnindet.getPn() + " BATCH: " + pnindet.getBatch());
			insertData(pnindet);
			
			if(i.getNotesText() != null && !i.getNotesText().isEmpty()) {
				logger.info("INSERTING NOTEPAD: " + notepad.getId().getNotes());
				insertData(notepad);
			}

		}else 
		{
			
			exceuted = "Can not insert/update PN: "+ i.getPN() +" as ERROR: input is null or does not have minimum values or PN does not exist";
			logger.severe(exceuted);
			ImportWarehouseController.addError(exceuted);
		}	

		
		


		return exceuted;
		
	}
	

	public String ProcessReqest(MaterialDetails i) {
		
		exceuted = "OK";
		
		
		String partNumber_Tool ;
		partNumber_Tool = i.getPN().replaceAll("\"", "in");
		partNumber_Tool = partNumber_Tool.replaceAll("'", "ft");
		
		
		if(!partNumber_Tool.contains(":") && getPN(partNumber_Tool) == null)
		{
			partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
		}
		i.setPN(partNumber_Tool);
		
	
		
		boolean existPnInventoryDetail = false,existNote = false;
		NotePad notepad = null;
		
		PnInventoryDetail pnindet = null;
		PnMaster pnmas = null;
		
		if(i != null  && checkMinValue(i)) 
		{
			try 
			{	
				if(i.getLOCATION() != null && !i.getLOCATION().isEmpty()) {
					pnindet = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :eqNum and p.sn is null and p.location = :loc and p.createdBy != :create", PnInventoryDetail.class)
							.setParameter("eqNum", i.getPN())
							.setParameter("loc", i.getLOCATION())
							.setParameter("create", "ISSUEIFACE")
							.getSingleResult();
					existPnInventoryDetail = true;
				}else {
					pnindet = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :eqNum and p.sn is null and p.location is null and p.createdBy != :create", PnInventoryDetail.class)
							.setParameter("eqNum", i.getPN())
							.setParameter("create", "ISSUEIFACE")
							.getSingleResult();
					existPnInventoryDetail = true;
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
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
					String company = (String) this.em.createQuery("select p.profile from ProfileMaster p")
							.getSingleResult();
					pnindet.setGlCompany(company);
				}
				catch(Exception e1) {
					
					exceuted = "Can not insert/update PN: "+ i.getPN() +" as ERROR: Company could not be found";
					logger.severe(exceuted);
					logger.severe(e.toString());
					ImportWarehouseController.addError(exceuted);
					return exceuted;
				}
				
				
				pnindet.setCondition("NEW");
				//gl
				
			}
			pnindet.setModifiedBy("TRAX_IFACE");
			pnindet.setModifiedDate(new Date());
			
			if(i.getWEIGHTUNIT() != null && !i.getWEIGHTUNIT().isEmpty()) 
			{
				pnindet.setWeightUnit(i.getWEIGHTUNIT());
			}
			
			if(i.getLOCATION() != null && !i.getLOCATION().isEmpty()) 
			{
				pnindet.setLocation(i.getLOCATION());
			}
			
			
			
			pnindet.setPn(i.getPN());
			
			
			
			if(i.getSN() != null && !i.getSN().isEmpty()) 
			{
				pnindet.setSn(i.getSN());
			}
			
			if(i.getQTY() != null && !i.getQTY().isEmpty()) {	
				try {
					pnindet.setQtyAvailable(new BigDecimal(i.getQTY()));
				}catch(Exception e){
					logger.warning(e.toString());
				}
			}
			
			if(i.getNotesText() != null && !i.getNotesText().isEmpty()) 
			{
				
				
					try 
					{
						notepad = em.createQuery("Select n from NotePad n where n.id.notes = :not", NotePad.class)
								.setParameter("not", pnindet.getNotes())
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
						
						//EMRO fields to create basic object
					}
					notepad.setModifiedBy("TRAX_IFACE");
					notepad.setModifiedDate(new Date());
					notepad.setNotesText(i.getNotesText());
					
					
				
			}
			
			if(!existPnInventoryDetail) {
				try {
					long batch;
					batch =	getTransactionNo("BATCH").longValue();
					//batch = getBatch(lastBatch);
					pnindet.setBatch(batch);
					pnindet.setGoodsRcvdBatch(new BigDecimal(batch));
				} catch (Exception e1) {
					
				}
			}
			
			
			
			if(!existNote) {
				try {
					notepad.getId().setNotes(((getTransactionNo("NOTES").longValue())));
					pnindet.setNotes(new BigDecimal(notepad.getId().getNotes()));
				} catch (Exception e1) {
					
				}
			}
			
			
			/*
			logger.info("INSERTING PN MASTER: " + pnmas.getPn());
			insertData(pnmas);
			*/
			
			logger.info("INSERTING PN INVENTORY DETAIL: " + pnindet.getPn() + " TRAX BATCH: " +pnindet.getBatch());
			insertData(pnindet);
			
			if(i.getNotesText() != null && !i.getNotesText().isEmpty()) {
				logger.info("INSERTING NOTEPAD: " + notepad.getId().getNotes());
				insertData(notepad);
			}

		}else 
		{
			
			exceuted = "Can not insert/update PN: "+ i.getPN() +" as ERROR: input is null or does not have minimum values or PN does not exist";
			logger.severe(exceuted);
			ImportWarehouseController.addError(exceuted);
		}	

		
		


		return exceuted;
		
	}
	
	
	
	public ArrayList<MT_TRAX_SND_I46_4077_REQ> getWarehouse() {
		
		
		ArrayList<MT_TRAX_SND_I46_4077_REQ> REQs = new ArrayList<MT_TRAX_SND_I46_4077_REQ>();
		MT_TRAX_SND_I46_4077_REQ REQ = null;
		List<PnMaster> pndlist = null;
		
		try
		{
			
			pndlist = (List<PnMaster>) em.createQuery("SELECT p FROM PnMaster p where p.interfaceTransferredDate is not null")
					.getResultList();
			
			if(pndlist != null &&  pndlist.size() != 0) {
				for(int i = 0; i < pndlist.size() ; i++) {
					REQ = new MT_TRAX_SND_I46_4077_REQ();
					REQs.add(REQ);
					
					String pn = pndlist.get(i).getPn();
					
					pn = pn.replaceAll("in", "\"");
					pn = pn.replaceAll("ft", "'");
					
					if(pn.contains(":UPLOAD"))
					{
						pn=  pn.substring(0, pn.indexOf(":"));
					}
					
					REQs.get(i).setMaterialNumber(pn);
					
					pndlist.get(i).setInterfaceTransferredDate(null);
					
					insertData(pndlist.get(i));
				}
			}
			
		}
		catch(Exception e)
		{
			REQs = null;
			
			exceuted = e.toString();
			logger.severe(exceuted);
			ImportWarehouseController.addError(exceuted);
		}
		
		return REQs;
		
	}
	
	
	
	
	/*
	public boolean deleteAllInventoryRecords() throws Exception
	{		
		CallableStatement cstmt = null;
		Connection con = null;
		Session session = null;
		try
		{
			em.getTransaction().begin();
			session = em.unwrap(Session.class);
			con = ((SessionImpl) session).connection();
			 
			cstmt = con.prepareCall("DELETE FROM pn_inventory_detail WHERE PN IS NOT NULL AND SN IS NULL AND INSTALLED_POSITION IS NULL");
			cstmt.execute();
									
			return true;			
		}
		catch (Exception e) 
		{
			
			throw new Exception("An unexpected error occurred deleting records. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
		}
		finally 
		{
			try 
			{
				if(cstmt != null && !cstmt.isClosed())
					cstmt.close();
				
				if(con != null && !con.isClosed())
					con.close();
				if(session != null)
					session.close();
				
				em.getTransaction().commit();
			} 
			catch (SQLException e) 
			{ 
				
				throw new Exception("An error " + e.getErrorCode() + " trying to close the statement. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
			}
		}
		//return false;
	}
	*/
	
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
	
	// get pn from pn master used to check if pn exist
		private String getPN(String PN) {
			try
			{
				PnInterchangeable pninter = em.createQuery("SELECT p FROM PnInterchangeable p where p.id.pnInterchangeable = :partn", PnInterchangeable.class)
				.setParameter("partn", PN)
				.getSingleResult();
				
				return pninter.getPnMaster().getPn();
			}
			catch (Exception e)
			{
				
			}
			return null;
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
				e.printStackTrace();
				exceuted = "insertData has encountered an Exception: "+e.toString();
				ImportWarehouseController.addError(exceuted);
				logger.severe(exceuted);
			}
		}
		
		private <T> void insertData( T data,String name) 
		{
			try 
			{	
				em.merge(data);
				em.flush();
			}catch (Exception e)
			{
				e.printStackTrace();
				logger.severe(e.getMessage());
			}
		}
	
		private boolean checkMinValue( MaterialDetails i) {
			
			if( i.getPN() == null || i.getPN().isEmpty() || getPN(i.getPN()) == null) {
				
				return false;
			}
			
			
			if(i.getQTY() !=null && !i.getQTY().isEmpty() && !i.getQTY().startsWith("0")) {
			
				if( i.getLOCATION() == null || i.getLOCATION().isEmpty()) {
					return false;
				}
					
				if( i.getWEIGHTUNIT() == null || i.getWEIGHTUNIT().isEmpty()) {
					return false;
				}
			}
				
			return true;
		}
		
		private boolean checkMinValueBatch( MT_TRAX_RCV_I46_4077_BATCH i) {
			
			if( i.getPN() == null || i.getPN().isEmpty() || getPN(i.getPN()) == null) {
				
				return false;
			}
			
			if(i.getQTY() !=null && !i.getQTY().isEmpty() && !i.getQTY().startsWith("0")) {
				
				if( i.getLOCATION() == null || i.getLOCATION().isEmpty()) {
					return false;
				}
					
				if( i.getWEIGHTUNIT() == null || i.getWEIGHTUNIT().isEmpty()) {
					return false;
				}
			}
					
			return true;
		}
		
		
		public void invokePOST() 
	    {    	
			String exceuted = "OK";
			final String url = System.getProperty("ImportWarehouse_URL");
			ArrayList<MT_TRAX_SND_I46_4077_REQ> out = new ArrayList<MT_TRAX_SND_I46_4077_REQ>();
			
			try 
	        {    	 
				ImportPoster poster = new ImportPoster();
				
				out = getWarehouse();
						
				boolean success = false;
				
				if(out != null && out.size() != 0) {
					for(MT_TRAX_SND_I46_4077_REQ o : out) {
						
						JAXBContext jc = JAXBContext.newInstance(MT_TRAX_SND_I46_4077_REQ.class);
						Marshaller marshaller = jc.createMarshaller();
						marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
						StringWriter sw = new StringWriter();
						marshaller.marshal(o, sw);
						
						logger.info("Ouput: " + sw.toString());
						
						success = poster.postImport(o, url);
					
						
						
						if(!success)
						{
							exceuted = "Unable to send Material: " +o.getMaterialNumber() + " to URL "+ url;
							logger.severe(exceuted);
							ImportWarehouseController.addError(exceuted);	
						}else {
							
							MT_TRAX_RCV_I46_4077_RES input = null;
							try 
					        { 
							
							
							
								String body = poster.getBody();
								StringReader sr = new StringReader(body);				
								jc = JAXBContext.newInstance(MT_TRAX_RCV_I46_4077_RES.class);
						        Unmarshaller unmarshaller = jc.createUnmarshaller();
						        input = (MT_TRAX_RCV_I46_4077_RES) unmarshaller.unmarshal(sr);
								
								
								jc = JAXBContext.newInstance(MT_TRAX_RCV_I46_4077_RES.class);
								marshaller = jc.createMarshaller();
								marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
								sw = new StringWriter();
								marshaller.marshal(input, sw);
								
								logger.info("Input: " + sw.toString());
								
								for(MaterialDetails i : input.getMaterialDetails()) {
								
									if(i.getMSGNBR().equalsIgnoreCase("53")) { 	 
										exceuted = ProcessReqest(i);
									}else {
										exceuted = "PN: "+i.getPN() +" MSGNBR: " +i.getMSGNBR() + " Remarks: " + i.getNotesText();
										ImportWarehouseController.addError(exceuted);
									}	
								}
					        	if(!exceuted.equalsIgnoreCase("OK")) {
					        		exceuted = "Issue found";
					        		throw new Exception("Issue found");
					        	}
								
						}
						catch(Exception e)
						{
							logger.severe(e.toString());
							
							ImportWarehouseController.sendEmailRest(input);
						}
				       finally 
				       {   
				    	   logger.info("finishing");
				    	   exceuted = "OK";
				       }
					}
						
					}
					if(!exceuted.equalsIgnoreCase("OK"))
					{
						throw new Exception("Issue found");
					}
				}
			}
			catch(Exception e)
			{
				logger.severe(e.toString());
				ImportWarehouseController.addError(e.toString());
				ImportWarehouseController.sendEmailPOST();
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
		
		
		public void lockTable(String notificationType)
		{
			InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType).getSingleResult();
			lock.setLocked(new BigDecimal(1));
			
			lock.setLockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			InetAddress address = null;
			try {
				address = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				
				logger.info(e.getMessage());
				//e.printStackTrace();
			}
			lock.setCurrentServer(address.getHostName());
			insertData(lock,"lock");
		}
		
		public void unlockTable(String notificationType)
		{
			
			InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType).getSingleResult();
			lock.setLocked(new BigDecimal(0));
			
			lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			insertData(lock,"lock");
		}
		
}
