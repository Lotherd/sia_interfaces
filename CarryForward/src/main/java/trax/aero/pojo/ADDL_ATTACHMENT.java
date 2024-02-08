package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ADDL_ATTACHMENT {

	@XmlElement(name = "ZADDATT")
 	private byte[] ZADDATT;

	public byte[] getZADDATT() {
		return ZADDATT;
	}

	public void setZADDATT(byte[] zADDATT) {
		ZADDATT = zADDATT;
	}
	
}
