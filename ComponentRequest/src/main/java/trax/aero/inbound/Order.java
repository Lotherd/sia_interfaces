package trax.aero.inbound;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Order {

	 @XmlElement(name = "OrderNumber", required = true)
	 protected String orderNumber;
	
	@XmlElement(name = "Component", required = true)
    protected ArrayList<Component> component;

    /**
     * Gets the value of the component property.
     * 
     * @return
     *     possible object is
     *     {@link Component }
     *     
     */
    public ArrayList<Component> getComponent() {
        return component;
    }

    /**
     * Sets the value of the component property.
     * 
     * @param value
     *     allowed object is
     *     {@link Component }
     *     
     */
    public void setComponent(ArrayList<Component> value) {
        this.component = value;
    }
	
    /**
     * Gets the value of the orderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the value of the orderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNumber(String value) {
        this.orderNumber = value;
    }

    
}
