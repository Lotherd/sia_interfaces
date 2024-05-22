package trax.aero.data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import trax.aero.logger.LogManager;
import trax.aero.model.AcMaster;
import trax.aero.model.AcTypeSeriesMaster;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.PnEffectivityDistribution;
import trax.aero.model.PnEffectivityDistributionPK;
import trax.aero.model.PnEffectivityHeader;
import trax.aero.model.PnEffectivityHeaderPK;
import trax.aero.model.PnInterchangeable;
import trax.aero.model.PnMaster;
import trax.aero.model.RelationMaster;
import trax.aero.model.RelationMasterPK;
import trax.aero.model.SystemTranCode;
import trax.aero.model.SystemTranCodePK;
import trax.aero.pojo.PartNumber;

public class PnMasterData 
{
	
	EntityManagerFactory factory = null;
	
	@PersistenceContext(unitName = "ImportDS")	public EntityManager em = null;
	
	public String error = "";
	Logger logger = LogManager.getLogger("PartMaster_I16I50");
	//public InterfaceLockMaster lock;
	
	
	public PnMasterData(EntityManagerFactory factory2)
	{
		factory = factory2;
		em = factory.createEntityManager();
		
	}
	
	public boolean savePnMaster(PartNumber part)
	{
		
		boolean success = true;		
			try
			{
				logger.info("Processing part: " + part.getPn());
				if(!insertPart(part))
				{
					success = false;
				}							
			}
			catch(Exception e)
			{
				e.printStackTrace();
				logger.severe(e.toString());
			}
		
		return success;
		
	}
	
	
	public boolean insertPart(PartNumber part)
	{
		if(part.getPn() != null && part.getPn().length() != 0)
		{
			part.setPn(part.getPn().replaceAll("&quot;","\""));
			part.setPn(part.getPn().replaceAll("'", "FT"));
			part.setPn(part.getPn().replaceAll("\"", "IN"));
		}
		boolean update = false;
		boolean change = false;
		boolean newRecord = false;
		PnMaster pn = null, pnChange = null;
		try
		{
			pn = (PnMaster) this.em.createQuery("select p from PnMaster p where p.pn = :pn")
					.setParameter("pn", part.getPn())
					.getSingleResult();
			update = true;
		}
		catch(Exception e)
		{
			newRecord = true;
			pn = new PnMaster();
			if(part.getPn() != null && part.getPn().length() != 0)
			{
				
				int index = part.getPn().lastIndexOf(':');
				if(index != -1)
				{
					pn.setPn(part.getPn());
					pn.setVendor(part.getPn().substring(index + 1));
				}
				else
				{
					pn.setPn(part.getPn() + ":UPLOAD");
					pn.setVendor("UPLOAD");
				}
				if(pn.getPn().length() > 35) {
					error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + "Pn size is greater than 35 SIZE " +pn.getPn().length()+
							 System.lineSeparator() +  System.lineSeparator());
					return false;
				}
				
				
				try
				{
					em.createQuery("select r from RelationMaster r where r.id.relationTransaction = :vendor and r.id.relationCode = :code")
					.setParameter("vendor", "VENDOR")
					.setParameter("code", pn.getVendor())
					.getSingleResult();
				}
				catch(Exception ex)
				{
					error = error.concat("Warning Inserting part " + part.getPn() + " Vendor "+pn.getVendor() +" was not found, Insert Vendor with default values " +
							 System.lineSeparator() +  System.lineSeparator());
					
					RelationMaster vendor = new RelationMaster();
					RelationMasterPK pk = new RelationMasterPK();
					vendor.setId(pk);
					
					vendor.getId().setRelationCode(pn.getVendor());
					vendor.getId().setRelationTransaction("VENDOR");
					vendor.setRelationCategory("DEFAULT");
					vendor.setName(pn.getVendor());
					vendor.setCurrency("USD");
					vendor.setStatus("ACTIVE");
					vendor.setSupplier("NO");
					vendor.setRepair("NO");
					vendor.setSupplierMaintenance("NO");
					vendor.setSupplierGeneral("NO");
					vendor.setSupplierGse("NO");
					vendor.setEarliest("N");
					vendor.setRepair("NO");
					vendor.setGst("NO");
					vendor.setGstTaxPercent(new BigDecimal(0));
					vendor.setRepairMaintenance("NO");
					vendor.setRepairGeneral("NO");
					vendor.setRepairGse("NO");
					vendor.setLhtVendor("N");
					vendor.setEmpVendor("NO");
					vendor.setRtvReference("NO");
					vendor.setAutoEmailRo("N");
					vendor.setExternalReceivingVendor("N");
					
					
					
					vendor.setCreatedBy("TRAX_IFACE");
					vendor.setCreatedDate(new Date());
					vendor.setModifiedBy("TRAX_IFACE");
					vendor.setModifiedDate(new Date());
					insertDefaultVendorCategory();
					
					
					
					if(!em.getTransaction().isActive())
						em.getTransaction().begin();
					em.merge(vendor);
					em.getTransaction().commit();
					em.clear();
					
				}
			}
			else
			{
				error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + "Pn field is required" +
						 System.lineSeparator() +  System.lineSeparator());
				return false;
			}
			pn = defaultVal(pn);
		}
		if(update) {
			pnChange = pn;
		}
			
		
		
		if(part.getDescription() != null && part.getDescription().length() != 0)
		{
			pn.setPnDescription(part.getDescription());
		}
		else if(!update)
		{
			error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + "Description field is required"
					+ System.lineSeparator() + System.lineSeparator());
			return false;
		}
		
		if(part.getStockUOM() != null && part.getStockUOM().length() != 0)
		{
			if(tranCodeExists("UOM", part.getStockUOM()))
			{
				pn.setStockUom(part.getStockUOM());
			}
			else
			{
				error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + "StockUOM was not found on SYSTEM_TRAN_CODE"
						+ System.lineSeparator() + System.lineSeparator());
				return false;
			}
			
		}
		else if(!update)
		{
			error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + "Stock UOM field is required"
					+ System.lineSeparator() + System.lineSeparator());
			return false;
		}
		
		if(part.getPnCategory() != null && part.getPnCategory().length() != 0)
		{
			try
			{
				SystemTranCode category = (SystemTranCode) em.createQuery("select s from SystemTranCode s where s.id.systemTransaction = :tran and s.id.systemCode = :code")
				.setParameter("tran", "PNCATEGORY")
				.setParameter("code",  part.getPnCategory())
				.getSingleResult();
				pn.setCategory(part.getPnCategory());
				
				if(category.getGlCompany() != null)
				{
					pn.setGlCompany(category.getGlCompany());
					pn.setGl(category.getGl());
					pn.setGlExpenditure(category.getGlExpenditure());
					pn.setGlCostCenter(category.getGlCostCenter());
				}
				
				if(category.getRoGlCompany() != null)
				{
					pn.setRoGlCompany(category.getRoGlCompany());
					pn.setRoGl(category.getRoGl());
					pn.setRoGlExpenditure(category.getRoGlExpenditure());
					pn.setRoGlCostCenter(category.getRoCostCenter());
				}
				
				if(category.getExGlCompany() != null)
				{
					pn.setExGlCompany(category.getExGlCompany());
					pn.setExGl(category.getExGl());
					pn.setExGlExpenditure(category.getExGlExpenditure());
					pn.setExGlCostCenter(category.getExCostCenter());
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + "Category was not found on SYSTEM_TRAN_CODE" 
						+ System.lineSeparator() + System.lineSeparator());
				return false;
			}
			
		}
		else if(!update)
		{
			error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + "Category field is required"
					+ System.lineSeparator() + System.lineSeparator());
			return false;
		}
		
		if(part.getEffectivity() != null && part.getEffectivity().length() != 0)
		{
			pn.setEffectivityPnInterchangeable(part.getEffectivity());
		}
		
		if(part.getToolControl() != null && part.getToolControl().length() != 0)
		{
			pn.setToolControlItem(part.getToolControl());
		}
		
		if(part.getToolCalibration() != null && part.getToolCalibration().length() != 0)
		{
			pn.setToolCalibrationFlag(part.getToolCalibration());
		}
		
		if(part.getToolLife() != null && part.getToolLife().length() != 0)
		{
			pn.setToolLifeEom(part.getToolLife());
		}
		
		if("X".equalsIgnoreCase(part.getDeletionFlag()) || "Y".equalsIgnoreCase(part.getDeletionFlag()))
		{
			pn.setStatus("INACTIVE");
		}
		else
		{
			pn.setStatus("ACTIVE");
		}
		
		
		
		try
		{
			if(part.getAta() != null && part.getAta().trim().length() != 0)
			{
				int sectIndex = part.getAta().indexOf('-');
				
				if(sectIndex != -1)
				{
					pn.setChapter(new BigDecimal(part.getAta().substring(0, sectIndex)));
					
					String sect = part.getAta().substring(sectIndex + 1);
					
					int partIndex = sect.indexOf('-');
								
					if(partIndex != -1)
					{
						pn.setSection(new BigDecimal(sect.substring(0, partIndex)));
						pn.setParagraph(new BigDecimal(sect.substring(partIndex)));
					}
					else
					{
						pn.setSection(new BigDecimal(sect));
						pn.setParagraph(new BigDecimal(0));
					}
				}
				else
				{
					
					if(part.getAta().length() <= 2) {
						if(!isNumeric(part.getAta())) {
							throw new Exception("ATA "+part.getAta() +" is not a number");
						}
						pn.setChapter(new BigDecimal(part.getAta()));
						pn.setSection(new BigDecimal(0));
						pn.setParagraph(new BigDecimal(0));
					}else {
						throw new Exception("ATA has size greater than 2 "+part.getAta() + " SIZE " +part.getAta().length());
					}
					
				}
			}
			else
			{
				pn.setChapter(new BigDecimal(0));
				pn.setSection(new BigDecimal(0));
				pn.setParagraph(new BigDecimal(0));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			error = error.concat(" WARNING ATA for part " + part.getPn() + ", will use default value 0 for chapter section paragraph, "+ System.lineSeparator() + e.getMessage() + System.lineSeparator()
			+ System.lineSeparator());
			pn.setChapter(new BigDecimal(0));
			pn.setSection(new BigDecimal(0));
			pn.setParagraph(new BigDecimal(0));
			//return false;
		}
		pn.setInventoryType("MAINTENANCE");
		if(!update) {
			pn.setCreatedBy("TRAX_IFACE");
			pn.setCreatedDate(new Date());
		}
		
		if(( update && checkIfChange(pnChange,pn) ) || newRecord) {
			change = true;
		}
		
		
		pn.setModifiedBy("TRAX_IFACE");
		pn.setModifiedDate(new Date());
		
		
		try
		{
			if(!change) {
				logger.info("No Change to PN " + pn.getPn() );
			}else {		
				logger.info("INSERTING PN " + pn.getPn());
				if(!em.getTransaction().isActive())
					em.getTransaction().begin();
				em.merge(pn);
				em.getTransaction().commit();
				em.clear();
			}
			
			
			if(part.getInterList() != null && part.getInterList().getInterchangeableBothPn() != null)
			{
				for(String inter : part.getInterList().getInterchangeableBothPn())	
				{
					if(inter.equals(pn.getPn()))
					{
						insertInter(inter, pn);
					}
				}
			
				
				if(!part.getInterList().getInterchangeableBothPn().contains(pn.getPn()))
					insertInter(pn.getPn(), pn);
			}
			else
				insertInter(pn.getPn(), pn);
			
			
			insertEffectivity(pn);
			
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			String errorMessage = "";
			int endOfError = e.getMessage().indexOf("Error Code:");
			if(endOfError != -1)
				errorMessage = e.getMessage().substring(0, endOfError);
			else
				errorMessage = e.getMessage();
			
			logger.severe(errorMessage);
			
					
			error = error.concat("Error Inserting part " + part.getPn() + System.lineSeparator() + errorMessage + System.lineSeparator()
			+ System.lineSeparator());
			return false;
		}
	}
	
	
	
	
	
	public boolean insertInter(String pnInter, PnMaster master)
	{
		
		PnInterchangeable inter = new PnInterchangeable();
		try
		{
			inter = (PnInterchangeable) em.createQuery("Select p from PnInterchangeable p where p.pnInterchangeable = :pn")
			.setParameter("pn", pnInter)
			.getSingleResult();
			
			if(inter.getPnMaster().getPn().equals(master.getPn()) || error.contains(pnInter))
				return true;
			error = error.concat("Error Inserting Interchangeable " + pnInter + System.lineSeparator() + "Interchangeable already exists" +
					 System.lineSeparator() +  System.lineSeparator());
			return false;
			
		}
		catch(Exception e)
		{
			
		}
		
		if(pnInter == null || pnInter.length() == 0)
		{
			return false;
		}
		
		int index = pnInter.lastIndexOf(":");
		
		if(index != -1)
		{
			inter.setPnInterchangeable(pnInter);
			inter.setVendor(pnInter.substring(index + 1));
			inter.setManufacturer(pnInter.substring(index + 1));
		}
		else
		{
			inter.setPnInterchangeable(pnInter + ":UPLOAD");
			inter.setVendor("UPLOAD");
			inter.setManufacturer("UPLOAD");
			
		}
		
		
		try
		{
			em.createQuery("select r from RelationMaster r where r.id.relationTransaction = :vendor and r.id.relationCode = :code")
			.setParameter("vendor", "VENDOR")
			.setParameter("code", inter.getVendor())
			.getSingleResult();
		}
		
		
		catch(Exception ex)
		{
			
			error = error.concat("Warning Inserting Interchangeable " + pnInter + System.lineSeparator() + " Vendor "+inter.getVendor() +" was not found, inserting Vendor with default values " +
					 System.lineSeparator() +  System.lineSeparator());
			RelationMaster vendor = new RelationMaster();
			RelationMasterPK pk = new RelationMasterPK();
			vendor.setId(pk);
			
			vendor.getId().setRelationCode(inter.getVendor());
			vendor.getId().setRelationTransaction("VENDOR");
			vendor.setRelationCategory("DEFAULT");
			vendor.setName(inter.getVendor());
			vendor.setCurrency("USD");
			vendor.setStatus("ACTIVE");
			vendor.setSupplier("NO");
			vendor.setRepair("NO");
			vendor.setSupplierMaintenance("NO");
			vendor.setSupplierGeneral("NO");
			vendor.setSupplierGse("NO");
			vendor.setEarliest("N");
			vendor.setRepair("NO");
			vendor.setGst("NO");
			vendor.setGstTaxPercent(new BigDecimal(0));
			vendor.setRepairMaintenance("NO");
			vendor.setRepairGeneral("NO");
			vendor.setRepairGse("NO");
			vendor.setLhtVendor("N");
			vendor.setEmpVendor("NO");
			vendor.setRtvReference("NO");
			vendor.setAutoEmailRo("N");
			vendor.setExternalReceivingVendor("N");
			
			
			
			vendor.setCreatedBy("TRAX_IFACE");
			vendor.setCreatedDate(new Date());
			vendor.setModifiedBy("TRAX_IFACE");
			vendor.setModifiedDate(new Date());
			insertDefaultVendorCategory();
			
			
			
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(vendor);
			em.getTransaction().commit();
			em.clear();
			
		}
		
		if(pnInter.equalsIgnoreCase(master.getPn()))
		{
			inter.setInterchangeableType("M");
		}
		else
			inter.setInterchangeableType("B");
		
		inter.setCreatedBy("TRAX_IFACE");
		inter.setCreatedDate(new Date());
		inter.setModifiedBy("TRAX_IFACE");
		inter.setModifiedDate(new Date());
		inter.setPnMaster(master);
		inter.setStatus("ACTIVE");
		
		
		try
		{
			logger.info("INSERTING INTER PN " + inter.getPnInterchangeable());
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(inter);
			em.getTransaction().commit();
			em.clear();
			return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			String errorMessage = "";
			int endOfError = e.getMessage().indexOf("Error Code:");
			if(endOfError != -1)
				errorMessage = e.getMessage().substring(0, endOfError);
			else
				errorMessage = e.getMessage();
			error = error.concat("Error Inserting Interchangeable " + pnInter + System.lineSeparator() + errorMessage +
					 System.lineSeparator() +  System.lineSeparator());
			return false;
		}
		
	}
	
	public boolean tranCodeExists(String transaction, String code)
	{
		
		try
		{
			em.createQuery("select s from SystemTranCode s where s.id.systemTransaction = :tran and s.id.systemCode = :code")
			.setParameter("tran", transaction)
			.setParameter("code", code)
			.getSingleResult();
			return true;
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.severe(e.toString());
			return false;
		}
	}

	
	
	private PnMaster defaultVal(PnMaster pn)
	{
		if(pn == null)
			return null;
		
		pn.setRiFlag("N");
		pn.setShelfLifeFlag("N");
		pn.setToolCalibrationFlag("N");
		pn.setLotControl("N");
		pn.setHazardousMaterial("N");
		pn.setEtopsFlag("NO");
		pn.setAutoInstalled("N");
		pn.setRemovalInstallationAudit("NO");
		pn.setAlertCategory("DAYS");
		pn.setInternalShowCapability("NO");
		pn.setTaxable("NO");
		pn.setToolControlItem("N");
		pn.setEngine("NONE");
		pn.setReliabilityMonitored("NO");
		pn.setWeightControlAtBatchLevel("NO");
		pn.setAutoSnCreate("NO");
		pn.setToolLifeDays(new BigDecimal(0));
		pn.setStandardCost(new BigDecimal(0));
		pn.setAverageCost(new BigDecimal(0));
		pn.setFaNoMonthDepreciation(new BigDecimal(0));
		pn.setFaResidualValuePercent(new BigDecimal(0));
		pn.setSecondaryCost(new BigDecimal(0));
		pn.setAlertNoOfRemoval(new BigDecimal(0));
		pn.setAlertCategoryNoOf(new BigDecimal(0));
		pn.setShelfLifeEom("NO");
		pn.setToolLifeEom("NO");
		pn.setShelfLifeDays(new BigDecimal(0));
		pn.setShockWatch("N");
		pn.setModStatus("NO");
		
		return pn;
	}

	
	void insertDefaultVendorCategory(){
		SystemTranCode category = null;
		try {
			 category = (SystemTranCode) em.createQuery("select s from SystemTranCode s where s.id.systemTransaction = :tran and s.id.systemCode = :code")
					.setParameter("tran", "VENDORCATEGORY")
					.setParameter("code",  "DEFAULT")
					.getSingleResult();
		}catch(Exception e) {
			category = new SystemTranCode();
			SystemTranCodePK pk = new SystemTranCodePK();
			category.setId(pk);
			category.setCreatedBy("TRAX_IFACE");
			category.setCreatedDate(new Date());
			category.setModifiedBy("TRAX_IFACE");
			category.setModifiedDate(new Date());
			
			category.getId().setSystemCode("DEFAULT");
			category.getId().setSystemTransaction("VENDORCATEGORY");
			category.getId().setSystemTranCodeSub("          ");
			category.setPnTransaction("C");
			category.setPnCostingMethod("A");
			category.setConfigFlag("N");
			category.setTagType("FORMONE");
			category.setTemporaryRevision("NO");
			category.setChapterMandatory("NO");
			category.setSectionMandatory("NO");
			category.setDeferCatAutoMddrClosing("YES");
			category.setDefectReportPilot("YES");
			category.setVendorStatus("ACTIVE");
			category.setStatus("ACTIVE");
			category.setPnCategoryInventoryType("MAINTENANCE");
			category.setDoNotAllowReset("NO");
			category.setAutoResetOnRo("NO");
			category.setDoNotAllowResetProd("NO");
			category.setAcRestriction("P");
			category.setRadiocative("N");
			category.setCodeColor("8421376");
			
			category.setConfigNumber(new BigDecimal(0));
			category.setCurrencyExchange(new BigDecimal(0));
			
			category.setMelCycles(new BigDecimal(0));
			category.setMelHours(new BigDecimal(0));
			category.setRevenueFlight("NO");
			category.setEtops("NO");
			
			
			
			category.setAlertNoOfRemoval(new BigDecimal(0));
			category.setAlertCategoryNoOf(new BigDecimal(0));
			
			category.setAcMandatory("NO");
			category.setEquipmentRefDesignator("N");
			category.setRosClassificationCode("O");
			category.setIfrs("NO");
			category.setSupplier("NO");
			category.setSupplierMaintenance("NO");
			category.setSupplierGeneral("NO");
			category.setSupplierGse("NO");
			category.setRepair("NO");
			category.setRepairMaintenance("NO");
			category.setRepairGeneral("NO");
			category.setRepairGse("NO");
			category.setFreightForwarder("N");
			category.setService("NO");
			category.setAdCategory("N");
			category.setEcNameOverride("YES");
			category.setVbLifeLimit("NO");
			category.setVopBaseOnCondition(new BigDecimal(0));
			category.setCabin("N");
			category.setHighDollar("N");
			category.setBinCountFrequency("YEAR");
			category.setBinCountNoWeekend("NO");
			category.setFollowGrb("NO");
			category.setLoadAtRecv("N");
			category.setAllowBust("NO");
			category.setSwoRequisition("N");
			
			category.setOpsRestriction("NO");
			category.setPaperRequired("YES");
			category.setImage("bmp-ghs-01-bmp");
			category.setScrapReorder("YES");
			category.setEssentialityCodeLevel("NOGO");
			category.setMonthDepreciation(new BigDecimal(0));
			category.setThirdPartyWo("N");
			
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(category);
			em.getTransaction().commit();
			em.clear();
		}
	}
	
	void insertEffectivity(PnMaster pn){
		
		List<AcMaster> ACs = null;
		List<AcTypeSeriesMaster> AcTypeSeries = null;
		
		try
		{
			ACs = (List<AcMaster>)em.createQuery("SELECT a FROM AcMaster a WHERE a.ac IS NOT NULL ")
					.getResultList();
			
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
			return;
		}
		
		try
		{
			AcTypeSeries = (List<AcTypeSeriesMaster>)em.createQuery("SELECT a FROM AcTypeSeriesMaster a WHERE a.id.acSeries IS NOT NULL")
					.getResultList();
			
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
			return;
		}
		try
		{
			for(AcMaster ac : ACs) {
				synchronized (ac) {
					String a = ac.getAc();
					String p = pn.getPn();
					insertEffectivityDistrabution(p, a);
				}
			}
			
			for(AcTypeSeriesMaster actypeaeries : AcTypeSeries) {
				synchronized (actypeaeries) {
					String t = actypeaeries.getId().getAcType();
					String s = actypeaeries.getId().getAcSeries();
					String p = pn.getPn();
					insertEffectivityHeader(p, t,s);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
			return;
		}
		
	}
	
	void insertEffectivityHeader(String pn,String AcType, String AcSeries ){
		PnEffectivityHeader header = null;
		try {
			header = (PnEffectivityHeader) em.createQuery("SELECT p FROM PnEffectivityHeader p WHERE p.id.acType = :ty and p.id.acSeries = :ser and p.id.pn = :pnn")
					.setParameter("ty", AcType)
					.setParameter("ser",  AcSeries)
					.setParameter("pnn",  pn)
					.getSingleResult();
		}catch(Exception e) {
			
			PnEffectivityHeaderPK pk =new PnEffectivityHeaderPK();
			header = new PnEffectivityHeader();
			header.setId(pk);
			header.setCreatedBy("TRAX_IFACE");
			header.setCreatedDate(new Date());
			header.setModifiedBy("TRAX_IFACE");
			header.setModifiedDate(new Date());
			header.setOverride("N");
			header.setSelect("Y");
			header.getId().setPn(pn);
			header.getId().setAcSeries(AcSeries);
			header.getId().setAcType(AcType);
			logger.info("INSERTING EFFECTIVITY HEADER " + header.getId().getAcSeries() + " " + header.getId().getAcType());
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(header);
			em.getTransaction().commit();
			em.clear();
		}
		return;
	}
	
	void insertEffectivityDistrabution(String pn,String ac ){
		PnEffectivityDistribution distribution = null;
		try {
			distribution = (PnEffectivityDistribution) em.createQuery("SELECT p FROM PnEffectivityDistribution p where p.id.ac = :acc and p.id.pn = :pnn")
					.setParameter("acc", ac)
					.setParameter("pnn",  pn)
					.getSingleResult();
		}catch(Exception e) {
			
			PnEffectivityDistributionPK pk = new PnEffectivityDistributionPK();
			distribution = new PnEffectivityDistribution();
			distribution.setId(pk);
			distribution.setCreatedBy("TRAX_IFACE");
			distribution.setCreatedDate(new Date());
			distribution.setModifiedBy("TRAX_IFACE");
			distribution.setModifiedDate(new Date());
			distribution.setSelect("Y");
			distribution.getId().setPn(pn);
			distribution.getId().setAc(ac);
			logger.info("INSERTING EFFECTIVITY DIS " + distribution.getId().getAc());
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(distribution);
			em.getTransaction().commit();
			em.clear();
		}
		return;
	}
	
	
	private boolean checkIfChange(PnMaster pnChange, PnMaster pn) {
		boolean change = false,fieldchange = false;	
		for(Field field : PnMaster.class.getFields()) {
			try {
				fieldchange = field.get(pn).equals(field.get(pnChange));
				if(fieldchange) {
					change = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return change;
		
			 
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

	private  boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        new BigDecimal(strNum);
	    } catch (Exception nfe) {
	        return false;
	    }
	    return true;
	}
	
}
