package trax.aero.data;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import trax.aero.controller.ToolsCalibrationController;
import trax.aero.exception.CustomizeHandledException;
import trax.aero.interfaces.IToolsCalibrationData;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.PnInterchangeable;
import trax.aero.model.PnInventoryDetail;
import trax.aero.model.PnMaster;
import trax.aero.pojo.ToolsCalibrationMaster;
import trax.aero.utils.DataSourceClient;
import trax.aero.utils.ErrorType;

@Stateless(name="ToolsCalibrationData" , mappedName="ToolsCalibrationData")
public class ToolsCalibrationData implements IToolsCalibrationData {
	
	
	
	@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
	String exceuted;
	public InterfaceLockMaster lock;
	Logger logger = LogManager.getLogger("ToolsCalibration_I75");

	public EntityManager getEM() {
		return em;
	}
	
	public ToolsCalibrationData()
	{		
			
	}
	
	
	
	public String updateTool(ToolsCalibrationMaster toolsCalibrationMaster)
	{
		//setting up variables
		exceuted = "OK";
		
		try 
		{
			updateTools(toolsCalibrationMaster);
		}
		catch (Exception e) 
        {
			ToolsCalibrationController.addError(e.toString());
			
            this.em.getTransaction().rollback();
            exceuted = e.toString();
            logger.severe(exceuted);
		}
		finally
		{
			//clean up 
			this.em.clear();

		}
		return exceuted;
	}
	
	//update a tool
	private void updateTools(ToolsCalibrationMaster toolsCalibrationMaster) throws Exception 
	{	
		//setting up variables
		PnInventoryDetail pnInventoryDetail = null;
		boolean existPN = false;
		boolean existPnInventoryDetail = false;
		DateFormat format = new SimpleDateFormat("yyyyMMdd");

		
		
		
		//check if object has min values
		if(toolsCalibrationMaster.getPartNumber() != null  && !toolsCalibrationMaster.getPartNumber().isEmpty() && toolsCalibrationMaster.getSerialNumber() != null  && !toolsCalibrationMaster.getSerialNumber().isEmpty()) 
		{
			String partNumber_Tool ;
			partNumber_Tool = toolsCalibrationMaster.getPartNumber().replaceAll("\"", "IN");
			partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
			if(!partNumber_Tool.contains(":"))
			{
				partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
			}
			toolsCalibrationMaster.setPartNumber(partNumber_Tool);
			logger.info("Tool: " + toolsCalibrationMaster.getPartNumber());
			
			try 
			{
				pnInventoryDetail = this.em.createQuery("SELECT p FROM PnInventoryDetail p WHERE p.id.pn = :par AND p.id.sn = :ser", PnInventoryDetail.class)
						.setParameter("par", toolsCalibrationMaster.getPartNumber())
						.setParameter("ser",toolsCalibrationMaster.getSerialNumber())
						.getSingleResult();
				existPN = true;
				existPnInventoryDetail = true;
			}
			catch(Exception e)
			{
				pnInventoryDetail = new PnInventoryDetail();
				pnInventoryDetail.setCreatedDate(new Date());
				pnInventoryDetail.setCreatedBy("TRAX_IFACE");
				
				//EMRO fields to create basic object
				pnInventoryDetail.setQtyAvailable(new BigDecimal(1));
				pnInventoryDetail.setQtyReserved(new BigDecimal(0));
				pnInventoryDetail.setQtyInTransfer(new BigDecimal(0));
				pnInventoryDetail.setQtyPendingRi(new BigDecimal(0));
				pnInventoryDetail.setQtyUs(new BigDecimal(0));
				pnInventoryDetail.setUnitCost(new BigDecimal(0));
				pnInventoryDetail.setCurrencyExchangeRate(new BigDecimal(1));
				pnInventoryDetail.setQtyInRental(new BigDecimal(0));
				pnInventoryDetail.setSecondaryCost(new BigDecimal(0));
				pnInventoryDetail.setSecondaryCurrencyExchange(new BigDecimal(1));
				
				pnInventoryDetail.setKitNo(new BigDecimal(0));
				pnInventoryDetail.setBlobNo(new BigDecimal(0));
				pnInventoryDetail.setDocumentNo(new BigDecimal(0));
				pnInventoryDetail.setFilingSequence(new BigDecimal(0));
				pnInventoryDetail.setNoOfTagPrint(new BigDecimal(0));
				pnInventoryDetail.setSosHour(new BigDecimal(0));
				pnInventoryDetail.setSlot(new BigDecimal(0));
				pnInventoryDetail.setNlaPosition(" ");
				
				pnInventoryDetail.setInventoryType("MAINTENANCE");	
				try
				{
					String company = (String) this.em.createQuery("select p.profile from ProfileMaster p")
							.getSingleResult();
					pnInventoryDetail.setGlCompany(company);
				}
				catch(Exception e1) {
					exceuted = "Can not update Tool: "+ toolsCalibrationMaster.getPartNumber() +" as ERROR: Company could not be found";
					logger.severe(exceuted);
					ToolsCalibrationController.addError(exceuted);
					return;
				}
				
				
				pnInventoryDetail.setCondition("NEW");
				//pnInventoryDetail = new PnInventoryDetail();
				//pnInventoryDetail.setCreatedDate(new Date());
				//pnInventoryDetail.setCreatedBy("TRAX_IFACE");
				
				//EMRO fields to create basic object
				
				
			}
			
			pnInventoryDetail.setModifiedBy("TRAX_IFACE");
			pnInventoryDetail.setModifiedDate(new Date());
			
			pnInventoryDetail.setPn(toolsCalibrationMaster.getPartNumber());
			pnInventoryDetail.setSn(toolsCalibrationMaster.getSerialNumber());
			
			if(toolsCalibrationMaster.getInventoryNumber() != null && !toolsCalibrationMaster.getInventoryNumber().isEmpty()) 
			{
				pnInventoryDetail.setVendorLot(toolsCalibrationMaster.getInventoryNumber());
			}
			else if(existPN)
			{	
				
			}
			else 
			{
				exceuted = "Can not update Tool: "+ toolsCalibrationMaster.getPartNumber() + " ERROR: Inventory Number ";
				logger.severe(exceuted);
				ToolsCalibrationController.addError(exceuted);
				return;
			}
			
			if(toolsCalibrationMaster.getNotificationDate() != null && !toolsCalibrationMaster.getNotificationDate().isEmpty()) 
			{
				
				pnInventoryDetail.setToolLifeExpirationLast(format.parse(toolsCalibrationMaster.getNotificationDate()));
				
			}
			else if(existPN)
			{	
				
			}
			else 
			{
				exceuted = "Can not update Tool: "+ toolsCalibrationMaster.getPartNumber() + " ERROR: Notification Date ";
				logger.severe(exceuted);
				ToolsCalibrationController.addError(exceuted);
				return;
			}
			
			if(toolsCalibrationMaster.getBasicStartDate() != null && !toolsCalibrationMaster.getNotificationDate().isEmpty()) 
			{
				pnInventoryDetail.setToolLifeExpiration(format.parse(toolsCalibrationMaster.getBasicStartDate()));
			}
			else if(existPN)
			{	
				
			}
			else 
			{
				exceuted = "Can not update Tool: "+ toolsCalibrationMaster.getPartNumber() + " ERROR: Basic Start Date ";
				logger.severe(exceuted);
				ToolsCalibrationController.addError(exceuted);
				return;
			}
			
			if(toolsCalibrationMaster.getNotificationNumber() != null && !toolsCalibrationMaster.getNotificationNumber().isEmpty()) 
			{
				pnInventoryDetail.setToolCalibrationNo(toolsCalibrationMaster.getNotificationNumber());
			}
			else if(existPN)
			{	
				
			}
			else 
			{
				exceuted = "Can not update Tool: "+ toolsCalibrationMaster.getPartNumber() + " ERROR: Notification Number ";
				logger.severe(exceuted);
				ToolsCalibrationController.addError(exceuted);
				return;
			}
			
			if(!existPnInventoryDetail) {
					long batch =	getTransactionNo("BATCH").longValue();
					pnInventoryDetail.setBatch(batch);
					pnInventoryDetail.setGoodsRcvdBatch(new BigDecimal(batch));
			}
			
			
			logger.info("UPDATING Tool: " + toolsCalibrationMaster.getPartNumber() + " TRAX BATCH:" + pnInventoryDetail.getBatch());
			insertData(pnInventoryDetail);

		}else 
		{
			exceuted = "Can not update Tool: "+ toolsCalibrationMaster.getPartNumber() +" as ERROR: PN or SN is Null or tool does not exist in PN master";
			logger.severe(exceuted);
			ToolsCalibrationController.addError(exceuted);
		}
		
	}
	

