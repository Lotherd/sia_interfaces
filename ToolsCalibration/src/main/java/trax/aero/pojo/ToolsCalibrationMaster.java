package trax.aero.pojo;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement(name="MT_TRAX_I72_4062", namespace="http://singaporeair.com/mro/TRAX")
@XmlRootElement(name="ToolsCalibrationMaster")
@XmlAccessorType(XmlAccessType.FIELD)
public class ToolsCalibrationMaster {
	

		@XmlElement(name = "PartNumber")
	    private String PartNumber;
		
		@XmlElement(name = "SerialNumber")
		private String SerialNumber;
		
		@XmlElement(name = "InventoryNumber")
		private String InventoryNumber;
		
		@XmlElement(name = "BasicStartDate")
		private String BasicStartDate;
		
		@XmlElement(name = "NotificationDate")
		private  String NotificationDate;
		
		@XmlElement(name = "NotificationNumber")
		private  String NotificationNumber;

		public String getPartNumber() {
			return PartNumber;
		}
		public void setPartNumber(String partNumber) {
			 this.PartNumber = partNumber;
		}

		public String getSerialNumber() {
			return SerialNumber;
		}
		public void setSerialNumber(String serialNumber) {
			 this.SerialNumber = serialNumber;
		}

		public String getInventoryNumber() {
			return InventoryNumber;
		}
		public void setInventoryNumber(String inventoryNumber) {
			 this.InventoryNumber = inventoryNumber;
		}

		public String getBasicStartDate() {
			return BasicStartDate;
		}
		public void setBasicStartDate(String basicStartDate) {
			 this.BasicStartDate = basicStartDate;
		}

		public String getNotificationDate() {
			return NotificationDate;
		}
		public void setNotificationDate(String notificationDate) {
			 this.NotificationDate = notificationDate;
		}
		
		public String getNotificationNumber() {
			return NotificationNumber;
		}
		public void setNotificationNumber(String notificationNumber) {
			 this.NotificationNumber = notificationNumber;
		}
}
