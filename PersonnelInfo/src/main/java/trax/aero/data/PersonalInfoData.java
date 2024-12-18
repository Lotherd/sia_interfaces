package trax.aero.data;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import trax.aero.controller.PersonalInfoController;
import trax.aero.logger.LogManager;
import trax.aero.model.EmployeeSkill;
import trax.aero.model.EmployeeSkillPK;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.JobCostCenterMaster;
import trax.aero.model.JobCostCenterMasterPK;
import trax.aero.model.RelationMaster;
import trax.aero.model.RelationMasterPK;
import trax.aero.model.SecurityHeader;
import trax.aero.model.SiteLocationMaster;
import trax.aero.model.SystemTranCode;
import trax.aero.model.SystemTranCodePK;
import trax.aero.pojo.EmployeeInfo;



public class PersonalInfoData {
	
	String ex;
	EmployeeInfo in;
	long lastBatch = 99999;
	EntityManagerFactory factory;
	@PersistenceContext(unitName = "ImportDS")	public EntityManager em;
	String exceuted = "OK";
	//public InterfaceLockMaster lock;
	
	static Logger logger = LogManager.getLogger("PersonalInfo_I28");
	
	public PersonalInfoData(EntityManagerFactory factory)
	{
		this.factory = factory;
		em = factory.createEntityManager();
	}
	
	


