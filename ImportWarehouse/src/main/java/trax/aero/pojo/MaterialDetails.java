package trax.aero.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialDetails {

	@XmlElement(name = "LOCATION")
	private String LOCATION;

	@XmlElement(name = "WEIGHTUNIT")
    private String WEIGHTUNIT;

	@XmlElement(name = "PNDESCRIPTION")
    private String PNDESCRIPTION;

	@XmlElement(name = "QTY")
    private String QTY;

	@XmlElement(name = "Remarks")
    private String NotesText;

	@XmlElement(name = "SN")
    private String SN;

	@XmlElement(name = "PN")
    private String PN;
	
	@XmlElement(name = "MSGNBR")
    private String MSGNBR;

    public String getLOCATION ()
    {
        return LOCATION;
    }

    public void setLOCATION (String LOCATION)
    {
        this.LOCATION = LOCATION;
    }

    public String getWEIGHTUNIT ()
    {
        return WEIGHTUNIT;
    }

    public void setWEIGHTUNIT (String WEIGHTUNIT)
    {
        this.WEIGHTUNIT = WEIGHTUNIT;
    }

    public String getPNDESCRIPTION ()
    {
        return PNDESCRIPTION;
    }

    public void setPNDESCRIPTION (String PNDESCRIPTION)
    {
        this.PNDESCRIPTION = PNDESCRIPTION;
    }

    public String getQTY ()
    {
        return QTY;
    }

    public void setQTY (String QTY)
    {
        this.QTY = QTY;
    }

    public String getNotesText ()
    {
        return NotesText;
    }

    public void setNotesText (String NotesText)
    {
        this.NotesText = NotesText;
    }

    public String getSN ()
    {
        return SN;
    }

    public void setSN (String SN)
    {
        this.SN = SN;
    }

    public String getPN ()
    {
        return PN;
    }

    public void setPN (String PN)
    {
        this.PN = PN;
    }

	public String getMSGNBR() {
		return MSGNBR;
	}

	public void setMSGNBR(String mSGNBR) {
		MSGNBR = mSGNBR;
	}
	
}
