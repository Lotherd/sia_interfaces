package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_RCV_TRAX_I19_4060", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class Hierarchy {
	
	@XmlElement(name = "TaskCards")
	private ArrayList<Ec> data;


	public ArrayList<Ec> getData() {
		return data;
	}


	public void setData(ArrayList<Ec> data) {
		this.data = data;
	}
	
	
}
