package trax.aero.data;


import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
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

import trax.aero.controller.MaterialMovementController;
import trax.aero.interfaces.IMaterialMovementData;
import trax.aero.logger.LogManager;
import trax.aero.model.BlobTable;
import trax.aero.model.BlobTablePK;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.model.PicklistDistribution;
import trax.aero.model.PicklistDistributionPK;
import trax.aero.model.PicklistHeader;
import trax.aero.model.PnInventoryDetail;
import trax.aero.model.PnInventoryHistory;
import trax.aero.model.PnInventoryHistoryPK;
import trax.aero.model.Wo;
import trax.aero.model.WoTaskCard;
import trax.aero.pojo.MaterialMovementMaster;
import trax.aero.pojo.OpsLineEmail;
import trax.aero.utils.SharePointPoster;





/*
 SELECT DISTINCT w.ops_line FROM WO w, WO_TASK_CARD wtc 
WHERE wtc.WO = '606567' AND wtc.task_card = 'DHC-6-3-34-060-750'  AND wtc.wo = w.wo and ROWNUM <= 1;   
 */

@Stateless(name="MaterialMovementData" , mappedName="MaterialMovementData")
public class MaterialMovementData implements IMaterialMovementData {
	
	
	@PersistenceContext(unitName = "TraxStandaloneDS") private EntityManager em;
	
	String exceuted;
	
	
	private Logger logger = LogManager.getLogger("MaterialMovement_I42&I44");
	public InterfaceLockMaster lock;
	
	
	
	public MaterialMovementData()
	{
		
			
	}
	
	
	
	public String updateMaterial(MaterialMovementMaster materialMovementMaster)
	{
		//setting up variables
		exceuted = "OK";
		
		try 
		{
			updateMaterials(materialMovementMaster);
		}
		catch (Exception e) 
        {
			e.printStackTrace();
			MaterialMovementController.addError(e.toString());
            logger.severe(e.toString());
            em.getTransaction().rollback();
            exceuted = e.toString();
		}
		finally
		{
			//clean up 
			em.clear();

		}
		return exceuted;
	}
	
