package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class OrderRES {

	
	@XmlElement(name = "Operations")
	private  ArrayList<OperationsRES> Operations;
	
	@XmlElement(name = "SAPOrderNumber")
	private  String SAPOrderNumber;
	
	@XmlElement(name = "ErrorCode")
	private  String ErrorCode;
	
	@XmlElement(name = "Remarks")
	private  String Remarks;
	
	@XmlElement(name = "ZTRAX_WO")
	private  String WO;
	
	@XmlElement(name = "ZTASK_CARD")
	private  String TaskCard;
	

	public ArrayList<OperationsRES> getOperations() {
		return Operations;
	}

	public void setOperations(ArrayList<OperationsRES> operations) {
		Operations = operations;
	}

	public String getSAPOrderNumber() {
		return SAPOrderNumber;
	}

	public void setSAPOrderNumber(String sAPOrderNumber) {
		SAPOrderNumber = sAPOrderNumber;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
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
