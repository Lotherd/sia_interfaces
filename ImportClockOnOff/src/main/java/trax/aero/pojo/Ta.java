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
    "COST_CENTRE",
    "MSG_TYPE",
    "STATUS",
    "STAFF_NO",
    "SEQ_NO",
    "CLK_OUT_TIME",
    "CLK_IN_TIME"
})
@Generated("jsonschema2pojo")
public class Ta {

    @JsonProperty("COST_CENTRE")
    private String costCentre;
    @JsonProperty("MSG_TYPE")
    private String msgType;
    @JsonProperty("STATUS")
    private String status;
    @JsonProperty("STAFF_NO")
    private String staffNo;
    @JsonProperty("SEQ_NO")
    private BigDecimal seqNo;
    @JsonProperty("CLK_OUT_TIME")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private String clkOutTime;
    @JsonProperty("CLK_IN_TIME")
   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private String clkInTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("COST_CENTRE")
    public String getCostCentre() {
        return costCentre;
    }

    @JsonProperty("COST_CENTRE")
    public void setCostCentre(String costCentre) {
        this.costCentre = costCentre;
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

    @JsonProperty("STAFF_NO")
    public String getStaffNo() {
        return staffNo;
    }

    @JsonProperty("STAFF_NO")
    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    @JsonProperty("SEQ_NO")
    public BigDecimal getSeqNo() {
        return seqNo;
    }

    @JsonProperty("SEQ_NO")
    public void setSeqNo(BigDecimal seqNo) {
        this.seqNo = seqNo;
    }

    @JsonProperty("CLK_OUT_TIME")
    public String getClkOutTime() {
        return clkOutTime;
    }

    @JsonProperty("CLK_OUT_TIME")
    public void setClkOutTime(String clkOutTime) {
        this.clkOutTime = clkOutTime;
    }
    
    @JsonProperty("CLK_IN_TIME")
    public String getClkInTime() {
        return clkInTime;
    }

    @JsonProperty("CLK_IN_TIME")
    public void setClkInTime(String clkInTime) {
        this.clkInTime = clkInTime;
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


