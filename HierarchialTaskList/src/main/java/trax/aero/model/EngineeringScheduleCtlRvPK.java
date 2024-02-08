package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ENGINEERING_SCHEDULE_CTL_RV database table.
 * 
 */
@Embeddable
public class EngineeringScheduleCtlRvPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String eo;

	private long item;

	@Column(name="SCHEDULE_EO")
	private String scheduleEo;

	private String revision;

	public EngineeringScheduleCtlRvPK() {
	}
	public String getEo() {
		return this.eo;
	}
	public void setEo(String eo) {
		this.eo = eo;
	}
	public long getItem() {
		return this.item;
	}
	public void setItem(long item) {
		this.item = item;
	}
	public String getScheduleEo() {
		return this.scheduleEo;
	}
	public void setScheduleEo(String scheduleEo) {
		this.scheduleEo = scheduleEo;
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
		if (!(other instanceof EngineeringScheduleCtlRvPK)) {
			return false;
		}
		EngineeringScheduleCtlRvPK castOther = (EngineeringScheduleCtlRvPK)other;
		return 
			this.eo.equals(castOther.eo)
			&& (this.item == castOther.item)
			&& this.scheduleEo.equals(castOther.scheduleEo)
			&& this.revision.equals(castOther.revision);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.eo.hashCode();
		hash = hash * prime + ((int) (this.item ^ (this.item >>> 32)));
		hash = hash * prime + this.scheduleEo.hashCode();
		hash = hash * prime + this.revision.hashCode();
		
		return hash;
	}
}