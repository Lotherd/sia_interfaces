package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_RCV_I74_4070_RES", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_RCV_I74_4070_RES {

	@XmlElement(name = "OrderNumber")
	private String OrderNumber;
	
	@XmlElement(name = "ErrorCode")
	private String ErrorCode;
	
	@XmlElement(name = "Remarks")
	private String Remarks;


	public String getOrderNumber() {
		return OrderNumber;
	}


	public void setOrderNumber(String orderNumber) {
		OrderNumber = orderNumber;
	}


	public String getErrorCode() {
		return ErrorCode;
	}


	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}


	public String getRemarks() {
		return Remarks;
	}


	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

   
	
	
	
	
	
}
