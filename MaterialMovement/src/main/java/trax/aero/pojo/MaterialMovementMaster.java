package trax.aero.pojo;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_RCV_I42_4082", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialMovementMaster {
	

	@XmlElement(name = "OrderNumber")
	private String OrderNumber;
	    
	@XmlElement(name = "ReservationNumber")
	private String ReservationNumber;
		
	@XmlElement(name = "ReservationItem")
	private String ReservationItem;
	
	@XmlElement(name = "RequisitionNumber")
	private String RequisitionNumber;
	
	@XmlElement(name = "RequisitionItem")
	private String RequisitionItem;
	
	@XmlElement(name = "Material")
	private String Material;
	    
	@XmlElement(name = "Plant")
	private String Plant;
		
	@XmlElement(name = "Quantity")
	private String Quantity;
	
	@XmlElement(name = "UnitofMeasure")
	private String UnitofMeasure;
	
	@XmlElement(name = "Batch")
	private String Batch;
	
	@XmlElement(name = "StorageLocation")
	private String StorageLocation;
			
	@XmlElement(name = "SerialNumber")
	private String SerialNumber;
	
	@XmlElement(name = "MovementType")
	private String MovementType;
	
	@XmlElement(name = "MaterialDocumentNo")
	private String MaterialDocumentNo;
		
	@XmlElement(name = "AttachedDocumentIDOC")
	private byte[] AttachedDocumentIDOC;
	
	@XmlElement(name = "AttachmentLinkSharepointlink")
	private byte[] AttachmentLinkSharepointlink;

	public String getOrderNumber() {
		return OrderNumber;
	}

	public void setOrderNumber(String ordernumber) {
		OrderNumber = ordernumber;
	}

	public String getReservationNumber() {
		return ReservationNumber;
	}

	public void setReservationNumber(String reservationNumber) {
		ReservationNumber = reservationNumber;
	}

	public String getReservationItem() {
		return ReservationItem;
	}

	public void setReservationItem(String reservationItem) {
		ReservationItem = reservationItem;
	}

	public String getRequisitionNumber() {
		return RequisitionNumber;
	}

	public void setRequisitionNumber(String requisitionNumber) {
		RequisitionNumber = requisitionNumber;
	}

	public String getRequisitionItem() {
		return RequisitionItem;
	}

	public void setRequisitionItem(String requisitionItem) {
		RequisitionItem = requisitionItem;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
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

	public String getUnitofMeasure() {
		return UnitofMeasure;
	}

	public void setUnitofMeasure(String unitofMeasure) {
		UnitofMeasure = unitofMeasure;
	}

	public String getBatch() {
		return Batch;
	}

	public void setBatch(String batch) {
		Batch = batch;
	}

	
	public String getSerialNumber() {
		return SerialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	

	public String getMovementType() {
		return MovementType;
	}

	public void setMovementType(String movementType) {
		MovementType = movementType;
	}

	public byte[] getAttachmentLinkSharepointlink() {
		return AttachmentLinkSharepointlink;
	}

	public void setAttachmentLinkSharepointlink(byte[] attachmentLinkSharepointlink) {
		AttachmentLinkSharepointlink = attachmentLinkSharepointlink;
	}

	public String getStorageLocation() {
		return StorageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		StorageLocation = storageLocation;
	}

	public String getMaterialDocumentNo() {
		return MaterialDocumentNo;
	}

	public void setMaterialDocumentNo(String materialDocumentNo) {
		MaterialDocumentNo = materialDocumentNo;
	}

	public byte[] getAttachedDocumentIDOC() {
		return AttachedDocumentIDOC;
	}

	public void setAttachedDocumentIDOC(byte[] attachedDocumentIDOC) {
		AttachedDocumentIDOC = attachedDocumentIDOC;
	}

}
