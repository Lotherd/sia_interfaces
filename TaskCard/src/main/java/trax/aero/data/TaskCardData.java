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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import trax.aero.controller.TaskCardController;
import trax.aero.logger.LogManager;
import trax.aero.model.AcMaster;
import trax.aero.model.AcTypeSeriesMaster;
import trax.aero.model.PnMaster;
import trax.aero.model.SystemTranCode;
import trax.aero.model.TaskCard;
import trax.aero.model.TaskCardControl;
import trax.aero.model.TaskCardControlMaster;
import trax.aero.model.TaskCardControlMasterPK;
import trax.aero.model.TaskCardControlPK;
import trax.aero.model.TaskCardControlRev;
import trax.aero.model.TaskCardControlRevPK;
import trax.aero.model.TaskCardEffectivity;
import trax.aero.model.TaskCardEffectivityHead;
import trax.aero.model.TaskCardEffectivityHeadPK;
import trax.aero.model.TaskCardEffectivityHeadRv;
import trax.aero.model.TaskCardEffectivityHeadRvPK;
import trax.aero.model.TaskCardEffectivityPK;
import trax.aero.model.TaskCardEffectivityRev;
import trax.aero.model.TaskCardEffectivityRevPK;
import trax.aero.model.TaskCardItem;
import trax.aero.model.TaskCardItemPK;
import trax.aero.model.TaskCardItemRev;
import trax.aero.model.TaskCardItemRevPK;
import trax.aero.model.TaskCardPn;
import trax.aero.model.TaskCardPnPK;
import trax.aero.model.TaskCardPnRev;
import trax.aero.model.TaskCardPnRevPK;
import trax.aero.model.TaskCardRev;
import trax.aero.model.TaskCardRevPK;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.EffectivityList;
import trax.aero.pojo.ItemList;
import trax.aero.pojo.MaterialList;
import trax.aero.pojo.PanelList;
import trax.aero.pojo.TaskCards;


public class TaskCardData {

	EntityManagerFactory factory;
	//public InterfaceLockMaster lock;
	
	
	@PersistenceContext(unitName = "ImportDS")	public EntityManager em;
	
	
	String exceuted;
	
	String tasklist;
	public boolean newExist = false;
	
	Logger logger = LogManager.getLogger("TaskCard_I18");
	
	public TaskCardData(EntityManagerFactory factory)
	{
		this.factory = factory;
		em = factory.createEntityManager();

	}
	
	public TaskCardData()
	{
		factory = Persistence.createEntityManagerFactory("ImportDS");
		em = factory.createEntityManager();		
	}
	
	
	public String insertTaskCard(TaskCards taskCards)
	{
		//setting up variables
		TaskCard taskCard = null;
		TaskCardItem taskCardItem = null;
		
		TaskCardRev taskCardRev = null;
		TaskCardItemRev taskCardItemRev = null;
		
		exceuted = "OK";
		ItemList item = new ItemList();
		item.setItemNumber("0010");
		ArrayList<ItemList> ItemListArray = new ArrayList<ItemList>();
		ItemListArray.add(item);
		try 
		{
					newExist = false;
					if(taskCards.getItemList() == null) {
						taskCards.setItemList(ItemListArray);
					}
					
					if(taskCards.getItemList() != null && taskCards != null)
					{
						
						for(ItemList itemList : taskCards.getItemList()) 
						{	
							taskCard = insertTaskCard(itemList,taskCards);
								
							if(taskCard == null) {
								break;
							}
							taskCardRev = insertTaskCardRev(taskCard);
							
							if(taskCards.getItemList() != null && taskCard != null)
							{
								taskCardItem = insertTaskCardItem(itemList,taskCard);
								if(taskCardItem != null) {
									taskCardItemRev	= insertTaskCardItemRev(taskCardItem,taskCardRev);
								}
							}
							if(taskCards.getPanelList() != null  && taskCard != null) 
							{
								for(PanelList panelList : taskCards.getPanelList()) 
								{
									if(panelList.getPanel() != null && taskCard != null) 
									{
										for(String panel : panelList.getPanel())
										{	
											boolean control = insertTaskCardControl(panel, taskCard, taskCards.getZone());
											if(taskCardRev != null && control) {
												insertTaskCardControlRev(panel,taskCardRev,taskCards.getZone());
											}
										}
									}	
								}	
							}
							
							if(itemList.getMaterialList() != null && taskCard != null && taskCardItem != null) 
							{
								for(MaterialList materialList: itemList.getMaterialList()) 
								{	
									TaskCardPn pn = insertTaskCardPN(materialList, taskCard, taskCardItem);
									if(taskCardItemRev != null && pn != null) {
										insertTaskCardPNRev(pn,taskCardItemRev);
									}
									
								}
							}
							
							if(itemList.getEffectivityList() != null  && taskCard != null) {
								for(EffectivityList effectivityList : itemList.getEffectivityList()) 
								{	
									if(effectivityList.getEffectivity() != null && taskCard != null) 
									{	
										for(String effectivity : effectivityList.getEffectivity()) 
										{	
											boolean effect = insertTaskCardEffectivity(effectivity,taskCard);
											if(taskCardRev != null && effect) {
												insertTaskCardEffectivityRev(effectivity, taskCardRev);
											}
											
										}
									}
								}
							}
						}
					}
					if(!exceuted.equalsIgnoreCase("OK")) {
						exceuted = "ERROR";
					}else if(newExist) {
						tasklist = "TaskCard name: "  + taskCard.getTaskCard() + " Group Number: " +taskCards.getGroupNo() + " Task SAP: " +taskCards.getTaskCard();
					}
		}
		catch (Exception e) 
        {
			TaskCardController.addError(e.toString());
			logger.severe(e.toString());
            em.getTransaction().rollback();
            exceuted = e.toString();
		}
		finally
		{
			//clean up 
			//em.clear();	
			//em.close();
		}
		return exceuted;
	}
	
	
	
