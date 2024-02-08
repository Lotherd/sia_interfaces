package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TASK_CARD_CONTROL_REV database table.
 * 
 */
@Embeddable
public class TaskCardControlRevPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_CARD", insertable=false, updatable=false)
	private String taskCard;

	@Column(name="CONTROL_CATEGORY")
	private String controlCategory;

	private String code;

	@Column(insertable=false, updatable=false)
	private String revision;

	@Column(name="TASK_CARD_ITEM")
	private long taskCardItem;

	@Column(name="AC_TYPE")
	private String acType;

	@Column(name="AC_SERIES")
	private String acSeries;

	public TaskCardControlRevPK() {
	}
	public String getTaskCard() {
		return this.taskCard;
	}
	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}
	public String getControlCategory() {
		return this.controlCategory;
	}
	public void setControlCategory(String controlCategory) {
		this.controlCategory = controlCategory;
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRevision() {
		return this.revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public long getTaskCardItem() {
		return this.taskCardItem;
	}
	public void setTaskCardItem(long taskCardItem) {
		this.taskCardItem = taskCardItem;
	}
	public String getAcType() {
		return this.acType;
	}
	public void setAcType(String acType) {
		this.acType = acType;
	}
	public String getAcSeries() {
		return this.acSeries;
	}
	public void setAcSeries(String acSeries) {
		this.acSeries = acSeries;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TaskCardControlRevPK)) {
			return false;
		}
		TaskCardControlRevPK castOther = (TaskCardControlRevPK)other;
		return 
			this.taskCard.equals(castOther.taskCard)
			&& this.controlCategory.equals(castOther.controlCategory)
			&& this.code.equals(castOther.code)
			&& this.revision.equals(castOther.revision)
			&& (this.taskCardItem == castOther.taskCardItem)
			&& this.acType.equals(castOther.acType)
			&& this.acSeries.equals(castOther.acSeries);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + this.controlCategory.hashCode();
		hash = hash * prime + this.code.hashCode();
		hash = hash * prime + this.revision.hashCode();
		hash = hash * prime + ((int) (this.taskCardItem ^ (this.taskCardItem >>> 32)));
		hash = hash * prime + this.acType.hashCode();
		hash = hash * prime + this.acSeries.hashCode();
		
		return hash;
	}
}