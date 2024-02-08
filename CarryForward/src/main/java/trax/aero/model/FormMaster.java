package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the FORM_MASTER database table.
 * 
 */
@Entity
@Table(name="FORM_MASTER")
@NamedQuery(name="FormMaster.findAll", query="SELECT f FROM FormMaster f")
public class FormMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FORM_ID")
	private long formId;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="FORM_DESC")
	private String formDesc;

	@Column(name="FORM_REV")
	private String formRev;

	@Column(name="FORM_TITLE")
	private String formTitle;

	@Column(name="FORM_TYPE")
	private String formType;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	@Column(name="NOTIFICATION_EMAIL")
	private String notificationEmail;

	@Column(name="RESP_MAND")
	private String respMand;

	private Date revdate;

	public FormMaster() {
	}

	public long getFormId() {
		return this.formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
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

	public Object getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getFormDesc() {
		return this.formDesc;
	}

	public void setFormDesc(String formDesc) {
		this.formDesc = formDesc;
	}

	public String getFormRev() {
		return this.formRev;
	}

	public void setFormRev(String formRev) {
		this.formRev = formRev;
	}

	public String getFormTitle() {
		return this.formTitle;
	}

	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}

	public String getFormType() {
		return this.formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Object getModifiedDate() {
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

	public String getNotificationEmail() {
		return this.notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public String getRespMand() {
		return this.respMand;
	}

	public void setRespMand(String respMand) {
		this.respMand = respMand;
	}

	public Object getRevdate() {
		return this.revdate;
	}

	public void setRevdate(Date revdate) {
		this.revdate = revdate;
	}

}