	//insert a taskcard
	private TaskCard insertTaskCard(ItemList items,TaskCards taskCards) 
	{	
		TaskCard taskCard = null;
		boolean exist = false;
		String taskCardString = null,  DeletionIndicatorString = null;
		String size = "";
		
		
		if(taskCards.getTaskCard() != null && taskCards.getGroupNo() != null && !(taskCards.getTaskCard().isEmpty() && taskCards.getGroupNo().isEmpty())) {
			size  = taskCards.getTaskCard().concat("_" + taskCards.getGroupNo()+"_" +items.getItemNumber());
		}
		
		if(size.length() > 35) {
			taskCardString = taskCards.getTaskCard().substring(0, 21);
			exceuted = "WARNING TaskCard: "+ taskCards.getTaskCard()
			+" Planning plant: " +taskCards.getPlanningPlant() +" Group number:  " 
			+taskCards.getGroupNo()+ " Size: "+size.length() 
			+"  WARNING: total string is longer than 35, Task Card "
			+ taskCards.getTaskCard()+ " is truncated to " + taskCardString ;
			logger.warning(exceuted);
			TaskCardController.addError(exceuted);
			
		}else 
		{
			taskCardString = taskCards.getTaskCard();
		}
		
		//check if object has min values
		if((taskCards.getTaskCard() != null && taskCards.getPlanningPlant() != null && taskCards.getGroupNo() != null) && !(taskCards.getTaskCard().isEmpty() && taskCards.getPlanningPlant().isEmpty() && taskCards.getGroupNo().isEmpty())) 
		{
			logger.info("Converting and appending information for TaskCard: " + taskCardString);
			taskCardString = taskCardString.concat("_" + taskCards.getGroupNo()+"_" +items.getItemNumber());
			
			DeletionIndicatorString = checkDeleteIndicator(taskCards.getDeletionIndicator());
			try 
			{
				taskCard = em.createQuery("Select t From TaskCard t where t.id.taskCard = :taskc", TaskCard.class)
						.setParameter("taskc", taskCardString)
						.getSingleResult();
				exist = true;
			}
			catch(Exception e)
			{
				taskCard = new TaskCard();
				
				
				taskCard.setCreatedDate(new Date());
				taskCard.setCreatedBy("TRAX_IFACE");
				
				newExist = true;
				
				//EMRO fields to create basic object
				taskCard.setTcSub(taskCardString);
				taskCard.setTcAcType("ALL");
				taskCard.setControlArea("E/C");
				
				try
				{
					String company = (String) this.em.createQuery("select p.profile from ProfileMaster p")
							.getSingleResult();
					taskCard.setTcCompany(company);
				}
				catch(Exception e1) {
					exceuted = "Can not insert/update Task Card: "+ taskCardString +" as ERROR: Company could not be found";
					logger.severe(exceuted);
					TaskCardController.addError(exceuted);
					taskCard = null;
					return taskCard;
				}
				
				
				
				taskCard.setAuthorization("ACCEPTED");
				taskCard.setAuthorizedBy("TRAX_IFACE");
				taskCard.setAuthorizedDate(new Date());
				
				taskCard.setSellMaterialMethod("STANDARD");
				taskCard.setSellMaterialAmount(new BigDecimal(0));
				taskCard.setSellLaborMethod("STANDARD");
				taskCard.setSellLaborAmount(new BigDecimal(0));
				taskCard.setSellOtherMethod("STANDARD");
				taskCard.setSellOtherAmount(new BigDecimal(0));
				taskCard.setStructureRepair("NONE");
				taskCard.setStructureRepairClass("OTHER");
				taskCard.setBasicEmptyWeight(new BigDecimal(0));
				taskCard.setTotalMoment(new BigDecimal(0));
				taskCard.setWeightOff(new BigDecimal(0));
				taskCard.setAircraftJacked("NO");
				taskCard.setElectricalPowerReq("OPTIONAL");
				taskCard.setHydraulicPowerReq("OPTIONAL");
				taskCard.setWeightBalance("N");
				taskCard.setEditorUsed("TASKCARD");
				taskCard.setWoStatus("OPEN");
				taskCard.setWeightOn(new BigDecimal(0));
				taskCard.setPaperRequired("YES");
				
				
			}
			
			
			
			
			taskCard.setTaskCard(taskCardString);
			
			taskCard.setModifiedBy("TRAX_IFACE");
			taskCard.setModifiedDate(new Date());
			
				if(taskCards.getRevision() != null && !taskCards.getRevision().isEmpty()) 
				{
					taskCard.setRevison(taskCards.getRevision());
				}
				else if(exist)
				{	
				
				}
				else 
				{
					taskCard.setRevison("0");
				}
				
				
				if(taskCards.getCategory() != null && !taskCards.getCategory().isEmpty() && getCategory(taskCards.getCategory()) != null) 
				{
					taskCard.setTaskCardCategory(taskCards.getCategory());
				}
				else if(exist)
				{	
					
				}
				else 
				{
					exceuted = "Can not insert/update Task Card: "+ taskCardString +" Category: "  +taskCards.getCategory()+ " ERROR: Category is null or does not exist ";
					logger.severe(exceuted);
					TaskCardController.addError(exceuted);
					taskCard = null;
					return taskCard;
				}
				
				if(DeletionIndicatorString != null && !DeletionIndicatorString.isEmpty() && !DeletionIndicatorString.equalsIgnoreCase("ERROR")) 
				{
					taskCard.setStatus(DeletionIndicatorString);
				}
				else if(exist)
				{	
					
				}
				else 
				{
					exceuted = "Can not insert/update Task Card: "+ taskCardString +" Deletion Indicator: "  +DeletionIndicatorString+  " ERROR: Deletion Indicator ";
					logger.severe(exceuted);
					TaskCardController.addError(exceuted);
					taskCard = null;
					return taskCard;
				}
				

				//change if there is new data
				if(taskCards.getDescription() != null && !taskCards.getDescription().isEmpty()) 
				{
					taskCard.setTaskCardDescription(taskCards.getDescription());
				}
				if(taskCards.getArea() != null && !taskCards.getArea().isEmpty()) 
				{
					//taskCard.setArea(taskCards.getArea());
				}
				
				taskCard.setRevisedDate(new Date());
				taskCard.setRevisedBy("TRAX_IFACE");
				
				logger.info("INSERTING TaskCard: " + taskCardString);
				insertData(taskCard, "TaskCard",taskCardString);
				
		}else 
		{
			exceuted = "Can not insert/update TaskCard: "+ taskCards.getTaskCard()+" Planning plant: " +taskCards.getPlanningPlant() +" Group number:  " +taskCards.getGroupNo()+ " Size: "+size.length() +" as ERROR: missing Task Card or Planning plant or group number";
			logger.severe(exceuted);
			TaskCardController.addError(exceuted);
			taskCard = null;
		}
		return taskCard;
	}
	
	//insert task card item
	private TaskCardItem insertTaskCardItem(ItemList items, TaskCard taskCard) 
	{
		TaskCardItem taskCardItem = null ;
		if(items.getItemNumber() != null && !items.getItemNumber().isEmpty())
		{
			try 
			{
				taskCardItem = em.createQuery("Select t From TaskCardItem t where t.id.taskCardItem = :item and t.id.taskCard = :taskc", TaskCardItem.class)
						.setParameter("item", new Long(1).longValue())
						.setParameter("taskc", taskCard.getTaskCard())
						.getSingleResult();
			}
			catch(Exception e)
			{
				
				TaskCardItemPK pk = new TaskCardItemPK();
				taskCardItem = new TaskCardItem();
				taskCardItem.setId(pk);
				taskCardItem.setCreatedDate(new Date());
				taskCardItem.setCreatedBy("TRAX_IFACE");
				
				//EMRO fields to create basic object
				taskCardItem.setMechanic("N");
				taskCardItem.setInspector("N");
				taskCardItem.setDuplicateInspection("N");
				taskCardItem.setInspectorManHours(new BigDecimal(0));
				taskCardItem.setInspectorManRequire(new BigDecimal(1));
				taskCardItem.setManHours(new BigDecimal(0));
				taskCardItem.setManRequire(new BigDecimal(1));
				taskCardItem.setDualInspectorManHours(new BigDecimal(0));
				taskCardItem.setDualInspectorManRequire(new BigDecimal(1));			
				taskCardItem.setInformationalOnly("N");
				taskCardItem.setMainSkill("YES");
				

			}
			
			taskCardItem.setModifiedBy("TRAX_IFACE");
			taskCardItem.setModifiedDate(new Date());
				
			taskCardItem.getId().setTaskCard(taskCard.getTaskCard());
			taskCardItem.setOpsNo(items.getItemNumber());	
			taskCardItem.setExternalCustRef(items.getItemNumber());	
			taskCardItem.getId().setTaskCardItem(1);
				
				
			if(items.getItemText() != null && !items.getItemText().isEmpty()) 
			{
				taskCardItem.setTaskCardText(items.getItemText());
			}
				

				
			logger.info("INSERTING TaskCard item: " + taskCardItem.getId().getTaskCardItem() + " ,ops No:" + taskCardItem.getOpsNo() + " ,Task Card: " + taskCardItem.getId().getTaskCard());
			insertData(taskCardItem,"TaskCard item",items.getItemNumber());
			
			try 
			{
				List<TaskCardItem> Items = em.createQuery("Select t From TaskCardItem t where t.opsNo = :item and t.id.taskCard = :taskc")
						.setParameter("item", items.getItemNumber())
						.setParameter("taskc", taskCard.getTaskCard())
						.getResultList();
				
				for(TaskCardItem i : Items) {
					i.setModifiedBy("TRAX_IFACE");
					i.setModifiedDate(new Date());
					if(items.getItemText() != null && !items.getItemText().isEmpty()) 
					{
						i.setTaskCardText(items.getItemText());
					}
					logger.info("UPDATING TaskCard item Ops No: " + i.getOpsNo() + " ,Item Number:" + i.getId().getTaskCardItem() + " ,Task Card: " + i.getId().getTaskCard());
					insertData(i,"TaskCard item",items.getItemNumber());
				}
			}catch(Exception e)
			{
					
			}
			
		}
		return  taskCardItem;
	}
	
