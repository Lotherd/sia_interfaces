package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class OperationText {

	@XmlElement(name = "OperationNumber")
	private String OperationNumber;
	
	@XmlElement(name = "LongText")
	private String LongText;

	public String getOperationNumber() {
		return OperationNumber;
	}

	public void setOperationNumber(String operationNumber) {
		OperationNumber = operationNumber;
	}

	public String getLongText() {
		return LongText;
	}

	public void setLongText(String longText) {
		LongText = longText;
	}
	
	
}
