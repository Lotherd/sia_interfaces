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
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;

import trax.aero.controller.ImportCertificationController;
import trax.aero.interfaces.IImportCertificationData;
import trax.aero.logger.LogManager;
import trax.aero.model.BlobTable;
import trax.aero.model.BlobTablePK;
import trax.aero.model.EmployeeSkill;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.RelationMaster;
import trax.aero.pojo.STAFF;
import trax.aero.pojo.STAFFMasterResponse;
import trax.aero.pojo.STAFFRequest;
import trax.aero.pojo.STAFFResponse;


/*
 SELECT DISTINCT w.ops_line FROM WO w, WO_TASK_CARD wtc 
WHERE wtc.WO = '606567' AND wtc.task_card = 'DHC-6-3-34-060-750'  AND wtc.wo = w.wo and ROWNUM <= 1;   
 */


@Stateless(name="ImportCertificationData" , mappedName="ImportCertificationData")
public class ImportCertificationData implements IImportCertificationData {
	
	
	@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
	
	ArrayList<String> Inactive = new ArrayList<>(Arrays.asList("Withdrawn","Suspension","Secondment","Resignation","Terminated","End of Contract Term","Retirement")); 
	
	String exceuted;
	STAFFMasterResponse responseStaff = new STAFFMasterResponse();
	final String key = System.getProperty("AWS_KMS_Key_ARN");
    
	final String endpoint = System.getProperty("AWS_KMS_Endpoint");
	final String CTX = System.getProperty("AWS_KMS_ENC_CTX");
	final String region = System.getProperty("AWS_REGION");

	//final String accessKey = System.getProperty("AWS_accessKey");
	//final String secretKey = System.getProperty("AWS_secretKey");
	private Logger logger = LogManager.getLogger("ImportCertification_I30_1");
	Integer code = null;
	//public InterfaceLockMaster lock;
    

	
	
	
	
	
	public String importCert(STAFF input)
	{
		//setting up variables
		exceuted = "OK";
		String result = "Failed";
		code = null;
		responseStaff.setSTAFF(new ArrayList<STAFFResponse>());
		
		try 
		{
			
			for(STAFFRequest i : input.getSTAFF()){
				result = "Failed";
				result = importCerts(i);
				//logger.info("HELLO");
				STAFFResponse response = new STAFFResponse(); 
				response.setStaffNumber(i.getStaffNumber());
				response.setStatus(result);
				if(code != null ) {
					response.setErrorCode(code.toString());
					response.setErrorDescription("API Internal Error");
				}else {
					response.setErrorCode("");
					response.setErrorDescription("");
				}
				
				responseStaff.getSTAFF().add(response);
				
			}
		}
		catch (Exception e) 
        {
			e.printStackTrace();
			exceuted = e.toString();
			ImportCertificationController.addError(exceuted);
            logger.severe(exceuted);
            em.getTransaction().rollback();
           
            
            
		}
		finally
		{
			//clean up 
			em.clear();

		}
		return exceuted;
	}
	
