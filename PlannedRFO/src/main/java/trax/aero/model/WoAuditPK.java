package trax.aero.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * The primary key class for the WO_AUDIT database table.
 * 
 */
@Embeddable
public class WoAuditPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private long wo;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	public WoAuditPK() {
	}
	public long getWo() {
		return this.wo;
	}
	public void setWo(long wo) {
		this.wo = wo;
	}
	public String getModifiedBy() {
		return this.modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getModifiedDate() {
		return this.modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof WoAuditPK)) {
			return false;
		}
		WoAuditPK castOther = (WoAuditPK)other;
		return 
			(this.wo == castOther.wo)
			&& this.modifiedBy.equals(castOther.modifiedBy)
			&& this.modifiedDate.equals(castOther.modifiedDate);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.wo ^ (this.wo >>> 32)));
		hash = hash * prime + this.modifiedBy.hashCode();
		hash = hash * prime + this.modifiedDate.hashCode();
		
		return hash;
	}
}