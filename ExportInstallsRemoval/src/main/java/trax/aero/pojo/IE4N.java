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
"Transaction",
"Transaction_Item",
"transaction",
"transaction_Item"
})
@Generated("IE4N")
public class IE4N {

	@JsonProperty("Transaction")
	private String Transaction;

	@JsonProperty("Transaction_Item")
    private String Transaction_Item;
	
	@JsonProperty("transaction")
	private String trans;

	@JsonProperty("transaction_Item")
    private String trans_Item;
	
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Transaction")
    public String getTransaction ()
    {
        return Transaction;
    }

    @JsonProperty("Transaction")
    public void setTransaction (String Transaction)
    {
        this.Transaction = Transaction;
    }

    @JsonProperty("Transaction_Item")
    public String getTransaction_Item ()
    {
        return Transaction_Item;
    }

    @JsonProperty("Transaction_Item")
    public void setTransaction_Item (String Transaction_Item)
    {
        this.Transaction_Item = Transaction_Item;
    } 
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
    }

    @JsonProperty("transaction")
	public String getTrans() {
		return trans;
	}

    @JsonProperty("transaction")
	public void setTrans(String trans) {
		this.trans = trans;
	}

    @JsonProperty("transaction_Item")
	public String getTrans_Item() {
		return trans_Item;
	}

    @JsonProperty("transaction_Item")
	public void setTrans_Item(String trans_Item) {
		this.trans_Item = trans_Item;
	}
	
}
