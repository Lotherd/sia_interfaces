package trax.aero.pojo;

import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LSC 
{
	private String location;
	private String site;
	private Date workDate;
	private String employee;
	private double manHours;
	private int manHoursReserved;
	private int month;
	private int year;
	private int hour;
	private int minute;
	private int notes;
	private String createdBy;
	private Date createdDate;
	private String modifiedBy;
	private Date modifiedDate;
	private String breaks;
	private String group;
	private String dayControl;
	private Date startWorkDate;
	private String transferBy;
	private Date transferDate;
	private String tranType;
	private String breakDescription;
	private int manHoursPending;
	private String overtimeFlag;
	private String hazmatFlag;
	private int headCount;
	private int factor;
	private String addScheduleFlag;	
	private int totalHoursToWork;
	private int totalMinutesToWork;
	private String vacation;
	private String timeChangeFlag;
	
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
	public Date getWorkDate() {
		return workDate;
	}
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
	public String getEmployee() {
		return employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public double getManHours() {
		return manHours;
	}
	public void setManHours(double timeToInsert) {
		this.manHours = timeToInsert;
	}
	public int getManHoursReserved() {
		return manHoursReserved;
	}
	public void setManHoursReserved(int manHoursReserved) {
		this.manHoursReserved = manHoursReserved;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getNotes() {
		return notes;
	}
	public void setNotes(int notes) {
		this.notes = notes;
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
	public String getBreaks() {
		return breaks;
	}
	public void setBreaks(String breaks) {
		this.breaks = breaks;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getDayControl() {
		return dayControl;
	}
	public void setDayControl(String dayControl) {
		this.dayControl = dayControl;
	}
	public Date getStartWorkDate() {
		return startWorkDate;
	}
	public void setStartWorkDate(Date startWorkDate) {
		this.startWorkDate = startWorkDate;
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
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	public String getBreakDescription() {
		return breakDescription;
	}
	public void setBreakDescription(String breakDescription) {
		this.breakDescription = breakDescription;
	}
	public int getManHoursPending() {
		return manHoursPending;
	}
	public void setManHoursPending(int manHoursPending) {
		this.manHoursPending = manHoursPending;
	}
	public String getOvertimeFlag() {
		return overtimeFlag;
	}
	public void setOvertimeFlag(String overtimeFlag) {
		this.overtimeFlag = overtimeFlag;
	}
	public String getHazmatFlag() {
		return hazmatFlag;
	}
	public void setHazmatFlag(String hazmatFlag) {
		this.hazmatFlag = hazmatFlag;
	}
	public int getHeadCount() {
		return headCount;
	}
	public void setHeadCount(int headCount) {
		this.headCount = headCount;
	}
	public int getFactor() {
		return factor;
	}
	public void setFactor(int factor) {
		this.factor = factor;
	}
	public String getAddScheduleFlag() {
		return addScheduleFlag;
	}
	public void setAddScheduleFlag(String addScheduleFlag) {
		this.addScheduleFlag = addScheduleFlag;
	}
	public int getTotalHoursToWork() {
		return totalHoursToWork;
	}
	public void setTotalHoursToWork(int totalHoursToWork) {
		this.totalHoursToWork = totalHoursToWork;
	}
	public int getTotalMinutesToWork() {
		return totalMinutesToWork;
	}
	public void setTotalMinutesToWork(int totalMinutesToWork) {
		this.totalMinutesToWork = totalMinutesToWork;
	}
	public String getVacation() {
		return vacation;
	}
	public void setVacation(String vacation) {
		this.vacation = vacation;
	}
	public String getTimeChangeFlag() {
		return timeChangeFlag;
	}
	public void setTimeChangeFlag(String timeChangeFlag) {
		this.timeChangeFlag = timeChangeFlag;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("\nlocation: ").append(this.getLocation());
        sb.append("\nsite: ").append(this.getSite());
        sb.append("\nworkDate: ").append(this.getWorkDate());
        sb.append("\nemployee: ").append(this.getEmployee());
        sb.append("\nmanHours: ").append(this.getManHours());
        sb.append("\nmanHoursReserved: ").append(this.getManHoursReserved());        
        sb.append("\nmonth: ").append(this.getMonth());
        sb.append("\nyear: ").append(this.getYear());
        sb.append("\nhour: ").append(this.getHour());
        sb.append("\nminute: ").append(this.getMinute());
        sb.append("\nnotes: ").append(this.getNotes());
        sb.append("\ncreatedBy: ").append(this.getCreatedBy());
        sb.append("\ncreatedDate: ").append(this.getCreatedDate());
        sb.append("\nmodifiedBy: ").append(this.getModifiedBy());
        sb.append("\nmodifiedDate: ").append(this.getModifiedDate());
        sb.append("\nbreaks: ").append(this.getBreaks());
        sb.append("\ngroup: ").append(this.getGroup());
        sb.append("\ndayControl: ").append(this.getDayControl());
        sb.append("\nstartWorkDate: ").append(this.getStartWorkDate());
        sb.append("\ntransferBy: ").append(this.getTransferBy());
        sb.append("\ntransferDate: ").append(this.getTransferDate());
        sb.append("\ntranType: ").append(this.getTranType());
        sb.append("\nbreakDescription: ").append(this.getBreakDescription());
        sb.append("\nmanHoursPending: ").append(this.getManHoursPending());
        sb.append("\novertimeFlag: ").append(this.getOvertimeFlag());
        sb.append("\nhazmatFlag: ").append(this.getHazmatFlag());
        sb.append("\nheadCount: ").append(this.getHeadCount());
        sb.append("\nfactor: ").append(this.getFactor());
        sb.append("\naddScheduleFlag: ").append(this.getAddScheduleFlag());
        sb.append("\ntotalHoursToWork: ").append(this.getTotalHoursToWork());
        sb.append("\ntotalMinutesToWork: ").append(this.getTotalMinutesToWork());
        sb.append("\nvacation: ").append(this.getVacation());
        sb.append("\ntimeChangeFlag: ").append(this.getTimeChangeFlag());
        
        return sb.toString();	    
	}

}
