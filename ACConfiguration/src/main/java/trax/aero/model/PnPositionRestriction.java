package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the PN_POSITION_RESTRICTIONS database table.
 * 
 */
@Entity
@Table(name="PN_POSITION_RESTRICTIONS")
@NamedQuery(name="PnPositionRestriction.findAll", query="SELECT p FROM PnPositionRestriction p")
public class PnPositionRestriction implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PnPositionRestrictionPK id;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="DOCUMENT_NO")
	private BigDecimal documentNo;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	public PnPositionRestriction() {
	}

	public PnPositionRestrictionPK getId() {
		return this.id;
	}

	public void setId(PnPositionRestrictionPK id) {
		this.id = id;
	}

	public BigDecimal getBlobNo() {
		return this.blobNo;
	}

	public void setBlobNo(BigDecimal blobNo) {
		this.blobNo = blobNo;
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

}