package trax.aero.data;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import trax.aero.controller.PlannedRFOController;
import trax.aero.interfaces.IPlannedRFOData;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.JournalEntriesExpenditure;
import trax.aero.model.JournalEntriesExpenditurePK;
import trax.aero.model.LocationMaster;
import trax.aero.model.LocationSite;
import trax.aero.model.PicklistDistribution;
import trax.aero.model.PicklistDistributionPK;
import trax.aero.model.PicklistHeader;
import trax.aero.model.PnInventoryDetail;
import trax.aero.model.PnMaster;
import trax.aero.model.Wo;
import trax.aero.model.WoAudit;
import trax.aero.model.WoAuditPK;
import trax.aero.model.WoShopDetail;
import trax.aero.model.WoShopDetailPK;
import trax.aero.model.WoTaskCard;
import trax.aero.model.WoTaskCardItem;
import trax.aero.model.WoTaskCardItemPK;
import trax.aero.model.WoTaskCardPK;
import trax.aero.model.WoTaskCardPn;
import trax.aero.model.WoTaskCardPnPK;
import trax.aero.pojo.Component;
import trax.aero.pojo.MT_TRAX_I61;
import trax.aero.pojo.Operation;
import trax.aero.pojo.Orders;

@Stateless(name="PlannedRFOData" , mappedName="PlannedRFOData")
public class PlannedRFOData implements IPlannedRFOData {

		@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
		
		String exceuted;
		//Wo wo;
		
		
		Logger logger = LogManager.getLogger("PlannedRFO_I61");
		public InterfaceLockMaster lock;
		
		
		public PlannedRFOData()
		{
			
		
			
		}

		
		
		public String insertRFO(MT_TRAX_I61 input)
		{
			
			//setting up variables
			exceuted = "OK";
			Wo wo = null;
			WoTaskCard wotaskcard= null;
			
			Orders o = input.getOrders();
			
			try 
			{
				checkPart(o);
				
				wo = insertShopWo(o);
				if(wo != null && o.getMaterialNumber() != null) {
					
					String partNumber_Tool ;
					partNumber_Tool = o.getMaterialNumber().replaceAll("\"", "IN");
					partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
					if(!partNumber_Tool.contains(":"))
					{
						partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
					}
					o.setMaterialNumber(partNumber_Tool);
					
					
					insertWoShopDetail(o,wo);
					updatePnInventoryDetail(o, o.getSerialNumber());
				}
				if(o.getOperations() != null) {
					for(Operation operation : o.getOperations()) {
						if(operation != null) {
							if(operation.getComponent() != null) {
								for(Component component : operation.getComponent()) {
									if(component.getMaterialNumber() != null) {
										updatePnInventoryDetail(component,o.getSerialNumber());
										if(wo !=null ) {
											insertComponent(o, component, wo);
										}
									}
								}
							}
						}
					}
				}
				
				
				
			}
			catch (Exception e) 
	        {
				e.printStackTrace();
				PlannedRFOController.addError(e.toString());
				logger.severe(e.toString());
	            exceuted = e.toString();
			}
			return exceuted;
		}
		
	

		private void checkPart(Orders o) throws Exception {
			
			if(o.getMaterialNumber() == null && o.getMaterialNumber().isEmpty() ) {
				throw new Exception("Can not insert/update WO SHOP PN: " + o.getMaterialNumber()  +" as PN does not exist in PN master");
			}else if (getPN(o.getMaterialNumber()) == null) {
				String partNumber_Tool ;
				partNumber_Tool = o.getMaterialNumber().replaceAll("\"", "IN");
				partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
				if(!partNumber_Tool.contains(":"))
				{
					partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
				}
				o.setMaterialNumber(partNumber_Tool);
				throw new Exception("Can not insert/update WO SHOP PN: " + o.getMaterialNumber()  +" as PN does not exist in PN master");
			}
			
			if(o.getOperations() != null) {
				for(Operation operation : o.getOperations()) {
					if(operation != null) {
						if(operation.getComponent() != null) {
							for(Component component : operation.getComponent()) {
								if(component.getMaterialNumber() != null) {
									String partNumber_Tool ;
									partNumber_Tool = component.getMaterialNumber().replaceAll("\"", "IN");
									partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
									if(!partNumber_Tool.contains(":"))
									{
										partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
									}
									component.setMaterialNumber(partNumber_Tool);
									if (getPN(component.getMaterialNumber()) == null) {	
										throw new Exception("Can not insert/update WO SHOP PN: " + component.getMaterialNumber()  +" as PN does not exist in PN master");
									}
								}
							}
						}
					}
				}
			}			
		}



		private void insertWoShopDetail(Component component, Wo wo, Orders o) {
			
			WoShopDetail woshopdetail = null;
			
			if(component.getMaterialNumber() != null && !component.getMaterialNumber().isEmpty() && getPN(component.getMaterialNumber()) != null ) {
			
				try {
					woshopdetail = em.createQuery("SELECT w FROM WoShopDetail w where w.id.wo =:work and w.pn =:part", WoShopDetail.class)
							.setParameter("work", wo.getWo())
							.setParameter("part", component.getMaterialNumber())
							.getSingleResult();
					
				} catch(Exception e) {
					WoShopDetailPK pk = new WoShopDetailPK();
					woshopdetail = new WoShopDetail();
					woshopdetail.setId(pk);
					woshopdetail.setCreatedDate(new Date());
					woshopdetail.setCreatedBy("TRAX_IFACE");
					
					
					woshopdetail.setAddToManufactureOrder("N");
					woshopdetail.setStatus("OPEN");
					woshopdetail.setPnQty(new BigDecimal(1));
					woshopdetail.setScheduleStartDate(wo.getScheduleStartDate());
					woshopdetail.setScheduleStartHour(wo.getScheduleStartHour());
					woshopdetail.setScheduleStartMinute(wo.getScheduleStartMinute());
					
					woshopdetail.setScheduleCompletionDate(wo.getScheduleCompletionDate());
					woshopdetail.setScheduleCompletionHour(wo.getScheduleCompletionHour());
					woshopdetail.setScheduleCompletionMinute(wo.getScheduleCompletionMinute());
					
					PnInventoryDetail pn = getPnInventoryDetail(component.getMaterialNumber(), o.getSerialNumber());
					if(pn != null) {
						woshopdetail.setBatch(new BigDecimal(pn.getBatch()));
					}
					
					woshopdetail.getId().setItem(getLine(new BigDecimal(wo.getWo()), "ITEM", "wo_shop_detail", "WO"));
					
				}
				woshopdetail.setModifiedBy("TRAX_IFACE");
				woshopdetail.setModifiedDate(new Date());
				
				
				
				woshopdetail.getId().setWo(wo.getWo());
				
				woshopdetail.setPn(component.getMaterialNumber());
				
				if(o.getSerialNumber() !=null && !o.getSerialNumber().isEmpty()) {
					woshopdetail.setPnSn(o.getSerialNumber());
				}
				
				logger.info("INSERTING WoShopDetail MATERIAL: "+ o.getMaterialNumber() + " WO: " + wo.getWo());
				insertData(woshopdetail,"woshopdetail",o.getMaterialNumber());
				
			}else {
				exceuted = "Can not insert/update WO SHOP PN: " + o.getMaterialNumber()  +" Wo: " +wo.getWo()+"  as PN does not exist in PN master";
				logger.severe(exceuted);
				PlannedRFOController.addError(exceuted);
				return;
			}
		}

