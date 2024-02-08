package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Orders {
	
	@XmlElement(name = "RFONumber")
	private String RFONumber;
	
	@XmlElement(name = "TraxNonRoutine")
	private String TRAXNonRoutineNumber;
	
	@XmlElement(name = "TraxWO")
	private String TraxWO;
	
	@XmlElement(name = "SVONumber")
	private String SVONumber;
	
	@XmlElement(name = "Material")
	private String MaterialNumber;
	
	@XmlElement(name = "SerialNumber")
	private String SerialNumber;
	
	@XmlElement(name = "FunctionalLocation")
	private String FuncationalNumber;
	
	@XmlElement(name = "WorkCenter")
	private String MainWorkCenter;
	
	@XmlElement(name = "Site")
	private String ShopSite;
	
	@XmlElement(name = "Priority")
	private String priority;
	
	@XmlElement(name = "Plant")
	private String WorkCenterPlant;
	
	@XmlElement(name = "Operation")
	private ArrayList<Operation> operations;
	
	@XmlElement(name = "OrderText")
	private ArrayList<OrderText> OrderText;
			
	
	public ArrayList<Operation> getOperations() {
		return operations;
	}

	public void setOperations(ArrayList<Operation> operations) {
		this.operations = operations;
	}

	public String getRFONumber() {
		return RFONumber;
	}

	public void setRFONumber(String rFONumber) {
		RFONumber = rFONumber;
	}

	public String getTRAXNonRoutineNumber() {
		return TRAXNonRoutineNumber;
	}

	public void setTRAXNonRoutineNumber(String tRAXNonRoutineNumber) {
		TRAXNonRoutineNumber = tRAXNonRoutineNumber;
	}

	public String getTraxWO() {
		return TraxWO;
	}

	public void setTraxWO(String traxWO) {
		TraxWO = traxWO;
	}

	public String getSVONumber() {
		return SVONumber;
	}

	public void setSVONumber(String sVONumber) {
		SVONumber = sVONumber;
	}

	public String getMaterialNumber() {
		return MaterialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		MaterialNumber = materialNumber;
	}

	public String getSerialNumber() {
		return SerialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	public String getFuncationalNumber() {
		return FuncationalNumber;
	}

	public void setFuncationalNumber(String funcationalNumber) {
		FuncationalNumber = funcationalNumber;
	}

	public String getMainWorkCenter() {
		return MainWorkCenter;
	}

	public void setMainWorkCenter(String mainWorkCenter) {
		MainWorkCenter = mainWorkCenter;
	}

	public String getShopSite() {
		return ShopSite;
	}

	public void setShopSite(String shopSite) {
		ShopSite = shopSite;
	}

	public String getWorkCenterPlant() {
		return WorkCenterPlant;
	}

	public void setWorkCenterPlant(String workCenterPlant) {
		WorkCenterPlant = workCenterPlant;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public ArrayList<OrderText> getOrderText() {
		return OrderText;
	}

	public void setOrderText(ArrayList<OrderText> orderText) {
		OrderText = orderText;
	}
	
	
	
	
}
