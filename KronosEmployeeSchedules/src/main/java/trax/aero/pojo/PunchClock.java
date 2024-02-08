package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PunchClock 
{
	private ArrayList<PunchClockItem> punchClockItems;
	
	public PunchClock() {}
	
	public ArrayList<PunchClockItem> getPunchClockItems() {
		return punchClockItems;
	}

	public void setPunchClockItems(ArrayList<PunchClockItem> schedules) {
		this.punchClockItems = schedules;
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
        sb.append("\npunchClockItems: ").append(this.punchClockItemsToString());
        
        return sb.toString();	    
	}
	
	/**
	 * Converting Object to Json
	 * 
	 * @return String
	 */	
	public String punchClockItemsToString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n").append("[");
		
		if(this.punchClockItems != null && !this.punchClockItems.isEmpty())
			for(PunchClockItem item : this.punchClockItems)
				sb.append("\n").append("{" + item.toString() + "\n}");
		
		sb.append("\n").append("]");
        
		return sb.toString();	    
	}
	
}