		private void updatePnInventoryDetail(Component component, String sn) {
			
			PnInventoryDetail pnInventoryDetail = null;
			
			if(sn == null || sn.isEmpty()) {
			
				try {
						pnInventoryDetail = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn is null and p.location is not null and p.createdBy != :create", PnInventoryDetail.class)
								.setParameter("par", component.getMaterialNumber())
								.setParameter("create", "ISSUEIFACE")
								.getSingleResult();
						
						pnInventoryDetail.setModifiedBy("TRAX_IFACE");
						pnInventoryDetail.setModifiedDate(new Date());
						if(component.getBatch() != null && !component.getBatch().isEmpty()) {
							pnInventoryDetail.setLegacyBatch(component.getBatch());
						}
						if(component.getPlant() != null && !component.getPlant().isEmpty()) {
							pnInventoryDetail.setLegacyLocation(component.getPlant());
						}
						logger.info("UPDATING PnInventoryDetail Component" + pnInventoryDetail.getPn());
						insertData(pnInventoryDetail, "pnInventoryDetail", pnInventoryDetail.getPn());
						
					} catch(Exception e) {
									
					}
			
			}else {
				try {
					pnInventoryDetail = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn = :sar and p.location is null and p.createdBy != :create", PnInventoryDetail.class)
							.setParameter("par", component.getMaterialNumber())
							.setParameter("sar", sn)
							.setParameter("create", "ISSUEIFACE")
							.getSingleResult();
					
					pnInventoryDetail.setModifiedBy("TRAX_IFACE");
					pnInventoryDetail.setModifiedDate(new Date());
					if(component.getBatch() != null && !component.getBatch().isEmpty()) {
						pnInventoryDetail.setLegacyBatch(component.getBatch());
					}
					if(component.getPlant() != null && !component.getPlant().isEmpty()) {
						pnInventoryDetail.setLegacyLocation(component.getPlant());
					}
					logger.info("UPDATING PnInventoryDetail Component " + pnInventoryDetail.getPn());
					insertData(pnInventoryDetail, "pnInventoryDetail", pnInventoryDetail.getPn());
					
				} catch(Exception e) {
								
				}
			}
			
			
		}
		
		private void updatePnInventoryDetail(Orders o, String sn) {
			
			PnInventoryDetail pnInventoryDetail = null;
			
			if(sn == null || sn.isEmpty()) {
			
				try {
						pnInventoryDetail = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn is null and p.location is not null and p.createdBy != :create", PnInventoryDetail.class)
								.setParameter("par", o.getMaterialNumber())
								.setParameter("create", "ISSUEIFACE")
								.getSingleResult();
						
						pnInventoryDetail.setModifiedBy("TRAX_IFACE");
						pnInventoryDetail.setModifiedDate(new Date());
						
						if(o.getWorkCenterPlant() != null && !o.getWorkCenterPlant().isEmpty()) {
							pnInventoryDetail.setLegacyLocation(o.getWorkCenterPlant());
						}
						logger.info("UPDATING PnInventoryDetail Orders MaterialNumber" + pnInventoryDetail.getPn());
						insertData(pnInventoryDetail, "pnInventoryDetail", pnInventoryDetail.getPn());
						
					} catch(Exception e) {
									
					}
			
			}else {
				try {
					pnInventoryDetail = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn = :sar and p.location is null and p.createdBy != :create", PnInventoryDetail.class)
							.setParameter("par", o.getMaterialNumber())
							.setParameter("sar", sn)
							.setParameter("create", "ISSUEIFACE")
							.getSingleResult();
					
					pnInventoryDetail.setModifiedBy("TRAX_IFACE");
					pnInventoryDetail.setModifiedDate(new Date());
					
					if(o.getWorkCenterPlant() != null && !o.getWorkCenterPlant().isEmpty()) {
						pnInventoryDetail.setLegacyLocation(o.getWorkCenterPlant());
					}
					logger.info("UPDATING PnInventoryDetail Orders MaterialNumber " + pnInventoryDetail.getPn());
					insertData(pnInventoryDetail, "pnInventoryDetail", pnInventoryDetail.getPn());
					
				} catch(Exception e) {
								
				}
			}
			
			
		}
		
		
		private PnInventoryDetail getPnInventoryDetail(String pn, String sn){
			
			PnInventoryDetail pnInventoryDetail = null;
			if(sn != null && !sn.isEmpty()) {
				try {
					pnInventoryDetail = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn = :sar and p.location is null and p.createdBy != :create", PnInventoryDetail.class)
							.setParameter("par", pn)
							.setParameter("sar", sn)
							.setParameter("create", "ISSUEIFACE")
							.getSingleResult();
					
					return pnInventoryDetail;
					
				} catch(Exception e) {
					pnInventoryDetail = new PnInventoryDetail();
					pnInventoryDetail.setCreatedDate(new Date());
					pnInventoryDetail.setCreatedBy("TRAX_IFACE");
					
					//EMRO fields to create basic object
					pnInventoryDetail.setQtyAvailable(new BigDecimal(1));
					pnInventoryDetail.setQtyReserved(new BigDecimal(0));
					pnInventoryDetail.setQtyInTransfer(new BigDecimal(0));
					pnInventoryDetail.setQtyPendingRi(new BigDecimal(0));
					pnInventoryDetail.setQtyUs(new BigDecimal(0));
					pnInventoryDetail.setUnitCost(new BigDecimal(0));
					pnInventoryDetail.setCurrencyExchangeRate(new BigDecimal(1));
					pnInventoryDetail.setQtyInRental(new BigDecimal(0));
					pnInventoryDetail.setSecondaryCost(new BigDecimal(0));
					pnInventoryDetail.setSecondaryCurrencyExchange(new BigDecimal(1));
					
					pnInventoryDetail.setKitNo(new BigDecimal(0));
					pnInventoryDetail.setBlobNo(new BigDecimal(0));
					pnInventoryDetail.setDocumentNo(new BigDecimal(0));
					pnInventoryDetail.setFilingSequence(new BigDecimal(0));
					pnInventoryDetail.setNoOfTagPrint(new BigDecimal(0));
					pnInventoryDetail.setSosHour(new BigDecimal(0));
					pnInventoryDetail.setSlot(new BigDecimal(0));
					pnInventoryDetail.setNlaPosition(" ");
					
					pnInventoryDetail.setInventoryType("MAINTENANCE");	
					try
					{
						String company = (String) this.em.createQuery("select p.profile from ProfileMaster p")
								.getSingleResult();
						pnInventoryDetail.setGlCompany(company);
					}
					catch(Exception e1) {
						pnInventoryDetail.setGlCompany("SIAEC");	
					}
					pnInventoryDetail.setCondition("NEW");
					
					pnInventoryDetail.setModifiedBy("TRAX_IFACE");
					pnInventoryDetail.setModifiedDate(new Date());
					
					pnInventoryDetail.setSn(sn);
					pnInventoryDetail.setPn(pn);
					long batch = getTransactionNo("BATCH").longValue();
					pnInventoryDetail.setBatch(batch);
					pnInventoryDetail.setGoodsRcvdBatch(new BigDecimal(batch));
					
					logger.info("INSERTING pnInventoryDetail PN: "	+	
					pnInventoryDetail.getPn() + " , SN: " + pnInventoryDetail.getSn()		
					+ ", BATCH: " + pnInventoryDetail.getBatch() );
					
					insertData(pnInventoryDetail,"pnInventoryDetail",pnInventoryDetail.getPn());
					
					return pnInventoryDetail;
				}	
			}else {
				try {
					pnInventoryDetail = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn is null and p.location is not null and p.createdBy != :create", PnInventoryDetail.class)
							.setParameter("par", pn)
							.setParameter("create", "ISSUEIFACE")
							.getSingleResult();
					
					return pnInventoryDetail;
					
				} catch(Exception e) {
					try {
						List<PnInventoryDetail> pns =(List<PnInventoryDetail>)  em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.createdBy != :create")
								.setParameter("par", pn)
								.setParameter("create", "ISSUEIFACE")
								.getResultList();
						
						return pns.get(0);
						
					} catch(Exception e1) {
						logger.info(e1.getMessage());	
					}	
				}
			}
			
			return null;	
		}