	//insert task card control 
	private boolean insertTaskCardControl(String panel, TaskCard taskCard, String zone) //, TaskCard taskCard, TaskCardItem taskCardItem
	{
		
		if(panel != null && !panel.isEmpty()) {
			logger.info("Converting and appending information for panel: " + panel);
			TaskCardControl taskCardControl = null;
			TaskCardControlMaster taskCardControlMaster = null;
			TaskCardControl taskCardControlZone = null;
			TaskCardControlMaster taskCardControlMasterZone = null;
			ArrayList<String> arrayPanel = parseString(panel,"/");
			if(arrayPanel.size() == 3) 
			{
				String acType= getStringFromDash(arrayPanel,0,0);
				String acSeries = getStringFromDash(arrayPanel,0,1);
				String code = getStringFromDash(arrayPanel,1,2);	
				
				try 
				{
					taskCardControl = em.createQuery("Select t From TaskCardControl t where "
							+ "t.id.controlCategory = :controlcat and t.id.acType = :act and t.id.acSeries = :acs and t.id.code = :cod "
							+ "and t.id.taskCardItem = :item and t.id.taskCard = :taskc", TaskCardControl.class)
							.setParameter("controlcat", "PANEL")
							.setParameter("act", acType)
							.setParameter("acs", acSeries)
							.setParameter("cod", code)
							.setParameter("item", new Long(0).longValue())
							.setParameter("taskc", taskCard.getTaskCard())
							.getSingleResult();
				}
				catch(Exception e)
				{
					
					TaskCardControlPK pk = new TaskCardControlPK();
					taskCardControl = new TaskCardControl();
					taskCardControl.setId(pk);
					taskCardControl.setCreatedDate(new Date());
					taskCardControl.setCreatedBy("TRAX_IFACE");
				}
				taskCardControl.setModifiedDate(new Date());
				taskCardControl.setModifiedBy("TRAX_IFACE");
				try 
				{
					taskCardControlMaster = em.createQuery("Select t From TaskCardControlMaster t where "
							+ "t.id.controlCategory = :controlcat and t.id.acType = :act and t.id.acSeries = :acs and t.id.code = :cod "
							, TaskCardControlMaster.class)
							.setParameter("controlcat", "PANEL")
							.setParameter("act", acType)
							.setParameter("acs", acSeries)
							.setParameter("cod", code)
							.getSingleResult();
				}
				catch(Exception e)
				{
					TaskCardControlMasterPK pk = new TaskCardControlMasterPK();
					taskCardControlMaster = new TaskCardControlMaster();
					taskCardControlMaster.setId(pk);
					taskCardControlMaster.setCreatedDate(new Date());
					taskCardControlMaster.setCreatedBy("TRAX_IFACE");
					taskCardControlMaster.setManHours(new BigDecimal(0));
					taskCardControlMaster.setManRequire(new BigDecimal(0));
				}
				taskCardControlMaster.setModifiedDate(new Date());
				taskCardControlMaster.setModifiedBy("TRAX_IFACE");
				if( zone != null && !zone.isEmpty()) {
					try 
					{
						taskCardControlZone = em.createQuery("Select t From TaskCardControl t where "
								+ "t.id.controlCategory = :controlcat and t.id.acType = :act and t.id.acSeries = :acs and t.id.code = :cod "
								+ "and t.id.taskCardItem = :item and t.id.taskCard = :taskc", TaskCardControl.class)
								.setParameter("controlcat", "ZONE")
								.setParameter("act", acType)
								.setParameter("acs", acSeries)
								.setParameter("cod", zone)
								.setParameter("item", new Long(0).longValue())
								.setParameter("taskc", taskCard.getTaskCard())
								.getSingleResult();
					}
					catch(Exception e)
					{
						
						TaskCardControlPK pk = new TaskCardControlPK();
						taskCardControlZone = new TaskCardControl();
						taskCardControlZone.setId(pk);
						taskCardControlZone.setCreatedDate(new Date());
						taskCardControlZone.setCreatedBy("TRAX_IFACE");
					}
					taskCardControlZone.setModifiedDate(new Date());
					taskCardControlZone.setModifiedBy("TRAX_IFACE");
					try 
					{
						taskCardControlMasterZone = em.createQuery("Select t From TaskCardControlMaster t where "
								+ "t.id.controlCategory = :controlcat and t.id.acType = :act and t.id.acSeries = :acs and t.id.code = :cod "
								, TaskCardControlMaster.class)
								.setParameter("controlcat", "ZONE")
								.setParameter("act", acType)
								.setParameter("acs", acSeries)
								.setParameter("cod", zone)
								.getSingleResult();
					}
					catch(Exception e)
					{
						TaskCardControlMasterPK pk = new TaskCardControlMasterPK();
						taskCardControlMasterZone = new TaskCardControlMaster();
						taskCardControlMasterZone.setId(pk);
						taskCardControlMasterZone.setCreatedDate(new Date());
						taskCardControlMasterZone.setCreatedBy("TRAX_IFACE");
						
						taskCardControlMasterZone.setManHours(new BigDecimal(0));
						taskCardControlMasterZone.setManRequire(new BigDecimal(0));
					}
					taskCardControlMasterZone.setModifiedDate(new Date());
					taskCardControlMasterZone.setModifiedBy("TRAX_IFACE");
				}
				
				taskCardControl.getId().setTaskCard(taskCard.getTaskCard());
				taskCardControl.getId().setTaskCardItem(0);
				
						
				
				taskCardControlMaster.getId().setCode(code);
				taskCardControlMaster.getId().setAcType(acType);
				taskCardControlMaster.getId().setAcSeries(acSeries);

				taskCardControlMaster.getId().setControlCategory("PANEL");
				

				taskCardControl.getId().setCode(code);
				taskCardControl.getId().setAcType(acType);
				taskCardControl.getId().setAcSeries(acSeries);
				
				taskCardControl.getId().setControlCategory("PANEL");
				logger.info("INSERTING Panel: " + panel);
				insertData(taskCardControlMaster,"Panel",panel);
				insertData(taskCardControl,"Panel",panel);	
				
				if( zone != null && !zone.isEmpty() ) {
					taskCardControlMasterZone.getId().setCode(zone);
					taskCardControlMasterZone.getId().setAcType(acType);
					taskCardControlMasterZone.getId().setAcSeries(acSeries);
	
					taskCardControlMasterZone.getId().setControlCategory("ZONE");
				
					taskCardControlZone.getId().setTaskCard(taskCard.getTaskCard());
					taskCardControlZone.getId().setTaskCardItem(0);
					taskCardControlZone.getId().setCode(zone);
					taskCardControlZone.getId().setAcType(acType);
					taskCardControlZone.getId().setAcSeries(acSeries);
				
					taskCardControlZone.getId().setControlCategory("ZONE");
					logger.info("INSERTING Zone: " + zone);
					insertData(taskCardControlMasterZone,"Zone",zone);
					insertData(taskCardControlZone,"Zone",zone);
				}
			}
			else 
			{
				exceuted = "Can not insert/update Panel: "+panel+ " Task Card: " +taskCard.getTaskCard()+" as Panel was incorrect format";
				logger.severe(exceuted);
				TaskCardController.addError(exceuted);
				return false;
			}
			return true;
		}
		return false;
	}
	
	
	//insert task card pn
	private TaskCardPn insertTaskCardPN(MaterialList material, TaskCard taskCard, TaskCardItem taskCardItem) 
	{
		TaskCardPn taskCardPn = null;
		if(material.getPartNumber_Tool() != null && !material.getPartNumber_Tool().isEmpty()) 
		{
			
			logger.info("Converting and appending information for material: " + material.getPartNumber_Tool());
			String partNumber_Tool , part_ToolIndicator;
			partNumber_Tool = material.getPartNumber_Tool().replaceAll("\"", "IN");
			partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
			if(!partNumber_Tool.contains(":"))
			{
				partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
			}
			part_ToolIndicator = checkPartToolIndicator(material.getPart_ToolIndicator());
			
			
			boolean exist = false; 
			try 
			{
				taskCardPn = em.createQuery("Select t From TaskCardPn t where t.id.taskCardItem = :item and t.id.taskCard = :taskc and t.id.pn = :taskpn", TaskCardPn.class)
						.setParameter("item", taskCardItem.getId().getTaskCardItem())
						.setParameter("taskc", taskCard.getTaskCard())
						.setParameter("taskpn",partNumber_Tool)
						.getSingleResult();
				exist = true;
			}
			catch(Exception e)
			{
				TaskCardPnPK pk = new TaskCardPnPK();
				taskCardPn = new TaskCardPn();
				taskCardPn.setId(pk);
				taskCardPn.setCreatedBy("TRAX_IFACE");
				taskCardPn.setCreatedDate(new Date());
				//EMRO fields to create basic object
				taskCardPn.setReserve("YES");
				taskCardPn.setTagType("NONE");
				
			}
			taskCardPn.setModifiedDate(new Date());
			taskCardPn.setModifiedBy("TRAX_IFACE");
			
			taskCardPn.getId().setTaskCard(taskCard.getTaskCard());
			taskCardPn.setTaskCardXRef(taskCard.getTaskCard());
			
				
			
			taskCardPn.getId().setTaskCardItem(taskCardItem.getId().getTaskCardItem());
			
			if(getPN(partNumber_Tool) == null) 
			{
				exceuted = "Can not insert/update TaskCard PN: " + partNumber_Tool  +" Task Card: " +taskCard.getTaskCard()+"  as PN does not exist in PN master";
				logger.severe(exceuted);
				TaskCardController.addError(exceuted);
				taskCardPn = null;
				return taskCardPn;
			}
			else
			{
				taskCardPn.getId().setPn(partNumber_Tool);
			}
		
			
			if(material.getQuantity() != null && !material.getQuantity().isEmpty() && !material.getQuantity().equalsIgnoreCase("0")) 
			{
				try 
				{
					taskCardPn.setQty(BigDecimal.valueOf(Double.valueOf(material.getQuantity())));
				}
				catch(NumberFormatException e)
				{
					exceuted = "Can not insert/update TaskCard PN: " + partNumber_Tool  +" Task Card: " +taskCard.getTaskCard()+" Quantity: " +material.getQuantity()+ " ERROR: Quantity";
					logger.severe(exceuted);
					TaskCardController.addError(exceuted);
					taskCardPn = null;
					return taskCardPn;
				}
			}
			else if(exist)
			{	
				
			}
			else 
			{
				exceuted = "Can not insert/update TaskCard PN: " + partNumber_Tool  +" Task Card: " +taskCard.getTaskCard()+" Quantity: " +material.getQuantity()+ " ERROR: Quantity";
				logger.severe(exceuted);
				TaskCardController.addError(exceuted);
				taskCardPn = null;
				return taskCardPn;
			}
			
			
			
			if(part_ToolIndicator != null && !part_ToolIndicator.isEmpty() && !part_ToolIndicator.equalsIgnoreCase("ERROR")) 
			{
				taskCardPn.setSpare(part_ToolIndicator);
			}
			else if(exist)
			{	
				
			}
			else 
			{
				exceuted = "Can not insert/update TaskCard PN: " + partNumber_Tool +" Task Card: " +taskCard.getTaskCard()+" Part Tool Indicator: " +part_ToolIndicator+ " ERROR: Part Tool Indicator";
				logger.severe(exceuted);
				TaskCardController.addError(exceuted);
				taskCardPn = null;
				return taskCardPn;
			}
			

			logger.info("INSERTING material: " + partNumber_Tool );
			insertData(taskCardPn,"PN",partNumber_Tool);
			return taskCardPn;
		}
		taskCardPn = null;
		return taskCardPn;
		
	}
	
	