	//insert a cert
	private String importCerts(STAFFRequest input) 
	{	
		//setting up variables
		RelationMaster employee = null;
			
		//check if object has min values
		if(input != null  && checkMinValue(input)) 
		{
			try 
			{
				employee = getRelationMaster(input);
			}
			catch(Exception e)
			{
				
				exceuted = "Can not Import Cert: "+ input.getStaffNumber() +" as ERROR: employee can not be found";
				logger.severe(exceuted);
				ImportCertificationController.addError(exceuted);
				code = 500;
				return "Failed";
				
			}
			setEmployeeSkillLicense(input);	
			employee.setModifiedBy("TRAX_IFACE");
			employee.setModifiedDate(new Date());
			if(input.getStaffStatus().equalsIgnoreCase("INACTIVE") && employee.getBlobNo() != null) {
				
				try {
					BlobTable blobStamp = getAttachmentLink(employee, "STAMP.png", input);
					BlobTable blobEsign = getAttachmentLink(employee, "ESIGN.png", input);
					logger.info("DELETE blob: " + blobStamp.getId().getBlobNo() + " Line: " + blobStamp.getId().getBlobLine());
					logger.info("DELETE blob: " + blobEsign.getId().getBlobNo() + " Line: " + blobEsign.getId().getBlobLine());
					deleteData(blobStamp);
					deleteData(blobEsign);
				}catch(Exception e) {
					exceuted = "Can not Import Cert: "+ input.getStaffNumber() +" as ERROR: Stamp and Signature not updated/inserted for inactive staff";
					logger.severe(exceuted);
					ImportCertificationController.addError(exceuted);
					code = 500;
					return "Failed";
				}
				
			}else {
				
				if(key != null && !key.isEmpty()) {
					try {
						employee = setAttachmentLink(employee, decryptData(key,input.getStamp()),"STAMP.png",input);
						employee = setAttachmentLink(employee, decryptData(key,input.getSign()),"ESIGN.png",input);
					}catch(Exception e){
							exceuted = "decrypt data has encountered an Exception: "+e.toString();
							ImportCertificationController.addError(exceuted);
							logger.severe(e.toString());
							code = 500;
							return "Failed";
							
					}
				}else {
					employee = setAttachmentLink(employee, input.getStamp(),"STAMP.png",input);
					employee = setAttachmentLink(employee, input.getSign(),"ESIGN.png",input);
				}
			}
			
			
			
			
			logger.info("INSERTING Employee: " + input.getStaffNumber());
			insertData(employee);
				
		}else 
		{
			exceuted = "Can not Import Cert: "+ input.getStaffNumber() +" as ERROR: STAFF is null or does not have minimum values";
			logger.severe(exceuted);
			ImportCertificationController.addError(exceuted);
			code = 500;
			return "Failed";
		}
		return "OK";
		
	}
	

	private RelationMaster getRelationMaster(STAFFRequest input) {
		
		RelationMaster relationMaster = em.createQuery("SELECT r FROM RelationMaster r WHERE r.id.relationCode = :sta AND r.id.relationTransaction = :tra", RelationMaster.class)
				.setParameter("sta", input.getStaffNumber())
				.setParameter("tra", "EMPLOYEE")
				.getSingleResult();
		
		return relationMaster;
	}
	
	
	private void setEmployeeSkillLicense(STAFFRequest e) {
		List<EmployeeSkill> employeeSkills = null;
			try {
				employeeSkills = em.createQuery("SELECT e FROM EmployeeSkill e WHERE e.id.employee =: em ")
						.setParameter("em", e.getStaffNumber())
						.getResultList();
				}catch(Exception e1){
					logger.info("Employee: " +e.getStaffNumber() + " Does not contain any skills" );
					return;
				}
				if(employeeSkills == null || employeeSkills.isEmpty()) {
					return;
				}
				
				for(EmployeeSkill employeeSkill : employeeSkills) {
					if(employeeSkill.getId().getSkill().contains("AH") || 
							employeeSkill.getId().getSkill().contains("LAE")) 
					{
						
						employeeSkill.setModifiedBy("TRAX_IFACE");
						employeeSkill.setModifiedDate(new Date());
						if(Inactive.contains(e.getStaffStatus()) || 
								e.getStaffStatus().equalsIgnoreCase("INACTIVE")) {
							employeeSkill.setStatus("INACTIVE");
						}else {
							employeeSkill.setStatus("ACTIVE");
						}		
											
						logger.info("UPDATING SKILL Skill: " + employeeSkill.getId().getSkill() + " Employee: " + employeeSkill.getId().getEmployee() );
						insertData(employeeSkill);
					}
				}
		
	}
	
	
	
	

	
	
	//****************** Helper functions ******************
	
