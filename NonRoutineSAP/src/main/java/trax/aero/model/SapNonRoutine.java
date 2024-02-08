package trax.aero.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the SAP_NON_ROUTINE database table.
 * 
 */
@Entity
@Table(name="SAP_NON_ROUTINE")
@NamedQuery(name="SapNonRoutine.findAll", query="SELECT s FROM SapNonRoutine s")
public class SapNonRoutine implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SapNonRoutinePK id;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="SYNC_DATE")
	private Date syncDate;

	public SapNonRoutine() {
	}

	public SapNonRoutinePK getId() {
		return this.id;
	}

	public void setId(SapNonRoutinePK id) {
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