	//insert task card effectivity
	private boolean insertTaskCardEffectivity(String effectivity,TaskCard taskCard) 
	{
		if(effectivity != null && !effectivity.isEmpty() && effectivity.contains("E_AC_TAIL_NO"))
		{
			logger.info("Converting and appending information for effectivity: " + effectivity );
			//if(effectivity.contains("'")) 
			//{
			effectivity = effectivity.substring(effectivity.indexOf("'") + 1);
			effectivity = effectivity.substring(0, effectivity.indexOf("'"));		
			//}else 
			//{
			effectivity = effectivity.substring(effectivity.lastIndexOf("_")+1);
			
				String acType, acSeries ;
				acType=  getACType(effectivity);
				acSeries = getACSeries(effectivity);
				//}
				TaskCardEffectivity taskCardEffectivity = null;
				TaskCardEffectivityHead taskCardEffectivityHead = null;
				try 
				{
					taskCardEffectivity = em.createQuery("Select t From TaskCardEffectivity t where t.id.taskCard = :taskc and t.id.ac = :airC", TaskCardEffectivity.class)
							.setParameter("taskc", taskCard.getTaskCard())
							.setParameter("airC", effectivity)
							.getSingleResult();
				}
				catch(Exception e)
				{
					TaskCardEffectivityPK pk = new TaskCardEffectivityPK();
					taskCardEffectivity = new TaskCardEffectivity();
					taskCardEffectivity.setId(pk);
					taskCardEffectivity.setCreatedBy("TRAX_IFACE");
					taskCardEffectivity.setCreatedDate(new Date());
				}
				taskCardEffectivity.setModifiedBy("TRAX_IFACE");
				taskCardEffectivity.setModifiedDate(new Date());
				try 
				{
					taskCardEffectivityHead = em.createQuery("Select t From TaskCardEffectivityHead t where t.id.taskCard = :taskc and t.id.acType = :airT and t.id.acSeries = :airS", TaskCardEffectivityHead.class)
							.setParameter("taskc", taskCard.getTaskCard())
							.setParameter("airT", acType)
							.setParameter("airS", acSeries)
							.getSingleResult();
				}
				catch(Exception e)
				{
					TaskCardEffectivityHeadPK pkHead = new TaskCardEffectivityHeadPK();
					taskCardEffectivityHead = new TaskCardEffectivityHead();
					taskCardEffectivityHead.setId(pkHead);
					taskCardEffectivityHead.setCreatedBy("TRAX_IFACE");
					taskCardEffectivityHead.setCreatedDate(new Date());
				}
				taskCardEffectivityHead.setModifiedBy("TRAX_IFACE");
				taskCardEffectivityHead.setModifiedDate(new Date());
				
				
				taskCardEffectivity.getId().setTaskCard(taskCard.getTaskCard());
				taskCardEffectivityHead.getId().setTaskCard(taskCard.getTaskCard());
				
				if (getAC(effectivity) == null) 
				{
					exceuted = "Can not insert/update Effectivity: " +effectivity+" Task Card: " +taskCard.getTaskCard()+" as AC does not exist";
					logger.severe(exceuted);
					TaskCardController.addError(exceuted);
					return false;
				}
				else 
				{
					taskCardEffectivityHead.getId().setAcSeries(acSeries);
					taskCardEffectivityHead.getId().setAcType(acType);
					taskCardEffectivity.getId().setAc(effectivity);
				}
				//EMRO fields to create basic object
				taskCardEffectivity.setSelect("Y");
				taskCardEffectivityHead.setSelect("YES");
				taskCardEffectivityHead.setOverride("Y");
				
				
				
				logger.info("INSERTING Effectivity: " + effectivity);
				insertData(taskCardEffectivityHead,"Effectivity",effectivity);	
				logger.info("INSERTING EffectivityHead: " + acSeries + " " + acType);
				insertData(taskCardEffectivity,"Effectivity",acSeries + " " + acType);	
			
			}else {
				
				logger.info("Converting and appending information for effectivity: " + effectivity );
				effectivity = effectivity.substring(effectivity.indexOf("'") + 1);
				effectivity = effectivity.substring(0, effectivity.indexOf("'"));		
				effectivity = effectivity.substring(effectivity.lastIndexOf("_")+1);
				
				
				String acType, acSeries ;
				
				ArrayList<String> arrayAC = new ArrayList<String>();
				 arrayAC = parseString(effectivity,"-");
				
				
				 acType = getStringFromDash(arrayAC,0,0);
				 acSeries = getStringFromDash(arrayAC,0,1);
				
				if(getTypeSeries(acType, acSeries) !=null) {
				
					TaskCardEffectivity taskCardEffectivity = null;
					TaskCardEffectivityHead taskCardEffectivityHead = null;
					
					
					List<AcMaster> ACs = null;
					try
					{
						ACs = getTypeSeries(acType, acSeries).getAcMasters();
						
					}catch(Exception e)
					{
						e.printStackTrace();
						logger.severe(e.toString());
						return false;
					}
					
					try 
					{
						taskCardEffectivityHead = em.createQuery("Select t From TaskCardEffectivityHead t where t.id.taskCard = :taskc and t.id.acType = :airT and t.id.acSeries = :airS", TaskCardEffectivityHead.class)
								.setParameter("taskc", taskCard.getTaskCard())
								.setParameter("airT", acType)
								.setParameter("airS", acSeries)
								.getSingleResult();
					}
					catch(Exception e)
					{
						TaskCardEffectivityHeadPK pkHead = new TaskCardEffectivityHeadPK();
						taskCardEffectivityHead = new TaskCardEffectivityHead();
						taskCardEffectivityHead.setId(pkHead);
						//EMRO fields to create basic object
						taskCardEffectivityHead.setCreatedBy("TRAX_IFACE");
						taskCardEffectivityHead.setCreatedDate(new Date());
						taskCardEffectivityHead.setSelect("YES");
						taskCardEffectivityHead.setOverride("Y");
					}
					taskCardEffectivityHead.setModifiedBy("TRAX_IFACE");
					taskCardEffectivityHead.setModifiedDate(new Date());
					
					
					
					taskCardEffectivityHead.getId().setTaskCard(taskCard.getTaskCard());
					taskCardEffectivityHead.getId().setAcSeries(acSeries);
					taskCardEffectivityHead.getId().setAcType(acType);
						
					
					
					logger.info("INSERTING EffectivityHead: " + acSeries + " " + acType);
					insertData(taskCardEffectivityHead,"Effectivity",effectivity);	
					
					
					logger.info("INSERTING Effectivity AC SIZE: " + ACs.size());
					
					for(AcMaster ac: ACs) {
						try 
						{
							taskCardEffectivity = em.createQuery("Select t From TaskCardEffectivity t where t.id.taskCard = :taskc and t.id.ac = :airC", TaskCardEffectivity.class)
									.setParameter("taskc", taskCard.getTaskCard())
									.setParameter("airC", ac.getAc())
									.getSingleResult();
						}
						catch(Exception e)
						{
							TaskCardEffectivityPK pk = new TaskCardEffectivityPK();
							taskCardEffectivity = new TaskCardEffectivity();
							taskCardEffectivity.setId(pk);
							taskCardEffectivity.setCreatedBy("TRAX_IFACE");
							taskCardEffectivity.setCreatedDate(new Date());
							taskCardEffectivity.setSelect("Y");
						}
						taskCardEffectivity.setModifiedBy("TRAX_IFACE");
						taskCardEffectivity.setModifiedDate(new Date());
						
						
						taskCardEffectivity.getId().setAc(ac.getAc());
						taskCardEffectivity.getId().setTaskCard(taskCard.getTaskCard());
						//logger.info("INSERTING Effectivity AC: " + ac.getAc());
						insertData(taskCardEffectivity,"Effectivity",ac.getAc());	
					}
				
				}else {
					exceuted = "Can not insert/update Effectivity: " +effectivity+" Task Card: " +taskCard.getTaskCard()+" as Type and Series does not exist";
					logger.severe(exceuted);
					TaskCardController.addError(exceuted);
					return false;
				}
				
			}
		return true;
		
	}
		
