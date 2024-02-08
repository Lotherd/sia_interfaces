package trax.aero.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;


public class STAFF {
	
	@JsonProperty("STAFF")
	 private ArrayList<STAFFRequest> staff;

	 public ArrayList<STAFFRequest> getSTAFF ()
	    {
	        return staff;
	    }

	    public void setSTAFF (ArrayList<STAFFRequest> staff)
	    {
	        this.staff = staff;
	    }

	  
}
