package trax.aero.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Ec 
{
	@XmlElement(name = "EC")
	private String ec;
	
	@XmlElement(name = "PlanningPlant")
	private String planningPlant;
	
	@XmlElement(name = "Description")
	private String desc;
	
	@XmlElement(name = "Category")
	private String category;
	
	@XmlElement(name = "SubEC")
	private String subEc;
	
	@XmlElement(name = "Classification")
	private String classif;
	
	@XmlElement(name = "Revision")
	private String revision;
	
	@XmlElement(name = "SubEC_list")
	private List<SubEc> subEcs;
	
	@XmlElement(name = "TaskCardList")
	private List<TaskCard> taskCards;
	
	@XmlElement(name = "DeletionIndicator")
	private String delInd;
	
	@XmlElement(name = "MPD")
	private String mpd;
	
	@XmlElement(name = "Effectivity")
	public String effectivity;
	
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubEc() {
		return subEc;
	}

	public void setSubEc(String subEc) {
		this.subEc = subEc;
	}

	public String getClassif() {
		return classif;
	}

	public void setClassif(String classif) {
		this.classif = classif;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public List<SubEc> getSubEcs() {
		return subEcs;
	}

	public void setSubEcs(List<SubEc> subEcs) {
		this.subEcs = subEcs;
	}

	public List<TaskCard> getTaskCards() {
		return taskCards;
	}

	public void setTaskCards(List<TaskCard> taskCards) {
		this.taskCards = taskCards;
	}

	public String getDelInd() {
		return delInd;
	}

	public void setDelInd(String delInd) {
		this.delInd = delInd;
	}

	public String getMpd() {
		return mpd;
	}

	public void setMpd(String mpd) {
		this.mpd = mpd;
	}

	public String getEffectivity() {
		return effectivity;
	}

	public void setEffectivity(String effectivity) {
		this.effectivity = effectivity;
	}
	
	
}
