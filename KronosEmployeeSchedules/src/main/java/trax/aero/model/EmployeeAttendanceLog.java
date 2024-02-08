package trax.aero.model;

import java.sql.Date;

public class EmployeeAttendanceLog 
{
	private String employee;
	private Date startTime;
	private String createdBy;
	private Date createdDate;
	private String modifiedBy;
	private Date modifiedDate;
	private int notes;
	private Date endTime;
	private String location;
	private String site;
	private Date startDate;
	private Date endDate;
	private String transferBy;
	private Date transferDate;
	private int transactionNo;
	private int blobNo;
	private int documentNo;
	private String transactionType;
	private int totalHours;
	private int totalMinutes;
	private int wo;
	private String taskCard;
	private String pn;
	private String pnSn;
	private String ac;
	private String group;
	private String reason;
	private String authorization;
	private Date authorizationDate;
	private String authorizationBy;
	private Date releaseDate;
	private String releaseBy;
	private String typeOff;
	private String dayPattern;
	private String selfAssigment;
	private String requestType;
	private String payCode;
	private String payValidateBy;
	private Date payValidateDate;
	
	public EmployeeAttendanceLog() { }
	
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getTransferBy() {
		return transferBy;
	}
	public void setTransferBy(String transferBy) {
		this.transferBy = transferBy;
	}
	public Date getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}
	public int getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(int transactionNo) {
		this.transactionNo = transactionNo;
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
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public int getTotalHours() {
		return totalHours;
	}
	public void setTotalHours(int totalHours) {
		this.totalHours = totalHours;
	}
	public int getTotalMinutes() {
		return totalMinutes;
	}
	public void setTotalMinutes(int totalMinutes) {
		this.totalMinutes = totalMinutes;
	}
	public int getWo() {
		return wo;
	}
	public void setWo(int wo) {
		this.wo = wo;
	}
	public String getTaskCard() {
		return taskCard;
	}
	public void setTaskCard(String taskCard) {
		this.taskCard = taskCard;
	}
	public String getPn() {
		return pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}
	public String getPnSn() {
		return pnSn;
	}
	public void setPnSn(String pnSn) {
		this.pnSn = pnSn;
	}
	public String getAc() {
		return ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public Date getAuthorizationDate() {
		return authorizationDate;
	}
	public void setAuthorizationDate(Date authorizationDate) {
		this.authorizationDate = authorizationDate;
	}
	public String getAuthorizationBy() {
		return authorizationBy;
	}
	public void setAuthorizationBy(String authorizationBy) {
		this.authorizationBy = authorizationBy;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getReleaseBy() {
		return releaseBy;
	}
	public void setReleaseBy(String releaseBy) {
		this.releaseBy = releaseBy;
	}
	public String getTypeOff() {
		return typeOff;
	}
	public void setTypeOff(String typeOff) {
		this.typeOff = typeOff;
	}
	public String getDayPattern() {
		return dayPattern;
	}
	public void setDayPattern(String dayPattern) {
		this.dayPattern = dayPattern;
	}
	public String getSelfAssigment() {
		return selfAssigment;
	}
	public void setSelfAssigment(String selfAssigment) {
		this.selfAssigment = selfAssigment;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getPayValidateBy() {
		return payValidateBy;
	}
	public void setPayValidateBy(String payValidateBy) {
		this.payValidateBy = payValidateBy;
	}
	public Date getPayValidateDate() {
		return payValidateDate;
	}
	public void setPayValidateDate(Date payValidateDate) {
		this.payValidateDate = payValidateDate;
	}
	
	
	
}
