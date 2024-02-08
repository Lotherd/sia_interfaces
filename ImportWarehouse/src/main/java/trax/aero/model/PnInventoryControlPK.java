package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PN_INVENTORY_CONTROL database table.
 * 
 */
@Embeddable
public class PnInventoryControlPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pn;

	private String sn;

	private String control;

	public PnInventoryControlPK() {
	}
	public String getPn() {
		return this.pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}
	public String getSn() {
		return this.sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getControl() {
		return this.control;
	}
	public void setControl(String control) {
		this.control = control;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PnInventoryControlPK)) {
			return false;
		}
		PnInventoryControlPK castOther = (PnInventoryControlPK)other;
		return 
			this.pn.equals(castOther.pn)
			&& this.sn.equals(castOther.sn)
			&& this.control.equals(castOther.control);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.sn.hashCode();
		hash = hash * prime + this.control.hashCode();
		
		return hash;
	}
}