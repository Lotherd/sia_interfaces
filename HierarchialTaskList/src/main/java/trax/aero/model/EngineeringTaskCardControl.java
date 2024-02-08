package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the ENGINEERING_TASK_CARD_CONTROL database table.
 * 
 */
@Entity
@Table(name="ENGINEERING_TASK_CARD_CONTROL")
@NamedQuery(name="EngineeringTaskCardControl.findAll", query="SELECT e FROM EngineeringTaskCardControl e")
public class EngineeringTaskCardControl implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EngineeringTaskCardControlPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="\"GROUP\"")
	private BigDecimal group;

	@Column(name="GROUP_WO_DESCRIPTION")
	private String groupWoDescription;

	private BigDecimal identifier;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	private String override;

	@Column(name="\"SELECT\"")
	private String select;

	@Column(name="SORT_ITEM")
	private BigDecimal sortItem;

	@Column(name="SUB_GROUP")
	private BigDecimal subGroup;

	//bi-directional many-to-one association to EngineeringOrder
	@ManyToOne
	@JoinColumn(name="EO", insertable=false, updatable=false)
	private EngineeringOrder engineeringOrder;

	public EngineeringTaskCardControl() {
	}

	public EngineeringTaskCardControlPK getId() {
		return this.id;
	}

	public void setId(EngineeringTaskCardControlPK id) {
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

	public BigDecimal getGroup() {
		return this.group;
	}

	public void setGroup(BigDecimal group) {
		this.group = group;
	}

	public String getGroupWoDescription() {
		return this.groupWoDescription;
	}

	public void setGroupWoDescription(String groupWoDescription) {
		this.groupWoDescription = groupWoDescription;
	}

	public BigDecimal getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(BigDecimal identifier) {
		this.identifier = identifier;
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

	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public BigDecimal getSortItem() {
		return this.sortItem;
	}

	public void setSortItem(BigDecimal sortItem) {
		this.sortItem = sortItem;
	}

	public BigDecimal getSubGroup() {
		return this.subGroup;
	}

	public void setSubGroup(BigDecimal subGroup) {
		this.subGroup = subGroup;
	}

	public EngineeringOrder getEngineeringOrder() {
		return this.engineeringOrder;
	}

	public void setEngineeringOrder(EngineeringOrder engineeringOrder) {
		this.engineeringOrder = engineeringOrder;
	}

}