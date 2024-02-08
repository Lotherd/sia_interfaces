package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "statusErrorCode",
    "statusMessage"
})
public class ApplicatonMessage {

	 @XmlElement(name = "Status_ErrorCode")
	 protected String statusErrorCode;
	  
	 
	 @XmlElement(name = "StatusMessage")
	 protected String statusMessage;
	   
	 public String getStatusErrorCode() {
	        return statusErrorCode;
	    }

	    public void setStatusErrorCode(String value) {
	        this.statusErrorCode = value;
	    }
	    
	    public String getStatusMessage() {
	        return statusMessage;
	    }

	    public void setStatusMessage(String value) {
	        this.statusMessage = value;
	    }
	
}
