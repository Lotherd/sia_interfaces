package trax.aero.application;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.google.gson.Gson;

import trax.aero.controller.ImportCertificationController;
import trax.aero.data.ImportCertificationData;
import trax.aero.interfaces.IImportCertificationData;
import trax.aero.logger.LogManager;
import trax.aero.pojo.STAFF;
import trax.aero.pojo.STAFFMasterResponse;
import trax.aero.pojo.STAFFRequest;





@Path("/ImportCertificationService")
public class Service {
	
	Logger logger = LogManager.getLogger("ImportCertification_I30_1");
	
	@EJB IImportCertificationData data;
	
	@POST
	@Path("/importCert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importCert(STAFF input)
	{
		String exceuted = "OK";
		
		STAFFMasterResponse responseStaff = null;
		try 
        {    
			//if(data.lockAvailable("I30_1"))
			//{
				String staff = "";
				for(STAFFRequest s: input.getSTAFF()) {
					staff = staff +" Staff Number:"+ s.getStaffNumber() + ", ";
				}
				
				logger.info("Input: " + staff);
				
				//data.lockTable("I30_1");
				exceuted = data.importCert(input);
	        	responseStaff = data.getResponseStaff();				
				
				//data.unlockTable("I30_1");
			//}
			
			if(!exceuted.equalsIgnoreCase("OK")) {
        		exceuted = "Issue found";
        		throw new Exception("Issue found");		
        	}
			        	
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			ImportCertificationController.addError(e.toString());		
			ImportCertificationController.sendEmail(input);
		}
       finally 
       {   
    	   logger.info("finishing");
       }
		
		Gson gson = new Gson();
		String json = gson.toJson(responseStaff);
		logger.info("Ouput: " + json);
		
		GenericEntity<STAFFMasterResponse> entity = new GenericEntity<STAFFMasterResponse>(responseStaff) {
		};
		
	   return Response.ok(entity,MediaType.APPLICATION_JSON).build();
	}
	
	@GET
    @Path("/healthCheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() 
    {    
		logger.info("Healthy");
    	return Response.ok("Healthy",MediaType.APPLICATION_JSON).build();
    }
	
	
	@POST
	@Path("/AwsTest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response AwsTest(STAFF input)
	{
		logger.info("AWS TEST");
		final String key = System.getProperty("AWS_KMS_Key_ARN");
		// Instantiate the SDK
		final AwsCrypto crypto = AwsCrypto.standard();

        // Set up the master key provider
        final KmsMasterKeyProvider prov = KmsMasterKeyProvider.builder().buildStrict(key);

       
        final CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(prov, input.getSTAFF().get(0).getSign());
       
 
        logger.info("Decrypted");

        input.getSTAFF().get(0).setStamp(decryptResult.getResult());
        
        input.getSTAFF().get(0).setSign(input.getSTAFF().get(0).getSign());
        
        GenericEntity<STAFF> entity = new GenericEntity<STAFF>(input) {
		};
        
		
		
		//logger.info("Healthy");
    	return Response.ok(entity,MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("/Aws")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response Aws(STAFF input)
	{
		logger.info("AWS TEST");
		final String key = System.getProperty("AWS_KMS_Key_ARN");
		// Instantiate the SDK
		final AwsCrypto crypto = AwsCrypto.standard();

        // Set up the master key provider
        final KmsMasterKeyProvider prov = KmsMasterKeyProvider.builder().buildStrict(key);
		
        final Map<String, String> context = Collections.singletonMap("ExampleContextKey", "ExampleContextValue");

        // Encrypt the data
        //        
        final CryptoResult<byte[], KmsMasterKey> encryptResult = crypto.encryptData(prov, input.getSTAFF().get(0).getSign(), context);
        final byte[] ciphertext = encryptResult.getResult();
        
        
        final CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(prov, ciphertext);
        
        
        // The data is correct, so return it. 
        //System.out.println("Decrypted: " + new String(decryptResult.getResult(), StandardCharsets.UTF_8));
        
        logger.info("Decrypted");

        input.getSTAFF().get(0).setStamp(decryptResult.getResult());
        
        input.getSTAFF().get(0).setSign(ciphertext);
        
        GenericEntity<STAFF> entity = new GenericEntity<STAFF>(input) {
		};
        
		
		
		//logger.info("Healthy");
    	return Response.ok(entity,MediaType.APPLICATION_JSON).build();
	}
	
}