package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TASK_CARD_ITEM_REV database table.
 * 
 */
@Embeddable
public class TaskCardItemRevPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_CARD", insertable=false, updatable=false)
	private String taskCard;

	@Column(name="TASK_CARD_ITEM")
	private long taskCardItem;

	@Column(name="REVISION_CODE", insertable=false, updatable=false)
	private String revisionCode;

	public TaskCardItemRevPK() {
	}
	public String getTaskCard() {
		return this.taskCard;
	}
	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}
	public long getTaskCardItem() {
		return this.taskCardItem;
	}
	public void setTaskCardItem(long taskCardItem) {
		this.taskCardItem = taskCardItem;
	}
	public String getRevisionCode() {
		return this.revisionCode;
	}
	public void setRevisionCode(String revisionCode) {
		this.revisionCode = revisionCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TaskCardItemRevPK)) {
			return false;
		}
		TaskCardItemRevPK castOther = (TaskCardItemRevPK)other;
		return 
			this.taskCard.equals(castOther.taskCard)
			&& (this.taskCardItem == castOther.taskCardItem)
			&& this.revisionCode.equals(castOther.revisionCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + ((int) (this.taskCardItem ^ (this.taskCardItem >>> 32)));
		hash = hash * prime + this.revisionCode.hashCode();
		
		return hash;
	}
}