package trax.aero.data;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import trax.aero.client.ServiceClient;
import trax.aero.interfaces.INRData;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.PicklistDistribution;
import trax.aero.model.PicklistDistributionPK;
import trax.aero.model.PicklistHeader;
import trax.aero.model.PnMaster;
import trax.aero.model.WoTaskCard;
import trax.aero.model.WO;
import trax.aero.model.WoTaskCardItem;
import trax.aero.pojo.ApplicatonMessage;
import trax.aero.pojo.Component;
import trax.aero.pojo.DTTRAXI37I384068;
import trax.aero.pojo.DTTRAXI37I38ACK4069;
import trax.aero.pojo.Operation;
import trax.aero.pojo.Order;
import trax.aero.pojo.OrderHeader;
import trax.aero.pojo.OrderLongText;
import trax.aero.util.EmailSender;

@Stateless(name="NRData" , mappedName="NRData")
public class NRData implements INRData {

	@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
	
	EmailSender emailer = null;
	String error = "";
	long Picklong = 9999;
	ServiceClient client = null;
	Logger logger = LogManager.getLogger("NR_I37I38");
	
	
	
	
	
	
	public NRData()
	{
		emailer = new EmailSender(System.getProperty("NR_toEmail"));
		client = new ServiceClient();
	}
	
