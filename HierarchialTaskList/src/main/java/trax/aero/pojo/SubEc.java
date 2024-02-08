package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)

public class SubEc 
{
	@XmlElement(name = "EC")
	private String ec;
	
	@XmlElement(name = "PlanningPlant")
	private String planningPlant;
	
	@XmlElement(name = "Control")
	private String control;
	
	@XmlElement(name = "ExclusionIndicator")
	private String excl;

	public String getEc() {
		return ec;
	}

	public void setEc(String ec) {
		this.ec = ec;
	}

	public String getPlanningPlant() {
		return planningPlant;
	}

	public void setPlanningPlant(String planningPlant) {
		this.planningPlant = planningPlant;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getExcl() {
		return excl;
	}

	public void setExcl(String excl) {
		this.excl = excl;
	}
	
	
	
}
