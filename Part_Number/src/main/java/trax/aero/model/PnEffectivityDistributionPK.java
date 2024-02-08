package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * The primary key class for the PN_EFFECTIVITY_DISTRIBUTION database table.
 * 
 */
@Embeddable
public class PnEffectivityDistributionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pn;

	private String ac;

	public PnEffectivityDistributionPK() {
	}
	public String getPn() {
		return this.pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
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
		if (!(other instanceof PnEffectivityDistributionPK)) {
			return false;
		}
		PnEffectivityDistributionPK castOther = (PnEffectivityDistributionPK)other;
		return 
			this.pn.equals(castOther.pn)
			&& this.ac.equals(castOther.ac);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.ac.hashCode();
		
		return hash;
	}
}