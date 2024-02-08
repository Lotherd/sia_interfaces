package trax.aero.data;
import java.math.BigDecimal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import trax.aero.controller.ACController;
import trax.aero.interfaces.IACData;
import trax.aero.logger.LogManager;
import trax.aero.model.AcMaster;
import trax.aero.model.AcTypeSeriesMaster;
import trax.aero.model.AcTypeSeriesMasterPK;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.LocationMaster;
import trax.aero.model.RelationMaster;
import trax.aero.pojo.ACMaster;

@Stateless(name="ACData" , mappedName="ACData")
public class ACData implements IACData {

	@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
	
		String exceuted;
		
		static Logger logger = LogManager.getLogger("ACFunction_I72");
		public InterfaceLockMaster lock;
		
		public ACData()
		{
		
		}
		
		public String insertACMaster(ACMaster aCMaster)
		{
			//setting up variables
			exceuted = "OK";
			
			try 
			{
				insertAC(aCMaster);
			}
			catch (Exception e) 
	        {
				ACController.addError(e.toString());
				logger.severe(e.toString());
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
		
		//insert a AC
		private void insertAC(ACMaster ACMaster) 
		{	
			//setting up variables
			AcMaster AcMaster = null;
			AcTypeSeriesMaster AcTypeSeriesMaster = null;
			boolean existAC = false,existTypeSeries = false;
			ArrayList<String> arrayAC = new ArrayList<String>();
			String  ACType, ACSeries,deletionFlag,inactiveIndicator;
			
			if(ACMaster.getAircraftType() != null && !ACMaster.getAircraftType().isEmpty() && ACMaster.getAircraftType().contains("-")) {
				 arrayAC = parseString(ACMaster.getAircraftType(),"-");
			}
			
			
			//check if object has min values
			if(ACMaster.getAircraftTailNumber() != null  && !ACMaster.getAircraftTailNumber().isEmpty() && ACMaster.getMaintenancePlant() != null  && !ACMaster.getMaintenancePlant().isEmpty()) 
			{
				logger.info("Converting and appending information for AC: " + ACMaster.getAircraftTailNumber());
				ACType = getStringFromDash(arrayAC,0,0);
				ACSeries = getStringFromDash(arrayAC,0,1);
				deletionFlag = checkFlag(ACMaster.getDeletionIndicator());
				inactiveIndicator = checkFlag(ACMaster.getInactiveIndicator());
				
				try 
				{
					AcMaster = em.createQuery("Select a From AcMaster a where a.id.ac = :airC", AcMaster.class)
							.setParameter("airC", ACMaster.getAircraftTailNumber())
							.getSingleResult();
					existAC = true;
				}
				catch(Exception e)
				{
					AcMaster = new AcMaster();
					AcMaster.setCreatedDate(new Date());
					AcMaster.setCreatedBy("TRAX_IFACE");
					
					//EMRO fields to create basic object
					AcMaster.setFlightStatus("Y");
					AcMaster.setAcFlightStatus("Y");
					AcMaster.setFlight("NO");
					AcMaster.setAcTearDown("NO");
					AcMaster.setEtopsDowngrade("NO");
					AcMaster.setSampling("NO");
					AcMaster.setLhtAirborne("N");
					AcMaster.setConcessionLimitMaxHours(new BigDecimal(0));
					AcMaster.setAcRin(new BigDecimal(0));
					AcMaster.setAcOwnershipControl("MANAGED");
					
					AcMaster.setServiceDate(new Date());
					AcMaster.setGlCompany("SIAEC");
					//AcMaster.setGlCompany("TRAX");
				}
				
				if( ACType != null && !ACType.isEmpty() && ACSeries != null && !ACSeries.isEmpty() && ACMaster.getAircraftType() != null && !ACMaster.getAircraftType().isEmpty() & arrayAC.size() == 2) {
					try 
					{
						AcTypeSeriesMaster = em.createQuery("Select a From AcTypeSeriesMaster a where a.id.acType = :airType and a.id.acSeries = :airSeries", AcTypeSeriesMaster.class)
								.setParameter("airType", ACType)
								.setParameter("airSeries", ACSeries)
								.getSingleResult();
						existTypeSeries = true;
					}
					catch(Exception e)
					{
						AcTypeSeriesMaster = new AcTypeSeriesMaster();
						AcTypeSeriesMasterPK AcTypeSeriesMasterPK = new AcTypeSeriesMasterPK();
						AcTypeSeriesMaster.setId(AcTypeSeriesMasterPK);
						AcTypeSeriesMaster.setCreatedDate(new Date());
						AcTypeSeriesMaster.setCreatedBy("TRAX_IFACE");
						
						//EMRO fields to create basic object
						AcTypeSeriesMaster.setConcessionLimit(new BigDecimal(0));
						AcTypeSeriesMaster.setEquipmentRefDesignator("N");
						AcTypeSeriesMaster.setFlightUse("N");
						AcTypeSeriesMaster.setConfiguration4NoSeats(new BigDecimal(0));
						AcTypeSeriesMaster.setConfiguration3NoSeats(new BigDecimal(0));
						AcTypeSeriesMaster.setConfiguration2NoSeats(new BigDecimal(0));	
					}
					AcTypeSeriesMaster.setModifiedBy("TRAX_IFACE");
					AcTypeSeriesMaster.setModifiedDate(new Date());
					AcTypeSeriesMaster.getId().setAcType(ACType);
					AcTypeSeriesMaster.getId().setAcSeries(ACSeries);
					
					logger.info("INSERTING Type Series: " + ACType + " " + ACSeries);
					try {
						insertDataType(AcTypeSeriesMaster);
					}catch(Exception e){
						exceuted = "insertData has encountered an Exception: "+e.toString();
						//ACController.addError(exceuted);
						logger.severe(e.toString());
					}
					AcMaster.setAcTypeSeriesMaster(AcTypeSeriesMaster);
				}	
				else if(existAC && ( ACMaster.getAircraftType() == null || ACMaster.getAircraftType().isEmpty() )){
						
				}
				else
				{
					exceuted = "Can not insert AC: "+ ACMaster.getAircraftTailNumber() + " ERROR: AC Type and Series";
					logger.severe(exceuted);
					ACController.addError(exceuted);
					return;
				}

				AcMaster.setModifiedBy("TRAX_IFACE");
				AcMaster.setModifiedDate(new Date());
				AcMaster.setAc(ACMaster.getAircraftTailNumber());
				AcMaster.setAcSn(ACMaster.getAircraftType());
				
				
				
				AcMaster.setHub(ACMaster.getMaintenancePlant());
				
				if(ACMaster.getBill_to_party() != null && !ACMaster.getBill_to_party().isEmpty()) 
				{
					ACMaster.setBill_to_party(ACMaster.getBill_to_party().replaceFirst("^0+(?!$)", ""));
				}
				
				if(deletionFlag.equalsIgnoreCase("INACTIVE") || inactiveIndicator.equalsIgnoreCase("INACTIVE")) {
					AcMaster.setStatus("INACTIVE");
				}else {
					AcMaster.setStatus("ACTIVE");
				}
				
				if(ACMaster.getBill_to_party() != null && !ACMaster.getBill_to_party().isEmpty() && ACMaster.getBill_to_party().length() <= 10) 
				{
					AcMaster.setCustomer(ACMaster.getBill_to_party());
				}
				else if(existAC)
				{	
					
				}
				else 
				{
					exceuted = "Can not insert/update AC: "+ ACMaster.getAircraftTailNumber() + " ERROR: Bill_to_party ";
					logger.severe(exceuted);
					ACController.addError(exceuted);
					return;
				}
					
				logger.info("INSERTING AC: " + ACMaster.getAircraftTailNumber());
				insertData(AcMaster);

			}else 
			{
				exceuted = "Can not insert/update AC: "+ ACMaster.getAircraftTailNumber() +" as ERROR: AC or MaintenancePlant";
				logger.severe(exceuted);
				ACController.addError(exceuted);
			}
			
		}
		

		// get category from SystemTranCode used to check if category exist
		private String getLocation(String location) {
			try
			{	
				LocationMaster locationMaster = em.createQuery("Select l FROM LocationMaster l where l.id.location = :loc", LocationMaster.class)
				.setParameter("loc", location)
				.getSingleResult();
				return locationMaster.getLocation();
			}
			catch (Exception e)
			{
				
			}
			return null;
		}
		
		// get category from SystemTranCode used to check if category exist
		private String getCustomer(String customer) {
				try
				{	
					RelationMaster relationMaster = em.createQuery("Select r FROM RelationMaster r where r.id.relationCode = :cu and r.id.relationTransaction = :reltr", RelationMaster.class)
					.setParameter("cu", customer)
					.setParameter("reltr", "CUSTOMER")
					.getSingleResult();
								
					return relationMaster.getId().getRelationCode();
				}
				catch (Exception e)
				{	
				}
				return null;
				}
		
		
		//****************** Helper functions ******************
		//modify delete indicator
		private String checkFlag(String deleteIndicator) {
			
			
			if(deleteIndicator == null)  
			{
				deleteIndicator = "ACTIVE";
			}
			else if(deleteIndicator.isEmpty())  
			{
				deleteIndicator = "ACTIVE";
			}
			else if(deleteIndicator.equalsIgnoreCase("FALSE"))
			{
				deleteIndicator = "ACTIVE";
			}
			else if(deleteIndicator.equalsIgnoreCase("TRUE")) 
			{	
				deleteIndicator = "INACTIVE";
			}
			else if(deleteIndicator.equalsIgnoreCase("X")) 
			{	
				deleteIndicator = "INACTIVE";
			}
			else
			{
				deleteIndicator = "ERROR";
			}
			return deleteIndicator;
		}
		
		//return a array list from a string formated with dashes
		private ArrayList<String> parseString(String string, String parse) 
		{
			ArrayList<String> arrayofString;
			arrayofString = new ArrayList<String>(Arrays.asList(string.split(parse)));
			return  arrayofString;
		}
		
		//return a string from the parsed string that formated with a dash
		private String getStringFromDash(ArrayList<String> Parsed, int min, int index ) {
			String string = null;
			if(Parsed.size() > min) 
			{
				string	= Parsed.get(index);
			}
			return  string;
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
				ACController.addError(exceuted);
				logger.severe(e.toString());
			}
		}
	

		//insert generic data from model objects
		private <T> void insertDataType( T data) 
			{
					
			em.merge(data);
			em.flush();
					
					
					
				}
		
		
		public boolean lockAvailable(String notificationType)
		{
			
			em.getTransaction().begin();
			lock = em.find(InterfaceLockMaster.class, notificationType, LockModeType.PESSIMISTIC_READ);
			
			if(lock.getLocked() == 1)
			{				
				LocalDateTime today = LocalDateTime.now();
				LocalDateTime locked = LocalDateTime.ofInstant(lock.getLockedDate().toInstant(), ZoneId.systemDefault());
				Duration diff = Duration.between(locked, today);
				if(diff.getSeconds() >= lock.getMax())
				{
					this.em.lock(lock, LockModeType.NONE);
					return true;
				}
				this.em.lock(lock, LockModeType.NONE);
				em.getTransaction().commit();
				return false;
			}
			else
			{
				this.em.lock(lock, LockModeType.NONE);
				em.getTransaction().commit();
				return true;
			}
			
		}
		
		
		public void lockTable(String notificationType)
		{
			em.getTransaction().begin();
			lock = em.find(InterfaceLockMaster.class, notificationType, LockModeType.PESSIMISTIC_WRITE);
			lock.setLocked(1);
			lock.setLockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			InetAddress address = null;
			try {
				address = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				
				logger.info(e.getMessage());
				//e.printStackTrace();
			}
			lock.setServer(address.getHostName());
			em.lock(lock, LockModeType.NONE);
			em.merge(lock);
			em.getTransaction().commit();
		}
		
		public void unlockTable(String notificationType)
		{
			em.getTransaction().begin();
			lock = em.find(InterfaceLockMaster.class, notificationType, LockModeType.PESSIMISTIC_WRITE);
			lock.setLocked(0);
			lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			em.lock(lock, LockModeType.NONE);
			em.merge(lock);
			em.getTransaction().commit();
		}
	
}
