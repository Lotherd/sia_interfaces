package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the TASK_CARD_REV database table.
 * 
 */
@Embeddable
public class TaskCardRevPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TASK_CARD")
	private String taskCard;

	private String revison;

	public TaskCardRevPK() {
	}
	public String getTaskCard() {
		return this.taskCard;
	}
	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}
	public String getRevison() {
		return this.revison;
	}
	public void setRevison(String revison) {
		this.revison = revison;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TaskCardRevPK)) {
			return false;
		}
		TaskCardRevPK castOther = (TaskCardRevPK)other;
		return 
			this.taskCard.equals(castOther.taskCard)
			&& this.revison.equals(castOther.revison);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + this.revison.hashCode();
		
		return hash;
	}
}