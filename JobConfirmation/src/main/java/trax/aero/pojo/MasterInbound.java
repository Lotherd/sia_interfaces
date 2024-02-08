package trax.aero.pojo;



import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_I39_I40_4064", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MasterInbound {
	
	
	@XmlElement(name = "Order_Header")
	Inbound JobConfirmationInbound;

	public Inbound getJobConfirmationInbounds() {
		return JobConfirmationInbound;
	}

	public void setJobConfirmationInbounds(Inbound JobConfirmationInbound) {
		this.JobConfirmationInbound = JobConfirmationInbound;
	}

   
   
}