		private void insertWoShopDetail(Orders o, Wo wo) {
			
			WoShopDetail woshopdetail = null;
			
			if(o.getMaterialNumber() != null && !o.getMaterialNumber().isEmpty() && getPN(o.getMaterialNumber()) != null ) {
			
				try {
					woshopdetail = em.createQuery("SELECT w FROM WoShopDetail w where w.id.wo =:work and w.pn =:part and w.id.item =:ite", WoShopDetail.class)
							.setParameter("work", wo.getWo())
							.setParameter("part", o.getMaterialNumber())
							.setParameter("ite", new Long(1).longValue())
							.getSingleResult();
					
				} catch(Exception e) {
					WoShopDetailPK pk = new WoShopDetailPK();
					woshopdetail = new WoShopDetail();
					woshopdetail.setId(pk);
					woshopdetail.setCreatedDate(new Date());
					woshopdetail.setCreatedBy("TRAX_IFACE");
					
					
					woshopdetail.setAddToManufactureOrder("N");
					woshopdetail.setStatus("OPEN");
					woshopdetail.setPnQty(new BigDecimal(1));
					woshopdetail.setScheduleStartDate(wo.getScheduleStartDate());
					woshopdetail.setScheduleStartHour(wo.getScheduleStartHour());
					woshopdetail.setScheduleStartMinute(wo.getScheduleStartMinute());
					
					woshopdetail.setScheduleCompletionDate(wo.getScheduleCompletionDate());
					woshopdetail.setScheduleCompletionHour(wo.getScheduleCompletionHour());
					woshopdetail.setScheduleCompletionMinute(wo.getScheduleCompletionMinute());
					
					PnInventoryDetail pn = getPnInventoryDetail(o.getMaterialNumber(),o.getSerialNumber());
					if(pn != null) {
						woshopdetail.setBatch(new BigDecimal(pn.getBatch()));
					}
					
				}
				woshopdetail.setModifiedBy("TRAX_IFACE");
				woshopdetail.setModifiedDate(new Date());
			
				String pn = o.getMaterialNumber();
				
				
				
				pn = pn.replaceAll("\"", "IN");
				pn = pn.replaceAll("'", "FT");
				if(!pn.contains(":"))
				{
					pn = pn.concat(":UPLOAD");
				}
				
				
				woshopdetail.getId().setWo(wo.getWo());
				woshopdetail.getId().setItem(1);
				woshopdetail.setPn(o.getMaterialNumber());
				if(o.getSerialNumber() !=null && !o.getSerialNumber().isEmpty()) {
					woshopdetail.setPnSn(o.getSerialNumber());
				}
				
				
				logger.info("INSERTING WoShopDetail MATERIAL: "+ o.getMaterialNumber() + " WO: " + wo.getWo());
				insertData(woshopdetail,"woshopdetail",o.getMaterialNumber());
				
			}else {
				exceuted = "Can not insert/update WO SHOP PN: " + o.getMaterialNumber()  +" Wo: " +wo.getWo()+"  as PN does not exist in PN master";
				logger.severe(exceuted);
				PlannedRFOController.addError(exceuted);
				return;
			}
			
		}

