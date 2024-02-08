package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PN_POSITION_RESTRICTION_EFF database table.
 * 
 */
@Embeddable
public class PnPositionRestrictionEffPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false)
	private String pn;

	@Column(name="\"POSITION\"")
	private String position;

	@Column(insertable=false, updatable=false)
	private String ac;

	@Column(name="PN_RESTRICTED")
	private String pnRestricted;

	public PnPositionRestrictionEffPK() {
	}
	public String getPn() {
		return this.pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}
	public String getPosition() {
		return this.position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getAc() {
		return this.ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
	public String getPnRestricted() {
		return this.pnRestricted;
	}
	public void setPnRestricted(String pnRestricted) {
		this.pnRestricted = pnRestricted;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PnPositionRestrictionEffPK)) {
			return false;
		}
		PnPositionRestrictionEffPK castOther = (PnPositionRestrictionEffPK)other;
		return 
			this.pn.equals(castOther.pn)
			&& this.position.equals(castOther.position)
			&& this.ac.equals(castOther.ac)
			&& this.pnRestricted.equals(castOther.pnRestricted);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.position.hashCode();
		hash = hash * prime + this.ac.hashCode();
		hash = hash * prime + this.pnRestricted.hashCode();
		
		return hash;
	}
}