package trax.aero.pojo;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import trax.aero.pojo.Punch;

public class Punch {
	private String employeeid;
	private String punchtype;
	private java.util.Date punchdatetime;
	
	public Punch() { }
	

	public Punch(Punch copy) 
	{
		this.employeeid = copy.getEmployeeid();
		this.punchtype = copy.getPunchtype();
		this.punchdatetime = copy.getPunchdatetime();
	}

	public String getEmployeeid() {
		return employeeid;
	}

	public void setEmployeeid(String employeeid) {
		this.employeeid = employeeid;
	}

	public String getPunchtype() {
		return punchtype;
	}

	public void setPunchtype(String punchtype) {
		this.punchtype = punchtype;
	}

	public java.util.Date getPunchdatetime() {
		return punchdatetime;
	}

	public void setPunchdatetime(java.util.Date punchdatetime) {
		this.punchdatetime = punchdatetime;
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
        sb.append("\nemployeeid: ").append(this.getEmployeeid());
        sb.append("\npunchtype: ").append(this.getPunchtype());

        DateTime date = null;
        if(this.getPunchdatetime() != null)
        	date = new DateTime(this.getPunchdatetime());
					
        sb.append("\npunchdatetime: ").append(date); 
        
        return sb.toString();	    
	}
}
