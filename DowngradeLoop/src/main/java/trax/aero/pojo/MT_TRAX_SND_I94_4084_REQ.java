package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_SND_I94_4084_REQ", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_SND_I94_4084_REQ {

	@XmlElement(name = "SAPOrderNumber")
	private String SAPServiceOrderNumber;

	public String getSAPServiceOrderNumber() {
		return SAPServiceOrderNumber;
	}

	public void setSAPServiceOrderNumber(String SAPServiceOrderNumber) {
		this.SAPServiceOrderNumber = SAPServiceOrderNumber;
	}

	
	
}