	public String insertEmployee(EmployeeInfo e) throws SQLException, ParseException {
		
		exceuted = "OK";
		RelationMaster employee = null;
		
		
		if(e != null  && checkMinValue(e)) 
		{
			try 
			{
				employee = em.createQuery("SELECT r FROM RelationMaster r where r.id.relationCode = :em and r.id.relationTransaction = :tr", RelationMaster.class)
						.setParameter("em", e.getStaffID())
						.setParameter("tr", "EMPLOYEE")
						.getSingleResult();
			}
			catch(Exception ex)
			{
				employee = new RelationMaster();
				RelationMasterPK employeepk = new RelationMasterPK();
				employee.setId(employeepk);
				employee.setCreatedBy("TRAX_IFACE");
				employee.setCreatedDate(new Date());
				
				//EMRO fields to create basic object
				
				employee.getId().setRelationTransaction("EMPLOYEE");
				employee.setDrugTesting("NONE");
				employee.setTrainingInstructor("NO");
				employee.setAllowIssueTo("NO");
				employee.setDrugTestingMaxDays(new BigDecimal(0));
				employee.setSection(null);
				
				
			}
			
			employee.setModifiedBy("TRAX_IFACE");
			employee.setModifiedDate(new Date());
			
			employee.getId().setRelationCode(e.getStaffID());
			employee.setLdapUsername(e.getEmployeeWin2KID().toUpperCase());
			employee.setName(e.getFullName().toUpperCase());
			
			if(e.getFirstName().length() > 45) {
				String firstNameString = e.getFirstName().substring(0, 45);
				exceuted = "Warning Employee: "+ e.getStaffID() +  " Size: "+e.getFirstName().length() +"  WARNING: total string is longer than 45, First Name: "+ e.getFirstName()+ " is truncated to " + firstNameString ;
				logger.warning(exceuted);
				PersonalInfoController.addError(exceuted);
				e.setJobTitle(firstNameString);
				
			}
			
			employee.setFirstName(encryptValue(e.getFirstName().toUpperCase()));
			employee.setLastName(encryptValue(e.getLastName().toUpperCase()));
			
			if(getLocation(e.getCostCenter()) != null) {
				employee.setRelatedLocation(getLocation(e.getCostCenter()));
			}else {
				employee.setRelatedLocation(e.getLocation().toUpperCase());
			}
			setCode("DEPARTMENT", e.getDepartmentCode().toUpperCase(),e.getDepartment());
			employee.setDepartment(e.getDepartmentCode().toUpperCase());
			
			setCode("DEPDIVISION", e.getDivisionCode(),e.getDivision());
			employee.setDivision(e.getDivisionCode());
			
			if(e.getJobTitle().length() > 45) {
				String jobTitleString = e.getJobTitle().substring(0, 45);
				exceuted = "Warning Employee: "+ e.getStaffID() +  " Size: "+e.getJobTitle().length() +"  WARNING: total string is longer than 45, Job Title: "+ e.getJobTitle()+ " is truncated to " + jobTitleString ;
				logger.warning(exceuted);
				PersonalInfoController.addError(exceuted);
				e.setJobTitle(jobTitleString);
				
			}
			setCode("EMPLPOS", e.getJobTitle(),e.getJobTitle());
			employee.setPosition(e.getJobTitle());
			
			if(e.getPhone() != null && !e.getPhone().isEmpty()) {
			//	employee.setMailPhone(encryptValue(e.getPhone()));
			}
			if(e.getMobileNumber() != null && !e.getMobileNumber().isEmpty()) {
				//employee.setMailCell(encryptValue(e.getMobileNumber()));
			}
			employee.setMailEmail(encryptValue(e.getEmail().toUpperCase()));
			
			if(getSkills(e.getJobRole(), e.getCostCenter()).toString().contains("AH") || getSkills(e.getJobRole(), e.getCostCenter()).toString().contains("LAE") ) {
				employee.setMechanicStamp("INSPECTOR");
			}
			
			employee.setDateHired(new SimpleDateFormat("dd/MM/yyyy").parse(e.getDateHired()));
			if(e.getDateTerminated() != null && !e.getDateTerminated().isEmpty()) {
				employee.setDateTerminated(new SimpleDateFormat("dd/MM/yyyy").parse(e.getDateTerminated()));
				if(employee.getDateTerminated().before(new Date())) {
					employee.setMechanicStamp(null);
					employee.setMechanicStamp2(null);
					employee.setMechanicStamp3(null);
					employee.setMechanicStamp4(null);
					employee.setMechanicStamp5(null);
					
				}
			}
			
			if(e.getCompany().equalsIgnoreCase("SIAE")) {
				e.setCompany("SIAEC");
			}
			employee.setGstGlCompany(e.getCompany());
			
			setCode("COSTCENTER", e.getCostCenter(),e.getCostCenter() );
			employee.setCostCenter(e.getCostCenter());
			
			employee.setRelationCategory(e.getEmployeeSubGroupDescription());
			
			employee.setStatus(e.getEmployeeStatus().toUpperCase());
			if(employee.getStatus().equalsIgnoreCase("INACTIVE")) {
				deactivateUser(employee);
				
			}
			
			logger.info("INSERTING EMPLOYEE: " + employee.getId().getRelationCode());
			insertData(employee);
			
			insertSkills(employee,getSkills(e.getJobRole(), e.getCostCenter()));
			

		}else 
		{
			exceuted = "Can not insert/update Employee: "+ e.getStaffID() +" as ERROR: input is null or does not have minimum values";
			logger.severe(exceuted);
			PersonalInfoController.addError(exceuted);
		}	


		return exceuted;
		
	}
		
