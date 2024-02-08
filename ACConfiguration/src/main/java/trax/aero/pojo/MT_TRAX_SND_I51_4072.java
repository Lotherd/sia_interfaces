package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_SND_I51_4072", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_SND_I51_4072 {

	@XmlElement(name = "AircraftTailNumber")
	String AircraftTailNumber;
	
	public String getAircraftTailNumber ()
	{
		return AircraftTailNumber;
	}
	
	public void setAircraftTailNumber (String Aircrafttailnumber)
	{
		this.AircraftTailNumber = Aircrafttailnumber;
	}
		
}
