package trax.aero.util;

import java.sql.Connection;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import trax.aero.dao.KronosData;
import trax.aero.dao.LocationSiteCapacityDao;
import trax.aero.dao.WorkScheduleDao;
import trax.aero.logger.LogManager;
import trax.aero.pojo.Schedule;
import trax.aero.pojo.WorkSchedule;

public class Worker implements Runnable {

	
	Logger logger = LogManager.getLogger("Kronos_I32I33I34");
	public Worker() {
		
	}
	
	private Connection con;
	
	private Schedule item =null;
	
	private Boolean work = true;
	
	private WorkSchedule sched = null;
	
	public void run() 
	{	
		if(work) {
		
			WorkScheduleDao wsDao = new WorkScheduleDao();						
			LocationSiteCapacityDao lscDao = new LocationSiteCapacityDao();
			logger.info(" group: " + item.getGroup()); 
			
				
				try {
					wsDao.addEmployeeScheduleGroup(item);	
					lscDao.addLocationSiteCapacity(item, sched.getStartdatetime(), sched.getEnddatetime());
				}
				catch(Exception e){
					e.printStackTrace();
					KronosData.data += e.getMessage();
				}finally {
					try {
						if(lscDao.getCon() != null && !lscDao.getCon().isClosed())
							lscDao.getCon().close();
						
						if(wsDao.getCon() != null && !wsDao.getCon().isClosed())
							wsDao.getCon().close();
					}
					catch(Exception e){
						logger.severe(e.toString());
					}
				}
			
		}else {
			
			
			LocationSiteCapacityDao lscDao = new LocationSiteCapacityDao();
			try {
			
			lscDao.deleteRowsByEmployee(item.getEmployee(), item.getGroup(), new DateTime(item.getEmpstartdt()), new DateTime(item.getEmpenddt()));
			}
			catch(Exception e){
				e.printStackTrace();
			}finally {
				try {
					if(lscDao.getCon() != null && !lscDao.getCon().isClosed())
						lscDao.getCon().close();
					
				}
				catch(Exception e){
					logger.severe(e.toString());
				}
			}
		}
			
		
		
	}

	

	public Boolean getWork() {
		return work;
	}

	public void setWork(Boolean work) {
		this.work = work;
	}



	public Schedule getInput() {
		return item;
	}



	public void setInput(Schedule input) {
		this.item = input;
	}



	public WorkSchedule getSched() {
		return sched;
	}



	public void setSched(WorkSchedule sched) {
		this.sched = sched;
	}

}
