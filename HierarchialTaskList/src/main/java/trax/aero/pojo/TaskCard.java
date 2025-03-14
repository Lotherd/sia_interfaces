package trax.aero.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

@XmlAccessorType(XmlAccessType.FIELD)
public class TaskCard 
{
	@XmlElement(name = "GroupNumber")
	private String groupNo;
	
	@XmlElement(name = "PlanningPlant")
	private String planningPlant;
	
	@XmlElement(name = "TaskCard")
	private String taskCard;
	
	@XmlElements({
	    @XmlElement(name = "DeletionIndicator", type = String.class),
	    @XmlElement(name = "ExclusionIndicator", type = String.class)
	})
	private List<String> excl;

	public String getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}

	public String getPlanningPlant() {
		return planningPlant;
	}

	public void setPlanningPlant(String planningPlant) {
		this.planningPlant = planningPlant;
	}

	public String getTaskCard() {
		return taskCard;
	}

	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}

	public List<String> getExcl() {
		return excl;
	}

	public void setExcl(List<String> excl) {
		this.excl = excl;
	}
	
	
}
