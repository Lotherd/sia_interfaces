package trax.aero.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


public class STAFFRequest {
	
	@JsonProperty("STAFF_AUTH_NUMBER")
	private String staffAuthNumber;

	@JsonProperty("STAFF_STATUS")
    private String staffStatus;

	@JsonProperty("STAFF_NUMBER")
    private String staffNumber;

	@JsonProperty("STAFF_AUTH_EXPIRY")
    private String staffAuthExpiry;

	@JsonProperty("SIGN")
    private byte[] sign;

	@JsonProperty("STAMP")
    private byte[] stamp;

	public String getStaffAuthNumber() {
		return staffAuthNumber;
	}

	public void setStaffAuthNumber(String staffAuthNumber) {
		this.staffAuthNumber = staffAuthNumber;
	}

	public String getStaffStatus() {
		return staffStatus;
	}

	public void setStaffStatus(String staffStatus) {
		this.staffStatus = staffStatus;
	}

	public String getStaffNumber() {
		return staffNumber;
	}

	public void setStaffNumber(String staffNumber) {
		this.staffNumber = staffNumber;
	}

	public byte[] getSign() {
		return sign;
	}

	public void setSign(byte[] sign) {
		this.sign = sign;
	}

	public byte[] getStamp() {
		return stamp;
	}

	public void setStamp(byte[] stamp) {
		this.stamp = stamp;
	}

	public String getStaffAuthExpiry() {
		return staffAuthExpiry;
	}

	public void setStaffAuthExpiry(String staffAuthExpiry) {
		this.staffAuthExpiry = staffAuthExpiry;
	}

	
	
	     
    
    
}
