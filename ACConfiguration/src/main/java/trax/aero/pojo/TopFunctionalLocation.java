package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TopFunctionalLocation {

	@XmlElement(name = "Equipment")
	private ArrayList<Equipment> Equipment;

	@XmlElement(name = "TopFunctionalLocation")
    private String TopFunctionalLocation;

    public ArrayList<Equipment> getEquipment ()
    {
        return Equipment;
    }

    public void setEquipment (ArrayList<Equipment> Equipment)
    {
        this.Equipment = Equipment;
    }

    public String getTopFunctionalLocation ()
    {
        return TopFunctionalLocation;
    }

    public void setTopFunctionalLocation (String TopFunctionalLocation)
    {
        this.TopFunctionalLocation = TopFunctionalLocation;
    }
}
