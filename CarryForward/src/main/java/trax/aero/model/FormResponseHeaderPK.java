package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the FORM_RESPONSE_HEADER database table.
 * 
 */
@Embeddable
public class FormResponseHeaderPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="FORM_NO")
	private long formNo;

	@Column(name="FORM_LINE")
	private long formLine;

	public FormResponseHeaderPK() {
	}
	public long getFormNo() {
		return this.formNo;
	}
	public void setFormNo(long formNo) {
		this.formNo = formNo;
	}
	public long getFormLine() {
		return this.formLine;
	}
	public void setFormLine(long formLine) {
		this.formLine = formLine;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FormResponseHeaderPK)) {
			return false;
		}
		FormResponseHeaderPK castOther = (FormResponseHeaderPK)other;
		return 
			(this.formNo == castOther.formNo)
			&& (this.formLine == castOther.formLine);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.formNo ^ (this.formNo >>> 32)));
		hash = hash * prime + ((int) (this.formLine ^ (this.formLine >>> 32)));
		
		return hash;
	}
}