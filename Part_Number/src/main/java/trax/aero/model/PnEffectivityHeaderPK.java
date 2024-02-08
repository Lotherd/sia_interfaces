package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * The primary key class for the PN_EFFECTIVITY_HEADER database table.
 * 
 */
@Embeddable
public class PnEffectivityHeaderPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pn;

	@Column(name="AC_TYPE")
	private String acType;

	@Column(name="AC_SERIES")
	private String acSeries;

	public PnEffectivityHeaderPK() {
	}
	public String getPn() {
		return this.pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
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
		if (!(other instanceof PnEffectivityHeaderPK)) {
			return false;
		}
		PnEffectivityHeaderPK castOther = (PnEffectivityHeaderPK)other;
		return 
			this.pn.equals(castOther.pn)
			&& this.acType.equals(castOther.acType)
			&& this.acSeries.equals(castOther.acSeries);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.acType.hashCode();
		hash = hash * prime + this.acSeries.hashCode();
		
		return hash;
	}
}