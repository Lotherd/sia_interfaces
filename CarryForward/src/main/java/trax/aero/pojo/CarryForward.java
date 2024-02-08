package trax.aero.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"WO",
"TASK_CARD"
})
@Generated("CarryForward")
public class CarryForward {

	@JsonProperty("WO")
	private String WO;

	@JsonProperty("TASK_CARD")
    private String TASK_CARD;
	
	@JsonProperty("PN")
    private String PN;
	
	@JsonProperty("SN")
    private String SN;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("WO")
    public String getWO ()
    {
        return WO;
    }

    @JsonProperty("WO")
    public void setWO (String WO)
    {
        this.WO = WO;
    }

    @JsonProperty("TASK_CARD")
    public String getTASK_CARD ()
    {
        return TASK_CARD;
    }

    @JsonProperty("TASK_CARD")
    public void setTASK_CARD (String TASK_CARD)
    {
        this.TASK_CARD = TASK_CARD;
    } 
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
    }
	
}
