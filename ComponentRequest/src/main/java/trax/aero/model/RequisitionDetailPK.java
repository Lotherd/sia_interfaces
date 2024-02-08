package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the REQUISITION_DETAIL database table.
 * 
 */
@Embeddable
public class RequisitionDetailPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false)
	private long requisition;

	@Column(name="REQUISITION_LINE")
	private long requisitionLine;

	public RequisitionDetailPK() {
	}
	public long getRequisition() {
		return this.requisition;
	}
	public void setRequisition(long requisition) {
		this.requisition = requisition;
	}
	public long getRequisitionLine() {
		return this.requisitionLine;
	}
	public void setRequisitionLine(long requisitionLine) {
		this.requisitionLine = requisitionLine;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RequisitionDetailPK)) {
			return false;
		}
		RequisitionDetailPK castOther = (RequisitionDetailPK)other;
		return 
			(this.requisition == castOther.requisition)
			&& (this.requisitionLine == castOther.requisitionLine);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.requisition ^ (this.requisition >>> 32)));
		hash = hash * prime + ((int) (this.requisitionLine ^ (this.requisitionLine >>> 32)));
		
		return hash;
	}
}