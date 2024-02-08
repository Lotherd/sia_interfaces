package trax.aero.model;

import java.sql.Date;



public class EmployeeScheduleGroup 
{
	private String group;
	
	private String location;
	
	private String site;
	
	private String createdBy;
	
	private Date createdDate;
	
	private String modifiedBy;
	private Date modifiedDate;
	private String mainGroup;	
	private int notes;
	private int blobNo;
	private int documentNo;	
	private String schedule;
	private String description;
	private String selfAssigment;
	private String shiftPattern;
	
	public EmployeeScheduleGroup() {}

	public EmployeeScheduleGroup(String location, String site) 
	{
		this.location = location;
		this.site = site;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
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

	public String getMainGroup() {
		return mainGroup;
	}

	public void setMainGroup(String mainGroup) {
		this.mainGroup = mainGroup;
	}

	public int getNotes() {
		return notes;
	}

	public void setNotes(int notes) {
		this.notes = notes;
	}

	public int getBlobNo() {
		return blobNo;
	}

	public void setBlobNo(int blobNo) {
		this.blobNo = blobNo;
	}

	public int getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(int documentNo) {
		this.documentNo = documentNo;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSelfAssigment() {
		return selfAssigment;
	}

	public void setSelfAssigment(String selfAssigment) {
		this.selfAssigment = selfAssigment;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getShiftPattern() {
		return shiftPattern;
	}

	public void setShiftPattern(String shiftPattern) {
		this.shiftPattern = shiftPattern;
	}
}
