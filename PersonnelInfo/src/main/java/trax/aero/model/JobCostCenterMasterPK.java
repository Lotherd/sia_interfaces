package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the JOB_COST_CENTER_MASTER database table.
 * 
 */
@Embeddable
public class JobCostCenterMasterPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="JOB_ROLE")
	private String jobRole;

	@Column(name="COST_CENTRE")
	private String costCentre;

	private String skill;

	public JobCostCenterMasterPK() {
	}
	public String getJobRole() {
		return this.jobRole;
	}
	public void setJobRole(String jobRole) {
		this.jobRole = jobRole;
	}
	public String getCostCentre() {
		return this.costCentre;
	}
	public void setCostCentre(String costCentre) {
		this.costCentre = costCentre;
	}
	public String getSkill() {
		return this.skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof JobCostCenterMasterPK)) {
			return false;
		}
		JobCostCenterMasterPK castOther = (JobCostCenterMasterPK)other;
		return 
			this.jobRole.equals(castOther.jobRole)
			&& this.costCentre.equals(castOther.costCentre)
			&& this.skill.equals(castOther.skill);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.jobRole.hashCode();
		hash = hash * prime + this.costCentre.hashCode();
		hash = hash * prime + this.skill.hashCode();
		
		return hash;
	}
}