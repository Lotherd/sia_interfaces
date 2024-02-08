package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the PN_EFFECTIVITY_HEADER database table.
 * 
 */
@Entity
@Table(name="PN_EFFECTIVITY_HEADER")
@NamedQuery(name="PnEffectivityHeader.findAll", query="SELECT p FROM PnEffectivityHeader p")
public class PnEffectivityHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PnEffectivityHeaderPK id;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	private BigDecimal notes;

	private String override;

	@Column(name="QTY_INSTALLED")
	private BigDecimal qtyInstalled;

	@Column(name="\"SELECT\"")
	private String select;

	@Temporal(TemporalType.DATE)
	@Column(name="TRAXDOC_CREATED_DATE")
	private Date traxdocCreatedDate;

	@Column(name="TRAXDOC_FILE_NAME_PATH")
	private String traxdocFileNamePath;

	@Column(name="TRAXDOC_ROW_ID")
	private BigDecimal traxdocRowId;

	//bi-directional many-to-one association to AcTypeSeriesMaster
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="AC_SERIES", referencedColumnName="AC_SERIES" , insertable=false, updatable=false),
		@JoinColumn(name="AC_TYPE", referencedColumnName="AC_TYPE" , insertable=false, updatable=false)
		})
	private AcTypeSeriesMaster acTypeSeriesMaster;

	public PnEffectivityHeader() {
	}

	public PnEffectivityHeaderPK getId() {
		return this.id;
	}

	public void setId(PnEffectivityHeaderPK id) {
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

	public BigDecimal getQtyInstalled() {
		return this.qtyInstalled;
	}

	public void setQtyInstalled(BigDecimal qtyInstalled) {
		this.qtyInstalled = qtyInstalled;
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

	public AcTypeSeriesMaster getAcTypeSeriesMaster() {
		return this.acTypeSeriesMaster;
	}

	public void setAcTypeSeriesMaster(AcTypeSeriesMaster acTypeSeriesMaster) {
		this.acTypeSeriesMaster = acTypeSeriesMaster;
	}

}