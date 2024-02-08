package trax.aero.model;

import java.sql.Date;

public class EmployeeAttendanceCurrent 
{
	private String employee;
	private String createdBy;
	private Date createdDate;
	private String modifiedBy;
	private Date modifiedDate;
	private int notes;
	private Date startTime;
	private String site;
	private String location;
	private String typeOff;
	private Date startTimeBreak;
	private Date startTimeLunch;
	
	public EmployeeAttendanceCurrent() {}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public int getNotes() {
		return notes;
	}

	public void setNotes(int notes) {
		this.notes = notes;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTypeOff() {
		return typeOff;
	}

	public void setTypeOff(String typeOff) {
		this.typeOff = typeOff;
	}

	public Date getStartTimeBreak() {
		return startTimeBreak;
	}

	public void setStartTimeBreak(Date startTimeBreak) {
		this.startTimeBreak = startTimeBreak;
	}

	public Date getStartTimeLunch() {
		return startTimeLunch;
	}

	public void setStartTimeLunch(Date startTimeLunch) {
		this.startTimeLunch = startTimeLunch;
	}
	
	
}
