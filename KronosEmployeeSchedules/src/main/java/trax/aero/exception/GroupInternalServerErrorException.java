package trax.aero.exception;

import trax.aero.pojo.WorkSchedule;

public class GroupInternalServerErrorException extends RuntimeException 
{
	private static final long serialVersionUID = 7718828512143293558L;
	
	private WorkSchedule wsched;
	private int error;
	

	public GroupInternalServerErrorException() {
		super();
	}
	
	public GroupInternalServerErrorException(String exception, WorkSchedule ws) 
	{
		super(exception);
		this.setWsched(ws);
	}
	
	public GroupInternalServerErrorException(String exception, WorkSchedule ws, String error) 
	{
		super(exception);
		this.setWsched(ws);
		this.setError(Integer.parseInt(error));
	}

	public GroupInternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) 
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GroupInternalServerErrorException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	public GroupInternalServerErrorException(String message) 
	{
		super(message);
	}

	public GroupInternalServerErrorException(Throwable cause) 
	{
		super(cause);
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public WorkSchedule getWsched() {
		return wsched;
	}

	public void setWsched(WorkSchedule wsched) {
		this.wsched = wsched;
	}
	
}
