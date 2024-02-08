package trax.aero.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the PN_POSITION_RESTRICTION_EFF database table.
 * 
 */
@Entity
@Table(name="PN_POSITION_RESTRICTION_EFF")
@NamedQuery(name="PnPositionRestrictionEff.findAll", query="SELECT p FROM PnPositionRestrictionEff p")
public class PnPositionRestrictionEff implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PnPositionRestrictionEffPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name="\"SELECT\"")
	private String select;

	//bi-directional many-to-one association to AcMaster
	@ManyToOne
	@JoinColumn(name="AC")
	private AcMaster acMaster;

	public PnPositionRestrictionEff() {
	}

	public PnPositionRestrictionEffPK getId() {
		return this.id;
	}

	public void setId(PnPositionRestrictionEffPK id) {
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

	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public AcMaster getAcMaster() {
		return this.acMaster;
	}

	public void setAcMaster(AcMaster acMaster) {
		this.acMaster = acMaster;
	}

}