		//insert a SHOP WO
		private Wo insertShopWo(Orders order) 
		{	
			//setting up variables
			
			Wo wo = null;
			Wo existwo = null;
			boolean newWO = true;
			
			//check if object has min values
			if(checkMinValue(order)) 
			{
				
				try 
				{
					existwo = em.createQuery("Select w From Wo w where w.id.wo =:work", Wo.class)
							.setParameter("work", new BigDecimal(order.getTraxWO()).longValue())
							.getSingleResult();
					
				}
				catch(Exception e)
				{
					logger.info("NO Production WO provided");
							//e.printStackTrace();		
				}
				
				try 
				{
					wo = em.createQuery("Select w From Wo w where w.refurbishmentOrder =:ord", Wo.class)
							.setParameter("ord", order.getRFONumber())
							.getSingleResult();
					newWO = false;
				}
				catch(Exception e)
				{
					//e.printStackTrace();
					wo = new Wo();
					wo.setCreatedDate(new Date());
					wo.setCreatedBy("TRAX_IFACE");	
					
					//EMRO fields to create basic object
					wo.setOrderType("W/O");
					wo.setModule("SHOP");
					
					wo.setNrAllow("YES");
					wo.setStatus("OPEN");
					wo.setWoCategory("SHOP");
					wo.setThirdPartyWo("Y");
					
					wo.setScheduleStartHour(new BigDecimal(0));
					wo.setScheduleStartMinute(new BigDecimal(0));
					wo.setScheduleCompletionHour(new BigDecimal(0));
					wo.setScheduleCompletionMinute(new BigDecimal(0));
					
					wo.setScheduleStartDate(new Date());
					wo.setScheduleCompletionDate(new Date());
					wo.setActualStartDate(new Date());
					wo.setScheduleOrgCompletionDate(new Date());	
						
					wo.setPnQty(new BigDecimal(1));
					wo.setWarrantyIndicator("N");
					wo.setDamageIndicator("N");
					wo.setInsuranceClaim("NO");
						
					wo.setActualStartHour(new BigDecimal(0));
					wo.setActualStartMinute(new BigDecimal(0));
					wo.setScheduleOrgCompletionHour(new BigDecimal(0));
					wo.setScheduleOrgCompletionMinute(new BigDecimal(0));
					wo.setManufactureOrder("N");
					wo.setAuthorization("Y");
					wo.setAuthorizationBy("TRAX_IFACE");
					wo.setAuthorizationDate(new Date());
					wo.setNrReqItem("N");
					wo.setRestrictActual("N");
						
					wo.setExpenditure(setExpenditure("General"));
					wo.setPriority("NORMAL");
					
					
					try
					{
						String company = (String) this.em.createQuery("select p.profile from ProfileMaster p")
								.getSingleResult();
						wo.setGlCompany(company);
					}
					catch(Exception e1) {
						exceuted = "Can not insert/update WO: "+ order.getTraxWO() +" as ERROR: Company could not be found";
						logger.severe(exceuted);
						PlannedRFOController.addError(exceuted);
						wo = null; 
						return wo;
					}
					
					try {
						long w;
						w =	getTransactionNo("WOSEQ").longValue();
						wo.setWo(w);
					} catch (Exception e1) {
						
					}
					
					
				}
				
			
				wo.setModifiedBy("TRAX_IFACE");
				wo.setModifiedDate(new Date());
				if(order.getRFONumber() != null && !order.getRFONumber().isEmpty()) {
					wo.setRefurbishmentOrder(order.getRFONumber());
				}else if(existwo != null && existwo.getRefurbishmentOrder() != null && !existwo.getRefurbishmentOrder().isEmpty()) {
					wo.setRefurbishmentOrder(existwo.getRefurbishmentOrder());
				}
				if(order.getOrderText() !=null && !order.getOrderText().isEmpty()) {
					if(order.getOrderText().get(0).getLongText() != null && !order.getOrderText().get(0).getLongText().isEmpty()) {
						wo.setWoDescription(order.getOrderText().get(0).getLongText() );
					}else if(existwo != null && existwo.getRefurbishmentOrder() != null && !existwo.getRefurbishmentOrder().isEmpty()) {
						wo.setWoDescription(existwo.getRefurbishmentOrder());
					}
				}
				
				
				
				 
				
				//ADD VLAID from ops line email table
				
				if(order.getShopSite() != null && !order.getShopSite().isEmpty()) {
					wo.setSite(order.getShopSite());
				}else if(existwo != null && existwo.getSite() != null && !existwo.getSite().isEmpty()) {
					wo.setSite(existwo.getSite());
				}
				
				if(existwo != null && existwo.getLocation() != null && !existwo.getLocation().isEmpty()) {
					wo.setLocation(existwo.getLocation());
				}
//				if(existwo != null && existwo.getSite() != null && !existwo.getSite().isEmpty()) {
//					wo.setLocation(setLocation(existwo.getSite()));
//				}else {
//					wo.setLocation(setLocation(wo.getSite()));
//				}
				
				
				if(existwo != null) {
					wo.setNhWo(new BigDecimal(existwo.getWo()));
				}
				
				logger.info("INSERTING RFO: "+ order.getRFONumber() + " WO: " + wo.getWo());
				insertData(wo,"wo",order.getTraxWO());
				if(newWO) {
					insertWoAudit(wo);	
				}
			}else 
			{
				exceuted = "Can not insert/update RFO: "+ order.getRFONumber() +" as ERROR: Order does not have Minimum values or are null";
				logger.severe(exceuted);
				PlannedRFOController.addError(exceuted);
				wo = null; 
			}
			return wo;
			
		}
		
		
		private String setExpenditure(String string) {
			JournalEntriesExpenditure journalEntriesExpenditure = null;
			try
			{
				journalEntriesExpenditure = em.createQuery("SELECT j FROM JournalEntriesExpenditure j WHERE j.id.categoryCode = :code AND  j.id.transaction = :tra AND j.id.category = :cat", JournalEntriesExpenditure.class)
				.setParameter("code", string)
				.setParameter("tra", "WIP")
				.setParameter("cat", "EXPENDITURE")
				.getSingleResult();
				
				
				return journalEntriesExpenditure.getId().getCategoryCode();
			}
			catch (Exception e)
			{
				journalEntriesExpenditure = new JournalEntriesExpenditure();
				JournalEntriesExpenditurePK pk = new JournalEntriesExpenditurePK();
				journalEntriesExpenditure.setId(pk);
				journalEntriesExpenditure.setModifiedBy("TRAX_IFACE");
				journalEntriesExpenditure.setModifiedDate(new Date());
				journalEntriesExpenditure.setCreatedBy("TRAX_IFACE");
				journalEntriesExpenditure.setCreatedDate(new Date());
				
				journalEntriesExpenditure.getId().setCategoryCode("DEFAULT");
				journalEntriesExpenditure.getId().setTransaction("WIP");
				journalEntriesExpenditure.getId().setCategory("EXPENDITURE");
				
				journalEntriesExpenditure.getId().setClass_("LABOR");
				
				journalEntriesExpenditure.setJournalDescription("DEFAULT");
				journalEntriesExpenditure.setExpenditureUse("PRODUCTION");
				
				
				logger.info("INSERTING CODE: " + journalEntriesExpenditure.getId().getCategoryCode());
				insertData(journalEntriesExpenditure,"journalEntriesExpenditure",journalEntriesExpenditure.getId().getCategoryCode());
			}
			return journalEntriesExpenditure.getId().getCategoryCode();
		}