	//****************** check if items exist on DB functions ******************
	
	// get pn from pn master used to check if pn exist
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
	
	
	// get ac from ac master used to check if ac exist
	private String getAC(String AC) {
		try
		{	
			AcMaster acMaster = em.createQuery("Select a From AcMaster a where a.id.ac = :airC", AcMaster.class)
			.setParameter("airC", AC)
			.getSingleResult();
					
			return acMaster.getAc();
		}
		catch (Exception e)
		{
			
		}
		return null;
	}
	
	// get ac from ac master used to check if ac exist
		private String getACType(String AC) {
			try
			{	
				AcMaster acMaster = em.createQuery("Select a From AcMaster a where a.id.ac = :airC", AcMaster.class)
				.setParameter("airC", AC)
				.getSingleResult();
						
				return acMaster.getAcTypeSeriesMaster().getId().getAcType();
			}
			catch (Exception e)
			{
				
			}
			return null;
		}
		
		// get ac from ac master used to check if ac exist
		private String getACSeries(String AC) {
			try
			{	
				AcMaster acMaster = em.createQuery("Select a From AcMaster a where a.id.ac = :airC", AcMaster.class)
				.setParameter("airC", AC)
				.getSingleResult();
						
				return acMaster.getAcTypeSeriesMaster().getId().getAcSeries();
			}
			catch (Exception e)
			{
				
			}
			return null;
		}
		
		
		// get used to check if ac type and series exist
		private AcTypeSeriesMaster getTypeSeries(String type,String series) {
			try
			{	
				AcTypeSeriesMaster acTypeSeriesMaster = em.createQuery("Select a From AcTypeSeriesMaster a where a.id.acType = :type and a.id.acSeries = :series", AcTypeSeriesMaster.class)
					.setParameter("type", type)
					.setParameter("series", series)
					.getSingleResult();
								
					return acTypeSeriesMaster;
			}
			catch (Exception e)
			{
				
			}
			return null;
			}
	
	// get category from SystemTranCode used to check if category exist
	private String getCategory(String Category) {
		try
		{	
			SystemTranCode systemTranCode = em.createQuery("Select s From SystemTranCode s where s.id.systemCode = :cat and s.id.systemTransaction = :systran", SystemTranCode.class)
			.setParameter("cat", Category)
			.setParameter("systran", "TASKCARDCATEGORY")
			.getSingleResult();
					
			return systemTranCode.getId().getSystemCode();
		}
		catch (Exception e)
		{
			
		}
		return null;
	}
	
	// get code from task card control master used to check if code exist
	private String getCode(String acType,String acSeries,String code) {
		try
		{	
			TaskCardControlMaster taskCardControlMaster = em.createQuery("Select t From TaskCardControlMaster t where "
					+ "t.id.controlCategory = :controlcat and t.id.acType = :act and t.id.acSeries = :acs and t.id.code = :cod", TaskCardControlMaster.class)
					.setParameter("controlcat", "PANEL")
					.setParameter("act", acType)
					.setParameter("acs", acSeries)
					.setParameter("cod", code)
			.getSingleResult();
						
			return taskCardControlMaster.getId().getCode();
		}
		catch (Exception e)
		{
				
		}
		return null;
	}
	
