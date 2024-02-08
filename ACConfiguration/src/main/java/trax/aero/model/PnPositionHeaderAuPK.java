package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PN_POSITION_HEADER_AU database table.
 * 
 */
@Embeddable
public class PnPositionHeaderAuPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String pn;

	@Column(name="AC_TYPE")
	private String acType;

	@Column(name="AC_SERIES")
	private String acSeries;

	@Column(name="\"POSITION\"")
	private String position;

	@Column(name="MODIFIED_DATE")
	private String modifiedDate;

	public PnPositionHeaderAuPK() {
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
	public String getPosition() {
		return this.position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getModifiedDate() {
		return this.modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PnPositionHeaderAuPK)) {
			return false;
		}
		PnPositionHeaderAuPK castOther = (PnPositionHeaderAuPK)other;
		return 
			this.pn.equals(castOther.pn)
			&& this.acType.equals(castOther.acType)
			&& this.acSeries.equals(castOther.acSeries)
			&& this.position.equals(castOther.position)
			&& this.modifiedDate.equals(castOther.modifiedDate);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.acType.hashCode();
		hash = hash * prime + this.acSeries.hashCode();
		hash = hash * prime + this.position.hashCode();
		hash = hash * prime + this.modifiedDate.hashCode();
		
		return hash;
	}
}