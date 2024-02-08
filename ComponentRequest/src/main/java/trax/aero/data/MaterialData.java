package trax.aero.data;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import trax.aero.client.ServiceClient;
import trax.aero.inbound.Component;
import trax.aero.inbound.DTTRAXI414066;
import trax.aero.inbound.Order;
import trax.aero.interfaces.IMaterialData;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.PicklistDistribution;
import trax.aero.model.PicklistHeader;
import trax.aero.model.Wo;
import trax.aero.model.WoTaskCard;
import trax.aero.outbound.DTTRAXI41ACK4067;
import trax.aero.outbound.Orders;
import trax.aero.util.EmailSender;

@Stateless(name="MaterialData" , mappedName="MaterialData")
public class MaterialData implements IMaterialData {
	
	Logger logger = LogManager.getLogger("ComponentRequest_I41");
	
	@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
	
	EmailSender emailer = null;
	String error = "";
	
	
	
	
	
	
	ServiceClient client = null;
	public MaterialData()
	{
		emailer = new EmailSender(System.getProperty("CM_toEmail"));
		client = new ServiceClient();
				
	}
	
	@SuppressWarnings("unchecked")
	public String sendComponent() throws JAXBException
	{
		Calendar cal = null;
		//List<DTTRAXI414066> results = new ArrayList<>();
		 cal = Calendar.getInstance();
		
		//cal.add(Calendar.SECOND, (int) (-(Integer.parseInt(System.getProperty("CM_interval"))) *1.5));
		cal.add(Calendar.SECOND, -Integer.parseInt(System.getProperty("CM_interval")));
		
		List<PicklistDistribution> updates = null;
		
		
		List<PicklistHeader> headers = null;
		try
		{
					
			headers = em.createQuery("SELECT p FROM PicklistHeader p where p.createdDate >= :date")
					.setParameter("date", cal.getTime())
					.getResultList();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
		}
		
		ArrayList<DTTRAXI414066> requisitions = new ArrayList<DTTRAXI414066>();
		Order order = new Order();
		ArrayList<Component> component = new ArrayList<Component>();
		ArrayList<Order> orders = new ArrayList<Order>();
		
		
		if(headers != null && headers.size() != 0)
		{
			logger.info("new");
			for(PicklistHeader header :headers)
			{
			
				em.refresh(header);
				
				WoTaskCard card = null;
				
				if(header.getWo() == null && header.getTaskCard() == null) {
					continue;
				}
				DTTRAXI414066 requisition = new DTTRAXI414066();
				orders = new ArrayList<Order>();
				requisition.setOrder(orders);
				component = new ArrayList<Component>();
				Order ord = new Order();
				
				try
				{				
					card =  em.createQuery("select w from WoTaskCard w where w.id.wo = :wo and w.id.taskCard = :card", WoTaskCard.class)
							.setParameter("wo", header.getWo().longValue())
							.setParameter("card", header.getTaskCard())
							.getSingleResult();
					em.refresh(card);
					
					if(card.getReferenceTaskCard() != null) {
						ord.setOrderNumber(card.getReferenceTaskCard());
					}
				}
				catch(Exception e)
				{
					//e.printStackTrace();
					logger.severe(e.toString());
				}
				
				
				
				
				String rfo = null;
				
				
				
				if(header.getWo() != null) {
					Wo w = getWo(header.getWo());
					if(	w.getModule().equalsIgnoreCase("SHOP") && w.getRefurbishmentOrder() != null) {
						rfo = w.getRefurbishmentOrder();
					}
					if(rfo != null) {
						ord.setOrderNumber(rfo);
					}
				}
								
				
				
				
				if(ord.getOrderNumber() == null ) {
					continue;
				}
				logger.info("CREATED DATE " + header.getCreatedDate() + " >=  CAL DATE " + cal.getTime());
				
				for(PicklistDistribution detail : header.getPicklistDistributions())
				{
					em.refresh(detail);
					
					if(detail.getId().getTransaction().equalsIgnoreCase("REQUIRE")) {
						
					
						if(detail.getInterfaceSyncFlag() != null &&  detail.getInterfaceSyncFlag().equalsIgnoreCase("S")) {
							continue;
						}
						Component c = new Component();
						
						
						try {
							if(card != null) {
								if(card.getWoTaskCardItems() != null && !card.getWoTaskCardItems().isEmpty()) {
									c.setOperationNumber(card.getWoTaskCardItems().get(0).getExternalCustRef());
									c.setOperationNumber(card.getWoTaskCardItems().get(0).getOpsNo());
								}
							}
						}catch(Exception e) {
							logger.info("No item found");
						}
						
						
												
						String pn = detail.getPn();
						
						pn = pn.replaceAll("in", "\"");
						pn = pn.replaceAll("ft", "'");
						
						if(pn.contains(":UPLOAD"))
						{
							pn=  pn.substring(0, pn.indexOf(":"));
						}
						
						c.setMaterialNumber(pn);
						
												
						//component.setIndicator("Create");
						
						c.setQuantity(detail.getQty().toString());
						c.setRequistionNumber(String.valueOf(header.getPicklist()));
						c.setRequistionLine(String.valueOf(detail.getId().getPicklistLine()));
						String site = "",recepient = "" ;
						site = getSAPSite(detail.getPicklistHeader().getWo());
						recepient = getRecepient(site);
						c.setRecepient(recepient);
						c.setReservationNumber(detail.getExternalCustRes());
						c.setReservationItem(detail.getExternalCustResItem());
						if(c.getReservationNumber() != null && !c.getReservationNumber().isEmpty() &&
						   c.getReservationItem() != null && !c.getReservationItem().isEmpty()) {
							
							continue;
						}
						boolean match = false;
						
						for(DTTRAXI414066 r: requisitions) {
							for(Order o: r.getOrder()) {
								
								if(o.getOrderNumber().equalsIgnoreCase(ord.getOrderNumber())) {
									o.getComponent().add(c);
									
									match = true;
								}
								
							}
						}
						if(match) {
							
							continue;
						}
						component.add(c);
					}
				}
				
				if(component.isEmpty()) {
					
					
					continue;
				}
				
				ord.setComponent(component);
				requisition.getOrder().add(ord);
				requisitions.add(requisition);
			}
		}
		
		try
		{
			updates = this.em.createQuery("SELECT p FROM PicklistDistribution p where ( p.interfaceSyncDate IS NOT NULL or p.createdDate > :date ) and  ( p.interfaceSyncFlag != :flag or p.interfaceSyncFlag is null)")
					.setParameter("date", cal.getTime())
					.setParameter("flag", "S")
					.getResultList();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.severe(e.toString());
		}
		
		if(updates != null && updates.size() > 0)
		{
			
			logger.info("updates");
			for(PicklistDistribution detail : updates)
			{
				if(detail.getId().getTransaction().equalsIgnoreCase("REQUIRE")) {
					if(detail.getPicklistHeader().getWo() == null || detail.getPicklistHeader().getWo() == null) {
						continue;
					}
					
					em.refresh(detail);
					
					
					if(detail.getInterfaceSyncFlag() != null &&  detail.getInterfaceSyncFlag().equalsIgnoreCase("S")) {
						continue;
					}
					
					
					Component c = new Component();
					Order ord = new Order();
					component = new ArrayList<Component>();
					orders = new ArrayList<Order>();
					
					DTTRAXI414066 requisition = new DTTRAXI414066();
					requisition.setOrder(orders);
					
					
					WoTaskCard card = null;
					try
					{
						card = (WoTaskCard) this.em.createQuery("select w from WoTaskCard w where w.id.wo = :wo and w.id.taskCard = :card")
								.setParameter("wo", detail.getPicklistHeader().getWo().longValue())
								.setParameter("card",  detail.getPicklistHeader().getTaskCard())
								.getSingleResult();
						em.refresh(card);
						if(card.getReferenceTaskCard() != null) {
							ord.setOrderNumber(card.getReferenceTaskCard());
						}
					}
					catch(Exception e)
					{
						//e.printStackTrace();
						logger.warning("No Task card found for picklist: " + detail.getId().getPicklist()
								+ " " + e.getMessage());
					}
					if(detail.getPicklistHeader().getWo() != null) {
						String rfo = null;
						Wo w = getWo(detail.getPicklistHeader().getWo());
						if(	w.getModule().equalsIgnoreCase("SHOP") && w.getRefurbishmentOrder() != null) {
							rfo = w.getRefurbishmentOrder();
						}
						if(rfo != null) {
							ord.setOrderNumber(rfo);
						}
					}
					
					if(ord.getOrderNumber() != null) {
															
						
						
						
						try {
							if(card != null) {
								if(card.getWoTaskCardItems() != null && !card.getWoTaskCardItems().isEmpty()) {
									c.setOperationNumber(card.getWoTaskCardItems().get(0).getExternalCustRef());
									c.setOperationNumber(card.getWoTaskCardItems().get(0).getOpsNo());
								}
							}
						}catch(Exception e) {
							logger.info("No item found");
						}
						
					}else {
						continue;
					}
					logger.info("CREATED DATE " + detail.getCreatedDate() + " >  CAL DATE " + cal.getTime());
					
					String pn = detail.getPn();
					
					pn = pn.replaceAll("in", "\"");
					pn = pn.replaceAll("ft", "'");
					
					if(pn.contains(":UPLOAD"))
					{
						pn=  pn.substring(0, pn.indexOf(":"));
					}
					
					
					c.setMaterialNumber(pn);
					
					if(detail.getStatus() != null && !detail.getStatus().isEmpty() && 
							detail.getStatus().equalsIgnoreCase("CANCEL")) {
						c.setDeletionFlag("X");
					}
					
					c.setQuantity(detail.getQty().toString());
					c.setRequistionNumber(String.valueOf(detail.getPicklistHeader().getPicklist()));
					c.setRequistionLine(String.valueOf(detail.getId().getPicklistLine()));
					String site = "",recepient = "" ;
					site = getSAPSite(detail.getPicklistHeader().getWo());
					recepient = getRecepient(site);
					
					c.setRecepient(recepient);
					ord.setComponent(component);
					
					c.setReservationNumber(detail.getExternalCustRes());
					c.setReservationItem(detail.getExternalCustResItem());
					
					
					//requisition.setOrder(order);
					//results.add(requisition);
					boolean match = false;
					for(DTTRAXI414066 r: requisitions) {
						for(Order o: r.getOrder()) {
							for(Component com: o.getComponent()){
								
								if(c.getMaterialNumber().equalsIgnoreCase(com.getMaterialNumber()) &&
								c.getRequistionLine().equalsIgnoreCase(com.getRequistionLine())  &&	
								c.getRequistionNumber().equalsIgnoreCase(com.getRequistionNumber())
								)
								{
									
									match = true;
								}
							}
	
						}
											
						
					}
					if(match) {
						continue;
					}
					match = false;
					for(DTTRAXI414066 r: requisitions) {
						for(Order o: r.getOrder()) {
							if(o.getOrderNumber().equalsIgnoreCase(ord.getOrderNumber())) {
								o.getComponent().add(c);
								match = true;
							}
							
						}
					}
					if(match) {
						continue;
					}
						
					
					component.add(c);
					ord.setComponent(component);
					requisition.getOrder().add(ord);
					requisitions.add(requisition);
				}
			}
			
			
			
		}
		if(requisitions != null && requisitions.size() > 0) {
		
			for(DTTRAXI414066 requisition : requisitions) {
				JAXBContext jc = JAXBContext.newInstance(DTTRAXI414066.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				marshaller.marshal(requisition, sw);
				
				logger.info("Ouput: " + sw.toString());
				
				if(!client.callSap(requisition))
				{
					String os = "";
					for(Order r : requisition.getOrder()) {
						os = os + "( OrderNumber: "+ r.getOrderNumber()+ "),";
					}
					markSentFailed(requisition);
					emailer.sendEmail("Trax was unable to call SAP Orders:\n" +orders);
				}else {
					markSent(requisition);
				}
			}
		}
		
		
		
		return null;
		
	}
	
	
	private Wo getWo(BigDecimal wo) {
		
		try
		{	
			Wo work = em.createQuery("SELECT w FROM Wo w where w.wo = :param", Wo.class)
					.setParameter("param", wo.longValue()).getSingleResult();
			return work;
		}
		catch(NoResultException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private void markSent(DTTRAXI414066 data) {
		try {
			
			for(Order o : data.getOrder()) {
				for(Component c: o.getComponent()) {
					
					
					PicklistDistribution require = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
							.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
							.setParameter("line", Long.valueOf(c.getRequistionLine()))
							.setParameter("tra", "REQUIRE")
							.getSingleResult();
					require.setInterfaceSyncFlag("S");
					insertData(require);
					
					try {
						PicklistDistribution req = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
								.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
								.setParameter("line", Long.valueOf(c.getRequistionLine()))
								.setParameter("tra", "DISTRIBU")
								.getSingleResult();
						
						req.setInterfaceSyncFlag("S");
						insertData(req);
						
					}catch(Exception e) {
						logger.info(e.getMessage());
					}	
				}
			}	
			
		}catch(Exception e) {
			logger.info(e.getMessage());
		}		
	}
	
	
	private void markSentFailed(DTTRAXI414066 data) {
try {
			
			for(Order o : data.getOrder()) {
				for(Component c: o.getComponent()) {
					
					
					PicklistDistribution require = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
							.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
							.setParameter("line", Long.valueOf(c.getRequistionLine()))
							.setParameter("tra", "REQUIRE")
							.getSingleResult();
					require.setInterfaceSyncDate(new Date());
					require.setInterfaceSyncFlag("Y");
					insertData(require);
					
					try {
						PicklistDistribution req = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
								.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
								.setParameter("line", Long.valueOf(c.getRequistionLine()))
								.setParameter("tra", "DISTRIBU")
								.getSingleResult();
						
						require.setInterfaceSyncDate(new Date());
						require.setInterfaceSyncFlag("Y");
						insertData(req);
						
					}catch(Exception e) {
						logger.info(e.getMessage());
					}	
				}
			}	
			
		}catch(Exception e) {
			logger.info(e.getMessage());
		}				
	}
	
	
	private String getRecepient(String site) {
		
		String query = "",group  = "";
		
		query = " Select recipient FROM SITE_RECIPIENT_MASTER where site = ?";
				
		try
		{
			group = (String) em.createNativeQuery(query).setParameter(1, site).getSingleResult();	
			return group;
		}
		catch (Exception e) 
		{
			//e.printStackTrace();
			logger.severe("An Exception occurred executing the query to get the recipient. " + "\n error: " + e.toString());
		}
		return group;
	}

	private String getSAPSite(BigDecimal wo) {
		String site = " ";
		
		String query = " SELECT site FROM WO where wo = ?";	
		try
		{
			site = (String) em.createNativeQuery(query).setParameter(1, wo.toString()).getSingleResult();	
			
		}
		
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the site. " + "\n error: " +  e.toString());
		}
		
		return site;
	}
	
	
	
	
	public String setSite(String site, String recipient) throws Exception{
		String Exceuted = "OK";
		String query = "INSERT INTO SITE_RECIPIENT_MASTER (SITE, RECIPIENT) VALUES (?, ?)";
		
		try
		{
			
			em.createNativeQuery(query).setParameter(1, site)
			.setParameter(2, recipient).executeUpdate();	
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to set the site recipient. " + "\n error: " + e.toString() );
			throw new Exception("An Exception occurred executing the query to set the site recipient. " + "\n error: " + e.toString());
		}
		
		return Exceuted;
	}
	
	public String deleteSite( String site) throws Exception{
		String Exceuted = "OK";
		String query = "DELETE SITE_RECIPIENT_MASTER where site = ?";		
		try
		{	
			em.createNativeQuery(query).setParameter(1, site).executeUpdate();	
			
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to delete the site . " + "\n error: " + e.toString());
			throw new Exception("An Exception occurred executing the query to delete the site. " + "\n error: " + e.toString());
		}
		
		return Exceuted;
	}
	
	
	public String getSite( String site) throws Exception{
		
		ArrayList<String> groups = new ArrayList<String>();
		
		String query = "", group = "";
		if(site != null && !site.isEmpty()) {
			query = " Select recipient, site FROM SITE_RECIPIENT_MASTER where site = ?";
		}else {
			query = " Select recipient, site FROM SITE_RECIPIENT_MASTER";
		}
		try
		{
			
			
			
			List<Object[]>	rs = null;
			
			if(site != null && !site.isEmpty()) {
				rs = em.createNativeQuery(query).setParameter(1, site).getResultList();	
			}else {
				rs = em.createNativeQuery(query).getResultList();	
			}
			
			
			if (rs != null) 
			{
				for(Object[] a : rs )
				{
					
				groups.add("Recipient: "+a[0] + " Site: " +a[1] );

				}
			}
			
			
			
		}
		catch (Exception e) 
		{
			logger.severe("An Exception occurred executing the query to get the site recipient. " + "\n error: " + e.toString());
			throw new Exception("An Exception occurred executing the query to get the site recipient. " + "\n error: " +  e.toString());
		}
		for(String g : groups) {
			group = group + g +"\n";
			
		}
		
		return group;
		
	}
	
	

	public void acceptReq(DTTRAXI41ACK4067 reqs)
	
	{
			if(reqs.getSuccessErrorLog() != null && reqs.getSuccessErrorLog().getIDOCStatus().equalsIgnoreCase("53"))
			{
				logger.info("IDOCStatus 53");
				for(Orders o : reqs.getOrders()) {
					for(trax.aero.outbound.Component c: o.getComponent()) {
						
						
						PicklistDistribution require = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
								.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
								.setParameter("line", Long.valueOf(c.getRequistioLine()))
								.setParameter("tra", "REQUIRE")
								.getSingleResult();
						//require.setInterfaceSyncFlag(null);
						require.setInterfaceSyncDate(null);
						require.setExternalCustRes(c.getReservationNumber());
						require.setExternalCustResItem(c.getReservationItem());
						insertData(require);
						
						try {
							PicklistDistribution req = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
									.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
									.setParameter("line", Long.valueOf(c.getRequistioLine()))
									.setParameter("tra", "DISTRIBU")
									.getSingleResult();
							req.setExternalCustRes(c.getReservationNumber());
							req.setExternalCustResItem(c.getReservationItem());
							
							insertData(require);
						
						}catch(Exception e) {
							logger.severe(e.toString());
						}
					}
				}
			}else if(reqs.getSuccessErrorLog() != null){
				logger.info("IDOCStatus 51");
				String orders = "";
				for(Orders o : reqs.getOrders()) {
					for(trax.aero.outbound.Component c : o.getComponent()) {
						
						PicklistDistribution require = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
								.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
								.setParameter("line", Long.valueOf(c.getRequistioLine()))
								.setParameter("tra", "REQUIRE")
								.getSingleResult();
						
						require.setInterfaceSyncDate(null);
						require.setInterfaceSyncFlag("Y");
						require.setExternalCustRes(c.getReservationNumber());
						require.setExternalCustResItem(c.getReservationItem());
						insertData(require);
						orders = orders + "( RequistionNumber: "+ c.getRequistionNumber() + " Requistionline: "+ c.getRequistioLine() + "),";
						
						try {
							PicklistDistribution req = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
									.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
									.setParameter("line", Long.valueOf(c.getRequistioLine()))
									.setParameter("tra", "DISTRIBU")
									.getSingleResult();
							req.setExternalCustRes(c.getReservationNumber());
							req.setExternalCustResItem(c.getReservationItem());
							insertData(require);
						}catch(Exception e) {
							logger.severe(e.toString());
						}
					}
				}	
				emailer.sendEmail("Received acknowledgement with IDOC Status: " + reqs.getSuccessErrorLog().getIDOCStatus() +", IDOC Number: "+reqs.getSuccessErrorLog().getIDOCNumber()+", Status Error Code: "+reqs.getSuccessErrorLog().getStatusErrorCode() + ", Status Message: " + reqs.getSuccessErrorLog().getStatusMessage() +"\n"+ orders) ;
			}else {
				logger.info("IDOCStatus unkown");
				String orders = "";
				for(Orders o : reqs.getOrders()) {
					for(trax.aero.outbound.Component c : o.getComponent()) {
						
						PicklistDistribution require = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
								.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
								.setParameter("line", Long.valueOf(c.getRequistioLine()))
								.setParameter("tra", "REQUIRE")
								.getSingleResult();
						
						require.setInterfaceSyncFlag("Y");
						require.setInterfaceSyncDate(null);
						require.setExternalCustRes(c.getReservationNumber());
						require.setExternalCustResItem(c.getReservationItem());
						
						insertData(require);
						
						orders = orders + "( RequistionNumber: "+ c.getRequistionNumber() + " Requistionline: "+ c.getRequistioLine() + "),";
						
						
						try {
							PicklistDistribution req = (PicklistDistribution) em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra")
									.setParameter("pick", Long.valueOf(c.getRequistionNumber()))
									.setParameter("line", Long.valueOf(c.getRequistioLine()))
									.setParameter("tra", "DISTRIBU")
									.getSingleResult();
							req.setExternalCustRes(c.getReservationNumber());
							req.setExternalCustResItem(c.getReservationItem());
							insertData(require);
						}catch(Exception e) {
							logger.severe(e.toString());
						}
					}
				}
				emailer.sendEmail("Received acknowledgement with NULL Success Error Log\n" +orders ) ;
			}
			
	}
	
	private <T> void insertData( T data) 
	{
		try 
		{	
			em.merge(data);
			em.flush();
		}catch (Exception e)
		{
			String exceuted = "insertData has encountered an Exception: "+e.toString();
			logger.severe(exceuted);
			e.printStackTrace();
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
		insertData(lock);
	}
	
	public void unlockTable(String notificationType)
	{
		
		InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
				.setParameter("type", notificationType).getSingleResult();
		lock.setLocked(new BigDecimal(0));
		//logger.info("lock " + lock.getLocked());
		
		lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
		//em.lock(lock, LockModeType.NONE);
		insertData(lock);
	}
	
	
}


