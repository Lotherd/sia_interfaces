package trax.aero.pojo;





import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_RCV_I94_4084_RES", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_RCV_I94_4084_RES {
	
	
	@XmlElement(name = "SAPOrderNumber")
	private String SAPOrderNumber;

	@XmlElement(name = "SAPServiceOrderNumber")
	private String SAPServiceOrderNumber;

	@XmlElement(name = "ErrorCode")
	private String ErrorCode;
	
	@XmlElement(name = "Remarks")
	private String Remarks;

	

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

	public String getSAPOrderNumber() {
		return SAPOrderNumber;
	}

	public void setSAPOrderNumber(String sAPOrderNumber) {
		SAPOrderNumber = sAPOrderNumber;
	}

	public String getSAPServiceOrderNumber() {
		return SAPServiceOrderNumber;
	}

	public void setSAPServiceOrderNumber(String sAPServiceOrderNumber) {
		SAPServiceOrderNumber = sAPServiceOrderNumber;
	}
   
   
}
