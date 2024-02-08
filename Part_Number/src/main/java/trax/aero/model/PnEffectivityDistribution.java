package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the PN_EFFECTIVITY_DISTRIBUTION database table.
 * 
 */
@Entity
@Table(name="PN_EFFECTIVITY_DISTRIBUTION")
@NamedQuery(name="PnEffectivityDistribution.findAll", query="SELECT p FROM PnEffectivityDistribution p")
public class PnEffectivityDistribution implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PnEffectivityDistributionPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.DATE)
	@Column(name="IFACE_BAXTER_BOM_TRANSFER_DATE")
	private Date ifaceBaxterBomTransferDate;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	@Column(name="\"SELECT\"")
	private String select;

	@Temporal(TemporalType.DATE)
	@Column(name="TRAXDOC_CREATED_DATE")
	private Date traxdocCreatedDate;

	@Column(name="TRAXDOC_FILE_NAME_PATH")
	private String traxdocFileNamePath;

	@Column(name="TRAXDOC_ROW_ID")
	private BigDecimal traxdocRowId;

	//bi-directional many-to-one association to AcMaster
	@ManyToOne
	@JoinColumn(name="AC", insertable=false, updatable=false)
	private AcMaster acMaster;

	public PnEffectivityDistribution() {
	}

	public PnEffectivityDistributionPK getId() {
		return this.id;
	}

	public void setId(PnEffectivityDistributionPK id) {
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

	public Date getIfaceBaxterBomTransferDate() {
		return this.ifaceBaxterBomTransferDate;
	}

	public void setIfaceBaxterBomTransferDate(Date ifaceBaxterBomTransferDate) {
		this.ifaceBaxterBomTransferDate = ifaceBaxterBomTransferDate;
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

	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public Date getTraxdocCreatedDate() {
		return this.traxdocCreatedDate;
	}

	public void setTraxdocCreatedDate(Date traxdocCreatedDate) {
		this.traxdocCreatedDate = traxdocCreatedDate;
	}

	public String getTraxdocFileNamePath() {
		return this.traxdocFileNamePath;
	}

	public void setTraxdocFileNamePath(String traxdocFileNamePath) {
		this.traxdocFileNamePath = traxdocFileNamePath;
	}

	public BigDecimal getTraxdocRowId() {
		return this.traxdocRowId;
	}

	public void setTraxdocRowId(BigDecimal traxdocRowId) {
		this.traxdocRowId = traxdocRowId;
	}

	public AcMaster getAcMaster() {
		return this.acMaster;
	}

	public void setAcMaster(AcMaster acMaster) {
		this.acMaster = acMaster;
	}

}