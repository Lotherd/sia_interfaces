package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="MT_TRAX_SND_I74_4070_REQ", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_SND_I74_4070_REQ {

	@XmlElement(name = "OrderNumber")
	private String OrderNumber;

	@XmlElement(name = "ReferenceDate")
    private String ReferenceDate;

	@XmlElement(name = "ReferenceTime")
    private String ReferenceTime;

	@XmlElement(name = "TRAXStatus")
    private String TRAXStatus;

	@XmlElement(name = "TRAXStatusCategory")
    private String TRAXStatusCategory;

	@XmlElement(name = "ReasonForTECO_reversal")
    private String ReasonForTECO_reversal;

	@XmlElement(name = "CFI_ATTACHMENT")
    private ArrayList<CFI_ATTACHMENT> CFI_ATTACHMENT;

	@XmlElement(name = "ADDL_ATTACHMENT")
    private ArrayList<ADDL_ATTACHMENT> ADDL_ATTACHMENT;

	public String getOrderNumber() {
		return OrderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		OrderNumber = orderNumber;
	}

	public String getReferenceDate() {
		return ReferenceDate;
	}

	public void setReferenceDate(String referenceDate) {
		ReferenceDate = referenceDate;
	}

	public String getReferenceTime() {
		return ReferenceTime;
	}

	public void setReferenceTime(String referenceTime) {
		ReferenceTime = referenceTime;
	}

	public String getTRAXStatus() {
		return TRAXStatus;
	}

	public void setTRAXStatus(String tRAXStatus) {
		TRAXStatus = tRAXStatus;
	}

	public String getTRAXStatusCategory() {
		return TRAXStatusCategory;
	}

	public void setTRAXStatusCategory(String tRAXStatusCategory) {
		TRAXStatusCategory = tRAXStatusCategory;
	}

	public String getReasonForTECO_reversal() {
		return ReasonForTECO_reversal;
	}

	public void setReasonForTECO_reversal(String reasonForTECO_reversal) {
		ReasonForTECO_reversal = reasonForTECO_reversal;
	}

	public ArrayList<CFI_ATTACHMENT> getCFI_ATTACHMENT() {
		return CFI_ATTACHMENT;
	}

	public void setCFI_ATTACHMENT(ArrayList<CFI_ATTACHMENT> cFI_ATTACHMENT) {
		CFI_ATTACHMENT = cFI_ATTACHMENT;
	}

	public ArrayList<ADDL_ATTACHMENT> getADDL_ATTACHMENT() {
		return ADDL_ATTACHMENT;
	}

	public void setADDL_ATTACHMENT(ArrayList<ADDL_ATTACHMENT> aDDL_ATTACHMENT) {
		ADDL_ATTACHMENT = aDDL_ATTACHMENT;
	}

	

	
    
	
}
