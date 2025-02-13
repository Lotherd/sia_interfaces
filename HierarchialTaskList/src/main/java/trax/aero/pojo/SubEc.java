package trax.aero.pojo;

import java.util.List;

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
	
	@XmlElements({
	    @XmlElement(name = "DeletionIndicator", type = String.class),
	    @XmlElement(name = "ExclusionIndicator", type = String.class)
	})
	private List<String> excl;

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
	

	public List<String> getExcl() {
		return excl;
	}

	public void setExcl(List<String> excl) {
		this.excl = excl;
	}
	
	
	
}