	//update a material
	private void updateMaterials(MaterialMovementMaster input) 
	{	
		logger.info("Inside updateMaterials");
		//setting up variables
		PnInventoryDetail pnInventoryDetail = null;
		PicklistHeader picklistHeader = null;
		PicklistDistribution picklistDistributionDIS = null;
		PicklistDistribution picklistDistributionREQ = null;
		
		WoTaskCard woTaskCard = null;
		Wo wo = null;

		//check if object has min values
		if(input != null  && checkMinValue(input)) 
		{
			logger.info("After checkMinValue");
			String partNumber_Tool ;
			partNumber_Tool = input.getMaterial().replaceAll("\"", "IN");
			partNumber_Tool = partNumber_Tool.replaceAll("'", "FT");
			if(!partNumber_Tool.contains(":"))
			{
				partNumber_Tool = partNumber_Tool.concat(":UPLOAD");
			}
			input.setMaterial(partNumber_Tool);
			
			try 
			{
					
					pnInventoryDetail = getPnInventoryDetail(input);
					woTaskCard= getWoTaskCard(input);
					
					if(woTaskCard == null) {
						wo = getWo(input);
					}
					
					if(input.getRequisitionNumber() != null && input.getRequisitionItem() != null &&
					!input.getRequisitionNumber().isEmpty() && !input.getRequisitionItem().isEmpty()) {
						picklistHeader = getPicklistHeader(input);
						picklistDistributionDIS = getPicklistDistribution(picklistHeader, input,"DISTRIBU",null);
						picklistDistributionREQ = getPicklistDistribution(picklistHeader, input,"REQUIRE",null);
					}else if(!input.getReservationNumber().equalsIgnoreCase("0000000000") && !input.getReservationItem().equalsIgnoreCase("0000")){
						
						picklistHeader = getPicklistHeaderRev(input);
						picklistDistributionREQ = getPicklistDistribution(picklistHeader, input,"REQUIRE",null );
						picklistDistributionDIS = getPicklistDistribution(picklistHeader, input,"DISTRIBU",picklistDistributionREQ);
						
					}else {
						
						picklistHeader = getPicklistHeaderTaskCard(woTaskCard,input);
						if(picklistHeader == null ) {
							picklistHeader= getPicklistHeaderTaskCardFirtOne(woTaskCard, input);
							if(picklistHeader == null ) {
								picklistHeader = getPicklistHeaderWo(wo,input);
								if(picklistHeader == null ) {
									picklistHeader = getPicklistHeaderWoFirtOne(wo,input);			
								}
							}
						}
						
						picklistDistributionREQ = getPicklistDistribution(picklistHeader, input,"REQUIRE",null );
						if(picklistDistributionREQ == null) {
							throw new Exception ("picklistDistribution REQ is null");
						}
						picklistDistributionDIS = getPicklistDistribution(picklistHeader, input,"DISTRIBU",picklistDistributionREQ);
						
					}
					
				}
				catch(Exception e)
				{
					logger.info("Exception 1 caught");
					e.printStackTrace();
					logger.info(e.getMessage());	
					logger.info(e.toString());
					exceuted = "Can not update Material QTY: "+ input.getMaterial() +" as ERROR: material or WO Task Card or PickList can not be found";
					logger.severe(exceuted);
					MaterialMovementController.addError(exceuted);
					return;
					
				}
				
				pnInventoryDetail.setModifiedBy("TRAX_IFACE");
				pnInventoryDetail.setModifiedDate(new Date());
				
				
				
				picklistDistributionDIS.setModifiedBy("TRAX_IFACE");
				picklistDistributionDIS.setModifiedDate(new Date());
				
				//issue
				if(input.getMovementType().equalsIgnoreCase("261")) {
				
					logger.info("Inside 261");
					BigDecimal qtyReserv = pnInventoryDetail.getQtyReserved();
					
					if(picklistHeader.getCreatedBy().equalsIgnoreCase("TRAX_IFACE") || picklistHeader.getCreatedBy().equalsIgnoreCase("TRAXIFACE")) {
						logger.info("After first if");
						if((new BigDecimal(input.getQuantity()).compareTo(picklistDistributionREQ.getQty()) <= 0) && picklistHeader.getStatus().equalsIgnoreCase("OPEN")) {
							logger.info("After second if");
							pnInventoryDetail.setQtyReserved(qtyReserv.subtract(new BigDecimal(input.getQuantity())));
							logger.info("Reserved " + pnInventoryDetail.getQtyReserved());
							picklistDistributionDIS.setQtyPicked(new BigDecimal(input.getQuantity()));
							picklistDistributionDIS.setQty(new BigDecimal(input.getQuantity()));
							logger.info("Picked " + input.getQuantity());
							if(picklistDistributionDIS.getQtyPicked().compareTo(
									picklistDistributionREQ.getQty()) == 0) {
								logger.info("inside compare to");
								
								picklistDistributionREQ.setStatus("ISSUED");
								picklistDistributionDIS.setStatus("ISSUED");
								picklistHeader.setStatus("CLOSED");
							
							}else if(picklistDistributionDIS.getQtyPicked().add(getPnInevtoryHistoryIssue(pnInventoryDetail,input,woTaskCard, "ISSUED",picklistDistributionDIS))
									.compareTo(
									picklistDistributionREQ.getQty()) == 0) {
								
								logger.info("inside compare to");
								
								BigDecimal picked = picklistDistributionDIS.getQtyPicked().add(getPnInevtoryHistoryIssue(pnInventoryDetail,input,woTaskCard, "ISSUED",picklistDistributionDIS));
								logger.info("Picked " + picked);
								picklistDistributionDIS.setQtyPicked(picked);
								picklistDistributionDIS.setQty(picked);
								
								
								picklistDistributionREQ.setStatus("ISSUED");
								picklistDistributionDIS.setStatus("ISSUED");
								picklistHeader.setStatus("CLOSED");
							}
							if(woTaskCard != null) {
								setPnInevtoryHistory(pnInventoryDetail,input,woTaskCard, "ISSUED",picklistDistributionDIS );
							}else {
								setPnInevtoryHistory(pnInventoryDetail,input,wo, "ISSUED",picklistDistributionDIS );
							}
						}else {
							logger.info("Inside inner else");
							exceuted = "Can not issue Material QTY: "+ input.getMaterial() +" as ERROR: QTY issued from PN inventory is greater than PN inventory QTY or QTY picked is greater than Picklist required or status is not open";
							logger.severe(exceuted);
							MaterialMovementController.addError(exceuted);
							exceuted = "Pn Inventory QTY: "+ pnInventoryDetail.getQtyAvailable() + " Requested QTY:" +input.getQuantity() + " Picklist QTY REQ: " + picklistDistributionREQ.getQty() +  " PickList status: "+ picklistHeader.getStatus();
							logger.severe(exceuted);
							MaterialMovementController.addError(exceuted);
							exceuted = "PickList: "+ picklistHeader.getPicklist() + " Set to Cancel";
							logger.severe(exceuted);
							MaterialMovementController.addError(exceuted);
							
							picklistDistributionREQ.setStatus("CANCEL");
							picklistDistributionDIS.setStatus("CANCEL");
							boolean headStatus = true;
							for(PicklistDistribution pd : picklistHeader.getPicklistDistributions()) {
								if(!pd.getStatus().equalsIgnoreCase("CANCEL") && 
									pd.getId().getPicklistLine() != picklistDistributionDIS.getId().getPicklist()) {
									headStatus = false;
								}
							}
							if(headStatus) {
								picklistHeader.setStatus("CANCEL");
							}
							
						}
					}else {
						logger.info("Inside outer else");
						if((new BigDecimal(input.getQuantity()).compareTo(picklistDistributionREQ.getQty()) <= 0) && picklistHeader.getStatus().equalsIgnoreCase("OPEN")) {
							
							pnInventoryDetail.setQtyReserved(qtyReserv.subtract(new BigDecimal(input.getQuantity())));

							logger.info("Inside outer else");
							picklistDistributionDIS.setQtyPicked(new BigDecimal(input.getQuantity()));
							logger.info("Picked " + input.getQuantity());
							picklistDistributionDIS.setQty(new BigDecimal(input.getQuantity()));
							
							if(picklistDistributionDIS.getQtyPicked().compareTo(
									picklistDistributionREQ.getQty()) == 0) {
								logger.info("Inside compareTo");
							
							picklistDistributionREQ.setStatus("ISSUED");
							picklistDistributionDIS.setStatus("ISSUED");
							picklistHeader.setStatus("CLOSED");
							
							}else if(picklistDistributionDIS.getQtyPicked().add(getPnInevtoryHistoryIssue(pnInventoryDetail,input,woTaskCard, "ISSUED",picklistDistributionDIS))
									.compareTo(
									picklistDistributionREQ.getQty()) == 0) {
								
								logger.info("inside compare to");
								
								BigDecimal picked = picklistDistributionDIS.getQtyPicked().add(getPnInevtoryHistoryIssue(pnInventoryDetail,input,woTaskCard, "ISSUED",picklistDistributionDIS))
										;
								logger.info("Picked " + picked);
								picklistDistributionDIS.setQtyPicked(picked);
								picklistDistributionDIS.setQty(picked);
								
								
								picklistDistributionREQ.setStatus("ISSUED");
								picklistDistributionDIS.setStatus("ISSUED");
								picklistHeader.setStatus("CLOSED");
							}
							
							if(woTaskCard != null) {
								setPnInevtoryHistory(pnInventoryDetail,input,woTaskCard, "ISSUED",picklistDistributionDIS );
							}else {
								setPnInevtoryHistory(pnInventoryDetail,input,wo, "ISSUED",picklistDistributionDIS );
							}								
						}else {
							logger.info("Inside inner else");
							exceuted = "Can not issue Material QTY: "+ input.getMaterial() +" as ERROR: QTY issued from PN inventory is greater than PN inventory QTY or QTY picked is greater than Picklist required or status is not open";
							logger.severe(exceuted);
							MaterialMovementController.addError(exceuted);
							exceuted = "Pn Inventory QTY: "+ pnInventoryDetail.getQtyAvailable() + " Requested QTY:" +input.getQuantity() + " Picklist QTY REQ: " + picklistDistributionREQ.getQty() +  " PickList status: "+ picklistHeader.getStatus();
							logger.severe(exceuted);
							MaterialMovementController.addError(exceuted);
							exceuted = "PickList: "+ picklistHeader.getPicklist() + " Set to Cancel";
							logger.severe(exceuted);
							MaterialMovementController.addError(exceuted);
							
							picklistDistributionREQ.setStatus("CANCEL");
							picklistDistributionDIS.setStatus("CANCEL");
							
							boolean headStatus = true;
							for(PicklistDistribution pd : picklistHeader.getPicklistDistributions()) {
								if(!pd.getStatus().equalsIgnoreCase("CANCEL") && 
									pd.getId().getPicklistLine() != picklistDistributionDIS.getId().getPicklist()) {
									headStatus = false;
								}
							}
							if(headStatus) {
								picklistHeader.setStatus("CANCEL");
							}
						}
					}
					
				}
				//Return
				else if(input.getMovementType().equalsIgnoreCase("262")) {
				
					logger.info("Inside 262");
					BigDecimal qtySubPickPicklistDIS = picklistDistributionDIS.getQtyPicked().subtract(new BigDecimal(input.getQuantity()));
					BigDecimal qtySubPicklistDIS = picklistDistributionDIS.getQty().subtract(new BigDecimal(input.getQuantity()));

					logger.info("Qty " + input.getQuantity());
					logger.info("Pick Picklist " + qtySubPickPicklistDIS.longValue());
					logger.info("Picklist " + qtySubPicklistDIS.longValue());
					
					BigDecimal qtyAddPn = pnInventoryDetail.getQtyAvailable().add(new BigDecimal(input.getQuantity()));
					BigDecimal qtySubPn = pnInventoryDetail.getQtyReserved().subtract(new BigDecimal(input.getQuantity()));
					
					
					logger.info("ADD qty " + qtyAddPn.longValue());
					logger.info("Sub qty " + qtySubPn.longValue());
						
					logger.info("Inside if for qtySubPickPicklistDIS");
						
					pnInventoryDetail.setQtyAvailable(qtyAddPn);
					if(woTaskCard != null) {
						setPnInevtoryHistory(pnInventoryDetail,input,woTaskCard, "RTS/WO",picklistDistributionDIS );
					}else {
						setPnInevtoryHistory(pnInventoryDetail,input,wo, "RTS/WO",picklistDistributionDIS );
					}
				}else {
					exceuted = "Can not update Material QTY: "+ input.getMaterial() +" as ERROR: Movement type is not valid";
					logger.severe(exceuted);
					MaterialMovementController.addError(exceuted);
				}
								
				if(input.getAttachedDocumentIDOC() != null ) 
				{
					if(woTaskCard != null) {
						woTaskCard = setAttachedDocument(woTaskCard,input);	
					}else if(wo != null){
						wo = setAttachedDocument(wo,input);	
					}
				}
				if(input.getAttachmentLinkSharepointlink() != null ) 
				{	
					byte[] file = getsharePointfile(new String(input.getAttachmentLinkSharepointlink(), StandardCharsets.UTF_8));
					String fileName = new String(input.getAttachmentLinkSharepointlink(), StandardCharsets.UTF_8);
					try {
						URL url =new URL(new String(input.getAttachmentLinkSharepointlink(), StandardCharsets.UTF_8));
						fileName = url.getFile().substring(url.getFile().lastIndexOf("/")+1,url.getFile().length());
					} catch (MalformedURLException e) {
						
					}
					if(woTaskCard != null) {
						woTaskCard = setAttachmentLink(woTaskCard,input,file,fileName);
					}else if(wo != null){
						wo = setAttachmentLink(wo,input,file,fileName);

					}
				}
				
				
				
				logger.info("UPDATING pnInventoryDetail: " + input.getMaterial());
				
				insertData(pnInventoryDetail);
				
				logger.info("UPDATING picklistDistribution: " + picklistHeader.getPicklist());
				insertData(picklistDistributionDIS);
				insertData(picklistDistributionREQ);
				
				insertData(picklistHeader);
			
				em.clear();
		}else 
		{
			exceuted = "Can not update Material: "+ input.getMaterial() +" as ERROR: Material is null or does not have minimum values";
			logger.severe(exceuted);
			MaterialMovementController.addError(exceuted);
		}
			
				
	}
	

	



