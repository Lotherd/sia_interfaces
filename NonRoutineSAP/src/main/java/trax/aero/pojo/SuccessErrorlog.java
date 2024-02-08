package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDOCNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Status_ErrorCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IDOCStatus" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="StatusMessage">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "idocNumber",
    "idocStatus",
    "applicatonMessage"
})
public class SuccessErrorlog {


    @XmlElement(name = "IDOCNumber", required = true)
    protected String idocNumber;

    @XmlElement(name = "IDOCStatus")
    protected String idocStatus;

    
    @XmlElement(name = "ApplicatonMessage")
    protected ArrayList<ApplicatonMessage> applicatonMessage;

    public ArrayList<ApplicatonMessage> getApplicatonMessage() {
        return applicatonMessage;
    }

    public void setApplicatonMessage(ArrayList<ApplicatonMessage> value) {
        this.applicatonMessage = value;
    }
    
    /**
     * Gets the value of the idocNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDOCNumber() {
        return idocNumber;
    }

    /**
     * Sets the value of the idocNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDOCNumber(String value) {
        this.idocNumber = value;
    }

    

    

    /**
     * Gets the value of the idocStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDOCStatus() {
        return idocStatus;
    }

    /**
     * Sets the value of the idocStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDOCStatus(String value) {
        this.idocStatus = value;
    }


}
