package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TASK_CARD_EFFECTIVITY_REV database table.
 * 
 */
@Entity
@Table(name="TASK_CARD_EFFECTIVITY_REV")
@NamedQuery(name="TaskCardEffectivityRev.findAll", query="SELECT t FROM TaskCardEffectivityRev t")
public class TaskCardEffectivityRev implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TaskCardEffectivityRevPK id;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	@Column(name="\"SELECT\"")
	private String select;

	//bi-directional many-to-one association to TaskCardRev
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="REVISION", referencedColumnName="REVISON" , insertable=false, updatable=false),
		@JoinColumn(name="TASK_CARD", referencedColumnName="TASK_CARD" , insertable=false, updatable=false)
		})
	private TaskCardRev taskCardRev;

	public TaskCardEffectivityRev() {
	}

	public TaskCardEffectivityRevPK getId() {
		return this.id;
	}

	public void setId(TaskCardEffectivityRevPK id) {
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

	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public TaskCardRev getTaskCardRev() {
		return this.taskCardRev;
	}

	public void setTaskCardRev(TaskCardRev taskCardRev) {
		this.taskCardRev = taskCardRev;
	}

}