	// get category from SystemTranCode used to check if category exist
	private String getPN(String PN) {
		try
		{
			PnMaster pnMaster = this.em.createQuery("Select p From PnMaster p where p.id.pn = :partn", PnMaster.class)
			.setParameter("partn", PN)
			.getSingleResult();
			
			return pnMaster.getPn();
		}
		catch (Exception e)
		{
			
		}
		return null;
	}
	
	
	
	
	//****************** Helper functions ******************
	
	
	private BigDecimal getTransactionNo(String code)
	{		
		try
		{
			BigDecimal acctBal = (BigDecimal) this.em.createNativeQuery("SELECT pkg_application_function.config_number ( ? ) "
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

			em.merge(data);
			em.flush();
		}catch (Exception e)
		{
			exceuted = "insertData has encountered an Exception: "+e.toString();
			ToolsCalibrationController.addError(exceuted);
			logger.severe(exceuted);
		}
	}
	
	public boolean lockAvailable(String notificationType)
	{
		
		lock = this.em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
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
		this.em.getTransaction().begin();
		lock = this.em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
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
		this.em.lock(lock, LockModeType.NONE);
		this.em.merge(lock);
		this.em.getTransaction().commit();
	}
	
	public void unlockTable(String notificationType)
	{
		this.em.getTransaction().begin();
		lock = this.em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.getSingleResult();
		lock.setLocked(new BigDecimal(0));
		lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
		this.em.lock(lock, LockModeType.NONE);
		this.em.merge(lock);
		this.em.getTransaction().commit();
	}

}
