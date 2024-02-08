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
 *         &lt;element name="OrderNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="12"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PartNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="40"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SerialNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="18"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="StatusCategory" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FabricationRadioButton" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CustomerLateRequestIndicator" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Priority" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PersonResponsible" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="RevisionNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ParentOrderNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="12"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Zone" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TaskDescription">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="40"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DefectText" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="40"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DateRequired">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TRAXNonRoutineNumber">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="35"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TRAXWO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
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
    "orderNumber",
    "partNumber",
    "serialNumber",
    "statusCategory",
    "fabricationRadioButton",
    "customerLateRequestIndicator",
    "priority",
    "personResponsible",
    "revisionNumber",
    "parentOrderNumber",
    "zone",
    "quantity_Wtihdrawn",
    "taskDescription",
    "defectText",
    "dateRequired",
    "traxNonRoutineNumber",
    "traxwo",
    "operation",
    "orderLongText"    
})
public class Order {


    @XmlElement(name = "OrderNumber")
    protected String orderNumber;
    @XmlElement(name = "PartNumber")
    protected String partNumber;
    @XmlElement(name = "SerialNumber")
    protected String serialNumber;
    @XmlElement(name = "StatusCategory")
    protected String statusCategory;
    @XmlElement(name = "FabricationRadioButton")
    protected String fabricationRadioButton;
    @XmlElement(name = "CustomerLateRequestIndicator")
    protected String customerLateRequestIndicator;
    @XmlElement(name = "Priority")
    protected String priority;
    @XmlElement(name = "PersonResponsible")
    protected String personResponsible;
    @XmlElement(name = "RevisionNumber", required = true)
    protected String revisionNumber;
    @XmlElement(name = "ParentOrderNumber")
    protected String parentOrderNumber;
    @XmlElement(name = "Zone")
    protected String zone;
    @XmlElement(name = "TaskDescription", required = true)
    protected String taskDescription;
    @XmlElement(name = "DefectText")
    protected String defectText;
    @XmlElement(name = "DateRequired", required = true)
    protected String dateRequired;
    @XmlElement(name = "TRAXNonRoutineNumber", required = true)
    protected String traxNonRoutineNumber;
    @XmlElement(name = "TRAXWO", required = true)
    protected String traxwo;
    
    @XmlElement(name = "Quantity_Wtihdrawn")
    private String quantity_Wtihdrawn;

    @XmlElement(name = "OrderLongText")
    protected List<OrderLongText> orderLongText;

    @XmlElement(name = "Operation")
    protected List<Operation> operation;
    
    public List<OrderLongText> getOrderLongText() {
        return orderLongText;
    }
    
    public void setOrderLongText(List<OrderLongText> value) {
        this.orderLongText = value;
    }
    
    /**
     * Gets the value of the orderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the value of the orderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNumber(String value) {
        this.orderNumber = value;
    }

    /**
     * Gets the value of the partNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartNumber() {
        return partNumber;
    }

    /**
     * Sets the value of the partNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartNumber(String value) {
        this.partNumber = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

    /**
     * Gets the value of the statusCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusCategory() {
        return statusCategory;
    }

    /**
     * Sets the value of the statusCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusCategory(String value) {
        this.statusCategory = value;
    }

    /**
     * Gets the value of the fabricationRadioButton property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFabricationRadioButton() {
        return fabricationRadioButton;
    }

    /**
     * Sets the value of the fabricationRadioButton property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFabricationRadioButton(String value) {
        this.fabricationRadioButton = value;
    }

    /**
     * Gets the value of the customerLateRequestIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerLateRequestIndicator() {
        return customerLateRequestIndicator;
    }

    /**
     * Sets the value of the customerLateRequestIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerLateRequestIndicator(String value) {
        this.customerLateRequestIndicator = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriority(String value) {
        this.priority = value;
    }

    /**
     * Gets the value of the personResponsible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonResponsible() {
        return personResponsible;
    }

    /**
     * Sets the value of the personResponsible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonResponsible(String value) {
        this.personResponsible = value;
    }

    /**
     * Gets the value of the revisionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * Sets the value of the revisionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevisionNumber(String value) {
        this.revisionNumber = value;
    }

    /**
     * Gets the value of the parentOrderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentOrderNumber() {
        return parentOrderNumber;
    }

    /**
     * Sets the value of the parentOrderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentOrderNumber(String value) {
        this.parentOrderNumber = value;
    }

    /**
     * Gets the value of the zone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZone() {
        return zone;
    }

    /**
     * Sets the value of the zone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZone(String value) {
        this.zone = value;
    }

    /**
     * Gets the value of the taskDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * Sets the value of the taskDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskDescription(String value) {
        this.taskDescription = value;
    }

    /**
     * Gets the value of the defectText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefectText() {
        return defectText;
    }

    /**
     * Sets the value of the defectText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefectText(String value) {
        this.defectText = value;
    }

    /**
     * Gets the value of the dateRequired property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRequired() {
        return dateRequired;
    }

    /**
     * Sets the value of the dateRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRequired(String value) {
        this.dateRequired = value;
    }

    /**
     * Gets the value of the traxNonRoutineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRAXNonRoutineNumber() {
        return traxNonRoutineNumber;
    }

    /**
     * Sets the value of the traxNonRoutineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRAXNonRoutineNumber(String value) {
        this.traxNonRoutineNumber = value;
    }

    /**
     * Gets the value of the traxwo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRAXWO() {
        return traxwo;
    }

    /**
     * Sets the value of the traxwo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRAXWO(String value) {
        this.traxwo = value;
    }

    public List<Operation> getOperation() {
        if (operation == null) {
            operation = new ArrayList<Operation>();
        }
        return this.operation;
    }
    
    
    public void setOperation(List<Operation> operation) {
    	this.operation = operation;

    }

	public String getQuantity_Wtihdrawn() {
		return quantity_Wtihdrawn;
	}

	public void setQuantity_Wtihdrawn(String quantity_Wtihdrawn) {
		this.quantity_Wtihdrawn = quantity_Wtihdrawn;
	}


}
