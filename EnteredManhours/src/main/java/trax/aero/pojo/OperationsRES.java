package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class OperationsRES {

	@XmlElement(name = "OperationNumber")
	private  String OperationNumber;
	
	@XmlElement(name = "TRAXItemNumber")
	private  String TRAXItemNumber;

	public String getOperationNumber() {
		return OperationNumber;
	}

	public void setOperationNumber(String operationNumber) {
		OperationNumber = operationNumber;
	}

	public String getTRAXItemNumber() {
		return TRAXItemNumber;
	}

	public void setTRAXItemNumber(String tRAXItemNumber) {
		TRAXItemNumber = tRAXItemNumber;
	}
}