		//insert WO TASK CARD
		private WoTaskCard insertWoTaskCard(Orders orders, Operation operation, Wo wo) {
			
			WoTaskCard wotaskcard = null;
			//boolean exist = false;
			WoTaskCard existwotaskcard = null;
			
			long prodWo = getProductionWo(orders.getSVONumber()); 
			
			if(orders.getSVONumber() != null && !orders.getSVONumber().isEmpty()) {
				try 
				{
					existwotaskcard = em.createQuery("SELECT w FROM WoTaskCard w where w.referenceTaskCard = :tastcar and w.id.wo = :woo", WoTaskCard.class)
							.setParameter("tastcar", orders.getSVONumber())
							.setParameter("woo", prodWo)
							.getSingleResult();
					//exist = true;
				}
				catch(Exception e)
				{
					//e.printStackTrace();
					wotaskcard = null;
					return wotaskcard;
				}
				
				wotaskcard = new WoTaskCard();
				
				WoTaskCardPK pk = new WoTaskCardPK();
				
				wotaskcard.setId(pk);
				
				wotaskcard.setCreatedDate(new Date());
				wotaskcard.setCreatedBy("TRAX_IFACE");
				
					
				//EMRO fields to create basic object
				wotaskcard.setStatus("OPEN");
				wotaskcard.setNdt("N");
				wotaskcard.setSellMaterialMethod("STANDARD");
				wotaskcard.setSellMaterialAmount(new BigDecimal(0));
				wotaskcard.setSellLaborMethod("STANDARD");
				wotaskcard.setSellLaborAmount(new BigDecimal(0));
				wotaskcard.setSellOtherMethod("STANDARD");
				wotaskcard.setSellOtherAmount(new BigDecimal(0));
					
				wotaskcard.setAuthorization("AUTHORIZED");
				wotaskcard.setAuthorizationBy("TRAX_IFACE");
				wotaskcard.setAuthorizationDate(new Date());
				wotaskcard.setWeightOn(new BigDecimal(0));
				wotaskcard.setWeightOff(new BigDecimal(0));
				wotaskcard.setScheduleStartHour(new BigDecimal(0));
				wotaskcard.setScheduleStartMinute(new BigDecimal(0));
				
					
				//wotaskcard.getId().setPnSn("                                   ");
					
				wotaskcard.setParagraph(new BigDecimal(0));
				wotaskcard.setEditorUsed("NONE");
				wotaskcard.setScheduleEndHour(new BigDecimal(0));
				wotaskcard.setScheduleEndMinute(new BigDecimal(0));
				wotaskcard.setNoOfPrint(new BigDecimal(0));
				wotaskcard.setInsuranceClaim("NO");
				wotaskcard.setFaultConfirm("PENDING");
				wotaskcard.setPnRequired("NO");
					
				wotaskcard.setRectifiedByEngineering("N");
				wotaskcard.setAircraftJacked("NO");
				wotaskcard.setElectricalPowerReq("OPTIONAL");
				wotaskcard.setHydraulicPowerReq("OPTIONAL");
				wotaskcard.setNonRoutine("N");
				wotaskcard.setTaskCardNumberingSystem(new BigDecimal(getLine(new BigDecimal(wo.getWo()),"task_card_numbering_system","wo_task_card","wo" )));
						
				
				
				
				wotaskcard.getId().setAc("          ");
				wotaskcard.setReferenceTaskCard(orders.getSVONumber());	
				wotaskcard.getId().setWo(wo.getWo());
				wotaskcard.setModifiedBy("TRAX_IFACE");
				wotaskcard.setModifiedDate(new Date());
				
				wotaskcard.setRevisedDate(new Date());
				
				if(orders.getTRAXNonRoutineNumber() != null && !orders.getTRAXNonRoutineNumber().isEmpty()) {
					wotaskcard.getId().setTaskCard(orders.getTRAXNonRoutineNumber());
				}else if(existwotaskcard != null && existwotaskcard.getId().getTaskCard() != null && !existwotaskcard.getId().getTaskCard().isEmpty()) {
					wotaskcard.getId().setTaskCard(existwotaskcard.getId().getTaskCard());
				}
				
				
				if(orders.getMaterialNumber() != null && !orders.getMaterialNumber().isEmpty() ) 
				{
					
					String pn = orders.getMaterialNumber();
					
					
					
					pn = pn.replaceAll("\"", "IN");
					pn = pn.replaceAll("'", "FT");
					if(!pn.contains(":"))
					{
						pn = pn.concat(":UPLOAD");
					}
					
					if(getPN(pn) == null) {
						wotaskcard.getId().setPn("                                   ");
					}else
					{
						wotaskcard.getId().setPn(pn);
					}
				}else {
					wotaskcard.getId().setPn("                                   ");
				}
				
			
				if(orders.getSerialNumber() !=null && !orders.getSerialNumber().isEmpty()) {
					wotaskcard.getId().setPnSn(orders.getSerialNumber());
				}else {
					wotaskcard.getId().setPnSn("                                   ");
				}
				
				if(orders.getSVONumber() !=null && !orders.getSVONumber().isEmpty()) {
					wotaskcard.setReferenceTaskCard(orders.getSVONumber());
				}else if(existwotaskcard != null && existwotaskcard.getReferenceTaskCard() != null && !existwotaskcard.getReferenceTaskCard().isEmpty()) {
					wotaskcard.setReferenceTaskCard(existwotaskcard.getReferenceTaskCard());
				}
				
				if(orders.getFuncationalNumber() !=null && !orders.getFuncationalNumber().isEmpty()) {
					wotaskcard.setFunctionalLocation(orders.getFuncationalNumber());
				}else if(existwotaskcard != null && existwotaskcard.getFunctionalLocation() != null && !existwotaskcard.getFunctionalLocation().isEmpty()) {
					wotaskcard.setFunctionalLocation(existwotaskcard.getFunctionalLocation());
				}
				
				
				wotaskcard.getId().setWo(wo.getWo());
				
					
				logger.info("INSERTING woTaskCard: " + wotaskcard.getId().getTaskCard());
				insertData(wotaskcard,"woTaskCard",orders.getTRAXNonRoutineNumber());
		}else 
		{
			exceuted = "Can not insert/update WO TaskCard: "+ orders.getTRAXNonRoutineNumber() +" as ERROR: missing SVO is missing";
			logger.severe(exceuted);
			PlannedRFOController.addError(exceuted);
			wotaskcard = null;
		}

			return wotaskcard;
		}
		
		
		private long getProductionWo(String svoNumber) {
			
			List<WoTaskCard> taskCards = null;
			
			try
			{
				taskCards = em.createQuery("SELECT w FROM WoTaskCard w where w.referenceTaskCard = :tastcar", WoTaskCard.class)
				.setParameter("tastcar", svoNumber)
				.getResultList();
				
				for(WoTaskCard t : taskCards) {
					if(t.getWoBean().getModule().equalsIgnoreCase("PRODUCTION")) {
						return t.getId().getWo();
					}
				}
			}
			catch (Exception e)
			{
				logger.severe(e.toString());
			}
			return 0;
		}

