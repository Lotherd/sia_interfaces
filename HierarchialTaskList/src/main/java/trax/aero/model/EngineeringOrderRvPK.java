package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ENGINEERING_ORDER_RV database table.
 * 
 */
@Embeddable
public class EngineeringOrderRvPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String eo;

	private String revision;

	public EngineeringOrderRvPK() {
	}
	public String getEo() {
		return this.eo;
	}
	public void setEo(String eo) {
		this.eo = eo;
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
		if (!(other instanceof EngineeringOrderRvPK)) {
			return false;
		}
		EngineeringOrderRvPK castOther = (EngineeringOrderRvPK)other;
		return 
			this.eo.equals(castOther.eo)
			&& this.revision.equals(castOther.revision);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.eo.hashCode();
		hash = hash * prime + this.revision.hashCode();
		
		return hash;
	}
}