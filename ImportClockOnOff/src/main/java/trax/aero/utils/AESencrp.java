package trax.aero.utils;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.time.Instant;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class AESencrp {
     private static final String ALGO = "AES";
     private static String sessionToken; 
        private static final byte[] keyValue =            new byte[] {'T', 'r', 'a', 'X', '0', '1', 'M','o', 'B', 'i', 'l','e', '5', '1', '0', '2'}; // };                          // 'i','/','M','f','i','3','u','3','j','H','F','R','0','O','S','a','p','C','I','d','t','A','=','='         
        
        public static String getJwtToken(String user, String service) throws IllegalArgumentException, GeneralSecurityException, UnsupportedEncodingException, IOException
    	{
    		Instant now = Instant.now();
    		String issuer = service;
    		String subject = user;
    		String privKey = getKey("private.pem");
    		String pubKey = getKey("public.pem");
    		Algorithm algorithm = null;
    			algorithm = Algorithm.RSA512 ((RSAPublicKey) getPublicKeyFromX509Pem(pubKey,"RSA"), (RSAPrivateKey) getPrivateKeyFromPkcs8Pem(privKey,"RSA"));
    		

    		String token = JWT.create ()
    		                        .withIssuer (issuer)
    		                        .withSubject (subject)
//    		                        .withIssuedAt (Date.from (now))
//    		                        .withNotBefore (Date.from (now))
    		                        .withExpiresAt (Date.from (now.plus(8, ChronoUnit.HOURS)))
    		                        .sign (algorithm);

    		return token;
    	}
        
        private static String getCurrentPath() {
			
			String parentPath = "";
			parentPath = System.getenv("TRAX_RESOURCES_PATH");
	    	
	    	if (parentPath == null || parentPath.isEmpty())
	    	{
	        	if(System.getProperty("jboss.server.config.dir")!= null)
	        		parentPath = System.getProperty("jboss.server.config.dir");
				else
					parentPath = System.getProperty("user.dir") ;
	    	}

	    	
	    	if (parentPath != null && !parentPath.isEmpty())
				return parentPath;

			return null;


		}
        
        private static PublicKey getPublicKeyFromX509Pem (final String pem, final String algorithm)
    		    throws GeneralSecurityException
    		{
    		
    		    String stripped_pem = pem.replaceAll ("-----[A-Za-z]+( [A-Za-z]*)? PUBLIC KEY-----", "");
    		    byte[] der = java.util.Base64.getMimeDecoder ().decode (stripped_pem);
    		    X509EncodedKeySpec spec = new X509EncodedKeySpec (der);
    		    KeyFactory factory = KeyFactory.getInstance (algorithm);
    		    return factory.generatePublic (spec);
    		}

    		private static PrivateKey getPrivateKeyFromPkcs8Pem (final String pem,
    		                                              final String algorithm)
    		    throws GeneralSecurityException
    		{
    		    String stripped_pem = pem.replaceAll ("-----[A-Za-z]+( [A-Za-z]*)? PRIVATE KEY-----", "");
    		    byte[] der = java.util.Base64.getMimeDecoder ().decode (stripped_pem);
    		   
    		    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec (der);
    		    KeyFactory factory = KeyFactory.getInstance (algorithm);

    		    return factory.generatePrivate (spec);
    		}
        
        private static String getKey(String name) throws UnsupportedEncodingException, IOException
		{
			String path = getCurrentPath();
//			String path = "C:\\wildfly-24.0.1.Final\\standaloneServiceLayerTrunk\\configuration";
			String content = new String(Files.readAllBytes(Paths.get(path + File.separator + name)), "utf-8");
			String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
			
			
			
			return privateKey;
		}
        
        public static String encrypt(String Data) throws Exception {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key );
            byte[] encVal = c.doFinal(Data.getBytes());
            String encryptedValue = Base64.encodeBase64String(encVal);
            return encryptedValue;
        }
        public static String decrypt(String encryptedData) throws Exception {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decodeBase64(encryptedData);
            byte[] decValue = c.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            return decryptedValue;
        }
        private static Key generateKey() throws Exception {
            Key key = new SecretKeySpec(keyValue, ALGO);
            return key;
        }
        public static String generatePhrase()
        {
            // 'a' if r < 25 || 'A' if r >= 25 && r < 50 || '0' if r >= 50 && r < 75 || '!' if r >= 75 && r < 100            
        	String result = "";
            int started[] = {0,0,0,0};
            SecureRandom r = new SecureRandom();            
            while(result.length() < 20)
            {
                int index = r.nextInt(100);
                char c ;
                if (index < 25 && started[0] < 5)
                {
                    started[0]++;
                    c = (char) (r.nextInt(26) + 'a');
                    result += c;
                }
                else if(index >= 25 && index < 50 && started[1] < 5)
                {
                    started[1]++;
                    c = (char) (r.nextInt(26) + 'A');
                    result += c;
                }
                else if(index >= 50 && index < 75 && started[2] < 5)
                {
                    started[2]++;
                    c = (char) (r.nextInt(10) + '0');
                    result += c;
                }
                else if (started[3] < 5)
                {
                    started[3]++;
                    c = (char) (r.nextInt(15) + '!');
                    result += c;
                }
            }
            return result;      
        }
        public static String generateToken()
        {
            // 'a' if r < 25 || 'A' if r >= 25 && r < 50 || '0' if r >= 50 && r < 75 || '!' if r >= 75 && r < 100            
        	String result = "";
            int started[] = {0,0,0,0};
            SecureRandom r = new SecureRandom();            
            for (int i = 0; i < 50; i++)
            {
                int index = r.nextInt(100);
                char c ;
                if (index < 25 && started[0] < 10)
                {
                    started[0]++;
                    c = (char) (r.nextInt(26) + 'a');
                    result += c;
                }
                else if(index >= 25 && index < 50 && started[1] < 10)
                {
                    started[1]++;
                    c = (char) (r.nextInt(26) + 'A');
                    result += c;
                }
                else if(index >= 50 && index < 75 && started[2] < 10)
                {
                    started[2]++;
                    c = (char) (r.nextInt(10) + '0');
                    result += c;
                }
                else                {
                    started[3]++;
                    c = (char) (r.nextInt(10) + '0');
                    result += c;
                }
            }
            return result;      
        }
        public static String getSessionToken() {
            return sessionToken;
        }
        public static void setSessionToken(String sessionToken) {
            AESencrp.sessionToken = sessionToken;
        }
        public static void main(String [] args)
           {
                 String pass = "4WKK8jERok+t+iRMPB4YDA==" ;
                  System.out.println("encrypting passwoord: " + pass );
                 // AESencrp e = new  AESencrp() ;                  
                  try {
                                String encrypted  = encrypt("password9");//encrypt(pass) ;                                 
                                System.out.println("Decrypted: "  +  encrypted);
                                String decrypted  = decrypt("Er7ld6Pcj7akIognD/pMi+2EXiLn2wF50xOSvMqlOhNPpbtvKzR/a/DdagimNqx6M3cSDzcqioVXU6TcHf1J9g==") ;
                                System.out.println(" Encrypted: "  +  decrypted);
                         } catch (Exception e1) {
                                // TODO Auto-generated catch block                                
                        	 System.out.println(e1.getMessage());
                         } 
                 // UkOnsMruwUDj1vnrGY3LpQ==           
                  }
}