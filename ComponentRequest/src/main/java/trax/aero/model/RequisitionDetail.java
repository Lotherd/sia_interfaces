package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the REQUISITION_DETAIL database table.
 * 
 */
@Entity
@Table(name="REQUISITION_DETAIL")
@NamedQuery(name="RequisitionDetail.findAll", query="SELECT r FROM RequisitionDetail r")
public class RequisitionDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RequisitionDetailPK id;

	@Column(name="ASSIGN_TO")
	private String assignTo;

	@Column(name="AUTHORIZATION_CONTROL")
	private BigDecimal authorizationControl;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	@Column(name="CANCEL_REASON")
	private String cancelReason;

	@Column(name="CANCELED_BY")
	private String canceledBy;

	@Column(name="CANCELED_DATE")
	private Date canceledDate;

	@Column(name="CAPITAL_EXPENDITURE")
	private String capitalExpenditure;

	private String condition;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="DOCUMENT_NO")
	private BigDecimal documentNo;

	@Column(name="ESTIMATED_COST")
	private BigDecimal estimatedCost;

	@Column(name="ESTIMATED_CURRENCY")
	private String estimatedCurrency;

	@Column(name="EXPECTED_DELIVERY_DATE")
	private Date expectedDeliveryDate;

	@Column(name="EXTERNAL_POSITION")
	private String externalPosition;

	@Column(name="EXTERNAL_REFERENCE")
	private String externalReference;

	private String gl;

	@Column(name="GL_COMPANY")
	private String glCompany;

	@Column(name="GL_COST_CENTER")
	private String glCostCenter;

	@Column(name="GL_EXPENDITURE")
	private String glExpenditure;

	@Column(name="INTERFACE_MODIFIED_DATE")
	private Date interfaceModifiedDate;

	@Column(name="INTERFACE_TRNSFR_REQ_AZUL")
	private Date interfaceTrnsfrReqAzul;

	private String ipc;

	@Column(name="LHT_REFERENCE")
	private String lhtReference;

	private String location;

	@Column(name="MAT_RQST_STATUS")
	private String matRqstStatus;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name="NON_INVENTORY_FLAG")
	private String nonInventoryFlag;

	private BigDecimal notes;

	private String owner;

	private String pn;

	@Column(name="PN_DESCRIPTION")
	private String pnDescription;

	@Column(name="PN_GROUP")
	private String pnGroup;

	@Column(name="PREFER_VENDOR")
	private String preferVendor;

	@Column(name="QTY_RECEIVED")
	private BigDecimal qtyReceived;

	@Column(name="QTY_REQUIRE")
	private BigDecimal qtyRequire;

	private String recommended;

	@Column(name="REQUIRE_DATE")
	private Date requireDate;

	@Column(name="REQUIRE_HOUR")
	private BigDecimal requireHour;

	@Column(name="REQUIRE_MINUTE")
	private BigDecimal requireMinute;

	private String status;

	@Column(name="TAX_INCENTIVE")
	private String taxIncentive;

	@Column(name="TAX_INCENTIVE_NBR")
	private String taxIncentiveNbr;

	@Column(name="TRANSACTION_TYPE")
	private String transactionType;
	
	@Column(name="INTERFACE_SYNC_DATE")
	private Date interfaceSyncDate;
	
	@Column(name="INTERFACE_SYNC_FLAG")
	private String interfaceSyncFlag;

	private String uom;

	//bi-directional many-to-one association to RequisitionHeader
	@ManyToOne
	@JoinColumn(name="REQUISITION")
	private RequisitionHeader requisitionHeader;

	public RequisitionDetail() {
	}

	public RequisitionDetailPK getId() {
		return this.id;
	}

	public void setId(RequisitionDetailPK id) {
		this.id = id;
	}

	public String getAssignTo() {
		return this.assignTo;
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public BigDecimal getAuthorizationControl() {
		return this.authorizationControl;
	}

	public void setAuthorizationControl(BigDecimal authorizationControl) {
		this.authorizationControl = authorizationControl;
	}

	public BigDecimal getBlobNo() {
		return this.blobNo;
	}

	public void setBlobNo(BigDecimal blobNo) {
		this.blobNo = blobNo;
	}

	public String getCancelReason() {
		return this.cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getCanceledBy() {
		return this.canceledBy;
	}

	public void setCanceledBy(String canceledBy) {
		this.canceledBy = canceledBy;
	}

	public Date getCanceledDate() {
		return this.canceledDate;
	}

	public void setCanceledDate(Date canceledDate) {
		this.canceledDate = canceledDate;
	}

	public String getCapitalExpenditure() {
		return this.capitalExpenditure;
	}

	public void setCapitalExpenditure(String capitalExpenditure) {
		this.capitalExpenditure = capitalExpenditure;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
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

	public BigDecimal getDocumentNo() {
		return this.documentNo;
	}

	public void setDocumentNo(BigDecimal documentNo) {
		this.documentNo = documentNo;
	}

	public BigDecimal getEstimatedCost() {
		return this.estimatedCost;
	}

	public void setEstimatedCost(BigDecimal estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

	public String getEstimatedCurrency() {
		return this.estimatedCurrency;
	}

	public void setEstimatedCurrency(String estimatedCurrency) {
		this.estimatedCurrency = estimatedCurrency;
	}

	public Date getExpectedDeliveryDate() {
		return this.expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public String getExternalPosition() {
		return this.externalPosition;
	}

	public void setExternalPosition(String externalPosition) {
		this.externalPosition = externalPosition;
	}

	public String getExternalReference() {
		return this.externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public String getGl() {
		return this.gl;
	}

	public void setGl(String gl) {
		this.gl = gl;
	}

	public String getGlCompany() {
		return this.glCompany;
	}

	public void setGlCompany(String glCompany) {
		this.glCompany = glCompany;
	}

	public String getGlCostCenter() {
		return this.glCostCenter;
	}

	public void setGlCostCenter(String glCostCenter) {
		this.glCostCenter = glCostCenter;
	}

	public String getGlExpenditure() {
		return this.glExpenditure;
	}

	public void setGlExpenditure(String glExpenditure) {
		this.glExpenditure = glExpenditure;
	}

	public Date getInterfaceModifiedDate() {
		return this.interfaceModifiedDate;
	}

	public void setInterfaceModifiedDate(Date interfaceModifiedDate) {
		this.interfaceModifiedDate = interfaceModifiedDate;
	}

	public Date getInterfaceTrnsfrReqAzul() {
		return this.interfaceTrnsfrReqAzul;
	}

	public void setInterfaceTrnsfrReqAzul(Date interfaceTrnsfrReqAzul) {
		this.interfaceTrnsfrReqAzul = interfaceTrnsfrReqAzul;
	}

	public String getIpc() {
		return this.ipc;
	}

	public void setIpc(String ipc) {
		this.ipc = ipc;
	}

	public String getLhtReference() {
		return this.lhtReference;
	}

	public void setLhtReference(String lhtReference) {
		this.lhtReference = lhtReference;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMatRqstStatus() {
		return this.matRqstStatus;
	}

	public void setMatRqstStatus(String matRqstStatus) {
		this.matRqstStatus = matRqstStatus;
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

	public String getNonInventoryFlag() {
		return this.nonInventoryFlag;
	}

	public void setNonInventoryFlag(String nonInventoryFlag) {
		this.nonInventoryFlag = nonInventoryFlag;
	}

	public BigDecimal getNotes() {
		return this.notes;
	}

	public void setNotes(BigDecimal notes) {
		this.notes = notes;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getPn() {
		return this.pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public String getPnDescription() {
		return this.pnDescription;
	}

	public void setPnDescription(String pnDescription) {
		this.pnDescription = pnDescription;
	}

	public String getPnGroup() {
		return this.pnGroup;
	}

	public void setPnGroup(String pnGroup) {
		this.pnGroup = pnGroup;
	}

	public String getPreferVendor() {
		return this.preferVendor;
	}

	public void setPreferVendor(String preferVendor) {
		this.preferVendor = preferVendor;
	}

	public BigDecimal getQtyReceived() {
		return this.qtyReceived;
	}

	public void setQtyReceived(BigDecimal qtyReceived) {
		this.qtyReceived = qtyReceived;
	}

	public BigDecimal getQtyRequire() {
		return this.qtyRequire;
	}

	public void setQtyRequire(BigDecimal qtyRequire) {
		this.qtyRequire = qtyRequire;
	}

	public String getRecommended() {
		return this.recommended;
	}

	public void setRecommended(String recommended) {
		this.recommended = recommended;
	}

	public Date getRequireDate() {
		return this.requireDate;
	}

	public void setRequireDate(Date requireDate) {
		this.requireDate = requireDate;
	}

	public BigDecimal getRequireHour() {
		return this.requireHour;
	}

	public void setRequireHour(BigDecimal requireHour) {
		this.requireHour = requireHour;
	}

	public BigDecimal getRequireMinute() {
		return this.requireMinute;
	}

	public void setRequireMinute(BigDecimal requireMinute) {
		this.requireMinute = requireMinute;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaxIncentive() {
		return this.taxIncentive;
	}

	public void setTaxIncentive(String taxIncentive) {
		this.taxIncentive = taxIncentive;
	}

	public String getTaxIncentiveNbr() {
		return this.taxIncentiveNbr;
	}

	public void setTaxIncentiveNbr(String taxIncentiveNbr) {
		this.taxIncentiveNbr = taxIncentiveNbr;
	}

	public String getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getUom() {
		return this.uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public RequisitionHeader getRequisitionHeader() {
		return this.requisitionHeader;
	}

	public void setRequisitionHeader(RequisitionHeader requisitionHeader) {
		this.requisitionHeader = requisitionHeader;
	}

	public Date getInterfaceSyncDate() {
		return interfaceSyncDate;
	}

	public void setInterfaceSyncDate(Date interfaceSyncDate) {
		this.interfaceSyncDate = interfaceSyncDate;
	}

	public String getInterfaceSyncFlag() {
		return interfaceSyncFlag;
	}

	public void setInterfaceSyncFlag(String interfaceSyncFlag) {
		this.interfaceSyncFlag = interfaceSyncFlag;
	}

}