package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TASK_CARD_EFFECTIVITY database table.
 * 
 */
@Embeddable
public class TaskCardEffectivityPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_CARD", insertable=false, updatable=false)
	private String taskCard;

	private String ac;

	public TaskCardEffectivityPK() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TaskCardEffectivityPK)) {
			return false;
		}
		TaskCardEffectivityPK castOther = (TaskCardEffectivityPK)other;
		return 
			this.taskCard.equals(castOther.taskCard)
			&& this.ac.equals(castOther.ac);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + this.ac.hashCode();
		
		return hash;
	}
}