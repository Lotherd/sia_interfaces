package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Component {

	@XmlElement(name = "MaterialNumber")
	private String MaterialNumber;
	
	@XmlElement(name = "StorageLocation")
	private String StorageLocation;
	
	@XmlElement(name = "Plant")
	private String 	Plant;
	
	@XmlElement(name = "Quantity")
	private String Quantity;
	
	@XmlElement(name = "Batch")
	private String Batch;
	
	@XmlElement(name = "ReservationNumber")
	private String ReservationNumber;
	
	@XmlElement(name = "ReservationItem")
	private String ResrvationItem;

	public String getMaterialNumber() {
		return MaterialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		MaterialNumber = materialNumber;
	}

	public String getStorageLocation() {
		return StorageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		StorageLocation = storageLocation;
	}

	public String getPlant() {
		return Plant;
	}

	public void setPlant(String plant) {
		Plant = plant;
	}

	public String getQuantity() {
		return Quantity;
	}

	public void setQuantity(String quantity) {
		Quantity = quantity;
	}

	public String getBatch() {
		return Batch;
	}

	public void setBatch(String batch) {
		Batch = batch;
	}

	public String getReservationNumber() {
		return ReservationNumber;
	}

	public void setReservationNumber(String reservationNumber) {
		ReservationNumber = reservationNumber;
	}

	public String getResrvationItem() {
		return ResrvationItem;
	}

	public void setResrvationItem(String resrvationItem) {
		ResrvationItem = resrvationItem;
	}
	
	
}
