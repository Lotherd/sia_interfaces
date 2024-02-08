package trax.aero.pojo;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class TaskCards {

	@XmlElement(name = "GroupNumber")
    private String GroupNo;
	
	@XmlElement(name = "PlanningPlant")
	private String PlanningPlant;
	
	@XmlElement(name = "TaskCard")
	private String TaskCard;
	
	@XmlElement(name = "Description")
	private String Description;
	
	@XmlElement(name = "Category")
	private  String Category;
	
	@XmlElement(name = "Type")
	private  String Type;
	
	@XmlElement(name = "DeletionIndicator")
	private  String DeletionIndicator;
	
	@XmlElement(name = "Revision")
	private  String Revision;
	
	@XmlElement(name = "ATA")
	private  String ATA;
	
	@XmlElement(name = "Critical")
	private  String Critical;
	
	@XmlElement(name = "Phase")
	private String Phase;
	
	@XmlElement(name = "Area")
	private String Area;
	
	@XmlElement(name = "Zone")
	private String Zone;
	
	
	@XmlElement(name = "PanelList")
    ArrayList<PanelList> PanelList;
	
	@XmlElement(name = "ItemList")
    ArrayList<ItemList> ItemList;
	
	@XmlElement(name = "PredecessorList")
    ArrayList<PredecessorList> PredecessorList;
	
	public ArrayList<PanelList> getPanelList() {
		return PanelList;
	}

	public ArrayList<ItemList> getItemList() {
		return ItemList;
	}

	public ArrayList<PredecessorList> getPredecessorList() {
		return PredecessorList;
	}

	public void setPanelList(ArrayList<PanelList> panelList) {
		this.PanelList = panelList;
	}

	public void setItemList(ArrayList<ItemList> itemList) {
		this.ItemList = itemList;
	}

	public void setPredecessorList(ArrayList<PredecessorList> predecessorList) {
		this.PredecessorList = predecessorList;
	}
	
	public String getGroupNo() {
		return GroupNo;
	}

	public String getPlanningPlant() {
		return PlanningPlant;
	}

	public String getTaskCard() {
		return TaskCard;
	}

	public String getDescription() {
		return Description;
	}

	public String getCategory() {
		return Category;
	}

	public String getType() {
		return Type;
	}

	public String getDeletionIndicator() {
		return DeletionIndicator;
	}

	public String getRevision() {
		return Revision;
	}

	public String getATA() {
		return ATA;
	}

	public String getCritical() {
		return Critical;
	}

	public String getPhase() {
		return Phase;
	}

	public String getArea() {
		return Area;
	}

	public String getZone() {
		return Zone;
	}

	public void setGroupNo(String groupNo) {
		 this.GroupNo = groupNo;
	}

	public void setPlanningPlant(String planningPlant) {
		 this.PlanningPlant = planningPlant;
	}

	public void setTaskCard(String taskCard) {
		 this.TaskCard = taskCard;
	}

	public void setDescription(String description) {
		 this.Description = description;
	}

	public void setCategory(String category) {
		 this.Category = category;
	}

	public void setType(String type) {
		 this.Type = type;
	}

	public void setDeletionIndicator(String deletionIndicator) {
		 this.DeletionIndicator = deletionIndicator;
	}

	public void setRevision(String revision) {
		 this.Revision = revision;
	}

	public void setATA(String aTA) {
		 this.ATA = aTA;
	}

	public void setCritical(String critical) {
		 this.Critical = critical;
	}

	public void setPhase(String phase) {
		 this.Phase = phase;
	}

	public void setArea(String area) {
		 this.Area = area;
	}

	public void setZone(String zone) {
		 this.Zone = zone;
	}
	
	
	

}
