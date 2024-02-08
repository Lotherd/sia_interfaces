package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the ENGINEERING_SCHEDULE_CTL_RV database table.
 * 
 */
@Entity
@Table(name="ENGINEERING_SCHEDULE_CTL_RV")
@NamedQuery(name="EngineeringScheduleCtlRv.findAll", query="SELECT e FROM EngineeringScheduleCtlRv e")
public class EngineeringScheduleCtlRv implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EngineeringScheduleCtlRvPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="EO_SCHED_REV")
	private String eoSchedRev;

	@Column(name="LOWER_LEVEL")
	private String lowerLevel;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	@Column(name="OVERRIDE_LOWER_LEVEL")
	private String overrideLowerLevel;

	@Column(name="SCHEDULE_CONTROL")
	private String scheduleControl;

	@Column(name="SCHEDULE_FREQUENCY")
	private BigDecimal scheduleFrequency;

	@Column(name="START_AT_FREQUENCY")
	private BigDecimal startAtFrequency;

	public EngineeringScheduleCtlRv() {
	}

	public EngineeringScheduleCtlRvPK getId() {
		return this.id;
	}

	public void setId(EngineeringScheduleCtlRvPK id) {
		this.id = id;
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

	public String getEoSchedRev() {
		return this.eoSchedRev;
	}

	public void setEoSchedRev(String eoSchedRev) {
		this.eoSchedRev = eoSchedRev;
	}

	public String getLowerLevel() {
		return this.lowerLevel;
	}

	public void setLowerLevel(String lowerLevel) {
		this.lowerLevel = lowerLevel;
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

	public String getOverrideLowerLevel() {
		return this.overrideLowerLevel;
	}

	public void setOverrideLowerLevel(String overrideLowerLevel) {
		this.overrideLowerLevel = overrideLowerLevel;
	}

	public String getScheduleControl() {
		return this.scheduleControl;
	}

	public void setScheduleControl(String scheduleControl) {
		this.scheduleControl = scheduleControl;
	}

	public BigDecimal getScheduleFrequency() {
		return this.scheduleFrequency;
	}

	public void setScheduleFrequency(BigDecimal scheduleFrequency) {
		this.scheduleFrequency = scheduleFrequency;
	}

	public BigDecimal getStartAtFrequency() {
		return this.startAtFrequency;
	}

	public void setStartAtFrequency(BigDecimal startAtFrequency) {
		this.startAtFrequency = startAtFrequency;
	}

}