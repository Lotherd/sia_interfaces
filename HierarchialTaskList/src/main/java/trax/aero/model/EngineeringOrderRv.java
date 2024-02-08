package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the ENGINEERING_ORDER_RV database table.
 * 
 */
@Entity
@Table(name="ENGINEERING_ORDER_RV")
@NamedQuery(name="EngineeringOrderRv.findAll", query="SELECT e FROM EngineeringOrderRv e")
public class EngineeringOrderRv implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EngineeringOrderRvPK id;

	@Column(name="ACTIVATING_SCH_CYCLES")
	private BigDecimal activatingSchCycles;

	@Column(name="ACTIVATING_SCH_DAYS")
	private BigDecimal activatingSchDays;

	@Column(name="ACTIVATING_SCH_EARLIEST")
	private String activatingSchEarliest;

	@Column(name="ACTIVATING_SCH_FLAG")
	private String activatingSchFlag;

	@Column(name="ACTIVATING_SCH_HOURS")
	private BigDecimal activatingSchHours;

	private String amendment;

	private String applicable;

	@Column(name="ASSIGNED_TO")
	private String assignedTo;

	@Column(name="ASSOCIATED_TEMPLATE")
	private String associatedTemplate;

	@Column(name="\"AUTHORIZATION\"")
	private String authorization;

	@Column(name="AUTHORIZED_BY")
	private String authorizedBy;

	@Column(name="AUTHORIZED_DATE")
	private Date authorizedDate;

	@Column(name="AUTO_RESCHEDULE")
	private String autoReschedule;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	@Column(name="CALENDAR_CONTROL")
	private String calendarControl;

	@Column(name="CAPABILITY_AREA")
	private String capabilityArea;

	private BigDecimal chapter;

	@Column(name="CONFIG_AFFECTED_NOTIF")
	private String configAffectedNotif;

	@Column(name="CONTROL_AREA")
	private String controlArea;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	private String customer;

	private String daily;

	@Column(name="DO_NOT_INCLUDE_CHILD_MAN_HRS")
	private String doNotIncludeChildManHrs;

	@Column(name="DOCUMENT_NO")
	private BigDecimal documentNo;

	@Column(name="EC_AUTO_FLEET")
	private String ecAutoFleet;

	@Column(name="EC_CHECK_TYPE")
	private String ecCheckType;

	@Column(name="EC_LEGACY")
	private String ecLegacy;

	@Column(name="EC_TERMINATE_NOTIFICATION")
	private String ecTerminateNotification;

	@Column(name="EC_TYPE")
	private String ecType;

	@Column(name="EFF_REV_OVERRIDE")
	private String effRevOverride;

	@Column(name="EFFECTIVE_DATE")
	private Date effectiveDate;

	@Column(name="EFFECTIVITY_NO")
	private String effectivityNo;

	@Column(name="ELECTRICAL_LOAD_CHANGE")
	private String electricalLoadChange;

	@Column(name="EO_AC_TYPE")
	private String eoAcType;

	@Column(name="EO_CALL_ON")
	private String eoCallOn;

	@Column(name="EO_CALL_ON_10")
	private String eoCallOn10;

	@Column(name="EO_CALL_ON_11")
	private String eoCallOn11;

	@Column(name="EO_CALL_ON_12")
	private String eoCallOn12;

	@Column(name="EO_CALL_ON_13")
	private String eoCallOn13;

	@Column(name="EO_CALL_ON_14")
	private String eoCallOn14;

	@Column(name="EO_CALL_ON_15")
	private String eoCallOn15;

	@Column(name="EO_CALL_ON_16")
	private String eoCallOn16;

	@Column(name="EO_CALL_ON_2")
	private String eoCallOn2;

	@Column(name="EO_CALL_ON_3")
	private String eoCallOn3;

	@Column(name="EO_CALL_ON_4")
	private String eoCallOn4;

	@Column(name="EO_CALL_ON_5")
	private String eoCallOn5;

	@Column(name="EO_CALL_ON_6")
	private String eoCallOn6;

	@Column(name="EO_CALL_ON_7")
	private String eoCallOn7;

	@Column(name="EO_CALL_ON_8")
	private String eoCallOn8;

	@Column(name="EO_CALL_ON_9")
	private String eoCallOn9;

	@Column(name="EO_CANCEL")
	private String eoCancel;

	@Column(name="EO_CATEGORY")
	private String eoCategory;

	@Column(name="EO_COMPANY")
	private String eoCompany;

	@Column(name="EO_DESCRIPTION")
	private String eoDescription;

	@Lob
	@Column(name="EO_DESCRIPTION_ADDITIONAL")
	private String eoDescriptionAdditional;

	@Column(name="EO_NEW")
	private String eoNew;

	@Column(name="EO_SUB")
	private String eoSub;

	@Column(name="EO_SUPPERSEDE")
	private String eoSuppersede;

	@Column(name="EO_SUPPERSEDED_DATE")
	private Date eoSuppersededDate;

	@Column(name="EO_TYPE")
	private String eoType;

	private String etops;

	private String expenditure;

	private BigDecimal fc;

	@Column(name="FILE_NAME_PATH")
	private String fileNamePath;

	@Column(name="FLT_OPS_AFFECTED_NOTIF")
	private String fltOpsAffectedNotif;

	@Column(name="FORM_NO")
	private BigDecimal formNo;

	private BigDecimal fr;

	@Column(name="GROUND_TIME_REQUIRE")
	private BigDecimal groundTimeRequire;

	@Column(name="HOUR_CALENDAR_CONTROL")
	private String hourCalendarControl;

	@Column(name="INSPECTION_PROGRAM_REFERENCE")
	private String inspectionProgramReference;

	@Column(name="INTERNAL_CAPABILITY")
	private String internalCapability;

	@Column(name="ISSUED_BY")
	private String issuedBy;

	@Column(name="ISSUED_DATE")
	private Date issuedDate;

	@Column(name="LIMIT_DATE")
	private Date limitDate;

	@Column(name="LOWER_LEVEL")
	private String lowerLevel;

	@Column(name="LOWER_LEVEL_10")
	private String lowerLevel10;

	@Column(name="LOWER_LEVEL_11")
	private String lowerLevel11;

	@Column(name="LOWER_LEVEL_12")
	private String lowerLevel12;

	@Column(name="LOWER_LEVEL_13")
	private String lowerLevel13;

	@Column(name="LOWER_LEVEL_14")
	private String lowerLevel14;

	@Column(name="LOWER_LEVEL_15")
	private String lowerLevel15;

	@Column(name="LOWER_LEVEL_16")
	private String lowerLevel16;

	@Column(name="LOWER_LEVEL_2")
	private String lowerLevel2;

	@Column(name="LOWER_LEVEL_3")
	private String lowerLevel3;

	@Column(name="LOWER_LEVEL_4")
	private String lowerLevel4;

	@Column(name="LOWER_LEVEL_5")
	private String lowerLevel5;

	@Column(name="LOWER_LEVEL_6")
	private String lowerLevel6;

	@Column(name="LOWER_LEVEL_7")
	private String lowerLevel7;

	@Column(name="LOWER_LEVEL_8")
	private String lowerLevel8;

	@Column(name="LOWER_LEVEL_9")
	private String lowerLevel9;

	@Column(name="MAINTENANCE_PROGRAM")
	private String maintenanceProgram;

	private String manditory;

	@Column(name="MAX_YIELD_PERCENT")
	private BigDecimal maxYieldPercent;

	@Column(name="MOD_NO")
	private String modNo;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name="NEW_EO")
	private String newEo;

	@Column(name="NEXT_CONTROLLER_EC")
	private String nextControllerEc;

	@Column(name="NEXT_EO")
	private String nextEo;

	@Column(name="NEXT_GROUND_OP")
	private String nextGroundOp;

	@Column(name="NOT_DO_ALLOW_CONCESSION")
	private String notDoAllowConcession;

	private BigDecimal notes;

	@Column(name="OVERRIDE_LOWER_LEVEL")
	private String overrideLowerLevel;

	@Column(name="OVERRIDE_LOWER_LEVEL_10")
	private String overrideLowerLevel10;

	@Column(name="OVERRIDE_LOWER_LEVEL_11")
	private String overrideLowerLevel11;

	@Column(name="OVERRIDE_LOWER_LEVEL_12")
	private String overrideLowerLevel12;

	@Column(name="OVERRIDE_LOWER_LEVEL_13")
	private String overrideLowerLevel13;

	@Column(name="OVERRIDE_LOWER_LEVEL_14")
	private String overrideLowerLevel14;

	@Column(name="OVERRIDE_LOWER_LEVEL_15")
	private String overrideLowerLevel15;

	@Column(name="OVERRIDE_LOWER_LEVEL_16")
	private String overrideLowerLevel16;

	@Column(name="OVERRIDE_LOWER_LEVEL_2")
	private String overrideLowerLevel2;

	@Column(name="OVERRIDE_LOWER_LEVEL_3")
	private String overrideLowerLevel3;

	@Column(name="OVERRIDE_LOWER_LEVEL_4")
	private String overrideLowerLevel4;

	@Column(name="OVERRIDE_LOWER_LEVEL_5")
	private String overrideLowerLevel5;

	@Column(name="OVERRIDE_LOWER_LEVEL_6")
	private String overrideLowerLevel6;

	@Column(name="OVERRIDE_LOWER_LEVEL_7")
	private String overrideLowerLevel7;

	@Column(name="OVERRIDE_LOWER_LEVEL_8")
	private String overrideLowerLevel8;

	@Column(name="OVERRIDE_LOWER_LEVEL_9")
	private String overrideLowerLevel9;

	@Column(name="OVERRIDE_SCHEDULE")
	private String overrideSchedule;

	private BigDecimal paragraph;

	@Column(name="PLAN_EO")
	private String planEo;

	@Column(name="PLAN_LEAD_TIME")
	private BigDecimal planLeadTime;

	@Column(name="PLANNING_DEPT")
	private String planningDept;

	@Column(name="PLANNING_SUB_DEPT")
	private String planningSubDept;

	@Column(name="PRESSURE_CYCLES")
	private String pressureCycles;

	@Column(name="REDO_EC")
	private String redoEc;

	@Column(name="RELEASE_FOR_AUTHORIZATION")
	private String releaseForAuthorization;

	@Column(name="RELEASE_FOR_AUTHORIZATION_ON")
	private Date releaseForAuthorizationOn;

	@Column(name="REPAIR_CODE")
	private String repairCode;

	@Column(name="REPEAT_NUMBER")
	private BigDecimal repeatNumber;

	@Column(name="REQUIREMENT_TYPE")
	private String requirementType;

	@Column(name="REVISED_BY")
	private String revisedBy;

	@Column(name="REVISED_DATE")
	private Date revisedDate;

	@Column(name="REVISION_ISSUED_BY")
	private String revisionIssuedBy;

	@Column(name="\"SCHEDULE\"")
	private String schedule;

	@Column(name="SCHEDULE_AC")
	private String scheduleAc;

	@Column(name="SCHEDULE_AC_CYCLES")
	private BigDecimal scheduleAcCycles;

	@Column(name="SCHEDULE_AC_DAYS")
	private BigDecimal scheduleAcDays;

	@Column(name="SCHEDULE_AC_HOURS")
	private BigDecimal scheduleAcHours;

	@Column(name="SCHEDULE_COUNTER")
	private BigDecimal scheduleCounter;

	@Column(name="SCHEDULE_COUNTER_REPEAT")
	private BigDecimal scheduleCounterRepeat;

	@Column(name="SCHEDULE_COUNTER_TYPE")
	private String scheduleCounterType;

	@Column(name="SCHEDULE_COUNTER_TYPE_REPEAT")
	private String scheduleCounterTypeRepeat;

	@Column(name="SCHEDULE_CYCLES")
	private BigDecimal scheduleCycles;

	@Column(name="SCHEDULE_CYCLES_REPEAT")
	private BigDecimal scheduleCyclesRepeat;

	@Column(name="SCHEDULE_DATE")
	private Date scheduleDate;

	@Column(name="SCHEDULE_DATE_REPEAT")
	private Date scheduleDateRepeat;

	@Column(name="SCHEDULE_DAYS")
	private BigDecimal scheduleDays;

	@Column(name="SCHEDULE_DAYS_EOM")
	private String scheduleDaysEom;

	@Column(name="SCHEDULE_DAYS_EOM_REPEAT")
	private String scheduleDaysEomRepeat;

	@Column(name="SCHEDULE_DAYS_REPEAT")
	private BigDecimal scheduleDaysRepeat;

	@Column(name="SCHEDULE_HOURS")
	private BigDecimal scheduleHours;

	@Column(name="SCHEDULE_HOURS_REPEAT")
	private BigDecimal scheduleHoursRepeat;

	@Column(name="SCHEDULE_LIMIT_CYCLES")
	private BigDecimal scheduleLimitCycles;

	@Column(name="SCHEDULE_LIMIT_DAYS")
	private BigDecimal scheduleLimitDays;

	@Column(name="SCHEDULE_LIMIT_HOURS")
	private BigDecimal scheduleLimitHours;

	@Column(name="SCHEDULE_LIMIT_RIN")
	private BigDecimal scheduleLimitRin;

	@Column(name="SCHEDULE_REPEAT")
	private String scheduleRepeat;

	@Column(name="SCHEDULE_RIN")
	private BigDecimal scheduleRin;

	@Column(name="SCHEDULE_RIN_REPEAT")
	private BigDecimal scheduleRinRepeat;

	@Column(name="\"SECTION\"")
	private BigDecimal section;

	private String status;

	@Column(name="STRUCTURE_REPAIR")
	private String structureRepair;

	@Column(name="STRUCTURE_REPAIR_CLASS")
	private String structureRepairClass;

	@Column(name="SUB_CATEGORY")
	private String subCategory;

	@Column(name="TOTAL_FREQUENCY")
	private BigDecimal totalFrequency;

	@Column(name="TRAXDOC_FILE_NAME")
	private String traxdocFileName;

	@Column(name="TRAXDOC_NO")
	private BigDecimal traxdocNo;

	@Column(name="TRAXDOC_ROW_ID")
	private BigDecimal traxdocRowId;

	@Column(name="WARNING_AT_PERCENT_DUE")
	private BigDecimal warningAtPercentDue;

	@Column(name="WARNING_SUPRESS")
	private String warningSupress;

	@Column(name="WARRANTY_AFFECTED_NOTIF")
	private String warrantyAffectedNotif;

	@Column(name="WEIGHT_BALANCE")
	private String weightBalance;

	@Column(name="WEIGHT_OFF")
	private BigDecimal weightOff;

	@Column(name="WEIGHT_ON")
	private BigDecimal weightOn;

	@Column(name="WO_CATEGORY")
	private String woCategory;

	@Column(name="WO_COMPLIANCE_NOTIFICATION")
	private String woComplianceNotification;

	public EngineeringOrderRv() {
	}

	public EngineeringOrderRvPK getId() {
		return this.id;
	}

	public void setId(EngineeringOrderRvPK id) {
		this.id = id;
	}

	public BigDecimal getActivatingSchCycles() {
		return this.activatingSchCycles;
	}

	public void setActivatingSchCycles(BigDecimal activatingSchCycles) {
		this.activatingSchCycles = activatingSchCycles;
	}

	public BigDecimal getActivatingSchDays() {
		return this.activatingSchDays;
	}

	public void setActivatingSchDays(BigDecimal activatingSchDays) {
		this.activatingSchDays = activatingSchDays;
	}

	public String getActivatingSchEarliest() {
		return this.activatingSchEarliest;
	}

	public void setActivatingSchEarliest(String activatingSchEarliest) {
		this.activatingSchEarliest = activatingSchEarliest;
	}

	public String getActivatingSchFlag() {
		return this.activatingSchFlag;
	}

	public void setActivatingSchFlag(String activatingSchFlag) {
		this.activatingSchFlag = activatingSchFlag;
	}

	public BigDecimal getActivatingSchHours() {
		return this.activatingSchHours;
	}

	public void setActivatingSchHours(BigDecimal activatingSchHours) {
		this.activatingSchHours = activatingSchHours;
	}

	public String getAmendment() {
		return this.amendment;
	}

	public void setAmendment(String amendment) {
		this.amendment = amendment;
	}

	public String getApplicable() {
		return this.applicable;
	}

	public void setApplicable(String applicable) {
		this.applicable = applicable;
	}

	public String getAssignedTo() {
		return this.assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getAssociatedTemplate() {
		return this.associatedTemplate;
	}

	public void setAssociatedTemplate(String associatedTemplate) {
		this.associatedTemplate = associatedTemplate;
	}

	public String getAuthorization() {
		return this.authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getAuthorizedBy() {
		return this.authorizedBy;
	}

	public void setAuthorizedBy(String authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	public Date getAuthorizedDate() {
		return this.authorizedDate;
	}

	public void setAuthorizedDate(Date authorizedDate) {
		this.authorizedDate = authorizedDate;
	}

	public String getAutoReschedule() {
		return this.autoReschedule;
	}

	public void setAutoReschedule(String autoReschedule) {
		this.autoReschedule = autoReschedule;
	}

	public BigDecimal getBlobNo() {
		return this.blobNo;
	}

	public void setBlobNo(BigDecimal blobNo) {
		this.blobNo = blobNo;
	}

	public String getCalendarControl() {
		return this.calendarControl;
	}

	public void setCalendarControl(String calendarControl) {
		this.calendarControl = calendarControl;
	}

	public String getCapabilityArea() {
		return this.capabilityArea;
	}

	public void setCapabilityArea(String capabilityArea) {
		this.capabilityArea = capabilityArea;
	}

	public BigDecimal getChapter() {
		return this.chapter;
	}

	public void setChapter(BigDecimal chapter) {
		this.chapter = chapter;
	}

	public String getConfigAffectedNotif() {
		return this.configAffectedNotif;
	}

	public void setConfigAffectedNotif(String configAffectedNotif) {
		this.configAffectedNotif = configAffectedNotif;
	}

	public String getControlArea() {
		return this.controlArea;
	}

	public void setControlArea(String controlArea) {
		this.controlArea = controlArea;
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

	public String getCustomer() {
		return this.customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getDaily() {
		return this.daily;
	}

	public void setDaily(String daily) {
		this.daily = daily;
	}

	public String getDoNotIncludeChildManHrs() {
		return this.doNotIncludeChildManHrs;
	}

	public void setDoNotIncludeChildManHrs(String doNotIncludeChildManHrs) {
		this.doNotIncludeChildManHrs = doNotIncludeChildManHrs;
	}

	public BigDecimal getDocumentNo() {
		return this.documentNo;
	}

	public void setDocumentNo(BigDecimal documentNo) {
		this.documentNo = documentNo;
	}

	public String getEcAutoFleet() {
		return this.ecAutoFleet;
	}

	public void setEcAutoFleet(String ecAutoFleet) {
		this.ecAutoFleet = ecAutoFleet;
	}

	public String getEcCheckType() {
		return this.ecCheckType;
	}

	public void setEcCheckType(String ecCheckType) {
		this.ecCheckType = ecCheckType;
	}

	public String getEcLegacy() {
		return this.ecLegacy;
	}

	public void setEcLegacy(String ecLegacy) {
		this.ecLegacy = ecLegacy;
	}

	public String getEcTerminateNotification() {
		return this.ecTerminateNotification;
	}

	public void setEcTerminateNotification(String ecTerminateNotification) {
		this.ecTerminateNotification = ecTerminateNotification;
	}

	public String getEcType() {
		return this.ecType;
	}

	public void setEcType(String ecType) {
		this.ecType = ecType;
	}

	public String getEffRevOverride() {
		return this.effRevOverride;
	}

	public void setEffRevOverride(String effRevOverride) {
		this.effRevOverride = effRevOverride;
	}

	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getEffectivityNo() {
		return this.effectivityNo;
	}

	public void setEffectivityNo(String effectivityNo) {
		this.effectivityNo = effectivityNo;
	}

	public String getElectricalLoadChange() {
		return this.electricalLoadChange;
	}

	public void setElectricalLoadChange(String electricalLoadChange) {
		this.electricalLoadChange = electricalLoadChange;
	}

	public String getEoAcType() {
		return this.eoAcType;
	}

	public void setEoAcType(String eoAcType) {
		this.eoAcType = eoAcType;
	}

	public String getEoCallOn() {
		return this.eoCallOn;
	}

	public void setEoCallOn(String eoCallOn) {
		this.eoCallOn = eoCallOn;
	}

	public String getEoCallOn10() {
		return this.eoCallOn10;
	}

	public void setEoCallOn10(String eoCallOn10) {
		this.eoCallOn10 = eoCallOn10;
	}

	public String getEoCallOn11() {
		return this.eoCallOn11;
	}

	public void setEoCallOn11(String eoCallOn11) {
		this.eoCallOn11 = eoCallOn11;
	}

	public String getEoCallOn12() {
		return this.eoCallOn12;
	}

	public void setEoCallOn12(String eoCallOn12) {
		this.eoCallOn12 = eoCallOn12;
	}

	public String getEoCallOn13() {
		return this.eoCallOn13;
	}

	public void setEoCallOn13(String eoCallOn13) {
		this.eoCallOn13 = eoCallOn13;
	}

	public String getEoCallOn14() {
		return this.eoCallOn14;
	}

	public void setEoCallOn14(String eoCallOn14) {
		this.eoCallOn14 = eoCallOn14;
	}

	public String getEoCallOn15() {
		return this.eoCallOn15;
	}

	public void setEoCallOn15(String eoCallOn15) {
		this.eoCallOn15 = eoCallOn15;
	}

	public String getEoCallOn16() {
		return this.eoCallOn16;
	}

	public void setEoCallOn16(String eoCallOn16) {
		this.eoCallOn16 = eoCallOn16;
	}

	public String getEoCallOn2() {
		return this.eoCallOn2;
	}

	public void setEoCallOn2(String eoCallOn2) {
		this.eoCallOn2 = eoCallOn2;
	}

	public String getEoCallOn3() {
		return this.eoCallOn3;
	}

	public void setEoCallOn3(String eoCallOn3) {
		this.eoCallOn3 = eoCallOn3;
	}

	public String getEoCallOn4() {
		return this.eoCallOn4;
	}

	public void setEoCallOn4(String eoCallOn4) {
		this.eoCallOn4 = eoCallOn4;
	}

	public String getEoCallOn5() {
		return this.eoCallOn5;
	}

	public void setEoCallOn5(String eoCallOn5) {
		this.eoCallOn5 = eoCallOn5;
	}

	public String getEoCallOn6() {
		return this.eoCallOn6;
	}

	public void setEoCallOn6(String eoCallOn6) {
		this.eoCallOn6 = eoCallOn6;
	}

	public String getEoCallOn7() {
		return this.eoCallOn7;
	}

	public void setEoCallOn7(String eoCallOn7) {
		this.eoCallOn7 = eoCallOn7;
	}

	public String getEoCallOn8() {
		return this.eoCallOn8;
	}

	public void setEoCallOn8(String eoCallOn8) {
		this.eoCallOn8 = eoCallOn8;
	}

	public String getEoCallOn9() {
		return this.eoCallOn9;
	}

	public void setEoCallOn9(String eoCallOn9) {
		this.eoCallOn9 = eoCallOn9;
	}

	public String getEoCancel() {
		return this.eoCancel;
	}

	public void setEoCancel(String eoCancel) {
		this.eoCancel = eoCancel;
	}

	public String getEoCategory() {
		return this.eoCategory;
	}

	public void setEoCategory(String eoCategory) {
		this.eoCategory = eoCategory;
	}

	public String getEoCompany() {
		return this.eoCompany;
	}

	public void setEoCompany(String eoCompany) {
		this.eoCompany = eoCompany;
	}

	public String getEoDescription() {
		return this.eoDescription;
	}

	public void setEoDescription(String eoDescription) {
		this.eoDescription = eoDescription;
	}

	public String getEoDescriptionAdditional() {
		return this.eoDescriptionAdditional;
	}

	public void setEoDescriptionAdditional(String eoDescriptionAdditional) {
		this.eoDescriptionAdditional = eoDescriptionAdditional;
	}

	public String getEoNew() {
		return this.eoNew;
	}

	public void setEoNew(String eoNew) {
		this.eoNew = eoNew;
	}

	public String getEoSub() {
		return this.eoSub;
	}

	public void setEoSub(String eoSub) {
		this.eoSub = eoSub;
	}

	public String getEoSuppersede() {
		return this.eoSuppersede;
	}

	public void setEoSuppersede(String eoSuppersede) {
		this.eoSuppersede = eoSuppersede;
	}

	public Date getEoSuppersededDate() {
		return this.eoSuppersededDate;
	}

	public void setEoSuppersededDate(Date eoSuppersededDate) {
		this.eoSuppersededDate = eoSuppersededDate;
	}

	public String getEoType() {
		return this.eoType;
	}

	public void setEoType(String eoType) {
		this.eoType = eoType;
	}

	public String getEtops() {
		return this.etops;
	}

	public void setEtops(String etops) {
		this.etops = etops;
	}

	public String getExpenditure() {
		return this.expenditure;
	}

	public void setExpenditure(String expenditure) {
		this.expenditure = expenditure;
	}

	public BigDecimal getFc() {
		return this.fc;
	}

	public void setFc(BigDecimal fc) {
		this.fc = fc;
	}

	public String getFileNamePath() {
		return this.fileNamePath;
	}

	public void setFileNamePath(String fileNamePath) {
		this.fileNamePath = fileNamePath;
	}

	public String getFltOpsAffectedNotif() {
		return this.fltOpsAffectedNotif;
	}

	public void setFltOpsAffectedNotif(String fltOpsAffectedNotif) {
		this.fltOpsAffectedNotif = fltOpsAffectedNotif;
	}

	public BigDecimal getFormNo() {
		return this.formNo;
	}

	public void setFormNo(BigDecimal formNo) {
		this.formNo = formNo;
	}

	public BigDecimal getFr() {
		return this.fr;
	}

	public void setFr(BigDecimal fr) {
		this.fr = fr;
	}

	public BigDecimal getGroundTimeRequire() {
		return this.groundTimeRequire;
	}

	public void setGroundTimeRequire(BigDecimal groundTimeRequire) {
		this.groundTimeRequire = groundTimeRequire;
	}

	public String getHourCalendarControl() {
		return this.hourCalendarControl;
	}

	public void setHourCalendarControl(String hourCalendarControl) {
		this.hourCalendarControl = hourCalendarControl;
	}

	public String getInspectionProgramReference() {
		return this.inspectionProgramReference;
	}

	public void setInspectionProgramReference(String inspectionProgramReference) {
		this.inspectionProgramReference = inspectionProgramReference;
	}

	public String getInternalCapability() {
		return this.internalCapability;
	}

	public void setInternalCapability(String internalCapability) {
		this.internalCapability = internalCapability;
	}

	public String getIssuedBy() {
		return this.issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public Date getIssuedDate() {
		return this.issuedDate;
	}

	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}

	public Date getLimitDate() {
		return this.limitDate;
	}

	public void setLimitDate(Date limitDate) {
		this.limitDate = limitDate;
	}

	public String getLowerLevel() {
		return this.lowerLevel;
	}

	public void setLowerLevel(String lowerLevel) {
		this.lowerLevel = lowerLevel;
	}

	public String getLowerLevel10() {
		return this.lowerLevel10;
	}

	public void setLowerLevel10(String lowerLevel10) {
		this.lowerLevel10 = lowerLevel10;
	}

	public String getLowerLevel11() {
		return this.lowerLevel11;
	}

	public void setLowerLevel11(String lowerLevel11) {
		this.lowerLevel11 = lowerLevel11;
	}

	public String getLowerLevel12() {
		return this.lowerLevel12;
	}

	public void setLowerLevel12(String lowerLevel12) {
		this.lowerLevel12 = lowerLevel12;
	}

	public String getLowerLevel13() {
		return this.lowerLevel13;
	}

	public void setLowerLevel13(String lowerLevel13) {
		this.lowerLevel13 = lowerLevel13;
	}

	public String getLowerLevel14() {
		return this.lowerLevel14;
	}

	public void setLowerLevel14(String lowerLevel14) {
		this.lowerLevel14 = lowerLevel14;
	}

	public String getLowerLevel15() {
		return this.lowerLevel15;
	}

	public void setLowerLevel15(String lowerLevel15) {
		this.lowerLevel15 = lowerLevel15;
	}

	public String getLowerLevel16() {
		return this.lowerLevel16;
	}

	public void setLowerLevel16(String lowerLevel16) {
		this.lowerLevel16 = lowerLevel16;
	}

	public String getLowerLevel2() {
		return this.lowerLevel2;
	}

	public void setLowerLevel2(String lowerLevel2) {
		this.lowerLevel2 = lowerLevel2;
	}

	public String getLowerLevel3() {
		return this.lowerLevel3;
	}

	public void setLowerLevel3(String lowerLevel3) {
		this.lowerLevel3 = lowerLevel3;
	}

	public String getLowerLevel4() {
		return this.lowerLevel4;
	}

	public void setLowerLevel4(String lowerLevel4) {
		this.lowerLevel4 = lowerLevel4;
	}

	public String getLowerLevel5() {
		return this.lowerLevel5;
	}

	public void setLowerLevel5(String lowerLevel5) {
		this.lowerLevel5 = lowerLevel5;
	}

	public String getLowerLevel6() {
		return this.lowerLevel6;
	}

	public void setLowerLevel6(String lowerLevel6) {
		this.lowerLevel6 = lowerLevel6;
	}

	public String getLowerLevel7() {
		return this.lowerLevel7;
	}

	public void setLowerLevel7(String lowerLevel7) {
		this.lowerLevel7 = lowerLevel7;
	}

	public String getLowerLevel8() {
		return this.lowerLevel8;
	}

	public void setLowerLevel8(String lowerLevel8) {
		this.lowerLevel8 = lowerLevel8;
	}

	public String getLowerLevel9() {
		return this.lowerLevel9;
	}

	public void setLowerLevel9(String lowerLevel9) {
		this.lowerLevel9 = lowerLevel9;
	}

	public String getMaintenanceProgram() {
		return this.maintenanceProgram;
	}

	public void setMaintenanceProgram(String maintenanceProgram) {
		this.maintenanceProgram = maintenanceProgram;
	}

	public String getManditory() {
		return this.manditory;
	}

	public void setManditory(String manditory) {
		this.manditory = manditory;
	}

	public BigDecimal getMaxYieldPercent() {
		return this.maxYieldPercent;
	}

	public void setMaxYieldPercent(BigDecimal maxYieldPercent) {
		this.maxYieldPercent = maxYieldPercent;
	}

	public String getModNo() {
		return this.modNo;
	}

	public void setModNo(String modNo) {
		this.modNo = modNo;
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

	public String getNewEo() {
		return this.newEo;
	}

	public void setNewEo(String newEo) {
		this.newEo = newEo;
	}

	public String getNextControllerEc() {
		return this.nextControllerEc;
	}

	public void setNextControllerEc(String nextControllerEc) {
		this.nextControllerEc = nextControllerEc;
	}

	public String getNextEo() {
		return this.nextEo;
	}

	public void setNextEo(String nextEo) {
		this.nextEo = nextEo;
	}

	public String getNextGroundOp() {
		return this.nextGroundOp;
	}

	public void setNextGroundOp(String nextGroundOp) {
		this.nextGroundOp = nextGroundOp;
	}

	public String getNotDoAllowConcession() {
		return this.notDoAllowConcession;
	}

	public void setNotDoAllowConcession(String notDoAllowConcession) {
		this.notDoAllowConcession = notDoAllowConcession;
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

	public String getOverrideLowerLevel10() {
		return this.overrideLowerLevel10;
	}

	public void setOverrideLowerLevel10(String overrideLowerLevel10) {
		this.overrideLowerLevel10 = overrideLowerLevel10;
	}

	public String getOverrideLowerLevel11() {
		return this.overrideLowerLevel11;
	}

	public void setOverrideLowerLevel11(String overrideLowerLevel11) {
		this.overrideLowerLevel11 = overrideLowerLevel11;
	}

	public String getOverrideLowerLevel12() {
		return this.overrideLowerLevel12;
	}

	public void setOverrideLowerLevel12(String overrideLowerLevel12) {
		this.overrideLowerLevel12 = overrideLowerLevel12;
	}

	public String getOverrideLowerLevel13() {
		return this.overrideLowerLevel13;
	}

	public void setOverrideLowerLevel13(String overrideLowerLevel13) {
		this.overrideLowerLevel13 = overrideLowerLevel13;
	}

	public String getOverrideLowerLevel14() {
		return this.overrideLowerLevel14;
	}

	public void setOverrideLowerLevel14(String overrideLowerLevel14) {
		this.overrideLowerLevel14 = overrideLowerLevel14;
	}

	public String getOverrideLowerLevel15() {
		return this.overrideLowerLevel15;
	}

	public void setOverrideLowerLevel15(String overrideLowerLevel15) {
		this.overrideLowerLevel15 = overrideLowerLevel15;
	}

	public String getOverrideLowerLevel16() {
		return this.overrideLowerLevel16;
	}

	public void setOverrideLowerLevel16(String overrideLowerLevel16) {
		this.overrideLowerLevel16 = overrideLowerLevel16;
	}

	public String getOverrideLowerLevel2() {
		return this.overrideLowerLevel2;
	}

	public void setOverrideLowerLevel2(String overrideLowerLevel2) {
		this.overrideLowerLevel2 = overrideLowerLevel2;
	}

	public String getOverrideLowerLevel3() {
		return this.overrideLowerLevel3;
	}

	public void setOverrideLowerLevel3(String overrideLowerLevel3) {
		this.overrideLowerLevel3 = overrideLowerLevel3;
	}

	public String getOverrideLowerLevel4() {
		return this.overrideLowerLevel4;
	}

	public void setOverrideLowerLevel4(String overrideLowerLevel4) {
		this.overrideLowerLevel4 = overrideLowerLevel4;
	}

	public String getOverrideLowerLevel5() {
		return this.overrideLowerLevel5;
	}

	public void setOverrideLowerLevel5(String overrideLowerLevel5) {
		this.overrideLowerLevel5 = overrideLowerLevel5;
	}

	public String getOverrideLowerLevel6() {
		return this.overrideLowerLevel6;
	}

	public void setOverrideLowerLevel6(String overrideLowerLevel6) {
		this.overrideLowerLevel6 = overrideLowerLevel6;
	}

	public String getOverrideLowerLevel7() {
		return this.overrideLowerLevel7;
	}

	public void setOverrideLowerLevel7(String overrideLowerLevel7) {
		this.overrideLowerLevel7 = overrideLowerLevel7;
	}

	public String getOverrideLowerLevel8() {
		return this.overrideLowerLevel8;
	}

	public void setOverrideLowerLevel8(String overrideLowerLevel8) {
		this.overrideLowerLevel8 = overrideLowerLevel8;
	}

	public String getOverrideLowerLevel9() {
		return this.overrideLowerLevel9;
	}

	public void setOverrideLowerLevel9(String overrideLowerLevel9) {
		this.overrideLowerLevel9 = overrideLowerLevel9;
	}

	public String getOverrideSchedule() {
		return this.overrideSchedule;
	}

	public void setOverrideSchedule(String overrideSchedule) {
		this.overrideSchedule = overrideSchedule;
	}

	public BigDecimal getParagraph() {
		return this.paragraph;
	}

	public void setParagraph(BigDecimal paragraph) {
		this.paragraph = paragraph;
	}

	public String getPlanEo() {
		return this.planEo;
	}

	public void setPlanEo(String planEo) {
		this.planEo = planEo;
	}

	public BigDecimal getPlanLeadTime() {
		return this.planLeadTime;
	}

	public void setPlanLeadTime(BigDecimal planLeadTime) {
		this.planLeadTime = planLeadTime;
	}

	public String getPlanningDept() {
		return this.planningDept;
	}

	public void setPlanningDept(String planningDept) {
		this.planningDept = planningDept;
	}

	public String getPlanningSubDept() {
		return this.planningSubDept;
	}

	public void setPlanningSubDept(String planningSubDept) {
		this.planningSubDept = planningSubDept;
	}

	public String getPressureCycles() {
		return this.pressureCycles;
	}

	public void setPressureCycles(String pressureCycles) {
		this.pressureCycles = pressureCycles;
	}

	public String getRedoEc() {
		return this.redoEc;
	}

	public void setRedoEc(String redoEc) {
		this.redoEc = redoEc;
	}

	public String getReleaseForAuthorization() {
		return this.releaseForAuthorization;
	}

	public void setReleaseForAuthorization(String releaseForAuthorization) {
		this.releaseForAuthorization = releaseForAuthorization;
	}

	public Date getReleaseForAuthorizationOn() {
		return this.releaseForAuthorizationOn;
	}

	public void setReleaseForAuthorizationOn(Date releaseForAuthorizationOn) {
		this.releaseForAuthorizationOn = releaseForAuthorizationOn;
	}

	public String getRepairCode() {
		return this.repairCode;
	}

	public void setRepairCode(String repairCode) {
		this.repairCode = repairCode;
	}

	public BigDecimal getRepeatNumber() {
		return this.repeatNumber;
	}

	public void setRepeatNumber(BigDecimal repeatNumber) {
		this.repeatNumber = repeatNumber;
	}

	public String getRequirementType() {
		return this.requirementType;
	}

	public void setRequirementType(String requirementType) {
		this.requirementType = requirementType;
	}

	public String getRevisedBy() {
		return this.revisedBy;
	}

	public void setRevisedBy(String revisedBy) {
		this.revisedBy = revisedBy;
	}

	public Date getRevisedDate() {
		return this.revisedDate;
	}

	public void setRevisedDate(Date revisedDate) {
		this.revisedDate = revisedDate;
	}

	public String getRevisionIssuedBy() {
		return this.revisionIssuedBy;
	}

	public void setRevisionIssuedBy(String revisionIssuedBy) {
		this.revisionIssuedBy = revisionIssuedBy;
	}

	public String getSchedule() {
		return this.schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getScheduleAc() {
		return this.scheduleAc;
	}

	public void setScheduleAc(String scheduleAc) {
		this.scheduleAc = scheduleAc;
	}

	public BigDecimal getScheduleAcCycles() {
		return this.scheduleAcCycles;
	}

	public void setScheduleAcCycles(BigDecimal scheduleAcCycles) {
		this.scheduleAcCycles = scheduleAcCycles;
	}

	public BigDecimal getScheduleAcDays() {
		return this.scheduleAcDays;
	}

	public void setScheduleAcDays(BigDecimal scheduleAcDays) {
		this.scheduleAcDays = scheduleAcDays;
	}

	public BigDecimal getScheduleAcHours() {
		return this.scheduleAcHours;
	}

	public void setScheduleAcHours(BigDecimal scheduleAcHours) {
		this.scheduleAcHours = scheduleAcHours;
	}

	public BigDecimal getScheduleCounter() {
		return this.scheduleCounter;
	}

	public void setScheduleCounter(BigDecimal scheduleCounter) {
		this.scheduleCounter = scheduleCounter;
	}

	public BigDecimal getScheduleCounterRepeat() {
		return this.scheduleCounterRepeat;
	}

	public void setScheduleCounterRepeat(BigDecimal scheduleCounterRepeat) {
		this.scheduleCounterRepeat = scheduleCounterRepeat;
	}

	public String getScheduleCounterType() {
		return this.scheduleCounterType;
	}

	public void setScheduleCounterType(String scheduleCounterType) {
		this.scheduleCounterType = scheduleCounterType;
	}

	public String getScheduleCounterTypeRepeat() {
		return this.scheduleCounterTypeRepeat;
	}

	public void setScheduleCounterTypeRepeat(String scheduleCounterTypeRepeat) {
		this.scheduleCounterTypeRepeat = scheduleCounterTypeRepeat;
	}

	public BigDecimal getScheduleCycles() {
		return this.scheduleCycles;
	}

	public void setScheduleCycles(BigDecimal scheduleCycles) {
		this.scheduleCycles = scheduleCycles;
	}

	public BigDecimal getScheduleCyclesRepeat() {
		return this.scheduleCyclesRepeat;
	}

	public void setScheduleCyclesRepeat(BigDecimal scheduleCyclesRepeat) {
		this.scheduleCyclesRepeat = scheduleCyclesRepeat;
	}

	public Date getScheduleDate() {
		return this.scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public Date getScheduleDateRepeat() {
		return this.scheduleDateRepeat;
	}

	public void setScheduleDateRepeat(Date scheduleDateRepeat) {
		this.scheduleDateRepeat = scheduleDateRepeat;
	}

	public BigDecimal getScheduleDays() {
		return this.scheduleDays;
	}

	public void setScheduleDays(BigDecimal scheduleDays) {
		this.scheduleDays = scheduleDays;
	}

	public String getScheduleDaysEom() {
		return this.scheduleDaysEom;
	}

	public void setScheduleDaysEom(String scheduleDaysEom) {
		this.scheduleDaysEom = scheduleDaysEom;
	}

	public String getScheduleDaysEomRepeat() {
		return this.scheduleDaysEomRepeat;
	}

	public void setScheduleDaysEomRepeat(String scheduleDaysEomRepeat) {
		this.scheduleDaysEomRepeat = scheduleDaysEomRepeat;
	}

	public BigDecimal getScheduleDaysRepeat() {
		return this.scheduleDaysRepeat;
	}

	public void setScheduleDaysRepeat(BigDecimal scheduleDaysRepeat) {
		this.scheduleDaysRepeat = scheduleDaysRepeat;
	}

	public BigDecimal getScheduleHours() {
		return this.scheduleHours;
	}

	public void setScheduleHours(BigDecimal scheduleHours) {
		this.scheduleHours = scheduleHours;
	}

	public BigDecimal getScheduleHoursRepeat() {
		return this.scheduleHoursRepeat;
	}

	public void setScheduleHoursRepeat(BigDecimal scheduleHoursRepeat) {
		this.scheduleHoursRepeat = scheduleHoursRepeat;
	}

	public BigDecimal getScheduleLimitCycles() {
		return this.scheduleLimitCycles;
	}

	public void setScheduleLimitCycles(BigDecimal scheduleLimitCycles) {
		this.scheduleLimitCycles = scheduleLimitCycles;
	}

	public BigDecimal getScheduleLimitDays() {
		return this.scheduleLimitDays;
	}

	public void setScheduleLimitDays(BigDecimal scheduleLimitDays) {
		this.scheduleLimitDays = scheduleLimitDays;
	}

	public BigDecimal getScheduleLimitHours() {
		return this.scheduleLimitHours;
	}

	public void setScheduleLimitHours(BigDecimal scheduleLimitHours) {
		this.scheduleLimitHours = scheduleLimitHours;
	}

	public BigDecimal getScheduleLimitRin() {
		return this.scheduleLimitRin;
	}

	public void setScheduleLimitRin(BigDecimal scheduleLimitRin) {
		this.scheduleLimitRin = scheduleLimitRin;
	}

	public String getScheduleRepeat() {
		return this.scheduleRepeat;
	}

	public void setScheduleRepeat(String scheduleRepeat) {
		this.scheduleRepeat = scheduleRepeat;
	}

	public BigDecimal getScheduleRin() {
		return this.scheduleRin;
	}

	public void setScheduleRin(BigDecimal scheduleRin) {
		this.scheduleRin = scheduleRin;
	}

	public BigDecimal getScheduleRinRepeat() {
		return this.scheduleRinRepeat;
	}

	public void setScheduleRinRepeat(BigDecimal scheduleRinRepeat) {
		this.scheduleRinRepeat = scheduleRinRepeat;
	}

	public BigDecimal getSection() {
		return this.section;
	}

	public void setSection(BigDecimal section) {
		this.section = section;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStructureRepair() {
		return this.structureRepair;
	}

	public void setStructureRepair(String structureRepair) {
		this.structureRepair = structureRepair;
	}

	public String getStructureRepairClass() {
		return this.structureRepairClass;
	}

	public void setStructureRepairClass(String structureRepairClass) {
		this.structureRepairClass = structureRepairClass;
	}

	public String getSubCategory() {
		return this.subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public BigDecimal getTotalFrequency() {
		return this.totalFrequency;
	}

	public void setTotalFrequency(BigDecimal totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	public String getTraxdocFileName() {
		return this.traxdocFileName;
	}

	public void setTraxdocFileName(String traxdocFileName) {
		this.traxdocFileName = traxdocFileName;
	}

	public BigDecimal getTraxdocNo() {
		return this.traxdocNo;
	}

	public void setTraxdocNo(BigDecimal traxdocNo) {
		this.traxdocNo = traxdocNo;
	}

	public BigDecimal getTraxdocRowId() {
		return this.traxdocRowId;
	}

	public void setTraxdocRowId(BigDecimal traxdocRowId) {
		this.traxdocRowId = traxdocRowId;
	}

	public BigDecimal getWarningAtPercentDue() {
		return this.warningAtPercentDue;
	}

	public void setWarningAtPercentDue(BigDecimal warningAtPercentDue) {
		this.warningAtPercentDue = warningAtPercentDue;
	}

	public String getWarningSupress() {
		return this.warningSupress;
	}

	public void setWarningSupress(String warningSupress) {
		this.warningSupress = warningSupress;
	}

	public String getWarrantyAffectedNotif() {
		return this.warrantyAffectedNotif;
	}

	public void setWarrantyAffectedNotif(String warrantyAffectedNotif) {
		this.warrantyAffectedNotif = warrantyAffectedNotif;
	}

	public String getWeightBalance() {
		return this.weightBalance;
	}

	public void setWeightBalance(String weightBalance) {
		this.weightBalance = weightBalance;
	}

	public BigDecimal getWeightOff() {
		return this.weightOff;
	}

	public void setWeightOff(BigDecimal weightOff) {
		this.weightOff = weightOff;
	}

	public BigDecimal getWeightOn() {
		return this.weightOn;
	}

	public void setWeightOn(BigDecimal weightOn) {
		this.weightOn = weightOn;
	}

	public String getWoCategory() {
		return this.woCategory;
	}

	public void setWoCategory(String woCategory) {
		this.woCategory = woCategory;
	}

	public String getWoComplianceNotification() {
		return this.woComplianceNotification;
	}

	public void setWoComplianceNotification(String woComplianceNotification) {
		this.woComplianceNotification = woComplianceNotification;
	}

}