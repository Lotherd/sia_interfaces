package trax.aero.pojo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MT_TRAX_RCV_I51_ACK_4073", namespace="http://singaporeair.com/mro/TRAX")
@XmlAccessorType(XmlAccessType.FIELD)
public class MT_TRAX_RCV_I51_ACK_4073
{
	@XmlElement(name = "TopFunctionalLocation")
    private TopFunctionalLocation TopFunctionalLocation;

    public TopFunctionalLocation getTopFunctionalLocation ()
    {
        return TopFunctionalLocation;
    }

    public void setTopFunctionalLocation (TopFunctionalLocation TopFunctionalLocation)
    {
        this.TopFunctionalLocation = TopFunctionalLocation;
    }
}
