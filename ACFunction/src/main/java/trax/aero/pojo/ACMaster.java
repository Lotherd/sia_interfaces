package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_I72_4062", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class ACMaster {

	@XmlElement(name = "AircraftTailNumber")
    private String AircraftTailNumber;
	
	@XmlElement(name = "AircraftType")
	private String AircraftType;
	
	@XmlElement(name = "MaintenancePlant")
	private String MaintenancePlant;
	
	@XmlElement(name = "Bill_to_party")
	private String Bill_to_party;
	
	@XmlElement(name = "DeletionIndicator")
	private  String DeletionIndicator;
	
	@XmlElement(name = "InactiveIndicator")
	private  String InactiveIndicator;

	public String getAircraftTailNumber() {
		return AircraftTailNumber;
	}
	public void setAircraftTailNumber(String aircraftTailNumber) {
		 this.AircraftTailNumber = aircraftTailNumber;
	}

	public String getAircraftType() {
		return AircraftType;
	}
	public void setAircraftType(String aircraftType) {
		 this.AircraftType = aircraftType;
	}

	public String getMaintenancePlant() {
		return MaintenancePlant;
	}
	public void setMaintenancePlant(String maintenancePlant) {
		 this.MaintenancePlant = maintenancePlant;
	}

	public String getBill_to_party() {
		return Bill_to_party;
	}
	public void setBill_to_party(String bill_to_party) {
		 this.Bill_to_party = bill_to_party;
	}

	public String getDeletionIndicator() {
		return DeletionIndicator;
	}
	public void setDeletionIndicator(String deletionIndicator) {
		 this.DeletionIndicator = deletionIndicator;
	}
	
	public String getInactiveIndicator() {
		return InactiveIndicator;
	}
	public void setInactiveIndicator(String inactiveIndicator) {
		 this.InactiveIndicator = inactiveIndicator;
	}
}
