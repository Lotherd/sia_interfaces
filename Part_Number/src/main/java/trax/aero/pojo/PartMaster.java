package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;


@XmlRootElement(name="MT_RCV_TRAX_I16_I50_4061", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartMaster 
{
	
	@XmlElement(name = "PartNumber")
	private ArrayList<PartNumber> parts;
	
	public PartMaster()
	{
		parts = new ArrayList<>();
	}
	public ArrayList<PartNumber> getParts() {
		return parts;
	}

	public void setParts(ArrayList<PartNumber> parts) {
		this.parts = parts;
	}
	
	public void addPart(PartNumber part)
	{
		if(part != null)
		{
			parts.add(part);
		}
		
	}
	
}