	private Wo setAttachmentLink(Wo wo, MaterialMovementMaster input, byte[] file, String fileName) {
		BlobTable blob = null;
		if(file == null) {
			try 
			{
				blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.webLink = :link", BlobTable.class)
						.setParameter("bl", wo.getBlobNo().longValue())
						.setParameter("link", new String(input.getAttachmentLinkSharepointlink(), StandardCharsets.UTF_8))
						.getSingleResult();
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				BlobTablePK pk = new BlobTablePK();
				blob = new BlobTable();
				blob.setCreatedDate(new Date());
				blob.setCreatedBy("TRAX_IFACE");
				blob.setId(pk);
				
				blob.setPrintFlag("YES");
				blob.setBlobType("EXTLINK");
				if(wo.getBlobNo() == null) {
					try {
						blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
						wo.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
					} catch (Exception e1) {
					}
				}else {
					blob.getId().setBlobNo(wo.getBlobNo().longValue());
				}
				blob.getId().setBlobLine(getLine(wo.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
				
			}
			
			
			   
			
			blob.setModifiedBy("TRAX_IFACE");
			blob.setModifiedDate(new Date());
			blob.setWebLink(new String(input.getAttachmentLinkSharepointlink(), StandardCharsets.UTF_8));
			blob.setBlobDescription(fileName);
			blob.setCustomDescription("AttachmentLink");
			
			//blob.setDocType("LINK");
			
			
			
			logger.info("UPDATING wo:  WO: " + wo.getWo()  );
			insertData(wo);
			
			logger.info("UPDATING blob: " + blob.getId().getBlobNo());
			insertData(blob);
		
			return wo;

		}else {
			try 
			{
				blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des and b.customDescription = :cus", BlobTable.class)
						.setParameter("bl", wo.getBlobNo().longValue())
						.setParameter("des",fileName )
						.setParameter("cus","SHAREPOINT" )
						.getSingleResult();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				BlobTablePK pk = new BlobTablePK();
				blob = new BlobTable();
				blob.setCreatedDate(new Date());
				blob.setCreatedBy("TRAX_IFACE");
				blob.setId(pk);
				
				blob.setPrintFlag("YES");
				
				if(wo.getBlobNo() == null) {
					try {
						blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
						wo.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
					} catch (Exception e1) {
					}
				}else {
					blob.getId().setBlobNo(wo.getBlobNo().longValue());
				}
				
				blob.getId().setBlobLine(getLine(wo.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
				
				
			}
			
			
			blob.setDocType("SHAREPOINT");
			
			
			blob.setModifiedBy("TRAX_IFACE");
			blob.setModifiedDate(new Date());
			blob.setBlobItem(file);
			blob.setBlobDescription(fileName);
			blob.setCustomDescription("SHAREPOINT");
			
			
			
			logger.info("UPDATING wo: WO: " + wo.getWo()  );
			insertData(wo);
			
			logger.info("UPDATING blob: " + blob.getId().getBlobNo());
			insertData(blob);
		
			return wo;
		}
	}



	private Wo setAttachedDocument(Wo wo, MaterialMovementMaster input) {
		BlobTable blob = null;
		String filename = input.getMaterial()+ "_"+ input.getPlant() +"_IDOC.pdf";
		
		try 
		{
			blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
					.setParameter("bl", wo.getBlobNo().longValue())
					.setParameter("des",filename )
					.getSingleResult();
			//existBlob = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			BlobTablePK pk = new BlobTablePK();
			blob = new BlobTable();
			blob.setCreatedDate(new Date());
			blob.setCreatedBy("TRAX_IFACE");
			blob.setId(pk);
			
			blob.setPrintFlag("YES");
			
			blob.getId().setBlobLine(getLine(wo.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
			
			if(wo.getBlobNo() == null) {
				try {
					blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
					wo.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
				} catch (Exception e1) {
				}
			}else {
				blob.getId().setBlobNo(wo.getBlobNo().longValue());
			}
		}
		
		
		blob.setDocType("IDOC");
		
		
		blob.setModifiedBy("TRAX_IFACE");
		blob.setModifiedDate(new Date());
		blob.setBlobItem(input.getAttachedDocumentIDOC());
		blob.setBlobDescription(filename);
		blob.setCustomDescription(filename);
		
		
		
		logger.info("UPDATING wo: WO: " + wo.getWo()  );
		insertData(wo);
		
		logger.info("UPDATING blob: " + blob.getId().getBlobNo());
		insertData(blob);
	
		return wo;
	}



	private Wo getWo(MaterialMovementMaster input) {
		Wo wo = null;
		try {
			wo = em.createQuery("Select w From Wo w where w.refurbishmentOrder =:ord", Wo.class)
					.setParameter("ord", input.getOrderNumber())
					.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return wo;
	}



	private BigDecimal getPnInevtoryHistoryIssue(PnInventoryDetail pnInventoryDetail, MaterialMovementMaster input,
			WoTaskCard woTaskCard, String string, PicklistDistribution picklistDistributionDIS) {
		
		 BigDecimal sum = new BigDecimal(0);
		
		try {
			List<PnInventoryHistory> pihs = em.createQuery("SELECT p FROM PnInventoryHistory p WHERE "
					+ " p.orderNo = :pick and p.orderLine = :pickline "
					+ " and p.orderType = :pic and p.pn = :pnn")
					
					.setParameter("pick", new BigDecimal( picklistDistributionDIS.getId().getPicklist()))
					.setParameter("pickline",new BigDecimal( picklistDistributionDIS.getId().getPicklistLine()))
					.setParameter("pic","PICKLST")
					.setParameter("pnn", pnInventoryDetail.getPn())
					.getResultList();
			logger.info("PnInventoryHistory ISSUE SIZE " +pihs.size());
			for(PnInventoryHistory pih : pihs) {
				if(pih.getTransactionType().equalsIgnoreCase("ISSUED")) {
					sum = sum.add(pih.getQty());
				}else if(pih.getTransactionType().equalsIgnoreCase("RTS/WO")) {
					sum = sum.subtract(pih.getQty());
				}
			}
			
			return sum;
		}catch (Exception e) {
			e.printStackTrace();
			return new BigDecimal(0);
		}
		
		
	}



	private PnInventoryDetail getPnInventoryDetail(MaterialMovementMaster input) {
		PnInventoryDetail pnInventoryDetail = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn is null and p.location = :loc"
				+ " and p.createdBy != :create ", PnInventoryDetail.class)
				.setParameter("par", input.getMaterial())
				.setParameter("loc", input.getPlant())
				.setParameter("create", "ISSUEIFACE")
				.getSingleResult();
		logger.info("Found PnInventoryDetail");
		return pnInventoryDetail;
	}
	
	private WoTaskCard getWoTaskCard(MaterialMovementMaster input) {
		

		WoTaskCard woTaskCard = null;
		try
		{
			woTaskCard = (WoTaskCard) em.createQuery("Select w from WoTaskCard w, PicklistHeader p where w.id.wo = p.wo and "
					+ "w.id.taskCard = p.taskCard and w.id.pn = p.taskCardPn and w.id.pnSn = p.taskCardSn and "
					+ "p.picklist = :pick ")
					.setParameter("pick", Long.parseLong(input.getRequisitionNumber()))
					.getSingleResult();
			logger.info("Found WoTaskCard 1");
		}
		catch(Exception e)
		{
			try {
				woTaskCard = (WoTaskCard) em.createQuery("Select w from WoTaskCard w, PicklistHeader p, Wo wo, PicklistDistribution pd where w.id.wo = p.wo and "
						+ "w.id.taskCard = p.taskCard and w.id.pn = p.taskCardPn and w.id.pnSn = p.taskCardSn and "
						+ "wo.refurbishmentOrder = :order and pd.pn = :pn and p.picklist = pd.id.picklist and wo.wo = w.id.wo")
						.setParameter("order", input.getOrderNumber())
						.setParameter("pn", input.getMaterial())
						.setMaxResults(1)
						.getSingleResult();
				logger.info("Found WoTaskCard 2");
			}catch(Exception ex)
			{
				try {
				woTaskCard = em.createQuery("SELECT w FROM WoTaskCard w WHERE w.referenceTaskCard = :or AND (w.nonRoutine <> :cu OR w.nonRoutine IS NULL) ", WoTaskCard.class)
						.setParameter("or", input.getOrderNumber())
						.setParameter("cu", "S")
						.getSingleResult();
				
				logger.info("Found WoTaskCard 3");
				
				}catch(Exception exc) {
					try {
						woTaskCard = (WoTaskCard) em.createQuery("Select w from WoTaskCard w,Wo woo where "
								+ "woo.refurbishmentOrder = :order and woo.wo = w.id.wo and woo.module = :mod and (w.nonRoutine is null or w.nonRoutine <> :cu) ")
								.setParameter("order", input.getOrderNumber())
								.setParameter("mod", "SHOP")
								.setParameter("cu", "S")
								.setMaxResults(1)
								.getSingleResult();
						
						logger.info("Found WoTaskCard 4");
					}catch (Exception e4) {
						return woTaskCard;
					}
				}
			}
		}
		return woTaskCard;
	}
	
	private PicklistHeader getPicklistHeader(MaterialMovementMaster input) {
		
		PicklistHeader picklistHeader = em.createQuery("SELECT p FROM PicklistHeader p WHERE p.id.picklist = :woo", PicklistHeader.class)
					.setParameter("woo", new BigDecimal(input.getRequisitionNumber()).longValue())
					.getSingleResult();
		logger.info("Found PicklistHeader");
			return picklistHeader;
		}
	
	
	
	private PicklistDistribution getPicklistDistribution(PicklistHeader picklistHeader, MaterialMovementMaster input, String transaction, PicklistDistribution picklistDistributionREQ) {
		PicklistDistribution picklistDistribution = null;
		boolean newRecord = true;
		
		if(input.getRequisitionItem() != null  && !input.getRequisitionItem().isEmpty()) {
		
		
			try {
				picklistDistribution = em.createQuery("SELECT p FROM PicklistDistribution p WHERE p.id.picklist = :pic AND p.id.picklistLine = :res AND p.id.transaction = :act", PicklistDistribution.class)
						.setParameter("pic", picklistHeader.getPicklist())
						.setParameter("res",new BigDecimal(input.getRequisitionItem()).longValue())
						.setParameter("act",transaction)
						.getSingleResult();
				newRecord= false;
			}catch(Exception e) {
				
				if(transaction.equalsIgnoreCase("DISTRIBU")) {
					//EMRO fields to create basic object
					PicklistDistributionPK pk = new PicklistDistributionPK();
					picklistDistribution = new PicklistDistribution();
					picklistDistribution.setId(pk);
					picklistDistribution.setCreatedBy("TRAX_IFACE");
					picklistDistribution.setCreatedDate(new Date());
					picklistDistribution.setQtyPicked(new BigDecimal(1));
					picklistDistribution.setModifiedDate(new Date());
					picklistDistribution.setModifiedBy("TRAX_IFACE");
					picklistDistribution.setStatus("OPEN");
					picklistDistribution.setExternalCustRes(input.getReservationNumber());
					picklistDistribution.setExternalCustResItem(input.getReservationItem());
					picklistDistribution.getId().setPicklist(picklistHeader.getPicklist() );
					
					
					picklistDistribution.getId().setPicklistLine(new BigDecimal(input.getRequisitionItem()).longValue());
					
					picklistDistribution.getId().setDistributionLine(new Long(2));
					picklistDistribution.getId().setTransaction(transaction);
					picklistDistribution.setPn(input.getMaterial());
					picklistDistribution.setQty(new BigDecimal(input.getQuantity()));
					picklistDistribution.setPicklistHeader(picklistHeader);
				}
				
			}
			if(transaction.equalsIgnoreCase("DISTRIBU") && newRecord && picklistDistribution != null) {
				logger.info("INSERTING PICKLIST NEW DISTRIBUTION: " +picklistDistribution.getId().getPicklist()  );
				insertData(picklistDistribution);	
			}
		}else if(!input.getReservationNumber().equalsIgnoreCase("0000000000") && !input.getReservationItem().equalsIgnoreCase("0000")){
			try {
				picklistDistribution = em.createQuery("SELECT p FROM PicklistDistribution p WHERE p.id.picklist = :pic AND p.externalCustRes = :req AND p.externalCustResItem = :reqit AND p.id.transaction = :act", PicklistDistribution.class)
						.setParameter("pic", picklistHeader.getPicklist())
						.setParameter("req",input.getReservationNumber())
						.setParameter("reqit",input.getReservationItem())
						.setParameter("act",transaction)
						.getSingleResult();
				newRecord= false;
			}catch(Exception e) {
				
				if(transaction.equalsIgnoreCase("DISTRIBU")) {
					//EMRO fields to create basic object
					PicklistDistributionPK pk = new PicklistDistributionPK();
					picklistDistribution = new PicklistDistribution();
					picklistDistribution.setId(pk);
					picklistDistribution.setCreatedBy("TRAX_IFACE");
					picklistDistribution.setCreatedDate(new Date());
					picklistDistribution.setQtyPicked(new BigDecimal(1));
					picklistDistribution.setModifiedDate(new Date());
					picklistDistribution.setModifiedBy("TRAX_IFACE");
					picklistDistribution.setStatus("OPEN");
					picklistDistribution.setExternalCustRes(input.getReservationNumber());
					picklistDistribution.setExternalCustResItem(input.getReservationItem());
					picklistDistribution.getId().setPicklist(picklistHeader.getPicklist() );
					
					if(picklistDistributionREQ != null ) {
						picklistDistribution.getId().setPicklistLine(picklistDistributionREQ.getId().getPicklistLine());
						
					}else {
						picklistDistribution.getId().setPicklistLine(new Long(1).longValue());
					}
					
					picklistDistribution.getId().setDistributionLine(new Long(2));
					picklistDistribution.getId().setTransaction(transaction);
					picklistDistribution.setPn(input.getMaterial());
					picklistDistribution.setQty(new BigDecimal(input.getQuantity()));
					picklistDistribution.setPicklistHeader(picklistHeader);
				}
			}
		}else {
				try {
					picklistDistribution = em.createQuery("SELECT p FROM PicklistDistribution p WHERE p.id.picklist = :pic AND p.pn = :pnn AND p.id.transaction = :act", PicklistDistribution.class)
							.setParameter("pic", picklistHeader.getPicklist())
							.setParameter("pnn",input.getMaterial())
							.setParameter("act",transaction)
							.getSingleResult();
					newRecord= false;
				}catch(Exception e) {
					newRecord = true;
					try {
						
						picklistDistribution = em.createQuery("SELECT p FROM PicklistDistribution p WHERE p.id.picklist = :pic AND p.id.transaction = :act", PicklistDistribution.class)
								.setParameter("pic", picklistHeader.getPicklist())
								.setParameter("act",transaction)
								.getSingleResult();
						newRecord= false;
					}catch(Exception e1) {
						if(transaction.equalsIgnoreCase("DISTRIBU")) {
							//EMRO fields to create basic object
							PicklistDistributionPK pk = new PicklistDistributionPK();
							picklistDistribution = new PicklistDistribution();
							picklistDistribution.setId(pk);
							picklistDistribution.setCreatedBy("TRAX_IFACE");
							picklistDistribution.setCreatedDate(new Date());
							picklistDistribution.setQtyPicked(new BigDecimal(1));
							picklistDistribution.setModifiedDate(new Date());
							picklistDistribution.setModifiedBy("TRAX_IFACE");
							picklistDistribution.setStatus("OPEN");
							picklistDistribution.setExternalCustRes(input.getReservationNumber());
							picklistDistribution.setExternalCustResItem(input.getReservationItem());
							picklistDistribution.getId().setPicklist(picklistHeader.getPicklist() );
							
							if(picklistDistributionREQ != null ) {
								picklistDistribution.getId().setPicklistLine(picklistDistributionREQ.getId().getPicklistLine());
								
							}else {
								picklistDistribution.getId().setPicklistLine(new Long(1).longValue());
							}
							
							picklistDistribution.getId().setDistributionLine(new Long(2));
							picklistDistribution.getId().setTransaction(transaction);
							if(picklistDistributionREQ != null ) {
								picklistDistribution.setPn(picklistDistributionREQ.getPn());
							}else {
								picklistDistribution.setPn(input.getMaterial());
							}
							picklistDistribution.setQty(new BigDecimal(input.getQuantity()));
							picklistDistribution.setPicklistHeader(picklistHeader);
					}
					
					}
			}
			if(transaction.equalsIgnoreCase("DISTRIBU") && newRecord && picklistDistribution != null) {
				logger.info("INSERTING PICKLIST NEW DISTRIBUTION: " +picklistDistribution.getId().getPicklist()  );
				insertData(picklistDistribution);	
			}
		}
			
		logger.info("Found picklistDistribution");	
		return picklistDistribution;
	}
	
	
	//****************** Helper functions ******************
	
	
	
	
	//insert generic data from model objects
	private <T> void insertData( T data) 
	{
		try 
		{	
			em.merge(data);
			em.flush();
						
		}catch (Exception e)
		{
			e.printStackTrace();
			exceuted = "insertData has encountered an Exception: "+e.toString();
			MaterialMovementController.addError(exceuted);
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
	
		private boolean checkMinValue(MaterialMovementMaster input) {
		
			if( input.getMovementType() == null || input.getMovementType().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR MovementType");
				return false;
			}else {
				
			}
			
			if( input.getOrderNumber() == null || input.getOrderNumber().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR OrderNumber");
				return false;
			}
			if( input.getReservationNumber() == null || input.getReservationNumber().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR ReservationNumber");
				return false;
			}	
			if( input.getMaterial() == null || input.getMaterial().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR Material");
				return false;
			}
			if( input.getPlant() == null || input.getPlant().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR Plant");
				return false;
			}
			if( input.getQuantity() == null || input.getQuantity().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR Quantity");
				return false;
			}
			if( input.getBatch() == null || input.getBatch().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR Batch");
				return false;
			}
			if( input.getStorageLocation() == null || input.getStorageLocation().isEmpty()) {
				MaterialMovementController.addError("Can not update Material QTY: "+ input.getMaterial() +" as ERROR Storagelocation");
				return false;
			}
			
			
			return true;
		}
	
	
	public String getemailByOnlyOpsLine( String opsLine){
		
		String email = "ERROR";
		String sql = " Select \"EMAIL\" FROM OPS_LINE_EMAIL_MASTER where OPS_LINE = ?";
		
		
		try
		{
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, opsLine);  
		
			email = (String) query.getSingleResult(); 
				
			
		}
		catch (Exception e) 
		{
			email = "ERROR";
		}
		
		
		return email;
		
	}



	public OpsLineEmail getOpsLineStaffName(MaterialMovementMaster input){
	
		OpsLineEmail OpsLineEmail =  new OpsLineEmail();
		
		String sql = "SELECT DISTINCT w.ops_line FROM WO w, WO_TASK_CARD wtc WHERE wtc.reference_task_card =  ? AND wtc.wo = w.wo and ROWNUM <= 1";
			
		try
		{
			
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, input.getOrderNumber());  
			String ops = (String) query.getSingleResult(); 
			OpsLineEmail.setOpsLine(ops);	
		}
		catch (Exception e) 
		{
			logger.severe(e.getMessage());
			OpsLineEmail.setOpsLine("");
		}
		finally 
		{
			OpsLineEmail.setEmail(getemailByOnlyOpsLine(OpsLineEmail.getOpsLine()));
		}
		
		return OpsLineEmail;
		
	}
	

	private WoTaskCard setAttachedDocument(WoTaskCard woTaskCard, MaterialMovementMaster input) {
		BlobTable blob = null;
		String filename = input.getMaterial()+ "_"+ input.getPlant() +"_IDOC.pdf";
		
		try 
		{
			blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des", BlobTable.class)
					.setParameter("bl", woTaskCard.getBlobNo().longValue())
					.setParameter("des",filename )
					.getSingleResult();
			//existBlob = true;
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			BlobTablePK pk = new BlobTablePK();
			blob = new BlobTable();
			blob.setCreatedDate(new Date());
			blob.setCreatedBy("TRAX_IFACE");
			blob.setId(pk);
			
			blob.setPrintFlag("YES");
			
			blob.getId().setBlobLine(getLine(woTaskCard.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
			
			if(woTaskCard.getBlobNo() == null) {
				try {
					blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
					woTaskCard.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
				} catch (Exception e1) {
				}
			}else {
				blob.getId().setBlobNo(woTaskCard.getBlobNo().longValue());
			}
		}
		
		
		blob.setDocType("IDOC");
		
		
		blob.setModifiedBy("TRAX_IFACE");
		blob.setModifiedDate(new Date());
		blob.setBlobItem(input.getAttachedDocumentIDOC());
		blob.setBlobDescription(filename);
		blob.setCustomDescription(filename);
		
		
		
		logger.info("UPDATING woTaskCard: TASK_CARD: " + woTaskCard.getId().getTaskCard() + " WO: " + woTaskCard.getId().getWo()  );
		insertData(woTaskCard);
		
		logger.info("UPDATING blob: " + blob.getId().getBlobNo());
		insertData(blob);
	
		return woTaskCard;
	
	}
	
	private WoTaskCard setAttachmentLink(WoTaskCard woTaskCard, MaterialMovementMaster input, byte[] file, String fileName) {
		BlobTable blob = null;
		if(file == null) {
			try 
			{
				blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.webLink = :link", BlobTable.class)
						.setParameter("bl", woTaskCard.getBlobNo().longValue())
						.setParameter("link", new String(input.getAttachmentLinkSharepointlink(), StandardCharsets.UTF_8))
						.getSingleResult();
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				BlobTablePK pk = new BlobTablePK();
				blob = new BlobTable();
				blob.setCreatedDate(new Date());
				blob.setCreatedBy("TRAX_IFACE");
				blob.setId(pk);
				
				blob.setPrintFlag("YES");
				blob.setBlobType("EXTLINK");
				if(woTaskCard.getBlobNo() == null) {
					try {
						blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
						woTaskCard.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
					} catch (Exception e1) {
					}
				}else {
					blob.getId().setBlobNo(woTaskCard.getBlobNo().longValue());
				}
				blob.getId().setBlobLine(getLine(woTaskCard.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
				
			}
			
			
			   
			
			blob.setModifiedBy("TRAX_IFACE");
			blob.setModifiedDate(new Date());
			blob.setWebLink(new String(input.getAttachmentLinkSharepointlink(), StandardCharsets.UTF_8));
			blob.setBlobDescription(fileName);
			blob.setCustomDescription("AttachmentLink");
			
			//blob.setDocType("LINK");
			
			
			
			logger.info("UPDATING woTaskCard: TASK_CARD: " + woTaskCard.getId().getTaskCard() + " WO: " + woTaskCard.getId().getWo()  );
			insertData(woTaskCard);
			
			logger.info("UPDATING blob: " + blob.getId().getBlobNo());
			insertData(blob);
		
			return woTaskCard;

		}else {
			try 
			{
				blob = em.createQuery("SELECT b FROM BlobTable b where b.id.blobNo = :bl and b.blobDescription = :des and b.customDescription = :cus", BlobTable.class)
						.setParameter("bl", woTaskCard.getBlobNo().longValue())
						.setParameter("des",fileName )
						.setParameter("cus","SHAREPOINT" )
						.getSingleResult();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				BlobTablePK pk = new BlobTablePK();
				blob = new BlobTable();
				blob.setCreatedDate(new Date());
				blob.setCreatedBy("TRAX_IFACE");
				blob.setId(pk);
				
				blob.setPrintFlag("YES");
				
				if(woTaskCard.getBlobNo() == null) {
					try {
						blob.getId().setBlobNo(((getTransactionNo("BLOB").longValue())));
						woTaskCard.setBlobNo(new BigDecimal(blob.getId().getBlobNo()));
					} catch (Exception e1) {
					}
				}else {
					blob.getId().setBlobNo(woTaskCard.getBlobNo().longValue());
				}
				
				blob.getId().setBlobLine(getLine(woTaskCard.getBlobNo(),"BLOB_LINE","BLOB_TABLE","BLOB_NO" ));
				
				
			}
			
			
			blob.setDocType("SHAREPOINT");
			
			
			blob.setModifiedBy("TRAX_IFACE");
			blob.setModifiedDate(new Date());
			blob.setBlobItem(file);
			blob.setBlobDescription(fileName);
			blob.setCustomDescription("SHAREPOINT");
			
			
			
			logger.info("UPDATING woTaskCard: TASK_CARD: " + woTaskCard.getId().getTaskCard() + " WO: " + woTaskCard.getId().getWo()  );
			insertData(woTaskCard);
			
			logger.info("UPDATING blob: " + blob.getId().getBlobNo());
			insertData(blob);
		
			return woTaskCard;
		}
	}

	
	private PnInventoryHistory setPnInevtoryHistory(PnInventoryDetail pnInventoryDetail, MaterialMovementMaster input, WoTaskCard woTaskCard, String transactionType,PicklistDistribution pick) {
		
		PnInventoryDetail dumyRecord = getPnInventoryDetailEmpty(pnInventoryDetail,input );
		
		
		PnInventoryHistory pnInventoryHistory = null;
			
		pnInventoryHistory = new PnInventoryHistory();
		PnInventoryHistoryPK pk = new PnInventoryHistoryPK();
				
		pnInventoryHistory.setCreatedDate(new Date());
		pnInventoryHistory.setCreatedBy("TRAX_IFACE");
		pnInventoryHistory.setId(pk);
				
		pnInventoryHistory.setModifiedBy("TRAX_IFACE");
		pnInventoryHistory.setModifiedDate(new Date());
			
		pnInventoryHistory.setPn(dumyRecord.getPn());
		pnInventoryHistory.setSn(dumyRecord.getSn());
		pnInventoryHistory.setGoodsRcvdBatch(dumyRecord.getGoodsRcvdBatch());
		pnInventoryHistory.getId().setBatch(dumyRecord.getBatch());
		
		try {
			pnInventoryHistory.setWo(new BigDecimal(woTaskCard.getId().getWo()));
			pnInventoryHistory.setTaskCard(woTaskCard.getId().getTaskCard());
			pnInventoryHistory.setAc(woTaskCard.getId().getAc());
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(transactionType.equalsIgnoreCase("RTS/WO")) {
			pnInventoryHistory.setTransactionType("RTS/WO");
			pnInventoryHistory.setQtyReturnStock(new BigDecimal(input.getQuantity()));
		}else {
			pnInventoryHistory.setTransactionType("ISSUED");
			pnInventoryHistory.setIssuedTo("TRAX_IFACE");
			pnInventoryHistory.setQtyReturnStock(new BigDecimal(0));
		}
		
		
		pnInventoryHistory.setOrderNo(new BigDecimal(pick.getId().getPicklist()));
		pnInventoryHistory.setOrderLine(new BigDecimal(pick.getId().getPicklistLine()));
		pnInventoryHistory.setOrderType("PICKLST");
		pnInventoryHistory.setQty(new BigDecimal(input.getQuantity()));
		
		try {
			pnInventoryHistory.getId().setTransactionNo(getTransactionNo("PNINVHIS").longValue());
		} catch (Exception e) {
		}
		
		
		
		
		
		pnInventoryHistory.setUnitCost(new BigDecimal(0));
		pnInventoryHistory.setSecondaryCost(new BigDecimal(0));
		pnInventoryHistory.setSecondaryCurrencyExchange(new BigDecimal(1));
		pnInventoryHistory.setCurrencyExchangeRate(new BigDecimal(0));
		
		pnInventoryHistory.setNla("Y");
		
		pnInventoryHistory.setLocation(input.getPlant());
		
		logger.info("INSERTING pnInventoryHistory: " + input.getMaterial());
		insertData(pnInventoryHistory);
		
		return pnInventoryHistory;
	}
	
	private PnInventoryHistory setPnInevtoryHistory(PnInventoryDetail pnInventoryDetail, MaterialMovementMaster input, Wo wo, String transactionType,PicklistDistribution pick) {
		PnInventoryDetail dumyRecord = getPnInventoryDetailEmpty(pnInventoryDetail,input );
		
		
		PnInventoryHistory pnInventoryHistory = null;
			
		pnInventoryHistory = new PnInventoryHistory();
		PnInventoryHistoryPK pk = new PnInventoryHistoryPK();
				
		pnInventoryHistory.setCreatedDate(new Date());
		pnInventoryHistory.setCreatedBy("TRAX_IFACE");
		pnInventoryHistory.setId(pk);
				
		pnInventoryHistory.setModifiedBy("TRAX_IFACE");
		pnInventoryHistory.setModifiedDate(new Date());
			
		pnInventoryHistory.setPn(dumyRecord.getPn());
		pnInventoryHistory.setSn(dumyRecord.getSn());
		pnInventoryHistory.setGoodsRcvdBatch(dumyRecord.getGoodsRcvdBatch());
		pnInventoryHistory.getId().setBatch(dumyRecord.getBatch());
		
		try {
			pnInventoryHistory.setWo(new BigDecimal(wo.getWo()));
			pnInventoryHistory.setAc(wo.getAc());
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(transactionType.equalsIgnoreCase("RTS/WO")) {
			pnInventoryHistory.setTransactionType("RTS/WO");
			pnInventoryHistory.setQtyReturnStock(new BigDecimal(input.getQuantity()));
		}else {
			pnInventoryHistory.setTransactionType("ISSUED");
			pnInventoryHistory.setIssuedTo("TRAX_IFACE");
			pnInventoryHistory.setQtyReturnStock(new BigDecimal(0));
		}
		
		
		pnInventoryHistory.setOrderNo(new BigDecimal(pick.getId().getPicklist()));
		pnInventoryHistory.setOrderLine(new BigDecimal(pick.getId().getPicklistLine()));
		pnInventoryHistory.setOrderType("PICKLST");
		pnInventoryHistory.setQty(new BigDecimal(input.getQuantity()));
		
		try {
			pnInventoryHistory.getId().setTransactionNo(getTransactionNo("PNINVHIS").longValue());
		} catch (Exception e) {
		}
		
		
		
		
		
		pnInventoryHistory.setUnitCost(new BigDecimal(0));
		pnInventoryHistory.setSecondaryCost(new BigDecimal(0));
		pnInventoryHistory.setSecondaryCurrencyExchange(new BigDecimal(1));
		pnInventoryHistory.setCurrencyExchangeRate(new BigDecimal(0));
		
		pnInventoryHistory.setNla("Y");
		
		pnInventoryHistory.setLocation(input.getPlant());
		
		logger.info("INSERTING pnInventoryHistory: " + input.getMaterial());
		insertData(pnInventoryHistory);
		
		return pnInventoryHistory;
		
	}
	
	
	private PnInventoryDetail getPnInventoryDetailEmpty(PnInventoryDetail pnInventoryDetail,MaterialMovementMaster input ) {
		PnInventoryDetail pid = null;
		try {
			 pid = em.createQuery("SELECT p FROM PnInventoryDetail p where p.pn = :par and p.sn is null and p.location = :loc "
			 		+ " and p.legacyBatch = :bat and p.createdBy = :create", PnInventoryDetail.class)
					.setParameter("par", pnInventoryDetail.getPn())
					.setParameter("loc", pnInventoryDetail.getLocation())
					.setParameter("bat", input.getBatch())
					.setParameter("create", "ISSUEIFACE")
					.getSingleResult();
			logger.info("Found PnInventoryDetail empty record");
		}catch (Exception e) {
				pid = new PnInventoryDetail();
				pid.setCreatedDate(new Date());
				pid.setCreatedBy("ISSUEIFACE");
				
				//EMRO fields to create basic object
				pid.setQtyAvailable(new BigDecimal(0));
				pid.setQtyReserved(new BigDecimal(0));
				pid.setQtyInTransfer(new BigDecimal(0));
				pid.setQtyPendingRi(new BigDecimal(0));
				pid.setQtyUs(new BigDecimal(0));
				pid.setUnitCost(new BigDecimal(0));
				pid.setCurrencyExchangeRate(new BigDecimal(1));
				pid.setQtyInRental(new BigDecimal(0));
				pid.setSecondaryCost(new BigDecimal(0));
				pid.setSecondaryCurrencyExchange(new BigDecimal(1));
				
				pid.setKitNo(new BigDecimal(0));
				pid.setBlobNo(new BigDecimal(0));
				pid.setDocumentNo(new BigDecimal(0));
				pid.setFilingSequence(new BigDecimal(0));
				pid.setNoOfTagPrint(new BigDecimal(0));
				pid.setSosHour(new BigDecimal(0));
				pid.setSlot(new BigDecimal(0));
				pid.setNlaPosition(" ");
				
				pid.setInventoryType("MAINTENANCE");	
				try
				{
					String company = System.getProperty("profile_company");
					pid.setGlCompany(company);
				}
				catch(Exception e1) {
					pid.setGlCompany("SIAEC");	
				}
				pid.setCondition("NEW");
				
				pid.setModifiedBy("TRAX_IFACE");
				pid.setModifiedDate(new Date());
				
				pid.setPn(pnInventoryDetail.getPn());
				pid.setLocation(pnInventoryDetail.getLocation());
				long batch = getTransactionNo("BATCH").longValue();
				pid.setBatch(batch);
				pid.setGoodsRcvdBatch(new BigDecimal(batch));
				
				
		}
		pid.setModifiedBy("TRAX_IFACE");
		pid.setModifiedDate(new Date());
		pid.setLegacyBatch(input.getBatch());
		
		logger.info("INSERTING Empty PID RECORD PN: "	+	
				pid.getPn() + " , PLANT: " + pid.getLocation()		
				+ ", BATCH: " + pid.getBatch() );
				
		insertData(pid);
		
		return pid;
	}



	public byte[] getsharePointfile(String spurl){
		
		logger.info("Getting SharePoint File");
		
		logger.info("setup variables");
		String clientID = System.getProperty("clientId");
		String clientSecret = System.getProperty("clientSecret");
		String tenantId = System.getProperty("tenantId");
		String tenant = System.getProperty("tenant");
		byte[]file = null;
		
		
		String resource = "00000003-0000-0ff1-ce00-000000000000/"+tenant+".sharepoint.com@"+tenantId;
		
		String spsite = "";
		String sppath = "";
		
		String spfile = "";
		
		String token = "";
		
		String urlToken = "https://accounts.accesscontrol.windows.net/"+tenantId+"/tokens/OAuth/2";
		
		clientID = clientID + "@" + tenantId;
		
		try {
			
			URL url =new URL(spurl);    
			
			if(spurl.toUpperCase().contains("SITES") ) {
				spsite = url.getProtocol() + "://" + url.getHost() 
				+ url.getPath().substring(0, ordinalIndexOf(url.getPath(),"/",3));
				
				sppath = url.getFile().substring(ordinalIndexOf(url.getFile(),"/",3)+1, url.getFile().lastIndexOf("/")+ 1);
				
			}else if(spurl.toUpperCase().contains("TEAM") ) {
				
				String p = url.getPath().substring(0, ordinalIndexOf(url.getPath(),"%20Documents",1)); 
		        String s = 		p.substring(0,p.lastIndexOf("/"));
		        spsite = url.getProtocol() + "://" + url.getHost() + s;
								
				String t = 	p.substring(p.lastIndexOf("/")+1,p.length());
			
				sppath = t + url.getFile().substring(ordinalIndexOf(url.getFile(),"%20Document",0) , url.getFile().lastIndexOf("/")+ 1);
				
			}else {
				
				spsite = url.getProtocol() + "://" + url.getHost();
				
				sppath = url.getFile().substring(0, url.getFile().lastIndexOf("/")+ 1);
				
			}
			
			
			
			spfile = url.getFile().substring(url.getFile().lastIndexOf("/")+1,url.getFile().length());
			
			String urlApi = spsite + "/_api/web/GetFolderByServerRelativeUrl('"+sppath+"')/Files('"+spfile+"')/$value";
				
					
						
	        SharePointPoster poster = new SharePointPoster();
	        //logger.info(resource);
	        
	        //logger.info(resource);
	        
	        token = poster.getToken(clientID,clientSecret,tenantId,resource,urlToken);
	        
	        if(token == null) {
	        	return null;
	        }
	        
	        logger.info("Token: "+token);
	        
	        
	        
	        poster.postSharePoint(urlApi,token);
	        
	        file = poster.getBody();
	        			 
			 return file;
		
		}catch(Exception e) {
			e.printStackTrace();
			file = null;
			logger.info(e.toString());			
		}	
		return file;	
	}
	
	public  int ordinalIndexOf(String str, String substr, int n) {
	    int pos = str.indexOf(substr);
	    while (--n > 0 && pos != -1)
	        pos = str.indexOf(substr, pos + 1);
	    return pos;
	}
	
	private PicklistHeader getPicklistHeaderRev(MaterialMovementMaster input) {
		try
		{	
			ArrayList<PicklistDistribution>picklistdist = (ArrayList<PicklistDistribution>) em.createQuery("SELECT p FROM PicklistDistribution p where p.externalCustRes =:pi AND p.externalCustResItem =:it AND p.id.transaction =:tra AND p.id.distributionLine =:dl")
					.setParameter("pi", input.getReservationNumber())
					.setParameter("it", input.getReservationItem())
					.setParameter("tra", "REQUIRE")
					.setParameter("dl",new Long(0) )
					.getResultList();
			logger.info("Found PicklistHeader Rev");
			
			return picklistdist.get(0).getPicklistHeader();
		}
		catch (Exception e)
		{	
			
			logger.info("PICKLIST NOT FOUND");
		}
		return null;
	}
	
	private PicklistHeader getPicklistHeaderTaskCard(WoTaskCard woTaskCard, MaterialMovementMaster m) {
		try
		{	
			List<PicklistHeader> picklistHeader = em.createQuery("SELECT p FROM PicklistHeader p WHERE p.wo = :woo and p.taskCard = :tas ")
					.setParameter("woo", new BigDecimal(woTaskCard.getId().getWo()))
					.setParameter("tas", woTaskCard.getId().getTaskCard())
					.getResultList();
			
			for(PicklistHeader p : picklistHeader) {
				if(p.getPicklistDistributions() != null) {
					for(PicklistDistribution d : p.getPicklistDistributions()) {
						if(d.getId().getTransaction().equalsIgnoreCase("REQUIRE") 
								&& d.getPn().equalsIgnoreCase(m.getMaterial())) {
							logger.info("Found PicklistHeader TaskCard");	
							return d.getPicklistHeader();
						}
					}
				}
				
				
			}
			
			
			
		}
		catch (Exception e)
		{	
			e.printStackTrace();
			logger.info("PICKLIST NOT FOUND");
		}
		return null;
	}
	
	private PicklistHeader getPicklistHeaderTaskCardFirtOne(WoTaskCard woTaskCard, MaterialMovementMaster m) {
		try
		{	
			List<PicklistHeader> picklistHeader = em.createQuery("SELECT p FROM PicklistHeader p WHERE p.wo = :woo and p.taskCard = :tas ")
					.setParameter("woo", new BigDecimal(woTaskCard.getId().getWo()))
					.setParameter("tas", woTaskCard.getId().getTaskCard())
					.getResultList();
			logger.info("PICKLIST HEADER SIZE " +picklistHeader.size());
			for(PicklistHeader p : picklistHeader) {
				if(p.getPicklistDistributions() != null) {
					for(PicklistDistribution d : p.getPicklistDistributions()) {
						if(d.getId().getTransaction().equalsIgnoreCase("REQUIRE")  && 
						(d.getStatus() != null && !d.getStatus().isEmpty() 
						&& !d.getStatus().equalsIgnoreCase("CANCEL"))) {
							logger.info("Found PicklistHeader TaskCard Firt One");	
							return d.getPicklistHeader();
						}
					}
				}	
			}
			
		}
	catch (Exception e)
	{	
		e.printStackTrace();
		logger.info("PICKLIST NOT FOUND");
	}
	return null;
}	

	
	private PicklistHeader getPicklistHeaderWo(Wo wo, MaterialMovementMaster m) {
		try
		{	
			List<PicklistHeader> picklistHeader = em.createQuery("SELECT p FROM PicklistHeader p WHERE p.wo = :woo ")
					.setParameter("woo", new BigDecimal(wo.getWo()))
					.getResultList();
			
			for(PicklistHeader p : picklistHeader) {
				if(p.getPicklistDistributions() != null) {
					for(PicklistDistribution d : p.getPicklistDistributions()) {
						if(d.getId().getTransaction().equalsIgnoreCase("REQUIRE") 
								&& d.getPn().equalsIgnoreCase(m.getMaterial())) {
							logger.info("Found PicklistHeader TaskCard");	
							return d.getPicklistHeader();
						}
					}
				}				
			}			
		}
		catch (Exception e)
		{	
			e.printStackTrace();
			logger.info("PICKLIST NOT FOUND");
		}
		return null;
	}
	
	private PicklistHeader getPicklistHeaderWoFirtOne(Wo wo, MaterialMovementMaster m) {
		try
		{	
			List<PicklistHeader> picklistHeader = em.createQuery("SELECT p FROM PicklistHeader p WHERE p.wo = :woo ")
					.setParameter("woo", new BigDecimal(wo.getWo()))
					.getResultList();
			logger.info("PICKLIST HEADER SIZE " +picklistHeader.size());
			for(PicklistHeader p : picklistHeader) {
				if(p.getPicklistDistributions() != null) {
					for(PicklistDistribution d : p.getPicklistDistributions()) {
						if(d.getId().getTransaction().equalsIgnoreCase("REQUIRE")  && 
						(d.getStatus() != null && !d.getStatus().isEmpty() 
						&& !d.getStatus().equalsIgnoreCase("CANCEL"))) {
							logger.info("Found PicklistHeader TaskCard Firt One");	
							return d.getPicklistHeader();
						}
					}
				}	
			}			
		}
		catch (Exception e)
		{	
			e.printStackTrace();
			logger.info("PICKLIST NOT FOUND");
		}
		return null;
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
