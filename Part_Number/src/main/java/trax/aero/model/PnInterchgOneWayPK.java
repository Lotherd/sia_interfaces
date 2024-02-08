package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PN_INTERCHG_ONE_WAY database table.
 * 
 */
@Embeddable
public class PnInterchgOneWayPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PN_INTERCHANGEABLE")
	private String pnInterchangeable;

	private String pn;

	public PnInterchgOneWayPK() {
	}
	public String getPnInterchangeable() {
		return this.pnInterchangeable;
	}
	public void setPnInterchangeable(String pnInterchangeable) {
		this.pnInterchangeable = pnInterchangeable;
	}
	public String getPn() {
		return this.pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PnInterchgOneWayPK)) {
			return false;
		}
		PnInterchgOneWayPK castOther = (PnInterchgOneWayPK)other;
		return 
			this.pnInterchangeable.equals(castOther.pnInterchangeable)
			&& this.pn.equals(castOther.pn);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pnInterchangeable.hashCode();
		hash = hash * prime + this.pn.hashCode();
		
		return hash;
	}
}