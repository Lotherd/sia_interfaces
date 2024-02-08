package trax.aero.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


public class STAFFMasterResponse {
	
	@JsonProperty("STAFF")
	private ArrayList<STAFFResponse> staff;

	
	
    public ArrayList<STAFFResponse> getSTAFF ()
    {
        return staff;
    }

    public void setSTAFF (ArrayList<STAFFResponse> staff)
    {
        this.staff = staff;
    }

    
   
}
