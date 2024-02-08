package trax.aero.pojo;



import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_RCV_I95_4081_RES", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_RCV_I95_4081_RES {
	
	
	@XmlElement(name = "Order")
	private OrderRES Order;

	@XmlElement(name = "SuccessErrorLog")
	private SuccessErrorLog SuccessErrorLog;

	public OrderRES getOrder() {
		return Order;
	}

	public void setOrder(OrderRES order) {
		Order = order;
	}

	public SuccessErrorLog getSuccessErrorLog() {
		return SuccessErrorLog;
	}

	public void setSuccessErrorLog(SuccessErrorLog successErrorLog) {
		SuccessErrorLog = successErrorLog;
	}

   
   
}
