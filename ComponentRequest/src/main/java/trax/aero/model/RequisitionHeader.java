package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the REQUISITION_HEADER database table.
 * 
 */
@Entity
@Table(name="REQUISITION_HEADER")
@NamedQuery(name="RequisitionHeader.findAll", query="SELECT r FROM RequisitionHeader r")
public class RequisitionHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long requisition;

	private String ac;

	@Column(name="ASSIGN_TO")
	private String assignTo;

	@Column(name="\"AUTHORIZATION\"")
	private String authorization;

	@Column(name="AUTHORIZED_BY")
	private String authorizedBy;

	@Column(name="AUTHORIZED_DATE")
	private Date authorizedDate;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	private String category;

	@Column(name="CO_ORDER_LINE")
	private BigDecimal coOrderLine;

	@Column(name="CO_ORDER_NUMBER")
	private BigDecimal coOrderNumber;

	@Column(name="CO_ORDER_TYPE")
	private String coOrderType;

	private String company;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	private String defect;

	@Column(name="DEFECT_ITEM")
	private BigDecimal defectItem;

	@Column(name="DEFECT_TYPE")
	private String defectType;

	@Column(name="DOCUMENT_NO")
	private BigDecimal documentNo;

	private String eo;

	private String evaluation;

	@Column(name="INTERFACE_TRNSFR_REQ_AZUL")
	private Date interfaceTrnsfrReqAzul;

	@Column(name="INVENTORY_TYPE")
	private String inventoryType;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	@Column(name="ORDER_CATEGORY")
	private String orderCategory;

	private String owner;

	private String priority;

	@Column(name="RELEASE_FOR_AUTHORIZATION")
	private String releaseForAuthorization;

	@Column(name="RELEASE_FOR_AUTHORIZATION_ON")
	private Date releaseForAuthorizationOn;

	@Column(name="REQUESTER_LOCATION")
	private String requesterLocation;

	@Column(name="REQUISITION_DESCRIPTION")
	private String requisitionDescription;

	@Column(name="REQUISITION_GENERATED_BY")
	private BigDecimal requisitionGeneratedBy;

	@Column(name="REQUISITION_LINE_GENERATED_BY")
	private BigDecimal requisitionLineGeneratedBy;

	@Column(name="REQUISTION_TYPE")
	private String requistionType;

	private String site;

	private BigDecimal so;

	private String status;

	@Column(name="TASK_CARD")
	private String taskCard;

	@Column(name="TASK_CARD_PN")
	private String taskCardPn;

	@Column(name="TASK_CARD_SN")
	private String taskCardSn;

	private BigDecimal wo;

	//bi-directional many-to-one association to RequisitionDetail
	@OneToMany(mappedBy="requisitionHeader")
	private List<RequisitionDetail> requisitionDetails;

	public RequisitionHeader() {
	}

	public long getRequisition() {
		return this.requisition;
	}

	public void setRequisition(long requisition) {
		this.requisition = requisition;
	}

	public String getAc() {
		return this.ac;
	}

	public void setAc(String ac) {
		this.ac = ac;
	}

	public String getAssignTo() {
		return this.assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public String getAuthorization() {
		return this.authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getAuthorizedBy() {
		return this.authorizedBy;
	}

	public void setAuthorizedBy(String authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	public Date getAuthorizedDate() {
		return this.authorizedDate;
	}

	public void setAuthorizedDate(Date authorizedDate) {
		this.authorizedDate = authorizedDate;
	}

	public BigDecimal getBlobNo() {
		return this.blobNo;
	}

	public void setBlobNo(BigDecimal blobNo) {
		this.blobNo = blobNo;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getCoOrderLine() {
		return this.coOrderLine;
	}

	public void setCoOrderLine(BigDecimal coOrderLine) {
		this.coOrderLine = coOrderLine;
	}

	public BigDecimal getCoOrderNumber() {
		return this.coOrderNumber;
	}

	public void setCoOrderNumber(BigDecimal coOrderNumber) {
		this.coOrderNumber = coOrderNumber;
	}

	public String getCoOrderType() {
		return this.coOrderType;
	}

	public void setCoOrderType(String coOrderType) {
		this.coOrderType = coOrderType;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDefect() {
		return this.defect;
	}

	public void setDefect(String defect) {
		this.defect = defect;
	}

	public BigDecimal getDefectItem() {
		return this.defectItem;
	}

	public void setDefectItem(BigDecimal defectItem) {
		this.defectItem = defectItem;
	}

	public String getDefectType() {
		return this.defectType;
	}

	public void setDefectType(String defectType) {
		this.defectType = defectType;
	}

	public BigDecimal getDocumentNo() {
		return this.documentNo;
	}

	public void setDocumentNo(BigDecimal documentNo) {
		this.documentNo = documentNo;
	}

	public String getEo() {
		return this.eo;
	}

	public void setEo(String eo) {
		this.eo = eo;
	}

	public String getEvaluation() {
		return this.evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}

	public Date getInterfaceTrnsfrReqAzul() {
		return this.interfaceTrnsfrReqAzul;
	}

	public void setInterfaceTrnsfrReqAzul(Date interfaceTrnsfrReqAzul) {
		this.interfaceTrnsfrReqAzul = interfaceTrnsfrReqAzul;
	}

	public String getInventoryType() {
		return this.inventoryType;
	}

	public void setInventoryType(String inventoryType) {
		this.inventoryType = inventoryType;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public BigDecimal getNotes() {
		return this.notes;
	}

	public void setNotes(BigDecimal notes) {
		this.notes = notes;
	}

	public String getOrderCategory() {
		return this.orderCategory;
	}

	public void setOrderCategory(String orderCategory) {
		this.orderCategory = orderCategory;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPriority() {
		return this.priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getReleaseForAuthorization() {
		return this.releaseForAuthorization;
	}

	public void setReleaseForAuthorization(String releaseForAuthorization) {
		this.releaseForAuthorization = releaseForAuthorization;
	}

	public Date getReleaseForAuthorizationOn() {
		return this.releaseForAuthorizationOn;
	}

	public void setReleaseForAuthorizationOn(Date releaseForAuthorizationOn) {
		this.releaseForAuthorizationOn = releaseForAuthorizationOn;
	}

	public String getRequesterLocation() {
		return this.requesterLocation;
	}

	public void setRequesterLocation(String requesterLocation) {
		this.requesterLocation = requesterLocation;
	}

	public String getRequisitionDescription() {
		return this.requisitionDescription;
	}

	public void setRequisitionDescription(String requisitionDescription) {
		this.requisitionDescription = requisitionDescription;
	}

	public BigDecimal getRequisitionGeneratedBy() {
		return this.requisitionGeneratedBy;
	}

	public void setRequisitionGeneratedBy(BigDecimal requisitionGeneratedBy) {
		this.requisitionGeneratedBy = requisitionGeneratedBy;
	}

	public BigDecimal getRequisitionLineGeneratedBy() {
		return this.requisitionLineGeneratedBy;
	}

	public void setRequisitionLineGeneratedBy(BigDecimal requisitionLineGeneratedBy) {
		this.requisitionLineGeneratedBy = requisitionLineGeneratedBy;
	}

	public String getRequistionType() {
		return this.requistionType;
	}

	public void setRequistionType(String requistionType) {
		this.requistionType = requistionType;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public BigDecimal getSo() {
		return this.so;
	}

	public void setSo(BigDecimal so) {
		this.so = so;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaskCard() {
		return this.taskCard;
	}

	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}

	public String getTaskCardPn() {
		return this.taskCardPn;
	}

	public void setTaskCardPn(String taskCardPn) {
		this.taskCardPn = taskCardPn;
	}

	public String getTaskCardSn() {
		return this.taskCardSn;
	}

	public void setTaskCardSn(String taskCardSn) {
		this.taskCardSn = taskCardSn;
	}

	public BigDecimal getWo() {
		return this.wo;
	}

	public void setWo(BigDecimal wo) {
		this.wo = wo;
	}

	public List<RequisitionDetail> getRequisitionDetails() {
		return this.requisitionDetails;
	}

	public void setRequisitionDetails(List<RequisitionDetail> requisitionDetails) {
		this.requisitionDetails = requisitionDetails;
	}

	public RequisitionDetail addRequisitionDetail(RequisitionDetail requisitionDetail) {
		getRequisitionDetails().add(requisitionDetail);
		requisitionDetail.setRequisitionHeader(this);

		return requisitionDetail;
	}

	public RequisitionDetail removeRequisitionDetail(RequisitionDetail requisitionDetail) {
		getRequisitionDetails().remove(requisitionDetail);
		requisitionDetail.setRequisitionHeader(null);

		return requisitionDetail;
	}

}