		private WoTaskCardItem insertWoTaskCardItem(Operation operation, WoTaskCard wotaskcard){
			
			WoTaskCardItemPK itemKey = null;
			WoTaskCardItem item = null;
			
			
			if(operation.getOperationNumber() != null && !operation.getOperationNumber().isEmpty() && wotaskcard != null)
			{
				try 
				{
					item = em.createQuery("SELECT w FROM WoTaskCardItem w where w.id.taskCardItem =:item and w.id.taskcard =:taskc and w.id.wo =:work", WoTaskCardItem.class)
							.setParameter("item", new BigDecimal(1).longValue())
							.setParameter("taskc", wotaskcard.getId().getTaskCard())
							.setParameter("work", wotaskcard.getId().getWo())
							.getSingleResult();
				}
				catch(Exception e)
				{
					itemKey = new WoTaskCardItemPK();
					item = new WoTaskCardItem();
					item.setId(itemKey);
					item.setCreatedDate(new Date());
					item.setCreatedBy("TRAX_IFACE");
					
					
					//EMRO fields to create basic object
					item.setMechanic("N");
					item.setInspector("N");
					item.setDuplicateInspection("N");
					item.setInspectorManHours(new BigDecimal(0));
					item.setInspectorManRequire(new BigDecimal(1));
					item.setManHours(new BigDecimal(0));
					item.setManRequire(new BigDecimal(1));
					item.setDualInspectorManHours(new BigDecimal(0));
					item.setDualInspectorManRequire(new BigDecimal(1));			
					//taskCardItem.setSortItem(BigDecimal.valueOf(Double.valueOf(items.getItemNumber())));
					item.setInformationalOnly("N");
					item.setMainSkill("YES");
					item.getId().setTaskCardItem(new Long(1));
					
					

				}
				
				item.setModifiedBy("TRAX_IFACE");
				item.setModifiedDate(new Date());
				item.setOpsNo(operation.getOperationNumber());	
				item.getId().setTaskCard(wotaskcard.getId().getTaskCard());
				item.getId().setWo(wotaskcard.getId().getWo());
				
				item.getId().setTaskCardPn(wotaskcard.getId().getPn());
				item.getId().setTaskCardPnSn(wotaskcard.getId().getPnSn());
				item.getId().setAc(wotaskcard.getId().getAc());
				
				
				
				if(operation.getOperationShortText() != null && !operation.getOperationShortText().isEmpty()) 
				{
					item.setTaskCardText(operation.getOperationShortText());
				}
					
				if(operation.getWorkInvolved() != null && !operation.getWorkInvolved().isEmpty()) 
				{
					try 
					{
						item.setManHours(new BigDecimal(operation.getWorkInvolved()));
					}
					catch(NumberFormatException e)
					{
						exceuted = "Can not insert/update WoTaskCard Item: "+ operation.getOperationNumber() + " Task Card: "+wotaskcard.getId().getTaskCard() +" ERROR: WorkInvolved";
						logger.severe(exceuted);
						PlannedRFOController.addError(exceuted);
						item = null;
						return  item;
					}
				}
					
				logger.info("INSERTING WoTaskCard item: " + item.getId().getTaskCardItem());
				insertData(item,"WoTaskCardItem",operation.getOperationNumber());
			}
			return  item;
			
			
			
		}
		
		private void insertWoTaskCardPn(Orders order, Component component, WoTaskCard wotaskcard ) {
			
			if(order.getMaterialNumber() != null && !order.getMaterialNumber().isEmpty()) 
			{
				
				logger.info("Material: " + component.getMaterialNumber());
				
				
				String partNumber_Tool;
				partNumber_Tool = component.getMaterialNumber().replaceAll("\"", "IN");
				partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
				if(!partNumber_Tool.contains(":"))
				{
					partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
				}
				
				
				
				WoTaskCardPn taskCardPn = null;
				//boolean exist = false; 
				try 
				{
					taskCardPn = em.createQuery("SELECT w FROM WoTaskCardPn w where w.id.wo =:work and w.id.taskcard =:taskcar and w.id.pn =:taskpn", WoTaskCardPn.class)
							.setParameter("work", wotaskcard.getId().getWo())
							.setParameter("taskcar", wotaskcard.getId().getTaskCard())
							.setParameter("taskpn",partNumber_Tool)
							.getSingleResult();
					//exist = true;
				}
				catch(Exception e)
				{
					WoTaskCardPnPK pk = new WoTaskCardPnPK();
					taskCardPn = new WoTaskCardPn();
					taskCardPn.setId(pk);
					taskCardPn.setCreatedBy("TRAX_IFACE");
					taskCardPn.setCreatedDate(new Date());
					
					
					//EMRO fields to create basic object
					taskCardPn.setQty(new BigDecimal(1));
					taskCardPn.setTagType("NONE");
					taskCardPn.setSpare("SPARE");
					taskCardPn.setTaskCardItem(new BigDecimal(0));
					
				}
				taskCardPn.setModifiedDate(new Date());
				taskCardPn.setModifiedBy("TRAX_IFACE");
				
				taskCardPn.getId().setReserve("YES");
				taskCardPn.getId().setTaskCard(wotaskcard.getId().getTaskCard());
				taskCardPn.setTaskCardXRef(wotaskcard.getId().getTaskCard());
				taskCardPn.getId().setWo(wotaskcard.getId().getWo());
				taskCardPn.getId().setAc(wotaskcard.getId().getAc());

				taskCardPn.getId().setTaskCardPn(wotaskcard.getId().getPn());
				taskCardPn.getId().setTaskCardPnSn(wotaskcard.getId().getPnSn());
				
				
				if(getPN(partNumber_Tool) == null) 
				{
					exceuted = "Can not insert/update TaskCard PN: " + partNumber_Tool  +" Task Card: " +wotaskcard.getId().getTaskCard()+"  as PN does not exist in PN master";
					logger.severe(exceuted);
					PlannedRFOController.addError(exceuted);
					return;
				}
				else
				{
					taskCardPn.getId().setPn(partNumber_Tool);
					
				}
				
				
				
				taskCardPn.setQty(new BigDecimal(component.getQuantity().trim()));
				
				

				logger.info("INSERTING TaskCardPn material: " + taskCardPn.getId().getTaskCardPn() );
				insertData(taskCardPn,"PN",order.getMaterialNumber() );
			
			}
		}
		
		
		
		
		
