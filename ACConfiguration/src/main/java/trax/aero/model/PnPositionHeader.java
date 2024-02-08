package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the PN_POSITION_HEADER database table.
 * 
 */
@Entity
@Table(name="PN_POSITION_HEADER")
@NamedQuery(name="PnPositionHeader.findAll", query="SELECT p FROM PnPositionHeader p")
public class PnPositionHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PnPositionHeaderPK id;

	@Column(name="ALLOW_EMPTY_POSITION")
	private String allowEmptyPosition;

	private BigDecimal chapter;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	private String description;

	@Column(name="EO_REMOVAL")
	private String eoRemoval;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	private String override;

	private BigDecimal paragraph;

	@Column(name="POSITION_GROUP")
	private String positionGroup;

	@Column(name="QTY_INSTALLED")
	private BigDecimal qtyInstalled;

	@Column(name="\"SECTION\"")
	private BigDecimal section;

	@Column(name="\"SELECT\"")
	private String select;

	@Column(name="\"ZONE\"")
	private String zone;

	public PnPositionHeader() {
	}

	public PnPositionHeaderPK getId() {
		return this.id;
	}

	public void setId(PnPositionHeaderPK id) {
		this.id = id;
	}

	public String getAllowEmptyPosition() {
		return this.allowEmptyPosition;
	}

	public void setAllowEmptyPosition(String allowEmptyPosition) {
		this.allowEmptyPosition = allowEmptyPosition;
	}

	public BigDecimal getChapter() {
		return this.chapter;
	}

	public void setChapter(BigDecimal chapter) {
		this.chapter = chapter;
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEoRemoval() {
		return this.eoRemoval;
	}

	public void setEoRemoval(String eoRemoval) {
		this.eoRemoval = eoRemoval;
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

	public BigDecimal getParagraph() {
		return this.paragraph;
	}

	public void setParagraph(BigDecimal paragraph) {
		this.paragraph = paragraph;
	}

	public String getPositionGroup() {
		return this.positionGroup;
	}

	public void setPositionGroup(String positionGroup) {
		this.positionGroup = positionGroup;
	}

	public BigDecimal getQtyInstalled() {
		return this.qtyInstalled;
	}

	public void setQtyInstalled(BigDecimal qtyInstalled) {
		this.qtyInstalled = qtyInstalled;
	}

	public BigDecimal getSection() {
		return this.section;
	}

	public void setSection(BigDecimal section) {
		this.section = section;
	}

	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getZone() {
		return this.zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

}