	private String setCode(String cat, String code,String description) {
		SystemTranCode systemTranCode = null;
		try
		{
			systemTranCode = em.createQuery("SELECT s FROM SystemTranCode s WHERE s.id.systemTransaction = :tra AND  s.id.systemCode = :des", SystemTranCode.class)
			.setParameter("tra", cat)
			.setParameter("des", code)
			.getSingleResult();
			
			
			return systemTranCode.getId().getSystemCode();
		}
		catch (Exception e)
		{
			systemTranCode = new SystemTranCode();
			SystemTranCodePK pk = new SystemTranCodePK();
			systemTranCode.setId(pk);
			systemTranCode.setModifiedBy("TRAX_IFACE");
			systemTranCode.setModifiedDate(new Date());
			systemTranCode.setCreatedBy("TRAX_IFACE");
			systemTranCode.setCreatedDate(new Date());
			
			systemTranCode.getId().setSystemCode(code);
			systemTranCode.getId().setSystemTransaction(cat);
			systemTranCode.getId().setSystemTranCodeSub("          ");
			
			systemTranCode.setSystemCodeDescription(description);
			
			systemTranCode.setPnTransaction("C");
			systemTranCode.setPnCostingMethod("A");
			systemTranCode.setConfigFlag("N");
			systemTranCode.setTagType("FORMONE");
			systemTranCode.setTemporaryRevision("NO");
			systemTranCode.setChapterMandatory("NO");
			systemTranCode.setSectionMandatory("NO");
			systemTranCode.setDeferCatAutoMddrClosing("YES");
			systemTranCode.setDefectReportPilot("YES");
			systemTranCode.setVendorStatus("ACTIVE");
			systemTranCode.setStatus("ACTIVE");
			systemTranCode.setPnCategoryInventoryType("MAINTENANCE");
			systemTranCode.setDoNotAllowReset("NO");
			systemTranCode.setAutoResetOnRo("NO");
			systemTranCode.setDoNotAllowResetProd("NO");
			systemTranCode.setAcRestriction("P");
			systemTranCode.setRadiocative("N");
			systemTranCode.setCodeColor("8421376");
			
			systemTranCode.setConfigNumber(new BigDecimal(0));
			systemTranCode.setCurrencyExchange(new BigDecimal(0));
			
			systemTranCode.setMelCycles(new BigDecimal(0));
			systemTranCode.setMelHours(new BigDecimal(0));
			systemTranCode.setRevenueFlight("NO");
			systemTranCode.setEtops("NO");
			
			
			
			systemTranCode.setAlertNoOfRemoval(new BigDecimal(0));
			systemTranCode.setAlertCategoryNoOf(new BigDecimal(0));
			
			systemTranCode.setAcMandatory("NO");
			systemTranCode.setEquipmentRefDesignator("N");
			systemTranCode.setRosClassificationCode("O");
			systemTranCode.setIfrs("NO");
			systemTranCode.setSupplier("NO");
			systemTranCode.setSupplierMaintenance("NO");
			systemTranCode.setSupplierGeneral("NO");
			systemTranCode.setSupplierGse("NO");
			systemTranCode.setRepair("NO");
			systemTranCode.setRepairMaintenance("NO");
			systemTranCode.setRepairGeneral("NO");
			systemTranCode.setRepairGse("NO");
			systemTranCode.setFreightForwarder("N");
			systemTranCode.setService("NO");
			systemTranCode.setAdCategory("N");
			systemTranCode.setEcNameOverride("YES");
			systemTranCode.setVbLifeLimit("NO");
			systemTranCode.setVopBaseOnCondition(new BigDecimal(0));
			systemTranCode.setCabin("N");
			systemTranCode.setHighDollar("N");
			systemTranCode.setBinCountFrequency("YEAR");
			systemTranCode.setBinCountNoWeekend("NO");
			systemTranCode.setFollowGrb("NO");
			systemTranCode.setLoadAtRecv("N");
			systemTranCode.setAllowBust("NO");
			systemTranCode.setSwoRequisition("N");
			
			systemTranCode.setOpsRestriction("NO");
			systemTranCode.setPaperRequired("YES");
			systemTranCode.setImage("bmp-ghs-01-bmp");
			systemTranCode.setScrapReorder("YES");
			systemTranCode.setEssentialityCodeLevel("NOGO");
			systemTranCode.setMonthDepreciation(new BigDecimal(0));
			systemTranCode.setThirdPartyWo("N");
			
			logger.info("INSERTING CODE: " + code);
			insertData(systemTranCode);
		}
		return systemTranCode.getId().getSystemCode();
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
				exceuted = "insertData has encountered an Exception: "+e.getMessage();
				PersonalInfoController.addError(exceuted);
				logger.severe(e.toString());
			}
		}
	
		private boolean checkMinValue( EmployeeInfo e) {
			
			if( e.getEmployeeWin2KID() == null || e.getEmployeeWin2KID().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR EmployeeWin2KID");
				return false;
			}
				
			if( e.getStaffID() == null || e.getStaffID().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR StaffID");
				return false;
			}
				
			if( e.getFullName() == null || e.getFullName().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR FullName");
				return false;
			}
			if( e.getFirstName() == null || e.getFirstName().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR FirstName");
				return false;
			}
			if( e.getLastName() == null || e.getLastName().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR LastName");
				return false;
			}
			if( e.getLocation() == null || e.getLocation().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR Location");
				return false;
			}
			if( e.getDepartmentCode() == null || e.getDepartmentCode().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR DepartmentCode");
				return false;
			}
			if( e.getDepartment() == null || e.getDepartment().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR Department");
				return false;
			}
			if( e.getDivisionCode() == null || e.getDivisionCode().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR DivisionCode");
				return false;
			}	
			if( e.getDivision() == null || e.getDivision().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR Division");
				return false;
			}
			if( e.getJobTitle() == null || e.getJobTitle().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR JobTitle");
				return false;
			}
			if( e.getEmail() == null || e.getEmail().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR Email");
				return false;
			}
			/*
			if( e.getAge() == null || e.getAge().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR Age");
				return false;
			}
			*/
			if( e.getDateHired() == null || e.getDateHired().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR DateHired");
				return false;
			}
			if( e.getCompany() == null || e.getCompany().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR Company");
				return false;
			}
			if( e.getCompanyCodeDescription() == null || e.getCompanyCodeDescription().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR CompanyCodeDescription");
				return false;
			}
			if( e.getCostCenter() == null || e.getCostCenter().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR CostCenter");
				return false;
			}
			if( e.getJobRole() == null || e.getJobRole().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR JobRole");
				return false;
			}
			if( e.getJobRoleDescription() == null || e.getJobRoleDescription().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR JobRoleDescription");
				return false;
			}
			if( e.getEmployeeSubGroupDescription() == null || e.getEmployeeSubGroupDescription().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR EmployeeSubGroupDescription");
				return false;
			}
			if( e.getEmployeeStatus() == null || e.getEmployeeStatus().isEmpty()) {
				PersonalInfoController.addError("Can not insert/update Employee: "+ e.getStaffID() +" as ERROR Status");
				return false;
			}
			
			return true;
		}
		
		
		private String encryptValue(String value) throws SQLException{
			logger.info("Encrypting Value.");
			
			
			String value_enc = null;
			BigDecimal count = new BigDecimal(0);
			final String query = "SELECT COUNT(*) FROM SYSTEM_TRAN_CONFIG WHERE SYSTEM_TRANSACTION = ? AND SYSTEM_CODE = ?";
			try {
					
				try {
					count =  (BigDecimal) em.createNativeQuery(query)
		        			.setParameter(1, "CONFIGURATION")
		        			.setParameter(2, "CRYPTPASS")
		        			.getSingleResult();	              		            
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }			
				
				if (count.longValue() > 0){
					value_enc = trax.aero.Encryption.Encryption.cryptoControl(value, true);
				}else{
					value_enc = value;
				}
			} catch (InvalidKeyException e) {
				return value;
			} catch (NoSuchAlgorithmException e) {
				return value;
			} catch (NoSuchProviderException e) {
				return value;
			} catch (NoSuchPaddingException e) {
				return value;
			} catch (ShortBufferException e) {
				return value;
			} catch (IllegalBlockSizeException e) {
				return value;
			} catch (BadPaddingException e) {
				return value;
			} finally {
				
			}
			return value_enc;
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
				PersonalInfoController.addError(exceuted);
				logger.severe(e.toString());
			}
		}

		public String setSkill(String costCenter, String jobRole, String skill) {
			JobCostCenterMaster jobCostCenterMaster = null;
			
			try
			{
				jobCostCenterMaster = em.createQuery("SELECT j FROM JobCostCenterMaster j WHERE j.id.jobRole = :jr AND j.id.costCentre = :cc AND j.id.skill = :sk ", JobCostCenterMaster.class)
				.setParameter("cc", costCenter)
				.setParameter("jr", jobRole)
				.setParameter("sk", skill)
				.getSingleResult();
				
				
				
			}
			catch (Exception e)
			{
				jobCostCenterMaster = new JobCostCenterMaster();
				JobCostCenterMasterPK pk = new JobCostCenterMasterPK();
				jobCostCenterMaster.setId(pk);
			}
			
			jobCostCenterMaster.getId().setCostCentre(costCenter);
			jobCostCenterMaster.getId().setJobRole(jobRole);
			jobCostCenterMaster.getId().setSkill(skill);
			
			
			logger.info("INSERTING Skill: " + jobCostCenterMaster.getId().getSkill());
			insertData(jobCostCenterMaster);
			return jobCostCenterMaster.getId().getSkill();
			
		}

		public void deleteSkill(String costCenter, String jobRole, String skill) {
			 			
			 JobCostCenterMaster jobCostCenterMaster = em.createQuery("SELECT j FROM JobCostCenterMaster j WHERE j.id.jobRole = :jr AND j.id.costCentre = :cc AND j.id.skill = :sk ", JobCostCenterMaster.class)
					.setParameter("cc", costCenter)
					.setParameter("jr", jobRole)
					.setParameter("sk", skill)
					.getSingleResult();
				
			 logger.info("DELETE Skill: " + jobCostCenterMaster.getId().getSkill());
			deleteData(jobCostCenterMaster);

		}

		public String getSkill(String jobRole, String costCenter) {
			
			String output = "";
			List<JobCostCenterMaster> jobCostCenterMasters = null;
			if(jobRole != null && costCenter != null && !jobRole.isEmpty() && !costCenter.isEmpty()) {
				
				
				jobCostCenterMasters = em.createQuery("SELECT j FROM JobCostCenterMaster j WHERE j.id.jobRole = :jr AND j.id.costCentre = :cc ")
						.setParameter("cc", costCenter)
						.setParameter("jr", jobRole)
						.getResultList();
				
			}else {
				jobCostCenterMasters = em.createQuery("SELECT j FROM JobCostCenterMaster j")
						.getResultList();
			}
			
			for(JobCostCenterMaster jccm : jobCostCenterMasters) {
				
				output = output +  " Job Role: " + jccm.getId().getJobRole() 
						+ " Cost Center: " + jccm.getId().getCostCentre()
						+ " Skill: " + jccm.getId().getSkill() + ",\n";
				
			}
			
			
			return output;
		}
		
		
		private ArrayList<String> getSkills(String jobRole, String costCenter) {
			
			ArrayList<String> output = new ArrayList<String>();
			List<JobCostCenterMaster> jobCostCenterMasters = null;
			if(jobRole != null && costCenter != null && !jobRole.isEmpty() && !costCenter.isEmpty()) {
				
				try {
				jobCostCenterMasters = em.createQuery("SELECT j FROM JobCostCenterMaster j WHERE j.id.jobRole = :jr AND j.id.costCentre = :cc ")
						.setParameter("cc", costCenter)
						.setParameter("jr", jobRole)
						.getResultList();
				
				if(jobCostCenterMasters != null && jobCostCenterMasters.size() > 0) {
					for(JobCostCenterMaster jccm : jobCostCenterMasters) {
						
						output.add(jccm.getId().getSkill());
						
					}
				}else {
					output.add("NONOPS");
				}
				
				}catch(Exception e){
					
					output.add("NONOPS");
				}
				
			}

			return output;
		}
		
		
		private void insertSkills(RelationMaster employee, ArrayList<String> skills) {
			
			
			
			EmployeeSkill employeeSkill;
			
			deleteOldSkills(  skills,employee);
			
			for(String skill : skills) {
				
				try {
				employeeSkill = em.createQuery("SELECT e FROM EmployeeSkill e WHERE e.id.skill = :sk AND e.id.employee =: em ", EmployeeSkill.class)
						.setParameter("sk", skill)
						.setParameter("em", employee.getId().getRelationCode())
						.getSingleResult();
				}
				catch(Exception e){
					employeeSkill = new EmployeeSkill();
					EmployeeSkillPK pk = new EmployeeSkillPK();
					employeeSkill.setId(pk);
					
					employeeSkill.setCreatedDate(new Date());
					employeeSkill.setCreatedBy("TRAX_IFACE");
					
					employeeSkill.setExpirationOptional("NO");
					employeeSkill.setStatus("ACTIVE");
					
					employeeSkill.getId().setAcSeries("          ");
					employeeSkill.getId().setAcType("          ");
					
					employeeSkill.setTotalHours(new BigDecimal(0));
					
					employeeSkill.setCompletedFlag("NO");
					
				}
				
				employeeSkill.setModifiedBy("TRAX_IFACE");
				employeeSkill.setModifiedDate(new Date());
				
				employeeSkill.getId().setEmployee(employee.getId().getRelationCode());
				employeeSkill.getId().setSkill(skill);
				
				
				logger.info("INSERTING Skill: " + skill);
				insertData(employeeSkill);
			}
			
			return;
		}
		
		
		private void deleteOldSkills(ArrayList<String> skills, RelationMaster employee) {
				
				try {
					List<EmployeeSkill> employeeSkill = em.createQuery("SELECT e FROM EmployeeSkill e WHERE e.id.employee =: em ")
						.setParameter("em", employee.getId().getRelationCode())
						.getResultList();
					for(EmployeeSkill skillOld : employeeSkill) {
						boolean match = true;
						for(String s : skills) {
							if(s.equalsIgnoreCase(skillOld.getId().getSkill())) {
								match = false;
							}	
						}
						if(match) {
							logger.info("DELETING OLD Skill: " + skillOld.getId().getSkill() + " Employee: " + skillOld.getId().getEmployee());
							deleteData(skillOld);
						}
					}
				}catch(Exception e) {
					logger.info("No Older Skills found " + e.getMessage());
				}
			
		}




		private void deactivateUser(RelationMaster employee) {
			SecurityHeader securityHeader = null;
				try {
					securityHeader = em.createQuery("SELECT s FROM SecurityHeader s WHERE s.id.employee =: em ", SecurityHeader.class)
						.setParameter("em", employee.getId().getRelationCode())
						.getSingleResult();
				securityHeader.setStatus("INACTIVE");
				logger.info(" DEACTIVATING USER: " + securityHeader.getUser());
				insertData(securityHeader);
				}
				catch(Exception e){
					logger.info("No USER found TO DEACTIVATE");
					
				}
		}
		
		private <T> void insertData( T data, String s) 
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
					insertData(lock,"");
					return true;
				}
				return false;
			}
			else
			{
				lock.setLocked(new BigDecimal(1));
				insertData(lock,"");
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
		
		
		public String setLocation(String costCenter, String location) {
			SiteLocationMaster siteLocationMaster = null;
			
				siteLocationMaster = em.find(SiteLocationMaster.class, costCenter);
				if(siteLocationMaster == null) {
					siteLocationMaster = new SiteLocationMaster();
					siteLocationMaster.setCostCentre(costCenter);
					siteLocationMaster.setLocation(location);
					
				}
				logger.info("INSERTING cost Center: " + siteLocationMaster.getCostCentre());
				insertData(siteLocationMaster);
			return siteLocationMaster.getLocation();
			
		}

		public void deleteLocation(String costCenter, String location) {
			 			
			SiteLocationMaster siteLocationMaster = em.find(SiteLocationMaster.class, costCenter);
				
			logger.info("DELETE cost Center: " + siteLocationMaster.getCostCentre());
			deleteData(siteLocationMaster);

		}

		public String getLocation(String costCenter) {

			SiteLocationMaster siteLocationMaster = null;
			siteLocationMaster = em.find(SiteLocationMaster.class, costCenter);
			if(siteLocationMaster == null) {
				logger.info("No Location found Cost Center:" + costCenter);
				return null;				
			}
			logger.info("Found Location: " + siteLocationMaster.getLocation());
			return siteLocationMaster.getLocation();
		}
		
}