	//****************** Helper functions ******************
	
	//modify Part Tool Indicator
	private String checkPartToolIndicator(String partToolIndicator) 
	{
		if(partToolIndicator == null)
		{
			partToolIndicator = "SPARE";
		}
		else if(partToolIndicator.isEmpty())
		{
			partToolIndicator = "SPARE";
		}
		else if(partToolIndicator.equalsIgnoreCase("Tool")) 
		{
			partToolIndicator = "OTHER";
		}
		else
		{
			partToolIndicator = "ERROR";
		}
		return partToolIndicator;
	}
	
	//modify delete indicator
	private String checkDeleteIndicator(String deleteIndicator) {
		
		
		if(deleteIndicator == null)  
		{
			deleteIndicator = "OPEN";
		}
		else if(deleteIndicator.isEmpty())  
		{
			deleteIndicator = "OPEN";
		}
		else if(deleteIndicator.equalsIgnoreCase("FALSE"))
		{
			deleteIndicator = "OPEN";
		}
		else if(deleteIndicator.equalsIgnoreCase("TRUE")) 
		{	
			deleteIndicator = "CANCEL";
		}
		else if(deleteIndicator.equalsIgnoreCase("X")) 
		{	
			deleteIndicator = "CANCEL";
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
	private <T> void insertData( T data, String type, String name) 
	{
		try 
		{	
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
				em.merge(data);
			em.getTransaction().commit();
		}catch (Exception e)
		{
			exceuted = "insertData has encountered an Exception with inserting type: " + type +" Name: "+name +"Exception: "+e.toString();
			TaskCardController.addError(exceuted);
			logger.severe(e.toString());
		}
	}
	
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
			logger.severe(e.toString());
		}
	}
	
	String removeLeadingZeroes(String s) {
	    return s.replaceAll("^0+(?!$)", "");
	}

	String removeTrailingZeroes(String s) {
	    return s.replaceAll("(?!^)0+$", "");
	}


	public String getTaskList() {
		return tasklist;
	}
	public void setTaskList(String TaskList) {
		this.tasklist = TaskList;
	}
	
	
	// REV STUFF
	
		private TaskCardRev insertTaskCardRev(TaskCard taskCard) 
		{	
			
			TaskCardRev taskCardRev = null;
			
				try 
				{
					taskCardRev = em.createQuery("Select t From taskCardRev t where t.id.taskCard = :taskc and t.id.revison = :rev", TaskCardRev.class)
							.setParameter("taskc", taskCard.getTaskCard())
							.setParameter("rev", taskCard.getRevison())
							.getSingleResult();
					
				}
				catch(Exception e)
				{
					taskCardRev = new TaskCardRev();
					taskCardRev.setCreatedDate(new Date());
					taskCardRev.setCreatedBy("TRAX_IFACE");
					//EMRO fields to create basic object
					taskCardRev.setTcSub( taskCard.getTaskCard());
					taskCardRev.setTcAcType("ALL");
					taskCardRev.setControlArea("E/C");

					taskCardRev.setTcCompany(taskCard.getTcCompany());
					taskCardRev.setAuthorization("ACCEPTED");
					taskCardRev.setAuthorizedBy("TRAX_IFACE");
					taskCardRev.setAuthorizedDate(new Date());
					
					taskCardRev.setSellMaterialMethod("STANDARD");
					taskCardRev.setSellMaterialAmount(new BigDecimal(0));
					taskCardRev.setSellLaborMethod("STANDARD");
					taskCardRev.setSellLaborAmount(new BigDecimal(0));
					taskCardRev.setSellOtherMethod("STANDARD");
					taskCardRev.setSellOtherAmount(new BigDecimal(0));
					taskCardRev.setStructureRepair("NONE");
					taskCardRev.setStructureRepairClass("OTHER");
					taskCardRev.setBasicEmptyWeight(new BigDecimal(0));
					taskCardRev.setTotalMoment(new BigDecimal(0));
					taskCardRev.setWeightOff(new BigDecimal(0));
					taskCardRev.setAircraftJacked("NO");
					taskCardRev.setElectricalPowerReq("OPTIONAL");
					taskCardRev.setHydraulicPowerReq("OPTIONAL");
					taskCardRev.setWeightBalance("N");
					taskCardRev.setEditorUsed("TASKCARD");
					taskCardRev.setWoStatus("OPEN");
					taskCardRev.setWeightOn(new BigDecimal(0));
					taskCardRev.setPaperRequired("YES");
					
					TaskCardRevPK pk = new TaskCardRevPK();
					taskCardRev.setId(pk);
					
				}
				
				
				
					taskCardRev.getId().setTaskCard( taskCard.getTaskCard());
					
					taskCardRev.setModifiedBy("TRAX_IFACE");
					taskCardRev.setModifiedDate(new Date());
					taskCardRev.getId().setRevison(taskCard.getRevison());
					taskCardRev.setTaskCardCategory(taskCard.getTaskCardCategory());		
					taskCardRev.setStatus(taskCard.getStatus());	
					taskCardRev.setTaskCardDescription(taskCard.getTaskCardDescription());
					taskCardRev.setArea(taskCard.getArea());
						
					taskCardRev.setRevisedDate(new Date());
					taskCardRev.setRevisedBy("TRAX_IFACE");
					logger.info("INSERTING TaskCard REV: " +  taskCard.getTaskCard());
					insertData(taskCardRev, "TaskCard REV", taskCard.getTaskCard());
								
			return taskCardRev;
		}
		
		//insert task card item
		private TaskCardItemRev insertTaskCardItemRev(TaskCardItem item, TaskCardRev taskCardRev) 
		{
			TaskCardItemRev taskCardItemrev = null;
			
				try 
				{
					taskCardItemrev = em.createQuery("Select t From TaskCardItemRev t where t.id.taskCardItem = :item and t.id.taskCard = :taskc and t.id.revison = :rev", TaskCardItemRev.class)
							.setParameter("item",item.getId().getTaskCardItem() )
							.setParameter("taskc", item.getId().getTaskCard())
							.setParameter("rev", taskCardRev.getId().getRevison())
							.getSingleResult();
				}
				catch(Exception e)
				{
					TaskCardItemRevPK pk = new TaskCardItemRevPK();
					taskCardItemrev = new TaskCardItemRev();
					taskCardItemrev.setId(pk);
					taskCardItemrev.setCreatedDate(new Date());
					taskCardItemrev.setCreatedBy("TRAX_IFACE");
					
					//EMRO fields to create basic object
					taskCardItemrev.setMechanic("N");
					taskCardItemrev.setInspector("N");
					taskCardItemrev.setDuplicateInspection("N");
					taskCardItemrev.setInspectorManHours(new BigDecimal(0));
					taskCardItemrev.setInspectorManRequire(new BigDecimal(1));
					taskCardItemrev.setManHours(new BigDecimal(0));
					taskCardItemrev.setManRequire(new BigDecimal(1));
					taskCardItemrev.setDualInspectorManHours(new BigDecimal(0));
					taskCardItemrev.setDualInspectorManRequire(new BigDecimal(1));			
					//taskCardItem.setSortItem(BigDecimal.valueOf(Double.valueOf(items.getItemNumber())));
					taskCardItemrev.setInformationalOnly("N");
					taskCardItemrev.setMainSkill("YES");
					

				}
				taskCardItemrev.getId().setRevisionCode(taskCardRev.getId().getRevison());
				
				taskCardItemrev.setModifiedBy("TRAX_IFACE");
				taskCardItemrev.setModifiedDate(new Date());
					
				taskCardItemrev.getId().setTaskCard(taskCardRev.getId().getTaskCard());
				taskCardItemrev.setOpsNo(item.getOpsNo());	
				taskCardItemrev.getId().setTaskCardItem(item.getId().getTaskCardItem());
				taskCardItemrev.setTaskCardText(item.getTaskCardText());
				logger.info("INSERTING TaskCard item REV: " + taskCardItemrev.getId().getTaskCardItem());
				insertData(taskCardItemrev,"TaskCard itemREV",String.valueOf(taskCardItemrev.getId().getTaskCardItem()));
			
			return  taskCardItemrev;
		}
		