		// insert PICKLIST
		private void insertComponent(Orders order,Component component, Wo wo) {
			
			//setting up variables
			PicklistHeader picklistheader = null;
			//Boolean exist = false;
			PicklistDistribution picklistdistdistribu = null;
			PicklistDistribution picklistdistrequire = null;
			
			//check if object has min values
			if(component.getReservationNumber() != null && !component.getReservationNumber().isEmpty() && component.getResrvationItem()!= null && !component.getResrvationItem().isEmpty()) 
			{
				
				String pickNumber = findPicklistNumber(component.getReservationNumber(),component.getResrvationItem());
				
				
				
				
				try 
				{
					picklistheader = em.createQuery("SELECT p FROM PicklistHeader p where p.id.picklist =:pick", PicklistHeader.class)
							.setParameter("pick", Long.valueOf(pickNumber))
							.getSingleResult();
					//exist = true;
				}
				catch(Exception e)
				{
					//e.printStackTrace();
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
					
					picklistheader.setLocation(component.getStorageLocation());
					picklistheader.setRequireOn(new Date());
					picklistheader.setRequireHour(new BigDecimal(0));
					picklistheader.setRequireMinute(new BigDecimal(0));
					try {
						picklistheader.setPicklist(getTransactionNo("PICKLIST").longValue());
					} catch (Exception e1) {
						logger.severe(e1.toString());
					}
				}
				picklistheader.setModifiedDate(new Date());
				picklistheader.setModifiedBy("TRAX_IFACE");
				
				
				picklistheader.setWo(new BigDecimal(wo.getWo()));
				//picklistheader.setTaskCard(wotaskcard.getId().getTaskCard());
				picklistheader.setPriority(order.getPriority());
				
				String line = component.getResrvationItem().replaceAll("0+$", "");
				line = line.replaceFirst("^0+(?!$)", "");
				Long l = Long.parseLong(line);
				
				
				picklistdistdistribu = fillPicklistDistribution(picklistdistdistribu, component, "DISTRIBU","2", picklistheader.getPicklist(),l);
				picklistdistrequire = fillPicklistDistribution(picklistdistrequire, component,"REQUIRE","0" ,picklistheader.getPicklist(),l);
								
				
				
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
						exceuted = "Can not insert/update Component ReservationNumber: "+ component.getReservationNumber() + " ERROR: Quantity";
						logger.severe(exceuted);
						PlannedRFOController.addError(exceuted);
						
						return ;
					}
					
				}
				if(order.getSerialNumber() !=null && !order.getSerialNumber().isEmpty()) {
					picklistdistdistribu.setSn(order.getSerialNumber());
					picklistdistrequire.setSn(order.getSerialNumber());
				}
				
				
				
				if(component.getMaterialNumber() !=null && !component.getMaterialNumber().isEmpty()) {
					
					String partNumber_Tool;
					partNumber_Tool = component.getMaterialNumber().replaceAll("\"", "IN");
					partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
					if(!partNumber_Tool.contains(":"))
					{
						partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
					}
					
					picklistdistdistribu.setPn(partNumber_Tool);
					picklistdistrequire.setPn(partNumber_Tool);
				}
				
				
				logger.info("INSERTING picklist header: " + picklistheader.getPicklist() );
				insertData(picklistheader,"picklist header",String.valueOf(picklistheader.getPicklist()) );
				
