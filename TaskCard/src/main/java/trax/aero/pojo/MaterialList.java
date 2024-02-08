package trax.aero.pojo;
import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialList {

	@XmlElement(name = "PartNumber_Tool")
    private String PartNumber_Tool;
	
	@XmlElement(name = "Quantity")
    private String Quantity;
	
	@XmlElement(name = "Reservation")
    private String Reservation;
	
	@XmlElement(name = "Part_ToolIndicator")
    private String Part_ToolIndicator;

	public String getPartNumber_Tool() {
		return PartNumber_Tool;
	}

	public String getQuantity() {
		return Quantity;
	}

	public String getReservation() {
		return Reservation;
	}

	public String getPart_ToolIndicator() {
		return Part_ToolIndicator;
	}

	public void setPartNumber_Tool(String partNumber_Tool) {
		this.PartNumber_Tool = partNumber_Tool;
	}

	public void setQuantity(String quantity) {
		this.Quantity = quantity;
	}

	public void setReservation(String reservation) {
		this.Reservation = reservation;
	}

	public void setPart_ToolIndicator(String part_ToolIndicator) {
		this.Part_ToolIndicator = part_ToolIndicator;
	}
	
}
