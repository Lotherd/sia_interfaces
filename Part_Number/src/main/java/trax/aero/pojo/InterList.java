package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;




@XmlAccessorType(XmlAccessType.FIELD)
public class InterList 
{
	@XmlElement(name = "PartNumber")
	private ArrayList<String> interchangeableBothPn;

	public ArrayList<String> getInterchangeableBothPn() {
		return interchangeableBothPn;
	}

	public void setInterchangeableBothPn(ArrayList<String> interchangeableBothPn) {
		this.interchangeableBothPn = interchangeableBothPn;
	}
	
	


	
}