	public String sendNRData() throws JAXBException
	{
		newNonRoutines();
		return null;
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	private List<WoTaskCard> newNonRoutines() throws JAXBException
	{
		List<WoTaskCard> cards = null;
		List<WoTaskCard> updates = null;
		List <DTTRAXI37I384068> list = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		
		
		//em.clear();

		
		cal.add(Calendar.SECOND, -Integer.parseInt(System.getProperty("NR_interval")));
		
		
		//cal.add(Calendar.HOUR, -2);
		//logger.info(" CAL DATE " + cal.getTime());
		
		try
		{
			
			
			cards =  em.createQuery("Select w from WoTaskCard w, WO r where w.id.wo = r.wo and r.rfoNo is null and w.createdDate >= :date and w.nonRoutine = :nr and w.interfaceSyncDate is null and ( w.interfaceSyncFlag != :flag or w.interfaceSyncFlag is null) ")
				.setParameter("date", cal.getTime())
				.setParameter("nr", "Y")
				.setParameter("flag", "S")
				.getResultList();
			
			
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
		}
		
		if(cards != null && cards.size() != 0)
		{
			
			
			for(WoTaskCard card : cards)
			{
				
				
				em.refresh(card);
				
				if(card.getInterfaceSyncFlag() != null && ( card.getInterfaceSyncFlag().equalsIgnoreCase("S")  || card.getInterfaceSyncFlag().equalsIgnoreCase("Y")) ) {
					continue;
				}
				
				if(getWoShop(String.valueOf(card.getId().getWo()))) {
					logger.info("WO " + card.getId().getWo() + " is SHOP WO");
					continue;
				}
				if(card.getWoTaskCardItems() == null || card.getWoTaskCardItems().isEmpty()) {
					logger.info("Task Card " + card.getId().getTaskCard() + " Items are empty");
					continue;
				}
				
				if(card.getReferenceTaskCard() != null && !card.getReferenceTaskCard().isEmpty()) {
					//logger.info("Task Card " + card.getId().getTaskCard() + " Order Number is null");
					//continue;
				}
				
				logger.info("CREATED DATE " + card.getCreatedDate() + " >=  CAL DATE " + cal.getTime() + " NR: "+ card.getId().getTaskCard());
				
				
				DTTRAXI37I384068 data = new DTTRAXI37I384068();
				
				data.setOrder(new Order());
				
				if( card.getId().getPn() != null && !card.getId().getPn().isEmpty() && !card.getId().getPn().equalsIgnoreCase("                                   ")) {
					
					data.getOrder().setPartNumber(card.getId().getPn());
					
					data.getOrder().setPartNumber(data.getOrder().getPartNumber().replaceAll("IN", "\""));
					data.getOrder().setPartNumber(data.getOrder().getPartNumber().replaceAll("FT", "'"));
					
					if(data.getOrder().getPartNumber().contains(":UPLOAD"))
					{
						data.getOrder().setPartNumber( data.getOrder().getPartNumber().substring(0, data.getOrder().getPartNumber().indexOf(":")));
					}
					
					
					
					
					if(card.getCusReqDate() != null) {
						String dateRequired = new SimpleDateFormat("yyyyMMdd").format(card.getCusReqDate());
						data.getOrder().setDateRequired(dateRequired);
					}
				}
				if( card.getId().getPnSn() != null && !card.getId().getPnSn().isEmpty() && !card.getId().getPnSn().equalsIgnoreCase("                                   ")
						 && checkPnCategory(data.getOrder().getPartNumber())) {
					data.getOrder().setSerialNumber(card.getId().getPnSn());
				}
				
				if(card.getCorrosion() != null  && !card.getCorrosion().isEmpty() && card.getCorrosion().equalsIgnoreCase("CCS")) {
					data.getOrder().setStatusCategory(card.getCorrosion());
					
				}else {
					data.getOrder().setStatusCategory(card.getStatusCategory());
				}
				
				
				if(card.getCcsQty() != null) {
					//logger.info("CcsQty " + card.getCcsQty().toString() + " " + card.getId().getTaskCard());
					data.getOrder().setQuantity_Wtihdrawn(card.getCcsQty().toString());
				}
				
				data.getOrder().setRevisionNumber(card.getRevision());
				data.getOrder().setZone(card.getZonal());
				data.getOrder().setTaskDescription(card.getTaskCardDescription());
				
				List<OrderLongText> texts = new ArrayList<OrderLongText>();
				OrderLongText t = new OrderLongText();
				t.setLongText(card.getTaskCardDescription());
				texts.add(t);
				data.getOrder().setOrderLongText(texts);
				
				if(card.getIeIndicator() != null && !card.getIeIndicator().isEmpty() && card.getIeIndicator().equalsIgnoreCase("I")) {
					data.getOrder().setPriority("I");
				}else if(card.getIeIndicator() != null && !card.getIeIndicator().isEmpty() && card.getIeIndicator().equalsIgnoreCase("E"))
				{
					data.getOrder().setPriority("E");
				}
				
				data.getOrder().setDefectText(card.getDefectDescription());
				
				if(card.getCorrosion() != null  && !card.getCorrosion().isEmpty() && card.getCorrosion().equalsIgnoreCase("FABRIC")) {
					data.getOrder().setFabricationRadioButton("X");
				}
				
				
				if( card.getScheduleTaskCard() !=null && !card.getScheduleTaskCard().isEmpty()) {
						
					String svo = getPlannedTaskCardOrderNumber(card.getScheduleTaskCard(), card.getId().getWo());	
					if(svo !=null  && !svo.isEmpty()) {
							data.getOrder().setParentOrderNumber(svo);
					}else if(card.getReferenceTaskCard() !=null  && !card.getReferenceTaskCard().isEmpty()) {
							data.getOrder().setOrderNumber(card.getReferenceTaskCard());	
					}
				}else if(card.getReferenceTaskCard() !=null  && !card.getReferenceTaskCard().isEmpty()) {
					data.getOrder().setOrderNumber(card.getReferenceTaskCard());
				}	
					
				if(data.getOrder().getParentOrderNumber() != null && !data.getOrder().getParentOrderNumber().isEmpty()
				&& (data.getOrder().getStatusCategory()  == null || data.getOrder().getStatusCategory().isEmpty() || !data.getOrder().getStatusCategory().equalsIgnoreCase("CCS"))) {
					data.getOrder().setPartNumber(null);
					data.getOrder().setSerialNumber(null);
				}
				
				
				//DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				//if(card.getGeDueDate() != null)
				//	data.getOrder().setDateRequired(df.format(card.getGeDueDate()));
				
				data.getOrder().setTRAXNonRoutineNumber(card.getId().getTaskCard());
				data.getOrder().setTRAXWO(String.valueOf(card.getId().getWo()));
				data.getOrder().setOperation(new ArrayList<Operation>());
				data.getOrder().setPersonResponsible(card.getCreatedBy());
				
				if(card.getWoTaskCardItems() != null)
				{
					logger.info("item size " + card.getWoTaskCardItems().size());
				}
				
				if(card.getWoTaskCardItems() != null && card.getWoTaskCardItems().size() != 0)
				{
					Collections.sort(card.getWoTaskCardItems());

					
					for(WoTaskCardItem item : card.getWoTaskCardItems())
					{
						Operation operation = new Operation();
						operation.setOperationNumber(item.getExternalCustRef());
						operation.setOperationNumber(item.getOpsNo());
						
						BigDecimal hours = new BigDecimal(0) ;
						if(item.getManHours() != null ) {
							hours= hours.add(item.getManHours());	
						}
						if(item.getInspectorManHours() != null ) {
							hours= hours.add(item.getInspectorManHours());	
						}
						if(item.getDualInspectorManHours() != null ) {
							
							hours= hours.add(item.getDualInspectorManHours());	
						}
						if(hours.compareTo(new BigDecimal(0)) != 0) {
							operation.setManHours(hours.toString());
						}
						
						
						String text = "";
						RTFEditorKit rtfParser = new RTFEditorKit();
						Document document = rtfParser.createDefaultDocument();
						try {
							rtfParser.read(new ByteArrayInputStream(item.getTaskCardText().getBytes()), document, 0);
							text = document.getText(0, document.getLength());
						} catch (Exception e) {
						
						}
											
						operation.setLongText(text);
						data.getOrder().getOperation().add(operation);
					}
				}
				
				
				
				list.add(data);
			}
		}
		try
		{
			
			
			updates = this.em.createQuery("Select w from WoTaskCard w, WO r where w.id.wo = r.wo and r.rfoNo is null and w.interfaceSyncDate IS NOT NULL and w.nonRoutine = :nr and  ( w.interfaceSyncFlag != :flag or w.interfaceSyncFlag is null) ")
					.setParameter("nr", "Y")
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
			
			
			for(WoTaskCard card : updates)
			{
				em.refresh(card);
				
				DTTRAXI37I384068 data = new DTTRAXI37I384068();
				
				data.setOrder(new Order());
				if(card.getInterfaceSyncFlag() != null &&  card.getInterfaceSyncFlag().equalsIgnoreCase("S")) {
					continue;
				}
				
				if( card.getId().getPn() != null && !card.getId().getPn().isEmpty() && !card.getId().getPn().equalsIgnoreCase("                                   ")) {
						data.getOrder().setPartNumber(card.getId().getPn());
						
						data.getOrder().setPartNumber(data.getOrder().getPartNumber().replaceAll("IN", "\""));
						data.getOrder().setPartNumber(data.getOrder().getPartNumber().replaceAll("FT", "'"));
						
						if(data.getOrder().getPartNumber().contains(":UPLOAD"))
						{
							data.getOrder().setPartNumber( data.getOrder().getPartNumber().substring(0, data.getOrder().getPartNumber().indexOf(":")));
						}
						
						
						if(card.getCusReqDate() != null) {
							String dateRequired = new SimpleDateFormat("yyyyMMdd").format(card.getCusReqDate());
							data.getOrder().setDateRequired(dateRequired);
						}
				}
				if( card.getId().getPnSn() != null && !card.getId().getPnSn().isEmpty() && !card.getId().getPnSn().equalsIgnoreCase("                                   ")
						&& checkPnCategory(data.getOrder().getPartNumber())) {
					data.getOrder().setSerialNumber(card.getId().getPnSn());
				}
				if(card.getCorrosion() != null  && !card.getCorrosion().isEmpty() && card.getCorrosion().equalsIgnoreCase("CCS")) {
					data.getOrder().setStatusCategory(card.getCorrosion());
				}else {
					data.getOrder().setStatusCategory(card.getStatusCategory());
				}
				
				if(card.getCcsQty() != null) {
					//logger.info("CcsQty " + card.getCcsQty().toString() + " " + card.getId().getTaskCard());
					data.getOrder().setQuantity_Wtihdrawn(card.getCcsQty().toString());
				}
				
				data.getOrder().setRevisionNumber(card.getRevision());
				data.getOrder().setZone(card.getZonal());
				data.getOrder().setTaskDescription(card.getTaskCardDescription());
				
				List<OrderLongText> texts = new ArrayList<OrderLongText>();
				OrderLongText t = new OrderLongText();
				t.setLongText(card.getTaskCardDescription());
				texts.add(t);
				data.getOrder().setOrderLongText(texts);
				
				if(card.getIeIndicator() != null && !card.getIeIndicator().isEmpty() && card.getIeIndicator().equalsIgnoreCase("I")) {
					data.getOrder().setPriority("I");
				}else if(card.getIeIndicator() != null && !card.getIeIndicator().isEmpty() && card.getIeIndicator().equalsIgnoreCase("E"))
				{
					data.getOrder().setPriority("E");
				}
				
				data.getOrder().setDefectText(card.getDefectDescription());
				data.getOrder().setPersonResponsible(card.getCreatedBy());
				
				if(card.getCorrosion() != null && !card.getCorrosion().isEmpty() && card.getCorrosion().equalsIgnoreCase("FABRIC")) {
					data.getOrder().setFabricationRadioButton("X");
				}
				
				if( card.getScheduleTaskCard() !=null && !card.getScheduleTaskCard().isEmpty()) {
					
					String svo = getPlannedTaskCardOrderNumber(card.getScheduleTaskCard(), card.getId().getWo());	
					if(svo !=null  && !svo.isEmpty()) {
							data.getOrder().setParentOrderNumber(svo);
					}else if(card.getReferenceTaskCard() !=null  && !card.getReferenceTaskCard().isEmpty()) {
							data.getOrder().setOrderNumber(card.getReferenceTaskCard());	
					}
				}else if(card.getReferenceTaskCard() !=null  && !card.getReferenceTaskCard().isEmpty()) {
					data.getOrder().setOrderNumber(card.getReferenceTaskCard());
				}		
				
				//DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				//if(card.getGeDueDate() != null)
				//	data.getOrder().setDateRequired(df.format(card.getGeDueDate()));
				if(data.getOrder().getParentOrderNumber() != null && !data.getOrder().getParentOrderNumber().isEmpty()
						&& (data.getOrder().getStatusCategory()  == null || data.getOrder().getStatusCategory().isEmpty() || !data.getOrder().getStatusCategory().equalsIgnoreCase("CCS"))) {
							data.getOrder().setPartNumber(null);
							data.getOrder().setSerialNumber(null);
				}
				
				data.getOrder().setTRAXNonRoutineNumber(card.getId().getTaskCard());
				data.getOrder().setTRAXWO(String.valueOf(card.getId().getWo()));
				data.getOrder().setOperation(new ArrayList<Operation>());
				if(card.getWoTaskCardItems() != null)
				{
					//logger.info("item size " + card.getWoTaskCardItems());
				}
				if(card.getWoTaskCardItems() != null && card.getWoTaskCardItems().size() != 0)
				{
					Collections.sort(card.getWoTaskCardItems());
					
					for(WoTaskCardItem item : card.getWoTaskCardItems())
					{
						Operation operation = new Operation();
						operation.setOperationNumber(item.getExternalCustRef());
						operation.setOperationNumber(item.getOpsNo());
						BigDecimal hours = new BigDecimal(0) ;
						if(item.getManHours() != null ) {
							hours= hours.add(item.getManHours());	
						}
						if(item.getInspectorManHours() != null ) {
							hours= hours.add(item.getInspectorManHours());	
						}
						if(item.getDualInspectorManHours() != null ) {
							hours= hours.add(item.getDualInspectorManHours());	
						}
						
						if(hours.compareTo(new BigDecimal(0)) != 0) {
							operation.setManHours(hours.toString());
						}
						String text = "";
						RTFEditorKit rtfParser = new RTFEditorKit();
						Document document = rtfParser.createDefaultDocument();
						try {
							rtfParser.read(new ByteArrayInputStream(item.getTaskCardText().getBytes()), document, 0);
							text = document.getText(0, document.getLength());
						} catch (Exception e) {
						
						}
								
						operation.setLongText(text);
						data.getOrder().getOperation().add(operation);
					}
				}
				list.add(data);
			}			
		}

		if(list != null && list.size() > 0) {
			for(DTTRAXI37I384068 data :list) {
				JAXBContext jc = JAXBContext.newInstance(DTTRAXI37I384068.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				marshaller.marshal(data, sw);
				
				logger.info("Ouput: " + sw.toString());
				
				
				if(!client.callSap(data))
				{
					markSentFailed(data);
					emailer.sendEmail("Trax was unable to call SAP With TRAXNonRoutineNumber:" +data.getOrder().getTRAXNonRoutineNumber() + " ,TRAXWO: " + data.getOrder().getTRAXWO()) ;
				
				}
				else
				{
					
					markSent(data);
				}
			}
		
		}
		
		
		
		return null;
	}
	
	
	

	

	private void markSentFailed(DTTRAXI37I384068 data) {
		try {
			
			WoTaskCard nr = em.createQuery("Select s from WoTaskCard s where s.id.wo = :woo and s.id.taskCard = :card", WoTaskCard.class)
					.setParameter("woo", Long.valueOf((data.getOrder().getTRAXWO())))
					.setParameter("card", data.getOrder().getTRAXNonRoutineNumber())
					.getSingleResult();
			
			nr.setInterfaceSyncFlag("Y");
			nr.setInterfaceSyncDate(new Date());
			insertData(nr, "WoTaskCard", "nr");
		}catch(Exception e) {
			logger.info(e.getMessage());
		}
		
	}

	private void markSent(DTTRAXI37I384068 data) {
		try {
			
			WoTaskCard nr = em.createQuery("Select s from WoTaskCard s where s.id.wo = :woo and s.id.taskCard = :card", WoTaskCard.class)
					.setParameter("woo", Long.valueOf((data.getOrder().getTRAXWO())))
					.setParameter("card", data.getOrder().getTRAXNonRoutineNumber())
					.getSingleResult();
			
			nr.setInterfaceSyncFlag("S");
			insertData(nr, "WoTaskCard", "nr");
		}catch(Exception e) {
			logger.info(e.getMessage());
		}		
	}

	public void closeNr(DTTRAXI37I38ACK4069 orders)
	{
			if(orders.getOrders().getSuccessErrorlog() != null && orders.getOrders().getSuccessErrorlog().getIDOCStatus().equalsIgnoreCase("53"))
			{
				for(OrderHeader header : orders.getOrders().getOrderHeader())
				{
					
					WoTaskCard nr = em.createQuery("Select s from WoTaskCard s where s.id.wo = :woo and s.id.taskCard = :card", WoTaskCard.class)
							.setParameter("woo", Long.valueOf((orders.getOrders().getTRAXWO())))
							.setParameter("card", orders.getOrders().getTRAXNonRoutineNumber())
							.getSingleResult();
					if(header.getOrderNumber() != null && !header.getOrderNumber().isEmpty()) {
						nr.setReferenceTaskCard(header.getOrderNumber());
					}
					if(nr.getWoTaskCardItems() != null && !nr.getWoTaskCardItems().isEmpty()) {
						Collections.sort(nr.getWoTaskCardItems());
					}
					int i = 0; 
					for(Operation o : header.getOperation()) {
						if(getTranConfigFlag("CONFIGURATION", "TCTCNROPSNO").equalsIgnoreCase("N")) {
							nr.getWoTaskCardItems().get(i).setOpsNo(o.getOperationActivityNumber());
						}else {
							nr.getWoTaskCardItems().get(i).setOpsNo(getConfigOther("TCNROPSNO"));
						}
						i++;
					}
					//nr.setInterfaceSyncFlag(null);
					nr.setInterfaceSyncDate(null);
					insertData(nr, "nr", "NR");
				}
			
			}else if(orders.getOrders().getSuccessErrorlog() != null){
				for(OrderHeader header : orders.getOrders().getOrderHeader())
				{
					
					WoTaskCard nr = em.createQuery("Select s from WoTaskCard s where s.id.wo = :woo and s.id.taskCard = :card", WoTaskCard.class)
							.setParameter("woo", Long.valueOf((orders.getOrders().getTRAXWO())))
							.setParameter("card", orders.getOrders().getTRAXNonRoutineNumber())
							.getSingleResult();
					nr.setInterfaceSyncFlag("Y");
					nr.setInterfaceSyncDate(null);
					if(header.getOrderNumber() != null && !header.getOrderNumber().isEmpty()) {
						nr.setReferenceTaskCard(header.getOrderNumber());
					}
					int i = 0; 
					if(nr.getWoTaskCardItems() != null && !nr.getWoTaskCardItems().isEmpty()) {
						Collections.sort(nr.getWoTaskCardItems());
					}
					for(Operation o : header.getOperation()) {
						if(getTranConfigFlag("CONFIGURATION", "TCNROPSNO").equalsIgnoreCase("N")) {
							nr.getWoTaskCardItems().get(i).setOpsNo(o.getOperationActivityNumber());
						}else {
							nr.getWoTaskCardItems().get(i).setOpsNo(getConfigOther("TCNROPSNO"));
						}
						i++;
					}
					insertData(nr, "nr", "NR");
				}
				String message = "";
				for(ApplicatonMessage a : orders.getOrders().getSuccessErrorlog().getApplicatonMessage()){
					message = message + "Error Code: "+a.getStatusErrorCode() + " Status Message: "+a.getStatusMessage() + ",";
				}
				
				
				emailer.sendEmail("Received acknowledgement with IDOC Status: " + orders.getOrders().getSuccessErrorlog().getIDOCStatus() +
						", IDOC Number: "+orders.getOrders().getSuccessErrorlog().getIDOCNumber()+", Message: " +
						message +" TRAXNonRoutineNumber:" +orders.getOrders().getTRAXNonRoutineNumber() + " ,TRAXWO: " + orders.getOrders().getTRAXWO()) ;
			}else {
				for(OrderHeader header : orders.getOrders().getOrderHeader())
				{
					
					WoTaskCard nr = em.createQuery("Select s from WoTaskCard s where s.id.wo = :woo and s.id.taskCard = :card", WoTaskCard.class)
							.setParameter("woo", Long.valueOf((orders.getOrders().getTRAXWO())))
							.setParameter("card", orders.getOrders().getTRAXNonRoutineNumber())
							.getSingleResult();
					nr.setInterfaceSyncFlag("Y");
					nr.setInterfaceSyncDate(null);
					if(header.getOrderNumber() != null && !header.getOrderNumber().isEmpty()) {
						nr.setReferenceTaskCard(header.getOrderNumber());
					}
					int i = 0;
					if(nr.getWoTaskCardItems() != null && !nr.getWoTaskCardItems().isEmpty()) {
						Collections.sort(nr.getWoTaskCardItems());
					}
					for(Operation o : header.getOperation()) {
						if(getTranConfigFlag("CONFIGURATION", "TCNROPSNO").equalsIgnoreCase("N")) {
							nr.getWoTaskCardItems().get(i).setOpsNo(o.getOperationActivityNumber());
						}else {
							nr.getWoTaskCardItems().get(i).setOpsNo(getConfigOther("TCNROPSNO"));
						}
						i++;
					}
					insertData(nr, "nr", "NR");
				}			
				emailer.sendEmail("Received acknowledgement with NULL Success Error Log" +" TRAXNonRoutineNumber:" +orders.getOrders().getTRAXNonRoutineNumber() + " ,TRAXWO: " + orders.getOrders().getTRAXWO()) ;
			}
	}
	
	
	
	private <T> void insertData( T data, String type, String name) 
		{
			try 
			{	
				em.merge(data);
				em.flush();
			}catch (Exception e)
			{
				String exceuted = "insertData has encountered an Exception with inserting type: " + type +" Name: "+name +" Exception: "+e.toString();
				logger.severe(exceuted);
				e.printStackTrace();
			}
		}
		
		private PnMaster getPN(String PN) {
			try
			{
				PnMaster pnMaster = em.createQuery("Select p From PnMaster p where p.id.pn = :partn", PnMaster.class)
				.setParameter("partn", PN)
				.getSingleResult();
				
				return pnMaster;
			}
			catch (Exception e)
			{
				
			}
			return null;
		}
		
		
		private Boolean getWoShop(String w) {
			   logger.info("Checking WO " + w);
			   String sql = "SELECT R.MODULE FROM WO R WHERE R.WO = ? and RFO_NO is null";
		       
		       try {
		    	   
		    	   Query query = em.createNativeQuery(sql);
		    	   query.setParameter(1, w);  
				
					String mod = (String) query.getSingleResult();
					 if(mod != null && !mod.isEmpty() && mod.equalsIgnoreCase("SHOP")) { 	
						 return true;
					 }else {
						 return false;
					 }
		        } catch (Exception e) {
		        	e.printStackTrace();
		        	logger.severe("Error getting WO.");
		        	logger.severe(e.toString());
		        	return false;
		           // throw e;
		        }		       	
		}
		
		
		
		
		private String getPlannedTaskCardOrderNumber(String scheduleTaskCard, long woo) {
			boolean found = true;
			
			String tc = scheduleTaskCard;
			long wo = woo;
			
			logger.info("Start Wo " + wo + " schedule Task Card " + tc);
			
			while(found) {
				try
				{
					WoTaskCard cardSch =  em.createQuery("select w from WoTaskCard w where w.id.wo = :wo and w.id.taskCard = :card", WoTaskCard.class)
						.setParameter("wo", wo)
						.setParameter("card", tc)
						.getSingleResult();
					
					if((cardSch.getReferenceTaskCard() !=null && !cardSch.getReferenceTaskCard().isEmpty()) &&
							(cardSch.getNonRoutine() == null  || cardSch.getNonRoutine().isEmpty()  || cardSch.getNonRoutine().equalsIgnoreCase("N"))) 
					{	
						logger.info("Found ReferenceTaskCard");
						return cardSch.getReferenceTaskCard();
					
					}
					else if((cardSch.getReferenceTaskCard() == null || cardSch.getReferenceTaskCard().isEmpty()) 
							&& (cardSch.getNonRoutine() == null || cardSch.getNonRoutine().isEmpty() 
							|| cardSch.getNonRoutine().equalsIgnoreCase("N"))) 
					{
											
						logger.severe("No Order number found in Planned Task Card " + tc);
						return null;					
					
					}
					else if( cardSch.getScheduleTaskCard() !=null && !cardSch.getScheduleTaskCard().isEmpty() 
							&& !tc.equalsIgnoreCase(cardSch.getScheduleTaskCard()) && getTaskCard(cardSch.getScheduleTaskCard(), woo, tc) ) 
					{					
						logger.info("Setting Schedule Task Card: " + cardSch.getScheduleTaskCard());
						tc= cardSch.getScheduleTaskCard();
					
					}
					else 
					{
						logger.severe("No Planned Task Card found " + tc);
						return null;
					}
				}
				catch(Exception e)
				{
					logger.severe(e.getMessage());
					logger.severe("No Schedule Task Card found " + tc);
					return null;
				}				
			}
			
			return null;
		}
		
		
		private boolean getTaskCard(String scheduleTaskCard, long woo,String privousTC) {
			try
			{
				WoTaskCard cardSch =  em.createQuery("select w from WoTaskCard w where w.id.wo = :wo and w.id.taskCard = :card", WoTaskCard.class)
					.setParameter("wo", woo)
					.setParameter("card", scheduleTaskCard)
					.getSingleResult();
				if(!privousTC.equalsIgnoreCase(cardSch.getScheduleTaskCard()) || ( !cardSch.getNonRoutine().equalsIgnoreCase("Y"))) {
					logger.info("Future Task card is different or is not a Non Rouine " 
				+ cardSch.getScheduleTaskCard() + " NonRoutine " + cardSch.getNonRoutine());
					
					return true;
				}else {
					logger.severe("Future Task card is the same");
					return false;
				}
			
			}catch(Exception e)
			{
				logger.severe(e.getMessage());
				logger.severe("No Schedule Task Card found");
				return false;
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
					insertData(lock,"lock","InterfaceLockMaster");
					return true;
				}
				return false;
			}
			else
			{
				lock.setLocked(new BigDecimal(1));
				insertData(lock,"lock","InterfaceLockMaster");
				return true;
			}
			
		}
		
		private boolean checkPnCategory(String partNumber) {
			String sql = "SELECT PN_TRANSACTION FROM SYSTEM_TRAN_CODE where SYSTEM_TRANSACTION = 'PNCATEGORY' AND SYSTEM_CODE = ?";
			PnMaster pn = getPN(partNumber);
			//
			//("Serialized", "R"));
			//("Consumable", "C"));
			//("Kit", "K"));
			//("Repairable", "P"));
			
			if(pn == null) {
				return false;
			}
			try {
		    	   
		    	   Query query = em.createNativeQuery(sql);
		    	   query.setParameter(1, pn.getCategory());  
				
					String pnTran = (String) query.getSingleResult();
					 if(pnTran != null && !pnTran.isEmpty() && pnTran.equalsIgnoreCase("R")) { 	
						 return true;
					 }else {
						 return false;
					 }
		        } catch (Exception e) {
		        	e.printStackTrace();
		        	logger.severe("Error getting PN Category.");
		        	logger.severe(e.toString());
		        	return false;
		           // throw e;
		        }		
		}
		
		
		public String getTranConfigFlag(String systemTransaction,
				String systemCode) 
		{
			String flag = null;
			try
			{

				flag = (String) em.createNativeQuery("SELECT CONFIG_FLAG FROM SYSTEM_TRAN_CONFIG WHERE SYSTEM_TRANSACTION = ? AND SYSTEM_CODE = ?")
						.setParameter(1, systemTransaction)
						.setParameter(2, systemCode)
						.getSingleResult();


				if (flag == null || flag.length() <= 0)
					return "N";
				else
					return flag;


			} catch (final Exception e)
			{
				return "N";
			} 

		}
		
		public  String getConfigOther(String param) {
	        String result = "";
	        try {
	           Query query = this.em.createNativeQuery(
	                    "select \"PKG_APPLICATION_FUNCTION\".CONFIG_OTHER('"+param+"') as result from dual");
	            result = (String)(query).getSingleResult();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return result;
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
			insertData(lock,"lock","InterfaceLockMaster");
		}
		
		public void unlockTable(String notificationType)
		{
			
			InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType).getSingleResult();
			lock.setLocked(new BigDecimal(0));
			//logger.info("lock " + lock.getLocked());
			
			lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			//em.lock(lock, LockModeType.NONE);
			insertData(lock,"lock","InterfaceLockMaster");
		}
		
	
}
