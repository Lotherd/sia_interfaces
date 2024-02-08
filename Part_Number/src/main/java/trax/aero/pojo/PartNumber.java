package trax.aero.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlAccessType;



@XmlAccessorType(XmlAccessType.FIELD)
public class PartNumber 
{
	
	@XmlElement(name = "PartNumber")
	private String pn;
	
	@XmlElement(name = "Description")
	private String description;
	
	@XmlElement(name = "PN_category")
	private String pnCategory;
	
	@XmlElement(name = "UOM")
	private String StockUOM;
	
	@XmlElement(name = "ATA")
	private String ata;
	
	@XmlElement(name = "Effectivity")
	private String effectivity;
	
	@XmlElement(name = "ToolControl")
	private String toolControl;
	
	@XmlElement(name = "ToolCalibration")
	private String toolCalibration;
	
	@XmlElement(name = "ToolLife")
	private String toolLife;
	
	@XmlElement(name = "DeletionFlag")
	private String deletionFlag;
	
	//@XmlElementWrapper
	@XmlElement(name = "InterchangableList")
	private InterList InterList;
	

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPnCategory() {
		return pnCategory;
	}

	public void setPnCategory(String pnCategory) {
		this.pnCategory = pnCategory;
	}

	public String getStockUOM() {
		return StockUOM;
	}

	public void setStockUOM(String stockUOM) {
		StockUOM = stockUOM;
	}

	public String getAta() {
		return ata;
	}

	public void setAta(String ata) {
		this.ata = ata;
	}

	public String getEffectivity() {
		return effectivity;
	}

	public void setEffectivity(String effectivity) {
		this.effectivity = effectivity;
	}

	public String getToolControl() {
		return toolControl;
	}

	public void setToolControl(String toolControl) {
		this.toolControl = toolControl;
	}

	public String getToolCalibration() {
		return toolCalibration;
	}

	public void setToolCalibration(String toolCalibration) {
		this.toolCalibration = toolCalibration;
	}

	public String getToolLife() {
		return toolLife;
	}

	public void setToolLife(String toolLife) {
		this.toolLife = toolLife;
	}

	public InterList getInterList() {
		return InterList;
	}

	public void setInterList(InterList InterList) {
		this.InterList = InterList;
	}

	public String getDeletionFlag() {
		return deletionFlag;
	}

	public void setDeletionFlag(String deletionFlag) {
		this.deletionFlag = deletionFlag;
	}
	
	
	
	
}
