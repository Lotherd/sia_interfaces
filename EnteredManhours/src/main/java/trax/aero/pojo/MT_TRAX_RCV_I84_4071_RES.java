package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_RCV_I84_4071_RES", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_RCV_I84_4071_RES {
	
	
	@XmlElement(name = "Order")
	private  ArrayList<OrderRES> Order;

	public ArrayList<OrderRES> getOrder() {
		return Order;
	}

	public void setOrder(ArrayList<OrderRES> order) {
		Order = order;
	}
   
   
}
