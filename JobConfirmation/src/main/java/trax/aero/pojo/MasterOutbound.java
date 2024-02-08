package trax.aero.pojo;



import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_I39_I40_ACK_4065", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MasterOutbound {
	
	
	@XmlElement(name = "OrderHeader")
	ArrayList<Outbound> JobConfirmationOutbound;

	@XmlElement(name = "Success_errorLog")
	private Success_errorLog SuccessErrorLog;
	
	public Success_errorLog getSuccess_errorLog ()
    {
        return SuccessErrorLog;
    }

    public void setSuccess_errorLog (Success_errorLog SuccessErrorLog)
    {
        this.SuccessErrorLog = SuccessErrorLog;
    }
	
	public ArrayList<Outbound> getJobConfirmationOutbounds() {
		return JobConfirmationOutbound;
	}

	public void setJJobConfirmationOutbounds(ArrayList<Outbound> JobConfirmationOutbound) {
		this.JobConfirmationOutbound = JobConfirmationOutbound;
	}

   
   
}
