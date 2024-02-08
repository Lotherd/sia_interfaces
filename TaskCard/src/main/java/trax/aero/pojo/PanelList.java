package trax.aero.pojo;
import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class PanelList {

	@XmlElement(name = "Panel")
    private ArrayList<String> Panel;
	
	public ArrayList<String> getPanel() {
		return Panel;
	}

	public void setPanel(ArrayList<String> panel) {
		this.Panel = panel;
	}
}
