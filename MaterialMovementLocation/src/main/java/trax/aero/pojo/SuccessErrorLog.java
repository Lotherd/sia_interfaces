package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SuccessErrorLog")
@XmlAccessorType(XmlAccessType.FIELD)
public class SuccessErrorLog {

	@XmlElement(name = "StatusErrorCode")
	private String StatusErrorCode;
	
	@XmlElement(name = "StatusMessage")
	private String StatusMessage;

	public String getStatusErrorCode() {
		return StatusErrorCode;
	}

	public void setStatusErrorCode(String statusErrorCode) {
		StatusErrorCode = statusErrorCode;
	}

	public String getStatusMessage() {
		return StatusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		StatusMessage = statusMessage;
	}
	
}
