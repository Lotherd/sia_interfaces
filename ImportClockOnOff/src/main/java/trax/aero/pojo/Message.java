package trax.aero.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    "TA",
    "TRAX"
})
@Generated("jsonschema2pojo")
public class Message {

    @JsonProperty("TA")
    private Ta ta;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("TA")
    public Ta getTa() {
        return ta;
    }

    @JsonProperty("TA")
    public void setTa(Ta ta) {
        this.ta = ta;
    }
    
    @JsonProperty("TRAX")
    private Trax trax;
    
    @JsonProperty("TRAX")
    public Trax getTrax() {
    return trax;
    }

    @JsonProperty("TRAX")
    public void setTrax(Trax trax) {
    this.trax = trax;
    }
    
    
    public void setResendRequest(Ta TA)
    {
    	this.trax.setMsgType("ResendRequest");
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
    	this.trax.setDate(formatter.format(date));
    	this.trax.setStatus("RR");
    	this.trax.setSeqNo(TA.getSeqNo());
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
