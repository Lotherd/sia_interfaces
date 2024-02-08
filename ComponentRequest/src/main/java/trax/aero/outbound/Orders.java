package trax.aero.outbound;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Orders {

	@XmlElement(name = "Components")
    private ArrayList<Component> Component;
	
	@XmlElement(name = "OrderNumber")
	private String OrderNumber;
	
	 public ArrayList<Component> getComponent ()
	    {
	        return Component;
	    }

	    public void setComponent(ArrayList<Component> Component)
	    {
	        this.Component = Component;
	    }

	    public String getOrderNumber ()
	    {
	        return OrderNumber;
	    }

	    public void setOrderNumber (String OrderNumber)
	    {
	        this.OrderNumber = OrderNumber;
	    }

}

