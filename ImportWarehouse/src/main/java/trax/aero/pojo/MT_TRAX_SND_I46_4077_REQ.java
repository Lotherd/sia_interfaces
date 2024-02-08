package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_SND_I46_4077_REQ", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_SND_I46_4077_REQ {
	
	@XmlElement(name = "MaterialNumber")
	private String MaterialNumber;

    public String getMaterialNumber ()
    {
        return MaterialNumber;
    }

    public void setMaterialNumber (String materialNumber)
    {
        this.MaterialNumber = materialNumber;
    }
	
}
