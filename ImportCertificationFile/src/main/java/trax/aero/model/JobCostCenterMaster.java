package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the JOB_COST_CENTER_MASTER database table.
 * 
 */
@Entity
@Table(name="JOB_COST_CENTER_MASTER")
@NamedQuery(name="JobCostCenterMaster.findAll", query="SELECT j FROM JobCostCenterMaster j")
public class JobCostCenterMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private JobCostCenterMasterPK id;

	public JobCostCenterMaster() {
	}

	public JobCostCenterMasterPK getId() {
		return this.id;
	}

	public void setId(JobCostCenterMasterPK id) {
		this.id = id;
	}

}