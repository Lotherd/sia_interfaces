package trax.aero.pojo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"DATE",
"MSG_TYPE",
"STATUS",
"SEQ_NO"
})
@Generated("jsonschema2pojo")
public class Trax {

	@JsonProperty("DATE")
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private String date;
	@JsonProperty("MSG_TYPE")
	private String msgType;
	@JsonProperty("STATUS")
	private String status;
	@JsonProperty("SEQ_NO")
	private BigDecimal seqNo;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	@JsonProperty("DATE")
	public String getDate() {
	return date;
	}
	
	@JsonProperty("DATE")
	public void setDate(String date) {
	this.date = date;
	}
	
	@JsonProperty("MSG_TYPE")
	public String getMsgType() {
	return msgType;
	}
	
	@JsonProperty("MSG_TYPE")
	public void setMsgType(String msgType) {
	this.msgType = msgType;
	}
	
	@JsonProperty("STATUS")
	public String getStatus() {
	return status;
	}
	
	@JsonProperty("STATUS")
	public void setStatus(String status) {
	this.status = status;
	}
	
	@JsonProperty("SEQ_NO")
	public BigDecimal getSeqNo() {
	return seqNo;
	}
	
	@JsonProperty("SEQ_NO")
	public void setSeqNo(BigDecimal seqNo) {
	this.seqNo = seqNo;
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
