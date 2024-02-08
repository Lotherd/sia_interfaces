package trax.aero.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.opencsv.CSVWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import trax.aero.controller.JobConfirmationController;
import trax.aero.data.JobConfirmationData;

import trax.aero.logger.LogManager;
import trax.aero.pojo.Inbound;
import trax.aero.pojo.Job;

import trax.aero.pojo.MasterInbound;
import trax.aero.pojo.MasterOutbound;


public class Run implements Runnable {
	
	Logger logger = LogManager.getLogger("JobConfirmation_I39I40");
	//Variables
	JobConfirmationData data = null;
	//final String ID = System.getProperty("JobConfirmation_ID");
	//final String Password = System.getProperty("JobConfirmation_Password");
	final String url = System.getProperty("JobConfirmation_URL");
	
	public Run() {
		data = new JobConfirmationData();
	}
	
	
	private void process() {
		
		JobPoster poster = new JobPoster();
		
		ArrayList<MasterInbound> MasterInbounds = new ArrayList<MasterInbound>();
		
		String exceuted = "OK";
		ArrayList<Job> jobs = new ArrayList<Job>();
		try 
		{
							
			// loop
			MasterInbounds = data.getJobConfirmation();
			
			
			if(MasterInbounds != null && !MasterInbounds.isEmpty()) {	
				for(MasterInbound MasterInbound : MasterInbounds) {
				
					boolean success = false;
					
					JAXBContext jc = JAXBContext.newInstance(MasterInbound.class);
					Marshaller marshaller = jc.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					StringWriter sw = new StringWriter();
					marshaller.marshal(MasterInbound, sw);
					
					logger.info("Ouput: " + sw.toString());
					
					
					success = poster.postJob(MasterInbound, url);
					jobs = data.getInformationFromJob(MasterInbound);
					
					if(!success)
					{
						logger.severe("Unable to send data" + " to URL "+ url);
						JobConfirmationController.addError("Unable to send data" + " to URL "+ url);
					}else {
						if(System.getProperty("JobConfirmation_outFiles") !=null) {
							insertFile(jobs);
						}
					}
				}
				if(!JobConfirmationController.getError().isEmpty()) {
					 throw new Exception("Issue found");
				}
			}
		}
		catch(Throwable e)
		{
			logger.severe(e.toString());
			e.printStackTrace();
			JobConfirmationController.addError(e.toString());
			JobConfirmationController.sendEmail(jobs);
		}
	}
	
	public void run() 
	{
		try {
			if(data.lockAvailable("I39I40"))
			{
				data.lockTable("I39I40");
				process();
				data.unlockTable("I39I40");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertFile(ArrayList<Job> jobs)
	{
		try {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime currentDateTime = LocalDateTime.now();
        List<String[]> in = new ArrayList<String[]>();
        String[] header = {"Order_number", "Operation_number", "WO_ActualTransaction",
        "WO", "TASK_CARD","ITEM", "Actual_start_date","Actual_start_time","Actual_finish_date",
        "Actual_finish_time","Posting_date","Actual_work"};
        
        
		File compFolder = new File(System.getProperty("JobConfirmation_outFiles"));
		if (!compFolder.isDirectory())
		compFolder.mkdir();
		File todayFolder = new File(System.getProperty("JobConfirmation_outFiles")+ File.separator + dtf.format(currentDateTime));
		if (!todayFolder.isDirectory())
		todayFolder.mkdir();
	
		File output = new File(todayFolder + File.separator
		+ "SUCCESS" + "_"+  dtf.format(currentDateTime)+".csv");
		
		in.add(header);
		   
        for(Job i : jobs) {
        	String[]arr = {i.getOrder_number(),i.getOperation_number(), i.getWo_actual_transaction(),
        	i.getWo(),i.getTask_Card(),i.getTask_Card_Item(),i.getActual_start_date(),
        	i.getActual_start_time(),i.getActual_finish_date(),i.getActual_finish_time(),
        	i.getPosting_date(),i.getActual_work()};
        	
        	in.add(arr);
        }
		
        FileWriter outputfile = new FileWriter(output,true);
  

        CSVWriter writer = new CSVWriter(outputfile, ',',
                                         CSVWriter.NO_QUOTE_CHARACTER,
                                         CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                         CSVWriter.DEFAULT_LINE_END);
		
        writer.writeAll(in);
        writer.close();
        outputfile.close();
        
        logger.info("DONE processing file " + output.getName());
		}catch(Exception e) {
			 logger.info(e.toString());
		}
	}
}