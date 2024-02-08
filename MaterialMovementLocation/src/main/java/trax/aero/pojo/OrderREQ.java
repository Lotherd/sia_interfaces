package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderREQ {

	@XmlElement(name = "Customer")
	private String Customer;

	@XmlElement(name = "Quantity")
    private String Quantity;

	@XmlElement(name = "OrderNumber")
    private String OrderNumber;

	@XmlElement(name = "ReceivingStorageLocation")
    private String ReceivingStorageLocation;

	@XmlElement(name = "FromPlant")
    private String FromPlant;

	@XmlElement(name = "MovementType")
    private String MovementType;

	@XmlElement(name = "SpecialStockIndicator")
    private String SpecialStockIndicator;

	@XmlElement(name = "RequistionNumber")
    private String RequistionNumber;

	@XmlElement(name = "RequistionLine")
    private String RequistionLine;

	@XmlElement(name = "SerialNumber")
    private String SerialNumber;

	@XmlElement(name = "Batch")
    private String Batch;

	@XmlElement(name = "FromStorageLocation")
    private String FromStorageLocation;

	@XmlElement(name = "MaterialNumber")
    private String MaterialNumber;

    public String getCustomer ()
    {
        return Customer;
    }

    public void setCustomer (String Customer)
    {
        this.Customer = Customer;
    }

    public String getQuantity ()
    {
        return Quantity;
    }

    public void setQuantity (String Quantity)
    {
        this.Quantity = Quantity;
    }

    public String getOrderNumber ()
    {
        return OrderNumber;
    }

    public void setOrderNumber (String OrderNumber)
    {
        this.OrderNumber = OrderNumber;
    }

    public String getReceivingStorageLocation ()
    {
        return ReceivingStorageLocation;
    }

    public void setReceivingStorageLocation (String ReceivingStorageLocation)
    {
        this.ReceivingStorageLocation = ReceivingStorageLocation;
    }

    public String getFromPlant ()
    {
        return FromPlant;
    }

    public void setFromPlant (String FromPlant)
    {
        this.FromPlant = FromPlant;
    }

    public String getMovementType ()
    {
        return MovementType;
    }

    public void setMovementType (String MovementType)
    {
        this.MovementType = MovementType;
    }

    public String getSpecialStockIndicator ()
    {
        return SpecialStockIndicator;
    }

    public void setSpecialStockIndicator (String SpecialStockIndicator)
    {
        this.SpecialStockIndicator = SpecialStockIndicator;
    }

    public String getRequistionNumber ()
    {
        return RequistionNumber;
    }

    public void setRequistionNumber (String RequistionNumber)
    {
        this.RequistionNumber = RequistionNumber;
    }

    public String getRequistionLine ()
    {
        return RequistionLine;
    }

    public void setRequistionLine (String RequistionLine)
    {
        this.RequistionLine = RequistionLine;
    }

    public String getSerialNumber ()
    {
        return SerialNumber;
    }

    public void setSerialNumber (String SerialNumber)
    {
        this.SerialNumber = SerialNumber;
    }

    public String getBatch ()
    {
        return Batch;
    }

    public void setBatch (String Batch)
    {
        this.Batch = Batch;
    }

    public String getFromStorageLocation ()
    {
        return FromStorageLocation;
    }

    public void setFromStorageLocation (String FromStorageLocation)
    {
        this.FromStorageLocation = FromStorageLocation;
    }

    public String getMaterialNumber ()
    {
        return MaterialNumber;
    }

    public void setMaterialNumber (String MaterialNumber)
    {
        this.MaterialNumber = MaterialNumber;
    }

}
