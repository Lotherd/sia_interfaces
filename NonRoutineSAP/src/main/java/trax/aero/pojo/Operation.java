package trax.aero.pojo;

import java.util.ArrayList;
import java.util.List;

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
 *         &lt;element name="OperationNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ManHours" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="LongText" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="999"/>
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
 *         &lt;element name="Operation_ActivityNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="WorkCenter" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Plant" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ControlKey" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OperationShortText" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="40"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="WorkInvolvedInTheActivity" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="9"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="UnitForWork" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Component" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MaterialNumber">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="40"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="StorageLocation" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Plant" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Quantity">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="13"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Batch" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="10"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="ReservationNumber" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="10"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="ReservationItem" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
    "operationNumber",
    "manHours",
    "longText",
    "operationActivityNumber",
    "workCenter",
    "plant",
    "controlKey",
    "operationShortText",
    "workInvolvedInTheActivity",
    "unitForWork",
    "component"
})
public class Operation {


    @XmlElement(name = "OperationNumber")
    protected String operationNumber;
    @XmlElement(name = "ManHours")
    protected String manHours;
    @XmlElement(name = "LongText")
    protected String longText;
    
    @XmlElement(name = "Operation_ActivityNumber")
    protected String operationActivityNumber;
    @XmlElement(name = "WorkCenter")
    protected String workCenter;
    @XmlElement(name = "Plant")
    protected String plant;
    @XmlElement(name = "ControlKey")
    protected String controlKey;
    @XmlElement(name = "OperationShortText")
    protected String operationShortText;
    @XmlElement(name = "WorkInvolvedInTheActivity")
    protected String workInvolvedInTheActivity;
    @XmlElement(name = "UnitForWork")
    protected String unitForWork;
    @XmlElement(name = "Component")
    protected List< Component> component;

    /**
     * Gets the value of the operationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationNumber() {
        return operationNumber;
    }

    /**
     * Sets the value of the operationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationNumber(String value) {
        this.operationNumber = value;
    }

    /**
     * Gets the value of the manHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManHours() {
        return manHours;
    }

    /**
     * Sets the value of the manHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManHours(String value) {
        this.manHours = value;
    }

    /**
     * Gets the value of the longText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongText() {
        return longText;
    }

    /**
     * Sets the value of the longText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongText(String value) {
        this.longText = value;
    }
    
    /**
     * Gets the value of the operationActivityNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationActivityNumber() {
        return operationActivityNumber;
    }

    /**
     * Sets the value of the operationActivityNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationActivityNumber(String value) {
        this.operationActivityNumber = value;
    }

    /**
     * Gets the value of the workCenter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkCenter() {
        return workCenter;
    }

    /**
     * Sets the value of the workCenter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkCenter(String value) {
        this.workCenter = value;
    }

    /**
     * Gets the value of the plant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlant() {
        return plant;
    }

    /**
     * Sets the value of the plant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlant(String value) {
        this.plant = value;
    }

    /**
     * Gets the value of the controlKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControlKey() {
        return controlKey;
    }

    /**
     * Sets the value of the controlKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControlKey(String value) {
        this.controlKey = value;
    }

    /**
     * Gets the value of the operationShortText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationShortText() {
        return operationShortText;
    }

    /**
     * Sets the value of the operationShortText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationShortText(String value) {
        this.operationShortText = value;
    }

    /**
     * Gets the value of the workInvolvedInTheActivity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkInvolvedInTheActivity() {
        return workInvolvedInTheActivity;
    }

    /**
     * Sets the value of the workInvolvedInTheActivity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkInvolvedInTheActivity(String value) {
        this.workInvolvedInTheActivity = value;
    }

    /**
     * Gets the value of the unitForWork property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitForWork() {
        return unitForWork;
    }

    /**
     * Sets the value of the unitForWork property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitForWork(String value) {
        this.unitForWork = value;
    }

    /**
     * Gets the value of the component property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the component property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link  Component }
     * 
     * 
     */
    public List< Component> getComponent() {
        if (component == null) {
            component = new ArrayList< Component>();
        }
        return this.component;
    }


}
