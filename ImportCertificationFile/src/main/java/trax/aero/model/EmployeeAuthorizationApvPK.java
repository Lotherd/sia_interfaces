package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the EMPLOYEE_AUTHORIZATION_APV database table.
 * 
 */
@Embeddable
public class EmployeeAuthorizationApvPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String employee;

	private String customer;

	private String company;

	public EmployeeAuthorizationApvPK() {
	}
	
	public String getEmployee() {
		return this.employee;
	}
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	public String getCustomer() {
		return this.customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getCompany() {
		return this.company;
	}
	public void setCompany(String company) {
		this.company = company;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EmployeeAuthorizationApvPK)) {
			return false;
		}
		EmployeeAuthorizationApvPK castOther = (EmployeeAuthorizationApvPK)other;
		return 
			this.employee.equals(castOther.employee)
			&& this.customer.equals(castOther.customer)
			&& this.company.equals(castOther.company);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.employee.hashCode();
		hash = hash * prime + this.customer.hashCode();
		hash = hash * prime + this.company.hashCode();
		
		return hash;
	}
}