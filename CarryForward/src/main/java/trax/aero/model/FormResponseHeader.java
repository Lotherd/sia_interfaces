package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the FORM_RESPONSE_HEADER database table.
 * 
 */
@Entity
@Table(name="FORM_RESPONSE_HEADER")
@NamedQuery(name="FormResponseHeader.findAll", query="SELECT f FROM FormResponseHeader f")
public class FormResponseHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FormResponseHeaderPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="FORM_ID")
	private BigDecimal formId;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	public FormResponseHeader() {
	}

	public FormResponseHeaderPK getId() {
		return this.id;
	}

	public void setId(FormResponseHeaderPK id) {
		this.id = id;
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

	public BigDecimal getFormId() {
		return this.formId;
	}

	public void setFormId(BigDecimal formId) {
		this.formId = formId;
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

}