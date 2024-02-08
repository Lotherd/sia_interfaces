package trax.aero.pojo;
import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class EffectivityList {

	@XmlElement(name = "Effectivity")
    private ArrayList<String> Effectivity;

	public ArrayList<String> getEffectivity() {
		return Effectivity;
	}

	public void setEffectivity(ArrayList<String> effectivity) {
		this.Effectivity = effectivity;
	}
}
