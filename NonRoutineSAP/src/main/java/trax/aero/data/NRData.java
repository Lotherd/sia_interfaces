package trax.aero.data;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
			
			
			cards =  em.createQuery("Select w from WoTaskCard w where w.createdDate >= :date and w.nonRoutine = :nr and w.interfaceSyncDate is null and ( w.interfaceSyncFlag != :flag or w.interfaceSyncFlag is null) ")
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
				if( card.getId().getPnSn() != null && !card.getId().getPnSn().isEmpty() && !card.getId().getPnSn().equalsIgnoreCase("                                   ")) {
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
			
			
			updates = this.em.createQuery("Select w from WoTaskCard w where w.interfaceSyncDate IS NOT NULL and w.nonRoutine = :nr and  ( w.interfaceSyncFlag != :flag or w.interfaceSyncFlag is null) ")
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
				if( card.getId().getPnSn() != null && !card.getId().getPnSn().isEmpty() && !card.getId().getPnSn().equalsIgnoreCase("                                   ")) {
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
						nr.getWoTaskCardItems().get(i).setOpsNo(o.getOperationActivityNumber());
						if(o.getComponent() !=null && !o.getComponent().isEmpty()) {
							for(Component c : o.getComponent() ) {
								insertComponent( c, nr);
							}
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
						nr.getWoTaskCardItems().get(i).setOpsNo(o.getOperationActivityNumber());
						if(o.getComponent() !=null && !o.getComponent().isEmpty()) {
							for(Component c : o.getComponent() ) {
								insertComponent( c, nr);
							}
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
						nr.getWoTaskCardItems().get(i).setOpsNo(o.getOperationActivityNumber());
						if(o.getComponent() !=null && !o.getComponent().isEmpty()) {
							for(Component c : o.getComponent() ) {
								insertComponent( c, nr);
							}
						}
						i++;
					}
					insertData(nr, "nr", "NR");
				}			
				emailer.sendEmail("Received acknowledgement with NULL Success Error Log" +" TRAXNonRoutineNumber:" +orders.getOrders().getTRAXNonRoutineNumber() + " ,TRAXWO: " + orders.getOrders().getTRAXWO()) ;
			}
	}
	
	
	
	// insert PICKLIST
		private void insertComponent( Component component, WoTaskCard wotaskcard) {
					
					//setting up variables
					PicklistHeader picklistheader = null;
					//Boolean exist = false;
					PicklistDistribution picklistdistdistribu = null;
					PicklistDistribution picklistdistrequire = null;
					
					//check if object has min values
					if(component.getReservationNumber() != null && !component.getReservationNumber().isEmpty() && component.getReservationItem()!= null && !component.getReservationItem().isEmpty() && component.getMaterialNumber()!= null && !component.getMaterialNumber().isEmpty() &&  null != getPN(component.getMaterialNumber())) 
					{
						
						String pickNumber = findPicklistNumber(component.getReservationNumber(),component.getReservationItem());
						
						try 
						{
							picklistheader = em.createQuery("SELECT p FROM PicklistHeader p where p.id.picklist =:pick", PicklistHeader.class)
									.setParameter("pick", Long.valueOf(pickNumber))
									.getSingleResult();
						}
						catch(Exception e)
						{
							picklistheader = new PicklistHeader();
							picklistheader.setCreatedBy("TRAX_IFACE");
							picklistheader.setCreatedDate(new Date());
						
							
							//EMRO fields to create basic object
							picklistheader.setInventoryType("MAINTENANCE");
							picklistheader.setBypassRouting("Y");
							picklistheader.setBuildKit("NO");
							picklistheader.setNoOfPrint(new BigDecimal(1));
							picklistheader.setRequireHour(new BigDecimal(0));
							picklistheader.setRequireMinute(new BigDecimal(0));
							picklistheader.setBinTransfer("N");
							picklistheader.setStatus("OPEN");
							picklistheader.setLocation(component.getPlant());
							picklistheader.setRequireOn(new Date());
							picklistheader.setRequireHour(new BigDecimal(0));
							picklistheader.setRequireMinute(new BigDecimal(0));
							
							try {
								//picklistheader.setPicklist(getPickList(getCon(), Picklong));
								picklistheader.setPicklist(getTransactionNo("PICKLIST").longValue());
							} catch (Exception e1) {
								logger.severe(e1.toString());
							}
						}
						picklistheader.setModifiedDate(new Date());
						picklistheader.setModifiedBy("TRAX_IFACE");
						
						
						picklistheader.setWo(new BigDecimal(wotaskcard.getId().getWo()));
						picklistheader.setTaskCard(wotaskcard.getId().getTaskCard());
						picklistheader.setTaskCardPn(wotaskcard.getId().getPn());
						picklistheader.setTaskCardSn(wotaskcard.getId().getPnSn());
						
						
						String line = component.getReservationItem().replaceAll("0+$", "");
						line = line.replaceFirst("^0+(?!$)", "");
						Long l = Long.parseLong(line);
						
						
						picklistdistdistribu = fillPicklistDistribution(picklistdistdistribu, component, "DISTRIBU","2", picklistheader.getPicklist(),l.longValue() );
						picklistdistrequire = fillPicklistDistribution(picklistdistrequire, component,"REQUIRE","0" ,picklistheader.getPicklist(),l.longValue());
						
						picklistdistdistribu.setTaskCard(wotaskcard.getId().getTaskCard());
						picklistdistrequire.setTaskCard(wotaskcard.getId().getTaskCard());
						picklistdistdistribu.setStatus("OPEN");
						picklistdistrequire.setStatus("OPEN");
						
						
						if(component.getQuantity() !=null && !component.getQuantity().isEmpty()) {
							try 
							{
								picklistdistdistribu.setQty(new BigDecimal(component.getQuantity().trim()));
								picklistdistrequire.setQty(new BigDecimal(component.getQuantity().trim()));
								picklistdistdistribu.setQtyPicked(new BigDecimal(component.getQuantity().trim()));
								picklistdistrequire.setQtyPicked(new BigDecimal(1));
							}
							catch(NumberFormatException e)
							{
								logger.severe("Can not insert/update Material ReservationNumber: "+ component.getReservationNumber() +" Material: " +component.getMaterialNumber()+" ERROR: Quantity");
								error = error.concat("Can not insert/update Material ReservationNumber: "+ component.getReservationNumber() +" Material: " +component.getMaterialNumber()+ " ERROR: Quantity");
								return ;
							}
							
						}
						
						
						
						logger.info("INSERTING picklist header: " + picklistheader.getPicklist() );
						insertData(picklistheader,"picklist header",String.valueOf(picklistheader.getPicklist()) );
						
						logger.info("INSERTING picklist dist: " + picklistdistdistribu.getId().getTransaction()  );
						insertData(picklistdistdistribu,"picklist dist distribu",String.valueOf(picklistheader.getPicklist()));
						
						logger.info("INSERTING picklist require: " + picklistdistrequire.getId().getTransaction() );
						insertData(picklistdistrequire,"picklist dist require",String.valueOf(picklistheader.getPicklist()) );
						
						
					}else 
					{
						
					}
				}
	
		private String findPicklistNumber(String reservationNumber, String resrvationItem) {
			try
			{	
				ArrayList<PicklistDistribution>picklistdist = (ArrayList<PicklistDistribution>) em.createQuery("SELECT p FROM PicklistDistribution p where p.externalCustRes =:pi AND p.id.transaction =:tra AND p.id.distributionLine =:dl")
						.setParameter("pi", reservationNumber)
						.setParameter("tra", "DISTRIBU")
						.setParameter("dl",new Long(2) )
						.getResultList();
				return String.valueOf(picklistdist.get(0).getId().getPicklist());
			}
			catch (Exception e)
			{	
				logger.info("PICKLIST NOT FOUND");
				//logger.severe(e.toString());
			}
			return null;
		}
		
		
		
		
		
		private PicklistDistribution fillPicklistDistribution(PicklistDistribution picklistdist ,  Component mat , String transaction, String DistributionLine, long l, long line) {
			
			try 
			{
				picklistdist = em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pi AND p.id.picklistLine =:li AND p.id.transaction =:tra AND p.id.distributionLine =:dl", PicklistDistribution.class)
						.setParameter("pi", l)
						.setParameter("li", line)
						.setParameter("tra", transaction)
						.setParameter("dl", DistributionLine)
						.getSingleResult();
				//exist = true;
			}
			catch(Exception e)
			{
				//EMRO fields to create basic object
				PicklistDistributionPK pk = new PicklistDistributionPK();
				picklistdist = new PicklistDistribution();
				picklistdist.setId(pk);
				picklistdist.setCreatedBy("TRAX_IFACE");
				picklistdist.setCreatedDate(new Date());
				picklistdist.setQtyPicked(new BigDecimal(1));
				picklistdist.setStatus("OPEN");
			}
			picklistdist.setModifiedDate(new Date());
			picklistdist.setModifiedBy("TRAX_IFACE");
			
			picklistdist.setExternalCustRes(mat.getReservationNumber());
			picklistdist.setExternalCustResItem(mat.getReservationItem());
			picklistdist.getId().setPicklist(l );
			picklistdist.getId().setPicklistLine(line);
			picklistdist.getId().setDistributionLine(new Long(DistributionLine));
			picklistdist.getId().setTransaction(transaction);
			
			String pn = mat.getMaterialNumber();
			
			
			
			pn = pn.replaceAll("\"", "IN");
			pn = pn.replaceAll("'", "FT");
			if(!pn.contains(":"))
			{
				pn = pn.concat(":UPLOAD");
			}
			
			picklistdist.setPn(pn);
			
			return picklistdist;
			
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
		
		private String getPN(String PN) {
			try
			{
				PnMaster pnMaster = em.createQuery("Select p From PnMaster p where p.id.pn = :partn", PnMaster.class)
				.setParameter("partn", PN)
				.getSingleResult();
				
				return pnMaster.getPn();
			}
			catch (Exception e)
			{
				
			}
			return null;
		}
		
		
		private Boolean getWoShop(String w) {
			   logger.info("Checking WO " + w);
			   String sql = "SELECT R.MODULE FROM WO R WHERE R.WO = ?";
		       
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