		//insert task card control 
		private void insertTaskCardControlRev(String panel, TaskCardRev taskCardRev, String zone) //, TaskCard taskCard, TaskCardItem taskCardItem
		{
			
				TaskCardControlRev taskCardControlRev = null;
				
				TaskCardControlRev taskCardControlZoneRev = null;
				
				ArrayList<String> arrayPanel = parseString(panel,"/");
				if(arrayPanel.size() == 3) 
				{
					String acType= getStringFromDash(arrayPanel,0,0);
					String acSeries = getStringFromDash(arrayPanel,0,1);
					String code = getStringFromDash(arrayPanel,1,2);	
					
					try 
					{
						taskCardControlRev = em.createQuery("Select t From TaskCardControlRev t where "
								+ "t.id.controlCategory = :controlcat and t.id.acType = :act and t.id.acSeries = :acs and t.id.code = :cod "
								+ "and t.id.taskCardItem = :item and t.id.taskCard = :taskc and t.id.revison = :rev", TaskCardControlRev.class)
								.setParameter("controlcat", "PANEL")
								.setParameter("act", acType)
								.setParameter("acs", acSeries)
								.setParameter("cod", code)
								.setParameter("item", "0")
								.setParameter("taskc", taskCardRev.getId().getTaskCard())
								.setParameter("rev", taskCardRev.getId().getTaskCard())
								.getSingleResult();
					}
					catch(Exception e)
					{
						TaskCardControlRevPK pk = new TaskCardControlRevPK();
						taskCardControlRev = new TaskCardControlRev();
						taskCardControlRev.setId(pk);
						taskCardControlRev.setCreatedDate(new Date());
						taskCardControlRev.setCreatedBy("TRAX_IFACE");
					}
					taskCardControlRev.setModifiedDate(new Date());
					taskCardControlRev.setModifiedBy("TRAX_IFACE");
					
					if( zone != null && !zone.isEmpty()) {
						try 
						{
							taskCardControlZoneRev = em.createQuery("Select t From TaskCardControlRev t where "
									+ "t.id.controlCategory = :controlcat and t.id.acType = :act and t.id.acSeries = :acs and t.id.code = :cod "
									+ "and t.id.taskCardItem = :item and t.id.taskCard = :taskc and t.id.revison = :rev", TaskCardControlRev.class)
									.setParameter("controlcat", "ZONE")
									.setParameter("act", acType)
									.setParameter("acs", acSeries)
									.setParameter("cod", zone)
									.setParameter("item", "0")
									.setParameter("taskc", taskCardRev.getId().getTaskCard())
									.setParameter("rev", taskCardRev.getId().getTaskCard())
									.getSingleResult();
						}
						catch(Exception e)
						{
							TaskCardControlRevPK pk = new TaskCardControlRevPK();
							taskCardControlZoneRev = new TaskCardControlRev();
							taskCardControlZoneRev.setId(pk);
							taskCardControlZoneRev.setCreatedDate(new Date());
							taskCardControlZoneRev.setCreatedBy("TRAX_IFACE");
						}
						taskCardControlZoneRev.setModifiedDate(new Date());
						taskCardControlZoneRev.setModifiedBy("TRAX_IFACE");
						
					
						taskCardControlRev.getId().setTaskCard(taskCardRev.getId().getTaskCard());
						taskCardControlRev.getId().setTaskCardItem(0);
						taskCardControlRev.getId().setRevision(taskCardRev.getId().getRevison());
					
						taskCardControlRev.getId().setCode(code);
						taskCardControlRev.getId().setAcType(acType);
						taskCardControlRev.getId().setAcSeries(acSeries);
					
						taskCardControlRev.getId().setControlCategory("PANEL");
					logger.info("INSERTING Panel REV: " + panel);
					
					insertData(taskCardControlRev,"Panel REV",panel);	
					
					if( zone != null && !zone.isEmpty() ) {
											
						taskCardControlZoneRev.getId().setTaskCard(taskCardRev.getId().getTaskCard());
						taskCardControlZoneRev.getId().setRevision(taskCardRev.getId().getRevison());
						taskCardControlZoneRev.getId().setTaskCardItem(0);
						taskCardControlZoneRev.getId().setCode(zone);
						taskCardControlZoneRev.getId().setAcType(acType);
						taskCardControlZoneRev.getId().setAcSeries(acSeries);
					
						taskCardControlZoneRev.getId().setControlCategory("ZONE");
						logger.info("INSERTING Zone REV: " + zone);
						
						insertData(taskCardControlZoneRev,"Zone REV",zone);
					}	
				}
			}
		}
		
		
		//insert task card pn
		private void insertTaskCardPNRev(TaskCardPn pn, TaskCardItemRev taskCardItemRev) 
		{
			
				TaskCardPnRev taskCardPnRev = null;
				
				try 
				{
					taskCardPnRev = em.createQuery("Select t From TaskCardPnRev t where t.id.taskCardItem = :item and t.id.taskCard = :taskc and t.id.pn = :taskpn and t.id.revison = :rev", TaskCardPnRev.class)
							.setParameter("item", taskCardItemRev.getId().getTaskCardItem())
							.setParameter("taskc", taskCardItemRev.getId().getTaskCard())
							.setParameter("taskpn",pn.getId().getPn())
							.setParameter("rev", taskCardItemRev.getId().getRevisionCode())
							.getSingleResult();
					
				}
				catch(Exception e)
				{
					TaskCardPnRevPK pk = new TaskCardPnRevPK();
					taskCardPnRev = new TaskCardPnRev();
					taskCardPnRev.setId(pk);
					taskCardPnRev.setCreatedBy("TRAX_IFACE");
					taskCardPnRev.setCreatedDate(new Date());
					//EMRO fields to create basic object
					taskCardPnRev.setReserve("YES");
					taskCardPnRev.setTagType("NONE");
					
				}
				taskCardPnRev.setModifiedDate(new Date());
				taskCardPnRev.setModifiedBy("TRAX_IFACE");
				
				taskCardPnRev.getId().setTaskCard(taskCardItemRev.getId().getTaskCard());
				taskCardPnRev.setTaskCardXRef(taskCardItemRev.getId().getTaskCard());
				taskCardPnRev.getId().setRevision(taskCardItemRev.getId().getRevisionCode());
					
				
				taskCardPnRev.getId().setTaskCardItem(taskCardItemRev.getId().getTaskCardItem());
				
				
				taskCardPnRev.getId().setPn(pn.getId().getPn());
				try 
					{
					taskCardPnRev.setQty(pn.getQty());
					}
					catch(NumberFormatException e)
					{
						
				}
				
				
				
				
				
				
				taskCardPnRev.setSpare(pn.getSpare());
				
				
				

				logger.info("INSERTING material REV: " + taskCardPnRev.getId().getPn() );
				insertData(taskCardPnRev,"PN",taskCardPnRev.getId().getPn());
			
		}
		
		

