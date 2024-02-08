package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderAudit {

	@XmlElement(name = "SAPOrderNumber")
	private  String SAPOrderNumber;
	
	@XmlElement(name = "Description")
	private  String Description;
	
	@XmlElement(name = "MainWorkCenter")
	private  String MainWorkCenter;
	
	@XmlElement(name = "MainWorkCenterPlant")
	private  String MainWorkCenterPlant;
	
	@XmlElement(name = "Priority")
	private  String Priority;
	
	@XmlElement(name = "ZTRAX_WO")
	private  String WO;
	
	@XmlElement(name = "ZTASK_CARD")
	private  String TaskCard;
	
	public String getSAPOrderNumber() {
		return SAPOrderNumber;
	}

	public void setSAPOrderNumber(String sAPOrderNumber) {
		SAPOrderNumber = sAPOrderNumber;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getMainWorkCenter() {
		return MainWorkCenter;
	}

	public void setMainWorkCenter(String mainWorkCenter) {
		MainWorkCenter = mainWorkCenter;
	}

	public String getMainWorkCenterPlant() {
		return MainWorkCenterPlant;
	}

	public void setMainWorkCenterPlant(String mainWorkCenterPlant) {
		MainWorkCenterPlant = mainWorkCenterPlant;
	}

	public String getPriority() {
		return Priority;
	}

	public void setPriority(String priority) {
		Priority = priority;
	}

	public String getWO() {
		return WO;
	}

	public void setWO(String wO) {
		WO = wO;
	}

	public String getTaskCard() {
		return TaskCard;
	}

	public void setTaskCard(String taskCard) {
		TaskCard = taskCard;
	}
	
}