	//delete generic data from model objects
	private <T> void deleteData( T data) 
	{
		try 
		{	
			em.remove(data);
			em.flush();
		}catch (Exception e)
		{
			exceuted = "insertData has encountered an Exception: "+e.toString();
			ImportCertificationController.addError(exceuted);
			logger.severe(e.toString());
			}
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
			ImportCertificationController.addError(exceuted);
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
	
		private boolean checkMinValue(STAFFRequest input) {
		
			if( input.getStaffNumber() == null || input.getStaffNumber().isEmpty()) {
				ImportCertificationController.addError("Can not import Cert: "+ input.getStaffNumber() +" as ERROR Staff Number");
				return false;
			}
			if( input.getStaffStatus() == null || input.getStaffStatus().isEmpty()) {
				ImportCertificationController.addError("Can not import Cert: "+ input.getStaffNumber() +" as ERROR Staff Status");
				return false;
			}
			if( input.getStaffAuthNumber() == null || input.getStaffAuthNumber().isEmpty()) {
				ImportCertificationController.addError("Can not import Cert: "+ input.getStaffNumber() +" as ERROR Staff Authorization Number");
				return false;
			}
			if( input.getStaffAuthExpiry() == null || input.getStaffAuthExpiry().isEmpty()) {
				ImportCertificationController.addError("Can not import Cert: "+ input.getStaffNumber() +" as ERROR Staff Authorization Expiry Date");
				return false;
			}
			if( input.getStamp() == null && !input.getStaffStatus().equalsIgnoreCase("INACTIVE")  ) {
				ImportCertificationController.addError("Can not import Cert: "+ input.getStaffNumber() +" as ERROR Stamp");
				return false;
			}
			if( input.getSign() == null && !input.getStaffStatus().equalsIgnoreCase("INACTIVE") ) {
				ImportCertificationController.addError("Can not import Cert: "+ input.getStaffNumber() +" as ERROR eSign");
				return false;
			}
			
			return true;
		}
	
	
	

		private BlobTable getAttachmentLink(RelationMaster employee, String cert, STAFFRequest staff ) {
			BlobTable blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
					.setParameter("bl", employee.getBlobNo().longValue())
					.setParameter("des",cert )
					.getSingleResult();
			
			return blob;
		}
	
	

	
			
			
			
	
	private RelationMaster setAttachmentLink(RelationMaster employee, byte[] input,String cert, STAFFRequest staff ) {
		boolean existBlob = false;
		BlobTable blob = null;
	    	if(input == null) {
	    		logger.info("SKIPPING");
	    		return employee;
	    	}
			try 
			{
				blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
						.setParameter("bl", employee.getBlobNo().longValue())
						.setParameter("des",cert )
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
				
				blob.getId().setBlobLine(getLine(employee.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
			}
			
			if(cert.equalsIgnoreCase("STAMP.png")){
				blob.setDocType("MECHSTAMP");
			}else {
				blob.setDocType("INSPSTAMP");
			}
			
			blob.setModifiedBy("TRAX_IFACE");
			blob.setModifiedDate(new Date());
			blob.setBlobItem(input);
			blob.setBlobDescription(cert);
			blob.setCustomDescription(cert);
			
			
			
			if(!existBlob && employee.getBlobNo() == null) {
				try {
					blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
					employee.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
				} catch (Exception e1) {
					
				}
			}else if(employee.getBlobNo() != null){
				blob.getId().setBlobNo(employee.getBlobNo().longValue());
			}
			
			logger.info("INSERTING Employee: " + staff.getStaffNumber());
			insertData(employee);
			
			logger.info("INSERTING blob: " + blob.getId().getBlobNo() + " Line: " + blob.getId().getBlobLine());
			insertData(blob);
			
			return employee;
	}
	
	
	 private byte[] decryptData(String keyId,byte[] ciphertext) {
	        try {
	        	logger.info("--- Decryption the data ---");
	        	final AwsCrypto crypto = AwsCrypto.standard();
	        	
	        	final KmsMasterKeyProvider prov = KmsMasterKeyProvider.builder().buildStrict(key);
    			
	            final CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(prov, ciphertext);
	            
    			return decryptResult.getResult();			      	
	        } catch (Exception e) {
	        	logger.severe("Cypter : " + ciphertext);
	           logger.severe(e.toString());
	           e.printStackTrace();
	           throw e;
	        }
	}

	 public STAFFMasterResponse getResponseStaff() {
		return responseStaff;
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

	
	
	
}
