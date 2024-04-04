package trax.aero.pojo;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Order_Header")
@XmlAccessorType(XmlAccessType.FIELD)
public class Inbound {
	
	
	@XmlElement(name = "Order_number")
    private String Order_number;
	
	@XmlElement(name = "Operation_number")
	private String Operation_number;
	
	@XmlElement(name = "Work_Center")
	private String Work_Center;
	
	@XmlElement(name = "Plant")
	private String Plant;
	
	@XmlElement(name = "Personnel_number")
	private String Personnel_number;
		
	@XmlElement(name = "Actual_work")
	private String Actual_work;
	
	@XmlElement(name = "Unit_for_work")
	private  String Unit_for_work;
	
	@XmlElement(name = "Activity_type_for_confirmation")
	private String Activity_type_for_confirmation;
	
	@XmlElement(name = "Posting_date")
	private String Posting_date;
	
	@XmlElement(name = "Final_confirmation")
	private String Final_confirmation;
	
	@XmlElement(name = "Indicator_No_remaining_work")
	private String Indicator_No_remaining_work;
	
	@XmlElement(name = "Accounting_indicator")
	private String Accounting_indicator;
	
	@XmlElement(name = "Actual_start_date")
	private String Actual_start_date;
	
	@XmlElement(name = "Actual_start_time")
	private String Actual_start_time;
	
	@XmlElement(name = "Actual_finish_date")
	private String Actual_finish_date;
	
	@XmlElement(name = "Actual_finish_time")
	private String Actual_finish_time;
	
	@XmlElement(name = "General_flag")
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
    
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(obj instanceof Inbound)
        {
        	Inbound temp = (Inbound) obj;
            if(this.Order_number.equals(temp.Order_number) &&
            		this.Operation_number.equals(temp.Operation_number) &&	
            		this.Work_Center.equals(temp.Work_Center) &&
            		this.Plant.equals(temp.Plant) &&
            		this.Personnel_number.equals(temp.Personnel_number) &&
            		this.Actual_work.equals(temp.Actual_work) &&
            		this.Unit_for_work.equals(temp.Unit_for_work) &&
            		this.Activity_type_for_confirmation.equals(temp.Activity_type_for_confirmation) &&
            		this.Posting_date.equals(temp.Posting_date) &&
            		this.Final_confirmation.equals(temp.Final_confirmation) &&
            		this.Indicator_No_remaining_work.equals(temp.Indicator_No_remaining_work) &&
            		this.Accounting_indicator.equals(temp.Accounting_indicator) &&
            		this.Actual_start_date.equals(temp.Actual_start_date) &&
            		this.Actual_start_time.equals(temp.Actual_start_time) &&
            		this.Actual_finish_date.equals(temp.Actual_finish_date) &&
            		this.Actual_finish_time.equals(temp.Actual_finish_time) &&
            		this.General_flag.equals(temp.General_flag) &&
            		this.Confirmation_text.equals(temp.Confirmation_text) &&
            		this.Defect_text.equals(temp.Defect_text) &&
            		this.WO_ActualTransaction.equals(temp.WO_ActualTransaction)
            		)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        
        return (this.Order_number.hashCode() +
        		this.Operation_number.hashCode() +	
        		this.Work_Center.hashCode() +
        		this.Plant.hashCode() +
        		this.Personnel_number.hashCode() +
        		this.Actual_work.hashCode() +
        		this.Unit_for_work.hashCode() +
        		this.Activity_type_for_confirmation.hashCode() +
        		this.Posting_date.hashCode() +
        		this.Final_confirmation.hashCode() +
        		this.Indicator_No_remaining_work.hashCode() +
        		this.Accounting_indicator.hashCode() +
        		this.Actual_start_date.hashCode() +
        		this.Actual_start_time.hashCode() +
        		this.Actual_finish_date.hashCode() +
        		this.Actual_finish_time.hashCode() +
        		this.General_flag.hashCode() +
        		this.Confirmation_text.hashCode() +
        		this.Defect_text.hashCode() +
        		this.WO_ActualTransaction.hashCode());        
    }

   
}
