package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class CFI_ATTACHMENT {

	@XmlElement(name = "ZCFIATT")
 	private byte[] ZCFIATT;

	public byte[] getZCFIATT() {
		return ZCFIATT;
	}

	public void setZCFIATT(byte[] zCFIATT) {
		ZCFIATT = zCFIATT;
	}
	
}
