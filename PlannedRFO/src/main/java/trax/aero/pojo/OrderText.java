package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class OrderText {

	@XmlElement(name = "LongText")
	private String LongText;

	public String getLongText() {
		return LongText;
	}

	public void setLongText(String longText) {
		LongText = longText;
	}
}
