package trax.aero.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Schedule implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String employee;
	private String group;
	private String location;
	private String site;
	private java.util.Date empstartdt;
	private java.util.Date empenddt;
	
	public Schedule() {}
	
	public Schedule(String employee, String group, String location, String site, java.util.Date empstartdt, java.util.Date empenddt) 
	{
		this.employee = employee;
		this.group = group;
		this.location = location;
		this.site = site;
		this.empstartdt = empstartdt;
		this.empenddt = empenddt;
	}
	
	public Schedule(Schedule copy) 
	{
		this.employee = copy.getEmployee();
		this.group = copy.getGroup();
		this.location = copy.getLocation();
		this.site = copy.getSite();
		this.empstartdt = copy.getEmpstartdt();
		this.empenddt = copy.getEmpenddt();
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public java.util.Date getEmpstartdt() {
		return empstartdt;
	}

	public void setEmpstartdt(java.util.Date empstartdt) {
		this.empstartdt = empstartdt;
	}

	public java.util.Date getEmpenddt() {
		return empenddt;
	}

	public void setEmpenddt(java.util.Date empenddt) {
		this.empenddt = empenddt;
	}
	
	/**
	 * Converting Object to Json
	 * 
	 * @return String
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
        sb.append("\ngroup: ").append(this.getGroup() != null ? this.getGroup() : "null");
        sb.append("\nlocation: ").append(this.getLocation() != null ? this.getLocation() : "null");
        sb.append("\nsite: ").append(this.getSite() != null ? this.getSite() : "null"); 
        sb.append("\nempstartdt: ").append(this.getEmpstartdt() != null ? this.getEmpstartdt() : "null"); 
        sb.append("\nempenddt: ").append(this.getEmpenddt() != null ? this.getEmpenddt() : "null"); 
        
        return sb.toString();	    
	}	
}
