package trax.aero.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the TASK_CARD_EFFECTIVITY_REV_HD database table.
 * 
 */
@Entity
@Table(name="TASK_CARD_EFFECTIVITY_REV_HD")
@NamedQuery(name="TaskCardEffectivityRevHd.findAll", query="SELECT t FROM TaskCardEffectivityRevHd t")
public class TaskCardEffectivityRevHd implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TaskCardEffectivityRevHdPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name="\"SELECT\"")
	private String select;

	//bi-directional many-to-one association to TaskCardRev
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="REVISION", referencedColumnName="REVISON" , insertable=false, updatable=false),
		@JoinColumn(name="TASK_CARD", referencedColumnName="TASK_CARD" , insertable=false, updatable=false)
		})
	private TaskCardRev taskCardRev;

	public TaskCardEffectivityRevHd() {
	}

	public TaskCardEffectivityRevHdPK getId() {
		return this.id;
	}

	public void setId(TaskCardEffectivityRevHdPK id) {
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