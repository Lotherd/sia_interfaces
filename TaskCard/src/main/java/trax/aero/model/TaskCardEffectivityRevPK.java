package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TASK_CARD_EFFECTIVITY_REV database table.
 * 
 */
@Embeddable
public class TaskCardEffectivityRevPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_CARD", insertable=false, updatable=false)
	private String taskCard;

	private String ac;

	@Column(insertable=false, updatable=false)
	private String revision;

	public TaskCardEffectivityRevPK() {
	}
	public String getTaskCard() {
		return this.taskCard;
	}
	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}
	public String getAc() {
		return this.ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
	public String getRevision() {
		return this.revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TaskCardEffectivityRevPK)) {
			return false;
		}
		TaskCardEffectivityRevPK castOther = (TaskCardEffectivityRevPK)other;
		return 
			this.taskCard.equals(castOther.taskCard)
			&& this.ac.equals(castOther.ac)
			&& this.revision.equals(castOther.revision);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + this.ac.hashCode();
		hash = hash * prime + this.revision.hashCode();
		
		return hash;
	}
}