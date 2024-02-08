package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the SAP_NON_ROUTINE database table.
 * 
 */
@Embeddable
public class SapNonRoutinePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private long wo;

	@Column(name="TASK_CARD")
	private String taskCard;

	private String pn;

	@Column(name="PN_SN")
	private String pnSn;

	public SapNonRoutinePK() {
	}
	public long getWo() {
		return this.wo;
	}
	public void setWo(long wo) {
		this.wo = wo;
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
	public String getPnSn() {
		return this.pnSn;
	}
	public void setPnSn(String pnSn) {
		this.pnSn = pnSn;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SapNonRoutinePK)) {
			return false;
		}
		SapNonRoutinePK castOther = (SapNonRoutinePK)other;
		return 
			(this.wo == castOther.wo)
			&& this.taskCard.equals(castOther.taskCard)
			&& this.pn.equals(castOther.pn)
			&& this.pnSn.equals(castOther.pnSn);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.wo ^ (this.wo >>> 32)));
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.pnSn.hashCode();
		
		return hash;
	}
}