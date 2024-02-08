package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ENGINEERING_TASK_CARD_CONTROL database table.
 * 
 */
@Embeddable
public class EngineeringTaskCardControlPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="EO", insertable=false, updatable=false)
	private String eo;

	private String category;

	@Column(name="TASK_CARD", insertable=false, updatable=false)
	private String taskCard;

	@Column(name="AC_TYPE")
	private String acType;

	@Column(name="AC_SERIES")
	private String acSeries;

	private String ac;

	private String pn;

	private String sn;

	@Column(name="TASK_CARD_ITEM")
	private long taskCardItem;

	public EngineeringTaskCardControlPK() {
	}
	public String getEo() {
		return this.eo;
	}
	public void setEo(String eo) {
		this.eo = eo;
	}
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTaskCard() {
		return this.taskCard;
	}
	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
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
	public String getAc() {
		return this.ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
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
	public long getTaskCardItem() {
		return this.taskCardItem;
	}
	public void setTaskCardItem(long taskCardItem) {
		this.taskCardItem = taskCardItem;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EngineeringTaskCardControlPK)) {
			return false;
		}
		EngineeringTaskCardControlPK castOther = (EngineeringTaskCardControlPK)other;
		return 
			this.eo.equals(castOther.eo)
			&& this.category.equals(castOther.category)
			&& this.taskCard.equals(castOther.taskCard)
			&& this.acType.equals(castOther.acType)
			&& this.acSeries.equals(castOther.acSeries)
			&& this.ac.equals(castOther.ac)
			&& this.pn.equals(castOther.pn)
			&& this.sn.equals(castOther.sn)
			&& (this.taskCardItem == castOther.taskCardItem);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.eo.hashCode();
		hash = hash * prime + this.category.hashCode();
		hash = hash * prime + this.taskCard.hashCode();
		hash = hash * prime + this.acType.hashCode();
		hash = hash * prime + this.acSeries.hashCode();
		hash = hash * prime + this.ac.hashCode();
		hash = hash * prime + this.pn.hashCode();
		hash = hash * prime + this.sn.hashCode();
		hash = hash * prime + ((int) (this.taskCardItem ^ (this.taskCardItem >>> 32)));
		
		return hash;
	}
}