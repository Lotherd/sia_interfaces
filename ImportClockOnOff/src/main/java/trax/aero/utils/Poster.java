package trax.aero.utils;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



import trax.aero.logger.LogManager;
import trax.aero.pojo.Item;
import trax.aero.pojo.Task;

public class Poster 
{

	Logger logger = LogManager.getLogger("ImportClockOnOff_I29");
	private String body = null;
	
	
	
	
	public boolean postTask(Task data , String URL)
	{
		

		{

			Client client = null;
			Response response = null;

			try {
				
				
				

				String url = URL;

				if (url == null) {
					
					return false;
				}

				if (url.startsWith("https")) {
					client = getRestSSLClient(MediaType.APPLICATION_JSON , null);
				} else
					client = getRestHttpClient(MediaType.APPLICATION_JSON , null);

				

				WebTarget webTarget = client.target(url);

				
				
				Builder builder = webTarget.request();
				
				builder = builder.header("Content-type", MediaType.APPLICATION_JSON);
				builder = builder.header("Accept", MediaType.APPLICATION_JSON );
				
				
				
				String requests = "";
				
				
				
				logger.info("POSTING Requests:" + data.getTaskCard() + " to URL: " + url);
				body = null;
				
		
				response = builder.post(Entity.entity(data, MediaType.APPLICATION_JSON));
				body = response.readEntity(String.class);
				
				logger.info("Response: " + response.getStatus() + " Response Boby: " + body);
				
				
				
				
				if (response.getStatus() == 200 || response.getStatus() == 202) {
					
					return true;
					};
					body = null;
					return false;
				}
			 catch (Exception exc) {
				 logger.severe(exc.toString());
				
			} finally {
				if (response != null)
					response.close();
				
				
				if (client != null)
					client.close();
							
			}
		return false;
	}
	}
	
	public boolean postItem(Item data , String URL)
	{
		

		{

			Client client = null;
			Response response = null;

			try {
				
				
				

				String url = URL;

				if (url == null) {
					
					return false;
				}

				if (url.startsWith("https")) {
					client = getRestSSLClient(MediaType.APPLICATION_JSON , null);
				} else
					client = getRestHttpClient(MediaType.APPLICATION_JSON , null);

				

				WebTarget webTarget = client.target(url);

				
				
				Builder builder = webTarget.request();
				
				builder = builder.header("Content-type", MediaType.APPLICATION_JSON);
				builder = builder.header("Accept", MediaType.APPLICATION_JSON );
				
				
				
				String requests = "";
				
				
				
				logger.info("POSTING Requests:" + data.getTaskCard() + " to URL: " + url);
				body = null;
				
		
				response = builder.post(Entity.entity(data, MediaType.APPLICATION_JSON));
				body = response.readEntity(String.class);
				logger.info("Response: " + response.getStatus() + " Response Boby: " + body);
				
				
				
				
				if (response.getStatus() == 200 || response.getStatus() == 202) {
					
					return true;
					};
					body = null;
					return false;
				}
			 catch (Exception exc) {
				 logger.severe(exc.toString());
				
			} finally {
				if (response != null)
					response.close();
				
				
				if (client != null)
					client.close();
							
			}
		return false;
	}
	}
		
		private Client getRestSSLClient(String accept, String contentType) {

			Client client = null;
			try {

				ClientBuilder clientBuilder = ClientBuilder.newBuilder();
				clientBuilder = clientBuilder.sslContext(getSSLContext());
				clientBuilder = clientBuilder.hostnameVerifier(new TraxHostNameVerifier());
				client = clientBuilder.build();

				if (contentType != null)
					client.property("Content-Type", contentType);

				if (accept != null)
					client.property("accept", accept);

			} catch (Exception exc) {
				exc.printStackTrace();
			}
			return client;
		}

		/**
		 * Gets the SSL context.
		 *
		 * @return the SSL context
		 */
		private SSLContext getSSLContext() {

			SSLContext context = null;
			try {
				context = SSLContext.getInstance("SSL");
			} catch (NoSuchAlgorithmException exc) {
				exc.printStackTrace();
			}
			try {
				TraxX509TrustManager trustMger = new TraxX509TrustManager();
				context.init(null, new TrustManager[] { trustMger }, new SecureRandom());
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			return context;
		}
		
		private Client getRestHttpClient(String accept, String contentType) {

			Client client = null;
			try {
				client = ClientBuilder.newClient();
				if (contentType != null)
					client.property("Content-Type", contentType);

				if (accept != null)
					client.property("accept", accept);

			} catch (Exception exc) {
				exc.printStackTrace();
			}
			return client;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}
	
	
	
	
	
	


	
	
	
}

















