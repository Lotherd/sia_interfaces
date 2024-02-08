package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the SIA_REQUISITIONS database table.
 * 
 */
@Embeddable
public class SiaRequisitionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="REQUISITION_LINE")
	private long requisitionLine;

	private long requisition;

	public SiaRequisitionPK() {
	}
	public long getRequisitionLine() {
		return this.requisitionLine;
	}
	public void setRequisitionLine(long requisitionLine) {
		this.requisitionLine = requisitionLine;
	}
	public long getRequisition() {
		return this.requisition;
	}
	public void setRequisition(long requisition) {
		this.requisition = requisition;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SiaRequisitionPK)) {
			return false;
		}
		SiaRequisitionPK castOther = (SiaRequisitionPK)other;
		return 
			(this.requisitionLine == castOther.requisitionLine)
			&& (this.requisition == castOther.requisition);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.requisitionLine ^ (this.requisitionLine >>> 32)));
		hash = hash * prime + ((int) (this.requisition ^ (this.requisition >>> 32)));
		
		return hash;
	}
}