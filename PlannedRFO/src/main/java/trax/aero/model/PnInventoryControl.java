package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the PN_INVENTORY_CONTROL database table.
 * 
 */
@Entity
@Table(name="PN_INVENTORY_CONTROL")
@NamedQuery(name="PnInventoryControl.findAll", query="SELECT p FROM PnInventoryControl p")
public class PnInventoryControl implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PnInventoryControlPK id;

	@Column(name="ACTUAL_CYCLES")
	private BigDecimal actualCycles;

	@Column(name="ACTUAL_DAYS")
	private BigDecimal actualDays;

	@Column(name="ACTUAL_HOURS")
	private BigDecimal actualHours;

	@Column(name="ACTUAL_MINUTES")
	private BigDecimal actualMinutes;

	@Column(name="ACTUAL_REMOVALS")
	private BigDecimal actualRemovals;

	@Column(name="ACTUAL_RIN")
	private BigDecimal actualRin;

	@Column(name="\"AUTHORIZATION\"")
	private String authorization;

	@Column(name="CALENDAR_CONTROL")
	private String calendarControl;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="DATE_CONTROL")
	private String dateControl;

	@Column(name="DISPLAY_CYCLES")
	private String displayCycles;

	@Column(name="DISPLAY_DAYS")
	private String displayDays;

	@Column(name="DISPLAY_HOURS")
	private String displayHours;

	@Column(name="HOUR_CALENDAR_CONTROL")
	private String hourCalendarControl;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	private String override;

	@Column(name="PLAN_AC_BRAKE_CYCLES")
	private BigDecimal planAcBrakeCycles;

	@Column(name="PLAN_AC_CYCLES")
	private BigDecimal planAcCycles;

	@Column(name="PLAN_AC_HOURS")
	private BigDecimal planAcHours;

	@Column(name="PLAN_AC_MINUTES")
	private BigDecimal planAcMinutes;

	@Column(name="PLAN_DATE")
	private Date planDate;

	@Column(name="PLAN_HOUR")
	private BigDecimal planHour;

	@Column(name="PLAN_MINUTE")
	private BigDecimal planMinute;

	@Column(name="RESET_DATE")
	private Date resetDate;

	@Column(name="SCHEDULE_CYCLES")
	private BigDecimal scheduleCycles;

	@Column(name="SCHEDULE_DATE")
	private Date scheduleDate;

	@Column(name="SCHEDULE_DAYS")
	private BigDecimal scheduleDays;

	@Column(name="SCHEDULE_HOURS")
	private BigDecimal scheduleHours;

	@Column(name="SCHEDULE_REMOVALS")
	private BigDecimal scheduleRemovals;

	@Column(name="SCHEDULE_RIN")
	private BigDecimal scheduleRin;

	@Column(name="SHELF_LIMITED_CONTROL")
	private String shelfLimitedControl;

	public PnInventoryControl() {
	}

	public PnInventoryControlPK getId() {
		return this.id;
	}

	public void setId(PnInventoryControlPK id) {
		this.id = id;
	}

	public BigDecimal getActualCycles() {
		return this.actualCycles;
	}

	public void setActualCycles(BigDecimal actualCycles) {
		this.actualCycles = actualCycles;
	}

	public BigDecimal getActualDays() {
		return this.actualDays;
	}

	public void setActualDays(BigDecimal actualDays) {
		this.actualDays = actualDays;
	}

	public BigDecimal getActualHours() {
		return this.actualHours;
	}

	public void setActualHours(BigDecimal actualHours) {
		this.actualHours = actualHours;
	}

	public BigDecimal getActualMinutes() {
		return this.actualMinutes;
	}

	public void setActualMinutes(BigDecimal actualMinutes) {
		this.actualMinutes = actualMinutes;
	}

	public BigDecimal getActualRemovals() {
		return this.actualRemovals;
	}

	public void setActualRemovals(BigDecimal actualRemovals) {
		this.actualRemovals = actualRemovals;
	}

	public BigDecimal getActualRin() {
		return this.actualRin;
	}

	public void setActualRin(BigDecimal actualRin) {
		this.actualRin = actualRin;
	}

	public String getAuthorization() {
		return this.authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getCalendarControl() {
		return this.calendarControl;
	}

	public void setCalendarControl(String calendarControl) {
		this.calendarControl = calendarControl;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDateControl() {
		return this.dateControl;
	}

	public void setDateControl(String dateControl) {
		this.dateControl = dateControl;
	}

	public String getDisplayCycles() {
		return this.displayCycles;
	}

	public void setDisplayCycles(String displayCycles) {
		this.displayCycles = displayCycles;
	}

	public String getDisplayDays() {
		return this.displayDays;
	}

	public void setDisplayDays(String displayDays) {
		this.displayDays = displayDays;
	}

	public String getDisplayHours() {
		return this.displayHours;
	}

	public void setDisplayHours(String displayHours) {
		this.displayHours = displayHours;
	}

	public String getHourCalendarControl() {
		return this.hourCalendarControl;
	}

	public void setHourCalendarControl(String hourCalendarControl) {
		this.hourCalendarControl = hourCalendarControl;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public BigDecimal getNotes() {
		return this.notes;
	}

	public void setNotes(BigDecimal notes) {
		this.notes = notes;
	}

	public String getOverride() {
		return this.override;
	}

	public void setOverride(String override) {
		this.override = override;
	}

	public BigDecimal getPlanAcBrakeCycles() {
		return this.planAcBrakeCycles;
	}

	public void setPlanAcBrakeCycles(BigDecimal planAcBrakeCycles) {
		this.planAcBrakeCycles = planAcBrakeCycles;
	}

	public BigDecimal getPlanAcCycles() {
		return this.planAcCycles;
	}

	public void setPlanAcCycles(BigDecimal planAcCycles) {
		this.planAcCycles = planAcCycles;
	}

	public BigDecimal getPlanAcHours() {
		return this.planAcHours;
	}

	public void setPlanAcHours(BigDecimal planAcHours) {
		this.planAcHours = planAcHours;
	}

	public BigDecimal getPlanAcMinutes() {
		return this.planAcMinutes;
	}

	public void setPlanAcMinutes(BigDecimal planAcMinutes) {
		this.planAcMinutes = planAcMinutes;
	}

	public Date getPlanDate() {
		return this.planDate;
	}

	public void setPlanDate(Date planDate) {
		this.planDate = planDate;
	}

	public BigDecimal getPlanHour() {
		return this.planHour;
	}

	public void setPlanHour(BigDecimal planHour) {
		this.planHour = planHour;
	}

	public BigDecimal getPlanMinute() {
		return this.planMinute;
	}

	public void setPlanMinute(BigDecimal planMinute) {
		this.planMinute = planMinute;
	}

	public Date getResetDate() {
		return this.resetDate;
	}

	public void setResetDate(Date resetDate) {
		this.resetDate = resetDate;
	}

	public BigDecimal getScheduleCycles() {
		return this.scheduleCycles;
	}

	public void setScheduleCycles(BigDecimal scheduleCycles) {
		this.scheduleCycles = scheduleCycles;
	}

	public Date getScheduleDate() {
		return this.scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public BigDecimal getScheduleDays() {
		return this.scheduleDays;
	}

	public void setScheduleDays(BigDecimal scheduleDays) {
		this.scheduleDays = scheduleDays;
	}

	public BigDecimal getScheduleHours() {
		return this.scheduleHours;
	}

	public void setScheduleHours(BigDecimal scheduleHours) {
		this.scheduleHours = scheduleHours;
	}

	public BigDecimal getScheduleRemovals() {
		return this.scheduleRemovals;
	}

	public void setScheduleRemovals(BigDecimal scheduleRemovals) {
		this.scheduleRemovals = scheduleRemovals;
	}

	public BigDecimal getScheduleRin() {
		return this.scheduleRin;
	}

	public void setScheduleRin(BigDecimal scheduleRin) {
		this.scheduleRin = scheduleRin;
	}

	public String getShelfLimitedControl() {
		return this.shelfLimitedControl;
	}

	public void setShelfLimitedControl(String shelfLimitedControl) {
		this.shelfLimitedControl = shelfLimitedControl;
	}

}