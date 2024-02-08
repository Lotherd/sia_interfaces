package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the SKILL_MASTER database table.
 * 
 */
@Entity
@Table(name="SKILL_MASTER")
@NamedQuery(name="SkillMaster.findAll", query="SELECT s FROM SkillMaster s")
public class SkillMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String skill;

	@Column(name="AC_SERIES")
	private String acSeries;

	@Column(name="AC_TYPE")
	private String acType;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	private String defect;

	@Column(name="DOUBLE_TIME_BURDEN_COST")
	private BigDecimal doubleTimeBurdenCost;

	@Column(name="DOUBLE_TIME_COST")
	private BigDecimal doubleTimeCost;

	@Column(name="DOUBLE_TIME_SELL")
	private BigDecimal doubleTimeSell;

	private String etops;

	@Column(name="HIERARCHY_SKILL_ID")
	private BigDecimal hierarchySkillId;

	private String inspector;

	private String mechanic;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	@Column(name="OVER_TIME_BURDEN_COST")
	private BigDecimal overTimeBurdenCost;

	@Column(name="OVER_TIME_SELL")
	private BigDecimal overTimeSell;

	@Column(name="OVER_TIME_STANDARD_COST")
	private BigDecimal overTimeStandardCost;

	@Column(name="PRIORITY_RANK")
	private BigDecimal priorityRank;

	@Column(name="REGULAR_BURDEN_COST")
	private BigDecimal regularBurdenCost;

	@Column(name="REGULAR_SELL")
	private BigDecimal regularSell;

	@Column(name="REGULAR_STANDARD_COST")
	private BigDecimal regularStandardCost;

	@Column(name="SKILL_CATEGORY")
	private String skillCategory;

	@Column(name="SKILL_DESCRIPTION")
	private String skillDescription;

	@Column(name="SKILL_TYPE")
	private String skillType;

	private String status;

	public SkillMaster() {
	}

	public String getSkill() {
		return this.skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getAcSeries() {
		return this.acSeries;
	}

	public void setAcSeries(String acSeries) {
		this.acSeries = acSeries;
	}

	public String getAcType() {
		return this.acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public BigDecimal getBlobNo() {
		return this.blobNo;
	}

	public void setBlobNo(BigDecimal blobNo) {
		this.blobNo = blobNo;
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

	public String getDefect() {
		return this.defect;
	}

	public void setDefect(String defect) {
		this.defect = defect;
	}

	public BigDecimal getDoubleTimeBurdenCost() {
		return this.doubleTimeBurdenCost;
	}

	public void setDoubleTimeBurdenCost(BigDecimal doubleTimeBurdenCost) {
		this.doubleTimeBurdenCost = doubleTimeBurdenCost;
	}

	public BigDecimal getDoubleTimeCost() {
		return this.doubleTimeCost;
	}

	public void setDoubleTimeCost(BigDecimal doubleTimeCost) {
		this.doubleTimeCost = doubleTimeCost;
	}

	public BigDecimal getDoubleTimeSell() {
		return this.doubleTimeSell;
	}

	public void setDoubleTimeSell(BigDecimal doubleTimeSell) {
		this.doubleTimeSell = doubleTimeSell;
	}

	public String getEtops() {
		return this.etops;
	}

	public void setEtops(String etops) {
		this.etops = etops;
	}

	public BigDecimal getHierarchySkillId() {
		return this.hierarchySkillId;
	}

	public void setHierarchySkillId(BigDecimal hierarchySkillId) {
		this.hierarchySkillId = hierarchySkillId;
	}

	public String getInspector() {
		return this.inspector;
	}

	public void setInspector(String inspector) {
		this.inspector = inspector;
	}

	public String getMechanic() {
		return this.mechanic;
	}

	public void setMechanic(String mechanic) {
		this.mechanic = mechanic;
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

	public BigDecimal getOverTimeBurdenCost() {
		return this.overTimeBurdenCost;
	}

	public void setOverTimeBurdenCost(BigDecimal overTimeBurdenCost) {
		this.overTimeBurdenCost = overTimeBurdenCost;
	}

	public BigDecimal getOverTimeSell() {
		return this.overTimeSell;
	}

	public void setOverTimeSell(BigDecimal overTimeSell) {
		this.overTimeSell = overTimeSell;
	}

	public BigDecimal getOverTimeStandardCost() {
		return this.overTimeStandardCost;
	}

	public void setOverTimeStandardCost(BigDecimal overTimeStandardCost) {
		this.overTimeStandardCost = overTimeStandardCost;
	}

	public BigDecimal getPriorityRank() {
		return this.priorityRank;
	}

	public void setPriorityRank(BigDecimal priorityRank) {
		this.priorityRank = priorityRank;
	}

	public BigDecimal getRegularBurdenCost() {
		return this.regularBurdenCost;
	}

	public void setRegularBurdenCost(BigDecimal regularBurdenCost) {
		this.regularBurdenCost = regularBurdenCost;
	}

	public BigDecimal getRegularSell() {
		return this.regularSell;
	}

	public void setRegularSell(BigDecimal regularSell) {
		this.regularSell = regularSell;
	}

	public BigDecimal getRegularStandardCost() {
		return this.regularStandardCost;
	}

	public void setRegularStandardCost(BigDecimal regularStandardCost) {
		this.regularStandardCost = regularStandardCost;
	}

	public String getSkillCategory() {
		return this.skillCategory;
	}

	public void setSkillCategory(String skillCategory) {
		this.skillCategory = skillCategory;
	}

	public String getSkillDescription() {
		return this.skillDescription;
	}

	public void setSkillDescription(String skillDescription) {
		this.skillDescription = skillDescription;
	}

	public String getSkillType() {
		return this.skillType;
	}

	public void setSkillType(String skillType) {
		this.skillType = skillType;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}