package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class MT_TRAX_SND_I87_REQ {
	
	private String TRAXTCNumber;
	
	private String TRAXWO;
	
	private String SAPOrderNumber;
	
	private String SAPOperationNumber;
	
	private String SuccessErrorIndicator;
	
	private String Remarks;

	public String getTRAXTCNumber() {
		return TRAXTCNumber;
	}

	public void setTRAXTCNumber(String tRAXTCNumber) {
		TRAXTCNumber = tRAXTCNumber;
	}

	public String getTRAXWO() {
		return TRAXWO;
	}

	public void setTRAXWO(String tRAXWO) {
		TRAXWO = tRAXWO;
	}

	public String getSAPOrderNumber() {
		return SAPOrderNumber;
	}

	public void setSAPOrderNumber(String sAPOrderNumber) {
		SAPOrderNumber = sAPOrderNumber;
	}

	public String getSAPOperationNumber() {
		return SAPOperationNumber;
	}

	public void setSAPOperationNumber(String sAPOperationNumber) {
		SAPOperationNumber = sAPOperationNumber;
	}

	public String getSuccessErrorIndicator() {
		return SuccessErrorIndicator;
	}

	public void setSuccessErrorIndicator(String successErrorIndicator) {
		SuccessErrorIndicator = successErrorIndicator;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	
}
