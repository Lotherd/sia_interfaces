package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "LongText"
})
public class OrderLongText {

	@XmlElement(name = "LongText")
	protected String LongText;

	public String getLongText() {
		return LongText;
	}

	public void setLongText(String longText) {
		LongText = longText;
	}
}
