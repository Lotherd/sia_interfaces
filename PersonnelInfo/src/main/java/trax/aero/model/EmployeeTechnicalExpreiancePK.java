package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the EMPLOYEE_TECHNICAL_EXPREIANCE database table.
 * 
 */
@Embeddable
public class EmployeeTechnicalExpreiancePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String employee;

	private long item;

	public EmployeeTechnicalExpreiancePK() {
	}
	public String getEmployee() {
		return this.employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public long getItem() {
		return this.item;
	}
	public void setItem(long item) {
		this.item = item;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EmployeeTechnicalExpreiancePK)) {
			return false;
		}
		EmployeeTechnicalExpreiancePK castOther = (EmployeeTechnicalExpreiancePK)other;
		return 
			this.employee.equals(castOther.employee)
			&& (this.item == castOther.item);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.employee.hashCode();
		hash = hash * prime + ((int) (this.item ^ (this.item >>> 32)));
		
		return hash;
	}
}