package trax.aero.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the SIA_REQUISITIONS database table.
 * 
 */
@Entity
@Table(name="SIA_REQUISITIONS")
@NamedQuery(name="SiaRequisition.findAll", query="SELECT s FROM SiaRequisition s")
public class SiaRequisition implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SiaRequisitionPK id;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="SYNC_DATE")
	private Date syncDate;

	public SiaRequisition() {
	}

	public SiaRequisitionPK getId() {
		return this.id;
	}

	public void setId(SiaRequisitionPK id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getSyncDate() {
		return this.syncDate;
	}

	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

}