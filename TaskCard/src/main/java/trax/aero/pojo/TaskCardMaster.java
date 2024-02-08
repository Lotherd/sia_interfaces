package trax.aero.pojo;
import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlRootElement(name="MT_RCV_TRAX_I18_4059", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskCardMaster 
{
	@XmlElement(name = "TaskCards")
	ArrayList<TaskCards> TaskCards;

	public ArrayList<TaskCards> getTaskCards() {
		return TaskCards;
	}

	public void setTaskCards(ArrayList<TaskCards> taskCards) {
		this.TaskCards = taskCards;
	}
	
}
