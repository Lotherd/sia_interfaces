package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Success_errorLog")
@XmlAccessorType(XmlAccessType.FIELD)
public class Success_errorLog {

	@XmlElement(name = "IDOC_Number")
	private String IDOC_Number;
	
	@XmlElement(name = "Status_ErrorCode")
	private String Status_ErrorCode;
	
	@XmlElement(name = "IDOC_Status")
	private String IDOC_Status;
	
	@XmlElement(name = "StatusMessage")
	private String StatusMessage;
	
	public String getIDOC_Number ()
    {
        return IDOC_Number;
    }

    public void setIDOC_Number (String IDOC_Number)
    {
        this.IDOC_Number = IDOC_Number;
    }
    
    public String getStatus_ErrorCode ()
    {
        return Status_ErrorCode;
    }

    public void setStatus_ErrorCode (String Status_ErrorCode)
    {
        this.Status_ErrorCode = Status_ErrorCode;
    }
    
    public String getIDOC_Status ()
    {
        return IDOC_Status;
    }

    public void setIDOC_Status (String IDOC_Status)
    {
        this.IDOC_Status = IDOC_Status;
    }
    
    public String getStatusMessage ()
    {
        return StatusMessage;
    }

    public void setStatusMessage (String StatusMessage)
    {
        this.StatusMessage = StatusMessage;
    }
	
}
