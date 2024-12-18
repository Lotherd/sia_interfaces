package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the SITE_LOCATION_MASTER database table.
 * 
 */
@Entity
@Table(name="SITE_LOCATION_MASTER")
@NamedQuery(name="SiteLocationMaster.findAll", query="SELECT s FROM SiteLocationMaster s")
public class SiteLocationMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COST_CENTRE")
	private String costCentre;

	private String location;

	public SiteLocationMaster() {
	}

	public String getCostCentre() {
		return this.costCentre;
	}

	public void setCostCentre(String costCentre) {
		this.costCentre = costCentre;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}