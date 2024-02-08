package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TASK_CARD_PN_REV database table.
 * 
 */
@Embeddable
public class TaskCardPnRevPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_CARD", insertable=false, updatable=false)
	private String taskCard;

	private String pn;

	@Column(insertable=false, updatable=false)
	private String revision;

	@Column(name="TASK_CARD_ITEM")
	private long taskCardItem;

	public TaskCardPnRevPK() {
	}
	public String getTaskCard() {
		return this.taskCard;
	}
	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}
	public String getPn() {
		return this.pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TaskCardPnRevPK)) {
			return false;
		}
		TaskCardPnRevPK castOther = (TaskCardPnRevPK)other;
		return 
			this.taskCard.equals(castOther.taskCard)
			&& this.pn.equals(castOther.pn)
			&& this.revision.equals(castOther.revision)
			&& (this.taskCardItem == castOther.taskCardItem);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.revision.hashCode();
		hash = hash * prime + ((int) (this.taskCardItem ^ (this.taskCardItem >>> 32)));
		
		return hash;
	}
}