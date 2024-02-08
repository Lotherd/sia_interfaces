package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the EMPLOYEE_TECHNICAL_EXPREIANCE database table.
 * 
 */
@Entity
@Table(name="EMPLOYEE_TECHNICAL_EXPREIANCE")
@NamedQuery(name="EmployeeTechnicalExpreiance.findAll", query="SELECT e FROM EmployeeTechnicalExpreiance e")
public class EmployeeTechnicalExpreiance implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EmployeeTechnicalExpreiancePK id;

	private String approval;

	@Column(name="BLOB_NO")
	private BigDecimal blobNo;

	private String company;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="DATE_FROM")
	private Date dateFrom;

	@Column(name="DATE_TO")
	private Date dateTo;

	private String department;

	@Column(name="EXPERIANCE_TOTAL")
	private BigDecimal experianceTotal;

	@Column(name="EXPERIANCE_TYPE")
	private String experianceType;

	private String location;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	@Column(name="\"POSITION\"")
	private String position;

	private String series;

	private String skill;

	@Column(name="\"TYPE\"")
	private String type;

	@Column(name="TYPE_OF_RATING")
	private String typeOfRating;

	public EmployeeTechnicalExpreiance() {
	}

	public EmployeeTechnicalExpreiancePK getId() {
		return this.id;
	}

	public void setId(EmployeeTechnicalExpreiancePK id) {
		this.id = id;
	}

	public String getApproval() {
		return this.approval;
	}

	public void setApproval(String approval) {
		this.approval = approval;
	}

	public BigDecimal getBlobNo() {
		return this.blobNo;
	}

	public void setBlobNo(BigDecimal blobNo) {
		this.blobNo = blobNo;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public Date getDateFrom() {
		return this.dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return this.dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public BigDecimal getExperianceTotal() {
		return this.experianceTotal;
	}

	public void setExperianceTotal(BigDecimal experianceTotal) {
		this.experianceTotal = experianceTotal;
	}

	public String getExperianceType() {
		return this.experianceType;
	}

	public void setExperianceType(String experianceType) {
		this.experianceType = experianceType;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getSeries() {
		return this.series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getSkill() {
		return this.skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeOfRating() {
		return this.typeOfRating;
	}

	public void setTypeOfRating(String typeOfRating) {
		this.typeOfRating = typeOfRating;
	}

}