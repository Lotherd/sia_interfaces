package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_SND_I95_4081_REQ", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_SND_I95_4081_REQ {

	@XmlElement(name = "Order")
	private OrderREQ Order;

	public OrderREQ getOrder() {
		return Order;
	}

	public void setOrder(OrderREQ order) {
		Order = order;
	}
	
}
