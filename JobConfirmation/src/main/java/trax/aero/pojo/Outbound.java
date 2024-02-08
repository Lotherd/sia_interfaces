package trax.aero.pojo;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="OrderHeader")
@XmlAccessorType(XmlAccessType.FIELD)
public class Outbound {
	
	
	@XmlElement(name = "OrderNumber")
    private String Order_number;
	
	@XmlElement(name = "OperationNumber")
	private String Operation_number;
	
	@XmlElement(name = "WorkCenter")
	private String Work_Center;
	
	@XmlElement(name = "Plant")
	private String Plant;
	
	@XmlElement(name = "PersonnelNumber")
	private String Personnel_number;
		
	@XmlElement(name = "ActualWork")
	private String Actual_work;
	
	@XmlElement(name = "UnitForWork")
	private  String Unit_for_work;
	
	@XmlElement(name = "ActivityTypeForConfirmation")
	private String Activity_type_for_confirmation;
	
	@XmlElement(name = "PostingDate")
	private String Posting_date;
	
	@XmlElement(name = "FinalConfirmation")
	private String Final_confirmation;
	
	@XmlElement(name = "Indicator_No_remaining_work")
	private String Indicator_No_remaining_work;
	
	@XmlElement(name = "Accounting_indicator")
	private String Accounting_indicator;
	
	@XmlElement(name = "ActualStartDate")
	private String Actual_start_date;
	
	@XmlElement(name = "ActualStartTime")
	private String Actual_start_time;
	
	@XmlElement(name = "ActualFinishDate")
	private String Actual_finish_date;
	
	@XmlElement(name = "ActualFinishTime")
	private String Actual_finish_time;
	
	@XmlElement(name = "GeneralFlag")
	private String General_flag;
	
	@XmlElement(name = "Confirmation_text")
	private String Confirmation_text;
	
	@XmlElement(name = "Defect_text")
	private String Defect_text;
	
	@XmlElement(name = "WO_ActualTransaction")
	private String WO_ActualTransaction;
	
	public String getWO_ActualTransaction ()
    {
        return WO_ActualTransaction;
    }

    public void setWO_ActualTransaction (String WO_ActualTransaction)
    {
        this.WO_ActualTransaction = WO_ActualTransaction;
    }
	
	
	
	public String getOrder_number ()
    {
        return Order_number;
    }

    public void setOrder_number (String Order_number)
    {
        this.Order_number = Order_number;
    }

    public String getOperation_number ()
    {
        return Operation_number;
    }

    public void setOperation_number (String Operation_number)
    {
        this.Operation_number = Operation_number;
    }

    public String getWork_Center ()
    {
        return Work_Center;
    }

    public void setWork_Center (String Work_Center)
    {
        this.Work_Center = Work_Center;
    }
    
    public String getPlant ()
    {
        return Plant;
    }

    public void setPlant (String Plant)
    {
        this.Plant = Plant;
    }
    
    public String getPersonnel_number ()
    {
        return Personnel_number;
    }

    public void setPersonnel_number (String Personnel_number)
    {
        this.Personnel_number = Personnel_number;
    }
    
    public String getActual_work ()
    {
        return Actual_work;
    }

    public void setActual_work (String Actual_work)
    {
        this.Actual_work = Actual_work;
    }

    public String getUnit_for_work ()
    {
        return Unit_for_work;
    }

    public void setUnit_for_work (String Unit_for_work)
    {
        this.Unit_for_work = Unit_for_work;
    }

    public String getActivity_type_for_confirmation ()
    {
        return Activity_type_for_confirmation;
    }

    public void setActivity_type_for_confirmation (String Activity_type_for_confirmation)
    {
        this.Activity_type_for_confirmation = Activity_type_for_confirmation;
    }
    
    public String getPosting_date ()
    {
        return Posting_date;
    }

    public void setPosting_date (String Posting_date)
    {
        this.Posting_date = Posting_date;
    }
    
    public String getFinal_confirmation ()
    {
        return Final_confirmation;
    }

    public void setFinal_confirmation (String Final_confirmation)
    {
        this.Final_confirmation = Final_confirmation;
    }
    
    public String getIndicator_No_remaining_work ()
    {
        return Indicator_No_remaining_work;
    }

    public void setIndicator_No_remaining_work (String Indicator_No_remaining_work)
    {
        this.Indicator_No_remaining_work = Indicator_No_remaining_work;
    }
    
    public String getAccounting_indicator ()
    {
        return Accounting_indicator;
    }

    public void setAccounting_indicator (String Accounting_indicator)
    {
        this.Accounting_indicator = Accounting_indicator;
    }
    
    public String getActual_start_date ()
    {
        return Actual_start_date;
    }

    public void setActual_start_date (String Actual_start_date)
    {
        this.Actual_start_date = Actual_start_date;
    }
    
    public String getActual_start_time ()
    {
        return Actual_start_time;
    }

    public void setActual_start_time (String Actual_start_time)
    {
        this.Actual_start_time = Actual_start_time;
    }

    public String getActual_finish_date ()
    {
        return Actual_finish_date;
    }

    public void setActual_finish_date (String Actual_finish_date)
    {
        this.Actual_finish_date = Actual_finish_date;
    }

    public String getActual_finish_time ()
    {
        return Actual_finish_time;
    }

    public void setActual_finish_time (String Actual_finish_time)
    {
        this.Actual_finish_time = Actual_finish_time;
    }

    public String getGeneral_flag ()
    {
        return General_flag;
    }

    public void setGeneral_flag (String General_flag)
    {
        this.General_flag = General_flag;
    }

    public String getConfirmation_text ()
    {
        return Confirmation_text;
    }

    public void setConfirmation_text (String Confirmation_text)
    {
        this.Confirmation_text = Confirmation_text;
    }

    public String getDefect_text ()
    {
        return Defect_text;
    }

    public void setDefect_text (String Defect_text)
    {
        this.Defect_text = Defect_text;
    }

   
}