		//insert task card effectivity
		private void insertTaskCardEffectivityRev(String effectivity,TaskCardRev taskCard) 
		{
			if(effectivity != null && !effectivity.isEmpty() && effectivity.contains("E_AC_TAIL_NO"))
			{
				
				effectivity = effectivity.substring(effectivity.indexOf("'") + 1);
				effectivity = effectivity.substring(0, effectivity.indexOf("'"));		
				
				effectivity = effectivity.substring(effectivity.lastIndexOf("_")+1);
				
					String acType, acSeries ;
					acType=  getACType(effectivity);
					acSeries = getACSeries(effectivity);
					
					TaskCardEffectivityRev taskCardEffectivity = null;
					TaskCardEffectivityHeadRv taskCardEffectivityHead = null;
					try 
					{
						taskCardEffectivity = em.createQuery("Select t From TaskCardEffectivityRev t where t.id.taskCard = :taskc and t.id.ac = :airC"
								+ " and t.id.revison = :rev", TaskCardEffectivityRev.class)
								.setParameter("taskc", taskCard.getId().getTaskCard())
								.setParameter("airC", effectivity)
								.setParameter("rev", taskCard.getId().getRevison())
								.getSingleResult();
					}
					catch(Exception e)
					{
						TaskCardEffectivityRevPK pk = new TaskCardEffectivityRevPK();
						taskCardEffectivity = new TaskCardEffectivityRev();
						taskCardEffectivity.setId(pk);
						taskCardEffectivity.setCreatedBy("TRAX_IFACE");
						taskCardEffectivity.setCreatedDate(new Date());
					}
					taskCardEffectivity.setModifiedBy("TRAX_IFACE");
					taskCardEffectivity.setModifiedDate(new Date());
					try 
					{
						taskCardEffectivityHead = em.createQuery("Select t From TaskCardEffectivityHeadRv t where t.id.taskCard = :taskc and t.id.acType = :airT and t.id.acSeries = :airS"
								+ " and t.id.revison = :rev", TaskCardEffectivityHeadRv.class)
								.setParameter("taskc", taskCard.getId().getTaskCard())
								.setParameter("airT", acType)
								.setParameter("airS", acSeries)
								.setParameter("rev", taskCard.getId().getRevison())
								.getSingleResult();
					}
					catch(Exception e)
					{
						TaskCardEffectivityHeadRvPK pkHead = new TaskCardEffectivityHeadRvPK();
						taskCardEffectivityHead = new TaskCardEffectivityHeadRv();
						taskCardEffectivityHead.setId(pkHead);
						taskCardEffectivityHead.setCreatedBy("TRAX_IFACE");
						taskCardEffectivityHead.setCreatedDate(new Date());
					}
					taskCardEffectivityHead.setModifiedBy("TRAX_IFACE");
					taskCardEffectivityHead.setModifiedDate(new Date());
					
					
					taskCardEffectivity.getId().setTaskCard(taskCard.getId().getTaskCard());
					taskCardEffectivityHead.getId().setTaskCard(taskCard.getId().getTaskCard());
					
					
					taskCardEffectivity.getId().setRevision(taskCard.getId().getRevison());
					taskCardEffectivityHead.getId().setRevision(taskCard.getId().getRevison());
					
					
					
					
					taskCardEffectivityHead.getId().setAcSeries(acSeries);
					taskCardEffectivityHead.getId().setAcType(acType);
					taskCardEffectivity.getId().setAc(effectivity);
					
					//EMRO fields to create basic object
					taskCardEffectivity.setSelect("Y");
					taskCardEffectivityHead.setSelect("YES");
					taskCardEffectivityHead.setOverride("Y");
					
					
					
					logger.info("INSERTING Effectivity REV: " + effectivity);
					insertData(taskCardEffectivityHead,"Effectivity",effectivity);	
					logger.info("INSERTING EffectivityHead REV: " + acSeries + " " + acType);
					insertData(taskCardEffectivity,"Effectivity",acSeries + " " + acType);	
				
				}else {
					
					logger.info("Converting and appending information for effectivity REV: " + effectivity );
					effectivity = effectivity.substring(effectivity.indexOf("'") + 1);
					effectivity = effectivity.substring(0, effectivity.indexOf("'"));		
					effectivity = effectivity.substring(effectivity.lastIndexOf("_")+1);
					
					
					String acType, acSeries ;
					
					ArrayList<String> arrayAC = new ArrayList<String>();
					 arrayAC = parseString(effectivity,"-");
					
					
					 acType = getStringFromDash(arrayAC,0,0);
					 acSeries = getStringFromDash(arrayAC,0,1);
					
					if(getTypeSeries(acType, acSeries) !=null) {
					
						TaskCardEffectivityRev taskCardEffectivity = null;
						TaskCardEffectivityHeadRv taskCardEffectivityHead = null;
						
						
						List<AcMaster> ACs = null;
						try
						{
							ACs = getTypeSeries(acType, acSeries).getAcMasters();
							
						}catch(Exception e)
						{
							e.printStackTrace();
							logger.severe(e.toString());
							return;
						}
						
						try 
						{
							taskCardEffectivityHead = em.createQuery("Select t From TaskCardEffectivityHead t where t.id.taskCard = :taskc and t.id.acType = :airT and t.id.acSeries = :airS"
									+ " and t.id.revison = :rev", TaskCardEffectivityHeadRv.class)
									.setParameter("taskc", taskCard.getId().getTaskCard())
									.setParameter("airT", acType)
									.setParameter("airS", acSeries)
									.setParameter("rev", taskCard.getId().getRevison())
									.getSingleResult();
						}
						catch(Exception e)
						{
							TaskCardEffectivityHeadRvPK pkHead = new TaskCardEffectivityHeadRvPK();
							taskCardEffectivityHead = new TaskCardEffectivityHeadRv();
							taskCardEffectivityHead.setId(pkHead);
							//EMRO fields to create basic object
							taskCardEffectivityHead.setCreatedBy("TRAX_IFACE");
							taskCardEffectivityHead.setCreatedDate(new Date());
							taskCardEffectivityHead.setSelect("YES");
							taskCardEffectivityHead.setOverride("Y");
						}
						taskCardEffectivityHead.setModifiedBy("TRAX_IFACE");
						taskCardEffectivityHead.setModifiedDate(new Date());
						
						
						taskCardEffectivityHead.getId().setRevision(taskCard.getId().getRevison());
						taskCardEffectivityHead.getId().setTaskCard(taskCard.getId().getTaskCard());
						taskCardEffectivityHead.getId().setAcSeries(acSeries);
						taskCardEffectivityHead.getId().setAcType(acType);
							
						
						
						logger.info("INSERTING EffectivityHead REV: " + acSeries + " " + acType);
						insertData(taskCardEffectivityHead,"Effectivity",effectivity);	
						
						
						logger.info("INSERTING Effectivity AC SIZE REV: " + ACs.size());
						
						for(AcMaster ac: ACs) {
							try 
							{
								taskCardEffectivity = em.createQuery("Select t From TaskCardEffectivity t where t.id.taskCard = :taskc and t.id.ac = :airC "
										+ "and t.id.revison = :rev", TaskCardEffectivityRev.class)
										.setParameter("taskc", taskCard.getId().getTaskCard())
										.setParameter("airC", ac.getAc())
										.setParameter("rev", taskCard.getId().getRevison())
										.getSingleResult();
							}
							catch(Exception e)
							{
								TaskCardEffectivityRevPK pk = new TaskCardEffectivityRevPK();
								taskCardEffectivity = new TaskCardEffectivityRev();
								taskCardEffectivity.setId(pk);
								taskCardEffectivity.setCreatedBy("TRAX_IFACE");
								taskCardEffectivity.setCreatedDate(new Date());
								taskCardEffectivity.setSelect("Y");
							}
							taskCardEffectivity.setModifiedBy("TRAX_IFACE");
							taskCardEffectivity.setModifiedDate(new Date());
							taskCardEffectivity.getId().setRevision(taskCard.getId().getRevison());
							
							
							taskCardEffectivity.getId().setAc(ac.getAc());
							taskCardEffectivity.getId().setTaskCard(taskCard.getId().getTaskCard());
							//logger.info("INSERTING Effectivity AC: " + ac.getAc());
							insertData(taskCardEffectivity,"Effectivity REV",ac.getAc());	
						}
					
					}else {
						
						return;
					}
					
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
