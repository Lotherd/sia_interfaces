package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_SND_I84_4071_REQ", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_SND_I84_4071_REQ  {

	@XmlElement(name = "Order")
	private  ArrayList<OrderREQ> Order;

	public ArrayList<OrderREQ> getOrder() {
		return Order;
	}

	public void setOrder(ArrayList<OrderREQ> order) {
		Order = order;
	}

	
   
}
