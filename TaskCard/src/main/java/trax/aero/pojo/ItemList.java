package trax.aero.pojo;
import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ItemList {

	@XmlElement(name = "ItemNumber")
    private String ItemNumber;
	
	@XmlElement(name = "ItemText")
    private String ItemText;

	@XmlElement(name = "Mec_sign_Skill")
    private String Mec_sign_Skill;
	
	@XmlElement(name = "Mec_hrs")
    private String Mec_hrs;
	
	@XmlElement(name = "Mec_men")
    private String Mec_men;
	
	@XmlElement(name = "Insp_sign_Skill")
    private String Insp_sign_Skill;
	
	@XmlElement(name = "Insp_hrs")
    private String Insp_hrs;
	
	@XmlElement(name = "Insp_men")
    private String Insp_men;
	
	@XmlElement(name = "EffectivityList")
    ArrayList<EffectivityList> EffectivityList;
	
	@XmlElement(name = "MaterialList")
    ArrayList<MaterialList> MaterialList;

	public String getItemNumber() {
		return ItemNumber;
	}

	public String getMec_sign_Skill() {
		return Mec_sign_Skill;
	}

	public String getMec_hrs() {
		return Mec_hrs;
	}

	public String getMec_men() {
		return Mec_men;
	}

	public String getInsp_sign_Skill() {
		return Insp_sign_Skill;
	}

	public String getInsp_hrs() {
		return Insp_hrs;
	}

	public String getInsp_men() {
		return Insp_men;
	}

	public ArrayList<EffectivityList> getEffectivityList() {
		return EffectivityList;
	}

	public ArrayList<MaterialList> getMaterialList() {
		return MaterialList;
	}

	public void setItemNumber(String itemNumber) {
		this.ItemNumber = itemNumber;
	}

	public void setMec_sign_Skill(String mec_sign_Skill) {
		this.Mec_sign_Skill = mec_sign_Skill;
	}

	public void setMec_hrs(String mec_hrs) {
		this.Mec_hrs = mec_hrs;
	}

	public void setMec_men(String mec_men) {
		this.Mec_men = mec_men;
	}

	public void setInsp_sign_Skill(String insp_sign_Skill) {
		this.Insp_sign_Skill = insp_sign_Skill;
	}

	public void setInsp_hrs(String insp_hrs) {
		this.Insp_hrs = insp_hrs;
	}

	public void setInsp_men(String insp_men) {
		this.Insp_men = insp_men;
	}

	public void setEffectivityList(ArrayList<EffectivityList> effectivityList) {
		this.EffectivityList = effectivityList;
	}

	public void setMaterialList(ArrayList<MaterialList> materialList) {
		this.MaterialList = materialList;
	}
	
	public String getItemText() {
		return ItemText;
	}

	public void setItemText(String itemText) {
		this.ItemText = itemText;
	}
	
}
