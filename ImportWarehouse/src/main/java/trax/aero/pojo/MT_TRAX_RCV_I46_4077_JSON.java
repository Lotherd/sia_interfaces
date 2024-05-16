package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MT_TRAX_RCV_I46_4077_JSON {
	
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