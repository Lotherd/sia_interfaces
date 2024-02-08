package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Operations")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationsAudit {

	@XmlElement(name = "OperationNumber")
	private  String OperationNumber;
	
	@XmlElement(name = "OperationDescription")
	private  String OperationDescription;
	
	@XmlElement(name = "SuboperationNumber")
	private  String SuboperationNumber;
	
	@XmlElement(name = "Workcenter")
	private  String Workcenter;
	
	@XmlElement(name = "Plant")
	private  String Plant;
	
	@XmlElement(name = "ControlKey")
	private  String ControlKey;
	
	@XmlElement(name = "Work")
	private  String Work;
	
	@XmlElement(name = "EnteredManHours")
	private  String EnteredManHours;
	
	@XmlElement(name = "DeletionFlag")
	private  String DeletionFlag;
	
	@XmlElement(name = "TRAXItemNumber")
	private  String TRAXItemNumber;

	public String getOperationNumber() {
		return OperationNumber;
	}

	public void setOperationNumber(String operationNumber) {
		OperationNumber = operationNumber;
	}

	public String getOperationDescription() {
		return OperationDescription;
	}

	public void setOperationDescription(String operationDescription) {
		OperationDescription = operationDescription;
	}

	public String getSuboperationNumber() {
		return SuboperationNumber;
	}

	public void setSuboperationNumber(String suboperationNumber) {
		SuboperationNumber = suboperationNumber;
	}

	public String getWorkcenter() {
		return Workcenter;
	}

	public void setWorkcenter(String workcenter) {
		Workcenter = workcenter;
	}

	public String getPlant() {
		return Plant;
	}

	public void setPlant(String plant) {
		Plant = plant;
	}

	public String getControlKey() {
		return ControlKey;
	}

	public void setControlKey(String controlKey) {
		ControlKey = controlKey;
	}

	public String getWork() {
		return Work;
	}

	public void setWork(String work) {
		Work = work;
	}

	public String getEnteredManHours() {
		return EnteredManHours;
	}

	public void setEnteredManHours(String enteredManHours) {
		EnteredManHours = enteredManHours;
	}

	public String getDeletionFlag() {
		return DeletionFlag;
	}

	public void setDeletionFlag(String deletionFlag) {
		DeletionFlag = deletionFlag;
	}

	public String getTRAXItemNumber() {
		return TRAXItemNumber;
	}

	public void setTRAXItemNumber(String tRAXItemNumber) {
		TRAXItemNumber = tRAXItemNumber;
	}
	
}
