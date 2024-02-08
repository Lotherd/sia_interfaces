package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;

import java.util.Date;


/**
 * The persistent class for the PN_INTERCHG_ONE_WAY database table.
 * 
 */
@Entity
@Table(name="PN_INTERCHG_ONE_WAY")
@NamedQuery(name="PnInterchgOneWay.findAll", query="SELECT p FROM PnInterchgOneWay p")
public class PnInterchgOneWay implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PnInterchgOneWayPK id;

	@Column(name="AUTHORIZATION_CODE")
	private String authorizationCode;

	@Column(name="CONDITIONAL_NOTES")
	private BigDecimal conditionalNotes;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	@Temporal(TemporalType.DATE)
	private Date createdDate;

	@Column(name="IFACE_BAXTER_ONEWAY_XFER_DATE")
	@Temporal(TemporalType.DATE)
	private Date ifaceBaxterOnewayXferDate;

	@Column(name="INTERCHANGEABLE_TYPE")
	private String interchangeableType;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	@Temporal(TemporalType.DATE)
	private Date modifiedDate;

	private BigDecimal notes;

	private String prefer;

	@Column(name="TRAXDOC_CREATED_DATE")
	@Temporal(TemporalType.DATE)
	private Date traxdocCreatedDate;

	@Column(name="TRAXDOC_FILE_NAME_PATH")
	private String traxdocFileNamePath;

	@Column(name="TRAXDOC_ROW_ID")
	private BigDecimal traxdocRowId;

	private String vendor;

	public PnInterchgOneWay() {
	}

	public PnInterchgOneWayPK getId() {
		return this.id;
	}

	public void setId(PnInterchgOneWayPK id) {
		this.id = id;
	}

	public String getAuthorizationCode() {
		return this.authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public BigDecimal getConditionalNotes() {
		return this.conditionalNotes;
	}

	public void setConditionalNotes(BigDecimal conditionalNotes) {
		this.conditionalNotes = conditionalNotes;
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

	public Date getIfaceBaxterOnewayXferDate() {
		return this.ifaceBaxterOnewayXferDate;
	}

	public void setIfaceBaxterOnewayXferDate(Date ifaceBaxterOnewayXferDate) {
		this.ifaceBaxterOnewayXferDate = ifaceBaxterOnewayXferDate;
	}

	public String getInterchangeableType() {
		return this.interchangeableType;
	}

	public void setInterchangeableType(String interchangeableType) {
		this.interchangeableType = interchangeableType;
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

	public String getPrefer() {
		return this.prefer;
	}

	public void setPrefer(String prefer) {
		this.prefer = prefer;
	}

	public Date getTraxdocCreatedDate() {
		return this.traxdocCreatedDate;
	}

	public void setTraxdocCreatedDate(Date traxdocCreatedDate) {
		this.traxdocCreatedDate = traxdocCreatedDate;
	}

	public String getTraxdocFileNamePath() {
		return this.traxdocFileNamePath;
	}

	public void setTraxdocFileNamePath(String traxdocFileNamePath) {
		this.traxdocFileNamePath = traxdocFileNamePath;
	}

	public BigDecimal getTraxdocRowId() {
		return this.traxdocRowId;
	}

	public void setTraxdocRowId(BigDecimal traxdocRowId) {
		this.traxdocRowId = traxdocRowId;
	}

	public String getVendor() {
		return this.vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

}