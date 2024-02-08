package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ENGINEERING_SCHEDULE_CONTROL database table.
 * 
 */
@Embeddable
public class EngineeringScheduleControlPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="EO", insertable=false, updatable=false)
	private String eo;

	private long item;

	@Column(name="SCHEDULE_EO")
	private String scheduleEo;

	public EngineeringScheduleControlPK() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EngineeringScheduleControlPK)) {
			return false;
		}
		EngineeringScheduleControlPK castOther = (EngineeringScheduleControlPK)other;
		return 
			this.eo.equals(castOther.eo)
			&& (this.item == castOther.item)
			&& this.scheduleEo.equals(castOther.scheduleEo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.eo.hashCode();
		hash = hash * prime + ((int) (this.item ^ (this.item >>> 32)));
		hash = hash * prime + this.scheduleEo.hashCode();
		
		return hash;
	}
}