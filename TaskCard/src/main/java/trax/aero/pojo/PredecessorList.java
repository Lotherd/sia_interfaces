package trax.aero.pojo;
import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class PredecessorList {

	@XmlElement(name = "TaskCard")
    private ArrayList<String> TaskCard;
	
	public ArrayList<String> getTaskCard() {
		return TaskCard;
	}

	public void setTaskCard(ArrayList<String> taskcard) {
		this.TaskCard = taskcard;
	}
}