				logger.info("INSERTING picklist dist: " + picklistheader.getPicklist()  );
				insertData(picklistdistdistribu,"picklist dist distribu",String.valueOf(picklistheader.getPicklist()));
				insertData(picklistdistrequire,"picklist dist require",String.valueOf(picklistheader.getPicklist()) );
				
				
			}else 
			{
				exceuted = "Can not insert/update Component ReservationNumber: "+ component.getReservationNumber() +" as ERROR: Component does not have Minimum values or are null";
				logger.severe(exceuted);
				PlannedRFOController.addError(exceuted);
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
				logger.info("Picklist not found");
				//logger.severe(e.toString());
			}
			return null;
		}

		
		
		
			
		//insert generic data from model objects
		private <T> void insertData( T data, String type, String name) 
		{
			try 
			{	
				em.merge(data);
				em.flush();
			}catch (Exception e)
			{
				exceuted = "insertData has encountered an Exception with inserting type: " + type +" Name: "+name +" Exception: "+e.toString();
				PlannedRFOController.addError(exceuted);
				logger.severe(exceuted);
			}
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
		
		
		
		
		// get pn from pn master used to check if pn exist
		private String getPN(String PN) {
			try
			{
				PnMaster pnMaster = em.createQuery("Select p From PnMaster p where p.id.pn =:partn", PnMaster.class)
				.setParameter("partn", PN)
				.getSingleResult();
				
				return pnMaster.getPn();
			}
			catch (Exception e)
			{
				logger.severe(e.toString());
			}
			return null;
		}
		
		private boolean checkMinValue(Orders o) {
			
			if( o.getRFONumber() == null || o.getRFONumber().isEmpty()) {
				return false;
			}
			//if( o.getSVONumber() == null || o.getSVONumber().isEmpty()) {
			//	return false;
			//}
				
			return true;
		}
		
		
		private PicklistDistribution fillPicklistDistribution(PicklistDistribution picklistdist , Component component , String transaction, String DistributionLine, long l, Long li) {
			
			try 
			{
				picklistdist = em.createQuery("SELECT p FROM PicklistDistribution p where p.id.picklist =:pick AND p.id.picklistLine =:line AND p.id.transaction =:tra AND p.id.distributionLine =:dl", PicklistDistribution.class)
						.setParameter("pick", l)
						.setParameter("line", li.longValue())
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
			
			picklistdist.setExternalCustRes(component.getReservationNumber());
			picklistdist.setExternalCustResItem(component.getResrvationItem());
			picklistdist.getId().setPicklist(l );
			picklistdist.getId().setPicklistLine(li);
			picklistdist.getId().setDistributionLine(new Long(DistributionLine));
			picklistdist.getId().setTransaction(transaction);
			
			
			return picklistdist;
			
		}
		
		
		private String setLocation(String site) {
			List<LocationSite> locationsites = null;
			try
			{
				locationsites = em.createQuery("SELECT l FROM LocationSite l WHERE l.id.site = :sit", LocationSite.class)
				.setParameter("sit", site)
				.getResultList();
				
				for(LocationSite ls : locationsites) {
					if(getLocationType(ls.getId().getLocation())) {
						return ls.getId().getLocation();
					}
					
			}
				
			}catch (Exception e)
			{
				e.printStackTrace();
				logger.info(e.toString());
			}
			return "";
		}
		
		
		private boolean getLocationType(String loc) {
			LocationMaster locationMaster = null;
			try
			{
				locationMaster = em.createQuery("SELECT l FROM LocationMaster l WHERE l.location = :loc", LocationMaster.class)
				.setParameter("loc", loc)
				.getSingleResult();
				if(locationMaster.getInventoryQuarantine() !=null && !locationMaster.getInventoryQuarantine() .isEmpty() && locationMaster.getInventoryQuarantine() .equalsIgnoreCase("Y")){
					return true;
				}else{
					return false;
				}
			}catch (Exception e)
			{
				e.printStackTrace();
				logger.info(e.toString());
				return false;
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
	
		
		private boolean insertWoAudit(Wo wo){
			
			WoAudit auditWo = new WoAudit();
			auditWo.setId(new WoAuditPK());
			
			
			auditWo.setCreatedDate(wo.getCreatedDate());
			auditWo.setCreatedBy(wo.getCreatedBy());	
			
			auditWo.setOrderType(wo.getOrderType());
			auditWo.setModule(wo.getModule());
			
			auditWo.setNrAllow(wo.getNrAllow());
			auditWo.setStatus(wo.getStatus());
			auditWo.setWoCategory(wo.getWoCategory());
			auditWo.setThirdPartyWo(wo.getThirdPartyWo());
			
			auditWo.setScheduleStartHour(wo.getScheduleStartHour());
			auditWo.setScheduleStartMinute(wo.getScheduleStartMinute());
			auditWo.setScheduleCompletionHour(wo.getScheduleCompletionHour());
			auditWo.setScheduleCompletionMinute(wo.getScheduleCompletionMinute());
			
			auditWo.setScheduleStartDate(wo.getScheduleStartDate());
			auditWo.setScheduleCompletionDate(wo.getScheduleCompletionDate());
			auditWo.setActualStartDate(wo.getActualStartDate());
			auditWo.setScheduleOrgCompletionDate(wo.getScheduleOrgCompletionDate());	
				
			auditWo.setPnQty(wo.getPnQty());
			auditWo.setWarrantyIndicator(wo.getWarrantyIndicator());
			auditWo.setDamageIndicator(wo.getDamageIndicator());
			auditWo.setInsuranceClaim(wo.getInsuranceClaim());
				
			auditWo.setActualStartHour(wo.getActualStartHour());
			auditWo.setActualStartMinute(wo.getActualStartMinute());
			auditWo.setScheduleOrgCompletionHour(wo.getScheduleOrgCompletionHour());
			auditWo.setScheduleOrgCompletionMinute(wo.getScheduleOrgCompletionMinute());
			auditWo.setManufactureOrder(wo.getManufactureOrder());
			auditWo.setAuthorization(wo.getAuthorization());
			auditWo.setAuthorizationBy(wo.getAuthorizationBy());
			auditWo.setAuthorizationDate(wo.getAuthorizationDate());
			auditWo.setNrReqItem(wo.getNrReqItem());
			auditWo.setRestrictActual(wo.getRestrictActual());
				
			auditWo.setExpenditure(wo.getExpenditure());
			auditWo.setPriority(wo.getPriority());
			auditWo.setGlCompany(wo.getGlCompany());
			
			auditWo.getId().setModifiedBy(wo.getModifiedBy());
			auditWo.getId().setModifiedDate(new Date());
			
			auditWo.setRefurbishmentOrder(wo.getRefurbishmentOrder());
			
			auditWo.setWoDescription(wo.getWoDescription());

			auditWo.setSite(wo.getSite());

			auditWo.setLocation(wo.getLocation());

			auditWo.setNhWo(wo.getNhWo());
		
			auditWo.getId().setWo(wo.getWo());
			
			auditWo.setTransactionType("NEW");
			
			logger.info("INSERTING AUDIT RFO: "+ auditWo.getRefurbishmentOrder() + " WO: " + auditWo.getId().getWo());
			
			insertData(auditWo, "auditWo", "auditWo");
			
			return true;
					
		}
		
		
		public boolean lockAvailable(String notificationType)
		{
			
			lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType).getSingleResult();
			if(lock.getLocked().intValue() == 1)
			{				
				LocalDateTime today = LocalDateTime.now();
				LocalDateTime locked = LocalDateTime.ofInstant(lock.getLockedDate().toInstant(), ZoneId.systemDefault());
				Duration diff = Duration.between(locked, today);
				if(diff.getSeconds() >= lock.getMaxLock().longValue())
				{
					
					return true;
				}
				return false;
			}
			else
			{
				return true;
			}
			
		}
		
		
		public void lockTable(String notificationType)
		{
			em.getTransaction().begin();
			lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType)
					.setLockMode(LockModeType.PESSIMISTIC_WRITE)
					.getSingleResult();
			lock.setLocked(new BigDecimal(1));
			lock.setLockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			InetAddress address = null;
			try {
				address = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				logger.info(e.getMessage());
			}
			lock.setCurrentServer(address.getHostName());
			em.lock(lock, LockModeType.NONE);
			em.merge(lock);
			em.getTransaction().commit();
		}
		
		public void unlockTable(String notificationType)
		{
			em.getTransaction().begin();
			lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
					.setParameter("type", notificationType)
					.setLockMode(LockModeType.PESSIMISTIC_WRITE)
					.getSingleResult();
			lock.setLocked(new BigDecimal(0));
			lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
			em.lock(lock, LockModeType.NONE);
			em.merge(lock);
			em.getTransaction().commit();
		}

}
