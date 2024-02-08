package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Equipment {

	@XmlElement(name = "SystemStatus")
	private String SystemStatus;

	@XmlElement(name = "Description")
    private String Description;

	@XmlElement(name = "PartNumber")
    private String PartNumber;

	@XmlElement(name = "EquipmentCategory")
    private String EquipmentCategory;

	@XmlElement(name = "Position")
    private String Position;

	@XmlElement(name = "FunctionalLocation")
    private String FunctionalLocation;

	@XmlElement(name = "UserStatus")
    private String UserStatus;

	@XmlElement(name = "SerialNumber")
    private String SerialNumber;

	@XmlElement(name = "ValidTo")
    private String ValidTo;

	@XmlElement(name = "SuperiorEquipment")
    private String SuperiorEquipment;

	

	@XmlElement(name = "ValidFrom")
    private String ValidFrom;

	@XmlElement(name = "EquipmentNumber")
    private String EquipmentNumber;
    
    public String getSystemStatus ()
    {
        return SystemStatus;
    }

    public void setSystemStatus (String SystemStatus)
    {
        this.SystemStatus = SystemStatus;
    }

    public String getDescription ()
    {
        return Description;
    }

    public void setDescription (String Description)
    {
        this.Description = Description;
    }

    public String getPartNumber ()
    {
        return PartNumber;
    }

    public void setPartNumber (String PartNumber)
    {
        this.PartNumber = PartNumber;
    }

    public String getEquipmentCategory ()
    {
        return EquipmentCategory;
    }

    public void setEquipmentCategory (String EquipmentCategory)
    {
        this.EquipmentCategory = EquipmentCategory;
    }

    public String getPosition ()
    {
        return Position;
    }

    public void setPosition (String Position)
    {
        this.Position = Position;
    }

    public String getFunctionalLocation ()
    {
        return FunctionalLocation;
    }

    public void setFunctionalLocation (String FunctionalLocation)
    {
        this.FunctionalLocation = FunctionalLocation;
    }

    public String getUserStatus ()
    {
        return UserStatus;
    }

    public void setUserStatus (String UserStatus)
    {
        this.UserStatus = UserStatus;
    }

    public String getSerialNumber ()
    {
        return SerialNumber;
    }

    public void setSerialNumber (String SerialNumber)
    {
        this.SerialNumber = SerialNumber;
    }

    public String getValidTo ()
    {
        return ValidTo;
    }

    public void setValidTo (String ValidTo)
    {
        this.ValidTo = ValidTo;
    }

    public String getSuperiorEquipment ()
    {
        return SuperiorEquipment;
    }

    public void setSuperiorEquipment (String SuperiorEquipment)
    {
        this.SuperiorEquipment = SuperiorEquipment;
    }

    

    public String getValidFrom ()
    {
        return ValidFrom;
    }

    public void setValidFrom (String ValidFrom)
    {
        this.ValidFrom = ValidFrom;
    }

    public String getEquipmentNumber ()
    {
        return EquipmentNumber;
    }

    public void setEquipmentNumber (String EquipmentNumber)
    {
        this.EquipmentNumber = EquipmentNumber;
    }

	
}
