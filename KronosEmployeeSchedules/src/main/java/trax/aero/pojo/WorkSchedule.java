package trax.aero.pojo;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkSchedule implements Serializable
{
	private static final long serialVersionUID = 1L;
	private java.util.Date startdatetime;
	private java.util.Date enddatetime;
	private ArrayList<Schedule> schedules;
	
		
	public WorkSchedule() {}

	public java.util.Date getStartdatetime() {
		return startdatetime;
	}

	public void setStartdatetime(java.util.Date startdatetime) {
		this.startdatetime = startdatetime;
	}

	public java.util.Date getEnddatetime() {
		return enddatetime;
	}

	public void setEnddatetime(java.util.Date enddatetime) {
		this.enddatetime = enddatetime;
	}

	public ArrayList<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedules = schedules;
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
        sb.append("\nstartdatetime: ").append(this.getStartdatetime());
        sb.append("\nenddatetime: ").append(this.getEnddatetime());
        sb.append("\nschedules: ").append(this.schedulesToString()); 
        
        return sb.toString();	    
	}
	
	/**
	 * Converting Object to Json
	 * 
	 * @return String
	 */	
	public String schedulesToString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n").append("[");
		if(this.schedules!= null)
			for(Schedule item : this.schedules)
				sb.append("\n").append("{" + item.toString() + "\n}");
		
		sb.append("\n").append("]");
        
		return sb.toString();	    
	}
	
}
