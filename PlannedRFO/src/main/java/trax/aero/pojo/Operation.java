package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Operation {

	@XmlElement(name = "OperationNumber")
	private String OperationNumber;
	
	@XmlElement(name = "WorkCenter")
	private String Workcenter;
	
	@XmlElement(name = "Plant")
	private String OperationWorkcenterPlant;
	
	@XmlElement(name = "ControlKey")
	private String ControlKey;
	
	@XmlElement(name = "ShortText")
	private String OperationShortText;
	
	@XmlElement(name = "Work")
	private String WorkInvolved;
	
	@XmlElement(name = "Unit")
	private String Unit;
	
	@XmlElement(name = "Component")
	private ArrayList<Component> Component;

	
	@XmlElement(name = "OperationText")
	private ArrayList<OperationText> OperationText;

	
	public ArrayList<Component> getComponent() {
		return Component;
	}

	public void setComponent(ArrayList<Component> component) {
		Component = component;
	}

	public String getOperationNumber() {
		return OperationNumber;
	}

	public void setOperationNumber(String operationNumber) {
		OperationNumber = operationNumber;
	}

	public String getWorkcenter() {
		return Workcenter;
	}

	public void setWorkcenter(String workcenter) {
		Workcenter = workcenter;
	}

	public String getOperationWorkcenterPlant() {
		return OperationWorkcenterPlant;
	}

	public void setOperationWorkcenterPlant(String operationWorkcenterPlant) {
		OperationWorkcenterPlant = operationWorkcenterPlant;
	}

	public String getControlKey() {
		return ControlKey;
	}

	public void setControlKey(String controlKey) {
		ControlKey = controlKey;
	}

	public String getOperationShortText() {
		return OperationShortText;
	}

	public void setOperationShortText(String operationShortText) {
		OperationShortText = operationShortText;
	}

	public String getWorkInvolved() {
		return WorkInvolved;
	}

	public void setWorkInvolved(String workInvolved) {
		WorkInvolved = workInvolved;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public ArrayList<OperationText> getOperationText() {
		return OperationText;
	}

	public void setOperationText(ArrayList<OperationText> operationText) {
		OperationText = operationText;
	}
	
}
