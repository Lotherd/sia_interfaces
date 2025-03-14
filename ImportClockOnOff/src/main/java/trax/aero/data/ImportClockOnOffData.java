package trax.aero.data;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import trax.aero.utils.Validation;

import trax.aero.utils.ErrorType;
import trax.aero.utils.Poster;
import trax.aero.controller.ImportClockOnOffController;

import trax.aero.exception.CustomizeHandledException;
import trax.aero.logger.LogManager;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.Message;

import trax.aero.pojo.Punch;
import trax.aero.pojo.Import;
import trax.aero.pojo.Item;
import trax.aero.pojo.Ta;
import trax.aero.pojo.Task;
import trax.aero.pojo.Trax;
import trax.aero.utils.AESencrp;
import trax.aero.utils.DataSourceClient;



/*
select 
wa.wo,
wa.task_card
from 
wo_actuals wa
where
wa.employee = ?

 
update wo_task_card wtc
set 
wtc.status = 'STOP'
where 
wtc.status = 'INPROGRESS' and
wtc.wo = ? and
wtc.task_card ? 
 
 
 */


public class ImportClockOnOffData {

			EntityManagerFactory factory;
			EntityManager em;
			String exceuted;
			Logger logger = LogManager.getLogger("ImportClockOnOff_I29");
			//public InterfaceLockMaster lock;
			
			private Connection con;
			//final String ID = System.getProperty("ImportClockOnOff_ID");
			//final String Password = System.getProperty("ImportClockOnOff_Password");
			final String url = System.getProperty("ImportClockOnOff_URL");
			
			
			public ImportClockOnOffData()
			{
				try 
				{
					if(this.con == null || this.con.isClosed())
					{
						this.con = DataSourceClient.getConnection();
						logger.info("The connection was stablished successfully with status: " + String.valueOf(!this.con.isClosed()));
					}			
				} 
				catch (SQLException e) 
				{
					logger.severe("An error occured getting the status of the connection");
					ImportClockOnOffController.addError(e.getMessage());
					
				}
				catch (CustomizeHandledException e) {
					
					ImportClockOnOffController.addError(e.getMessage());
					
				} catch (Exception e) {
					
					ImportClockOnOffController.addError(e.getMessage());
					
				}
				
				factory = Persistence.createEntityManagerFactory("TraxStandaloneDS");
				em = factory.createEntityManager();
			}
			
			public Connection getCon() {
				return con;
			}
	
			public String clock(Import punchlock, String queueUrlTo) throws Exception
			{
				//setting up variables
				exceuted = "OK";
				boolean isOkay = true;
				boolean success = true;
				
				
				logger.info("starting");

				try 
				{
					Punch punch = new Punch();
					try {
						
						Validation.throwValidatePunches(punchlock);
					}
					catch (Exception e)
					{
						ImportClockOnOffController.addError(e.getMessage());
						
						exceuted = e.getMessage();
						logger.severe(exceuted);
						if(exceuted.contains("Employee does not exist")) {
							return exceuted; 
						}
						
						isOkay = false;
					}

					
					if(isOkay) {
						try {
							punch = convertTAToPunch(punchlock.getMessage().getTa());
							addClockInClockOut(punch);
							
							if(punch.getPunchtype().equalsIgnoreCase("OUT")){
								changeTaskCardStatus(punchlock.getMessage().getTa());
							}
							
						}catch (Exception e) {
								ImportClockOnOffController.addError(e.getMessage());
								e.printStackTrace();
								exceuted = e.getMessage();
								logger.severe(exceuted);
								isOkay = false;
							}
					}
					
					if(!isOkay) {
						
						Import resend = new Import();
						Message message = new Message();
						Trax trax = new Trax();
						
						
						resend.setMessage(message);
						resend.getMessage().setTrax(trax);
						resend.getMessage().setResendRequest(punchlock.getMessage().getTa());
						
						
						success = sendResend(resend,queueUrlTo );
						
						
						if(!success)
						{
							exceuted = "Unable to send Punch with Seq No: " + punchlock.getMessage().getTa().getSeqNo().toString() + " to SQS " + queueUrlTo;
							logger.severe(exceuted);
							ImportClockOnOffController.addError(exceuted); 
						}else {
							logger.info("Resend status: " + String.valueOf(success) + " Seq No: " + punchlock.getMessage().getTa().getSeqNo().toString());
						}
					}
				}
				catch (Exception e) 
		        {
					ImportClockOnOffController.addError(e.getMessage()); 
		            exceuted = e.getMessage();
		           
		            logger.severe(exceuted);
				}finally {
					if(!con.isClosed())
						con.close();
				}
				return exceuted;
			}
			
			
			private boolean sendResend(Import resend, String queueUrlTo) {
				
				ObjectMapper Obj = new ObjectMapper();
				String json;
				try {
					json = Obj.writeValueAsString(resend);
				} catch (JsonProcessingException e) {
					json = "JsonProcessingException";
				}
				
				logger.info("Resend Body: " + json);
				
				try {
					SqsClient sqsClient = SqsClient.builder().build();
					
					SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder().queueUrl(queueUrlTo)
							.entries(SendMessageBatchRequestEntry.builder().id(resend.getMessage().getTrax().getSeqNo().toString()).messageBody(json).build())
				            .build();
				    sqsClient.sendMessageBatch(sendMessageBatchRequest);
				     
				}catch(Exception e){
					logger.info(e.toString());
					e.printStackTrace();
					return false;
				}
				return true;
			}

			public void addClockInClockOut(Punch p) throws Exception
			{
				logger.info("Inserting punches...");
				PreparedStatement ps = null;
						
				
				
				try
				{	
					logger.info("Preparing the connection and the data to insert the punches...");
					
					
						DateTime punchDate = new DateTime(p.getPunchdatetime());
						String result[] = new String[2];
						
						//Extracting the Date from the DateTime
						SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
						String workDateString = sdFormat.format(new java.util.Date(punchDate.getMillis()));

						DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
						DateTime punchD = formatter.parseDateTime(workDateString);
						
						String costCenter;
						String group;
						try {
							 costCenter = getCostCenterByEmployee(p.getEmployeeid());
							 group = getGroupByCostCenterEmployee(costCenter);
							result = getLocationSiteByGroup(group);
						}catch (Exception e){
							logger.severe(e.toString());
							result[0] = "";
							result[1] = "";
							costCenter = "";
							group = "";
						}
						
						
						
						
						deleteClockInClockOut(p, result[0], result[1]);
						
						if("IN".equalsIgnoreCase(p.getPunchtype()))
						{
							String q ="  INSERT INTO employee_attendance_current (EMPLOYEE, CREATED_BY, CREATED_DATE, MODIFIED_BY, MODIFIED_DATE, START_TIME, LOCATION, SITE, TYPE_OFF) " +
									"  VALUES (?, 'TRAXIFACE', sysdate, 'TRAXIFACE', sysdate, ?, ?, ?, ?) ";
							
							ps = con.prepareStatement(q); 
							ps.setString(1,  p.getEmployeeid());							
							ps.setDate(2, new java.sql.Date(punchDate.getMillis()));
							ps.setString(3,  result[0] != null? result[0]: "");
							ps.setString(4,  result[1] != null? result[1]: "");
							ps.setString(5,  p.getPunchtype());						
							ps.executeUpdate();
						}

						
						addEmployeeAttendanceLog(p, result[0], result[1]);
						
						logger.info( "The punches has been created...");
				}
				catch (SQLException sqle) 
				{
					logger.severe("An error ocurrer trying to insert the punches...");
					throw new Exception("An error " + sqle.getErrorCode() + " occurred executing the query to clock In/Out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +p.toString() + "\n}" + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("An error ocurrer trying to insert the punches. Some values could be null...");
				throw new Exception("An error occurred executing the query to clock In/Out. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" +p.toString() + "\n}" + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An error ocurrer trying to insert the punches...");
				throw new Exception("An error occurred executing the query to clock In/Out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +p.toString() + "\n}" + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(ps != null && !ps.isClosed())
							ps.close();
						
						
						
						
						
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement or the connection...");
					throw new Exception("An error " + e.getErrorCode() + " occurred executing the query to clock In/Out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +p.toString() + "\n}" + "\nmessage: " + e.getMessage());
					}
				}
			}
			
			
			public void deleteClockInClockOut(Punch pci, String location, String site) throws Exception
			{
				PreparedStatement ps = null;
				
				try
				{				
					String q = " DELETE FROM employee_attendance_current WHERE EMPLOYEE = ? ";		
					ps = con.prepareStatement(q); 			
					
					ps.setString(1,  pci.getEmployeeid());	
					
					ps.executeUpdate();
					
					logger.info("Current employee attendance records have been removed for employee: " + pci.getEmployeeid());
				}
				catch (SQLException sqle) 
				{
					logger.severe("An error ocurrer trying to delete the existing punch with values: employee = " + pci.getEmployeeid() + "; punch type = " + pci.getPunchtype() + "; punch date = " + pci.getPunchdatetime() + "; location = " + location + "; site = " + site);
				throw new Exception("An error " + sqle.getErrorCode() + " occurred executing the query removing clock In/Out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("An error ocurrer trying to delete the existing punch with values: employee = " + pci.getEmployeeid() + "; punch type = " + pci.getPunchtype() + "; punch date = " + pci.getPunchdatetime() + "; location = " + location + "; site = " + site + ". Some values could be null");			
				throw new Exception("An error occurred executing the query removing clock In/Out. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An error ocurrer trying to delete the existing punch with values: employee = " + pci.getEmployeeid() + "; punch type = " + pci.getPunchtype() + "; punch date = " + pci.getPunchdatetime() + "; location = " + location + "; site = " + site);
					throw new Exception("An error occurred executing the query removing clock In/Out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(ps != null && !ps.isClosed())
							ps.close();
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement");
						throw new Exception("An error " + e.getErrorCode() + " occurred executing the query removing clock In/Out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + e.getMessage());
					}
				}
			}
			
			private BigDecimal getTransactionNo() throws Exception
			{		
				CallableStatement cstmt = null;
				try
				{
					cstmt = con.prepareCall("{?=call PKG_APPLICATION_GF.gf_config_number(?)}");
					cstmt.registerOutParameter(1, Types.BIGINT);
					cstmt.setString(2, "EMPATTSEQ");
					cstmt.executeUpdate();
					BigDecimal acctBal = cstmt.getBigDecimal(1);
								
					return acctBal;			
				}
				catch (SQLException sqle) 
				{
					logger.severe("An error ocurred getting the transaction number");
					throw new Exception("An error " + sqle.getErrorCode() + " occurred getting the sequence EMPATTSEQ. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("An error ocurred getting the transaction number");
					throw new Exception("An error occurred getting the sequence EMPATTSEQ because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An error ocurred getting the transaction number");
					throw new Exception("An unexpected error occurred getting the sequence EMPATTSEQ. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(cstmt != null && !cstmt.isClosed())
							cstmt.close();
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement");
						throw new Exception("An error " + e.getErrorCode() + " trying to close the statement. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
					}
				}
			}
			
			
			
			
			
			
			
			
			private void addEmployeeAttendanceLog(Punch pci, String location, String site) throws Exception
			{
				PreparedStatement ps = null;
				PreparedStatement ps1 = null;
				PreparedStatement checkDuplicates = null;
			    
			    try
			    {   
			       
			        String checkQuery = "SELECT COUNT(*) FROM employee_attendance_log " +
			                           "WHERE employee = ? AND type_off = ? " +
			                           "AND TRUNC(start_time) = TRUNC(?) " +
			                           "AND ABS(TO_NUMBER(TO_CHAR(start_time, 'HH24MISS')) - TO_NUMBER(TO_CHAR(?, 'HH24MISS'))) < 1000"; 
			                           
			        DateTime punchDateTime = new DateTime(pci.getPunchdatetime());
			        
			        checkDuplicates = con.prepareStatement(checkQuery);
			        checkDuplicates.setString(1, pci.getEmployeeid());
			        checkDuplicates.setString(2, pci.getPunchtype());
			        checkDuplicates.setDate(3, new java.sql.Date(punchDateTime.getMillis()));
			        checkDuplicates.setDate(4, new java.sql.Date(punchDateTime.getMillis()));
			        
			        ResultSet rs = checkDuplicates.executeQuery();
			        if (rs.next() && rs.getInt(1) > 0) {
			            logger.info("Skipping duplicate punch record for employee: " + pci.getEmployeeid() + 
			                       ", type: " + pci.getPunchtype() + 
			                       ", time: " + pci.getPunchdatetime());
			            return; 
			        }
	
					String q =  " insert into employee_attendance_log(transaction_no, transaction_type, employee, start_time, created_by, created_date, location, site, start_date, type_off, \"GROUP\")" + 
							 	" values( ?, ?, ?, ?, ?, sysdate, ?, ?, ?, ?, 'testGroup') ";
					
					//DateTime punchDateTime = new DateTime(pci.getPunchdatetime());
					
					//Extracting the Date from the DateTime
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
					String punchDateString = sdFormat.format(new java.util.Date(punchDateTime.getMillis()));
					DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
					DateTime punchDate = formatter.parseDateTime(punchDateString);
					
					ps = con.prepareStatement(q); 
					
					ps.setBigDecimal(1, getTransactionNo());
					ps.setString(2, "LOG" );	
					ps.setString(3,  pci.getEmployeeid());		
					ps.setDate(4, new java.sql.Date(punchDateTime.getMillis()));
					ps.setString(5, "TRAXIFACE");
					ps.setString(6,  location != null ? location : "");
					ps.setString(7,  site != null ? site : "");
					ps.setDate(8, new java.sql.Date(punchDate.getMillis()));
					ps.setString(9,  pci.getPunchtype());

					ps.executeUpdate();
					
					// TODO if clock OUT THEN UPDATE THE BOTH LOG RECORDS WITH END TIME 
					if("OUT".equalsIgnoreCase(pci.getPunchtype()))
					{
						q ="  UPDATE EMPLOYEE_ATTENDANCE_LOG ELA SET ELA.END_DATE = ?, ELA.END_TIME = ? " + 
							"WHERE ELA.EMPLOYEE = ? AND ELA.TRANSACTION_NO IN (SELECT ELA1.TRANSACTION_NO FROM EMPLOYEE_ATTENDANCE_LOG ELA1 WHERE ELA1.EMPLOYEE = ELA.EMPLOYEE " + 
							"AND ELA1.LOCATION = ? AND ELA1.SITE = ? and ELA1.TYPE_OFF IN ('OUT','IN') ORDER BY ELA1.CREATED_DATE DESC FETCH FIRST 2 ROWS ONLY) ";
						
						ps1 = con.prepareStatement(q); 
						ps1.setDate(1, new java.sql.Date(punchDate.getMillis()));
						ps1.setDate(2, new java.sql.Date(punchDateTime.getMillis()));
						ps1.setString(3,  pci.getEmployeeid());	
						ps1.setString(4, location != null? location: "");
						ps1.setString(5,  site != null? site: "");				
						ps1.executeUpdate();
					}
					
					logger.info("The punche has been created with values: employee = " + pci.getEmployeeid() + "; punch type = " + pci.getPunchtype() + "; punch date = " + pci.getPunchdatetime() + "; location = " + location + "; site = " + site);
				}
				catch (SQLException sqle) 
				{
					logger.severe("An error ocurrer trying to delete the existing punch with values: employee = " + pci.getEmployeeid() + "; punch type = " + pci.getPunchtype() + "; punch date = " + pci.getPunchdatetime() + "; location = " + location + "; site = " + site);
				throw new Exception("An error " + sqle.getErrorCode() + " occurred executing the query while tracing the clock in/out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("An error ocurrer trying to delete the existing punch with values: employee = " + pci.getEmployeeid() + "; punch type = " + pci.getPunchtype() + "; punch date = " + pci.getPunchdatetime() + "; location = " + location + "; site = " + site + ". Some values could be null");
					throw new Exception("An error occurred executing the query while tracing the clock in/out because some values could be null. " + "\n error: " + ErrorType.BAD_REQUEST +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An error ocurrer trying to delete the existing punch with values: employee = " + pci.getEmployeeid() + "; punch type = " + pci.getPunchtype() + "; punch date = " + pci.getPunchdatetime() + "; location = " + location + "; site = " + site);
					throw new Exception("An unexpected error occurred executing the query while tracing the clock in/out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(checkDuplicates != null && !checkDuplicates.isClosed())
			                checkDuplicates.close();
						if(ps != null && !ps.isClosed())
							ps.close();
						if(ps1 != null && !ps1.isClosed())
							ps1.close();
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement");
						throw new Exception("An error " + e.getErrorCode() + " occurred trying to close the tracing the clock in/out. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR +  "\n" + "data: {" +pci.toString() + "\n}" + "\nmessage: " + e.getMessage());
					}
				}
			}
			
			
			public HashMap<String, String> getLocationSiteCapacityByEmployee(String employee, DateTime dateTime) throws Exception
			{		
				logger.info("Preparing the data ... employee = " + employee + "; date = " + dateTime);
				

				String query = " SELECT related_location, site FROM RELATION_MASTER where relation_code = '"+employee+"' and RELATION_TRANSACTION = 'EMPLOYEE' and rownum = 1 ";
					
				PreparedStatement ps = null;
					
				HashMap<String, String> result = new HashMap<String, String>();
				try
				{
					logger.info("Preparing the connection and the data to get the site and location...");
					
					if(con == null || con.isClosed())
					{
						con = DataSourceClient.getConnection();
						logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
					}
					
					ps = con.prepareStatement(query);
					
					ResultSet rs = ps.executeQuery();		
					
					if(rs.next())
					{
						result.put("location", rs.getString(1));
						result.put("site", rs.getString(2));
						logger.info("Getting the location and site...");
					}
					
					rs.close();
				}
				catch (SQLException sqle) 
				{
					logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
					throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
					throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
					throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(ps != null && !ps.isClosed())
							ps.close();
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement");
						throw new Exception("A SQLException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
					}
				}
				
				return result;
			}
			
			public String getCostCenterByEmployee(String employee) throws Exception
			{		
				logger.info("Preparing the data ... employee = " + employee);
				
				String costCenter = "";
				
				String query = " SELECT cost_center FROM RELATION_MASTER where relation_code = '"+employee+"' and RELATION_TRANSACTION = 'EMPLOYEE' and rownum = 1 ";
					
				PreparedStatement ps = null;
					
				
				try
				{
					logger.info("Preparing the connection and the data to get the cost center...");
					
					if(con == null || con.isClosed())
					{
						con = DataSourceClient.getConnection();
						logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
					}
					
					ps = con.prepareStatement(query);
					
					ResultSet rs = ps.executeQuery();		
					
					if(rs.next())
					{
						
						return rs.getString(1);
					}
					
					rs.close();
				}
				catch (SQLException sqle) 
				{
					logger.severe("A SQLException" + " occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
					throw new Exception("A SQLException" + " occurred executing the query to get CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("A NullPointerException occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
					throw new Exception("A NullPointerException occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
					throw new Exception("An Exception occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(ps != null && !ps.isClosed())
							ps.close();
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement");
						throw new Exception("A SQLException occurred executing the query to get the CostCenter. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\nemployee: " + employee + "\n}" + "\nmessage: " + e.getMessage());
					}
				}
				
				return costCenter;
			}
			
			private Punch convertTAToPunch(Ta item) throws Exception
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date date = new Date();
				
				String target = "";
				
				Punch punch = new Punch();
				if(item.getStaffNo() != null && !item.getStaffNo().isEmpty()) {
					punch.setEmployeeid(item.getStaffNo());
				}
				
				if(item.getMsgType() != null && !item.getMsgType().isEmpty()) {
			        if("ClockIn".equalsIgnoreCase(item.getMsgType())) {
			            try {
			               
			                if(item.getClkInTime() != null && !item.getClkInTime().isEmpty()) {
			                    try {
			                        date = formatter.parse(item.getClkInTime());
			                    } catch (ParseException e) {
			                
			                        try {
			                            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.getClkInTime());
			                        } catch (ParseException e2) {
			                            logger.warning("Could not parse clock in time: " + item.getClkInTime() + ". Using current time.");
			                            date = new Date(); 
			                        }
			                    }
			                    target = formatter1.format(date);
			                    date = formatter1.parse(target);
			                    punch.setPunchdatetime(date);
			                } else {
			                    punch.setPunchdatetime(new Date()); 
			                }
			                
			                punch.setPunchtype("IN");
			            } catch (Exception e) {
			                logger.severe("Error processing ClockIn time: " + e.getMessage());
			                throw new Exception("Error processing ClockIn time: " + e.getMessage());
			            }
			        } else if("ClockOut".equalsIgnoreCase(item.getMsgType())) {
			            try {
			               
			                if(item.getClkOutTime() != null && !item.getClkOutTime().isEmpty()) {
			                    try {
			                        date = formatter.parse(item.getClkOutTime());
			                    } catch (ParseException e) {
			                     
			                        try {
			                            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.getClkOutTime());
			                        } catch (ParseException e2) {
			                            logger.warning("Could not parse clock out time: " + item.getClkOutTime() + ". Using current time.");
			                            date = new Date(); 
			                        }
			                    }
			                    target = formatter1.format(date);
			                    date = formatter1.parse(target);
			                    punch.setPunchdatetime(date);
			                } else {
			                    punch.setPunchdatetime(new Date()); 
			                }
			                
			                punch.setPunchtype("OUT");
			            } catch (Exception e) {
			                logger.severe("Error processing ClockOut time: " + e.getMessage());
			                throw new Exception("Error processing ClockOut time: " + e.getMessage());
			            }
			        } else {
			            logger.warning("Unknown message type: " + item.getMsgType() + ". Default to IN.");
			            punch.setPunchtype("IN");
			            punch.setPunchdatetime(new Date());
			        }
			    } else {
			        logger.warning("No message type specified. Default to IN.");
			        punch.setPunchtype("IN");
			        punch.setPunchdatetime(new Date());
			    }
			    
			    return punch;
			}
			
			
			private void changeTaskCardStatus(Ta item) throws Exception
			{
				
				String word = AESencrp.generatePhrase();
				String secWord = AESencrp.encrypt(word);
				
				String jwt = AESencrp.getJwtToken("SIAEC", "eMobilityServices");
				
				Poster poster = new Poster();
				
				String urlTask = System.getProperty("ImportClockOnOff_URLTask");
						
				String urlItem = System.getProperty("ImportClockOnOff_URLItem");
				
				urlTask += "?jwt="+ jwt;
				urlItem += "?jwt="+ jwt;
				
				
				
				ArrayList<Task> tasks = new ArrayList<Task>();
				ArrayList<Item> items = new ArrayList<Item>();
				
				//setting up variables
				exceuted = "OK";
				
				// call wo task card in progress table
				String sqlEmployee =
				"select  wtcwip.category,\r\n" + 
				"wtcwip.task_card_pn,wtcwip.task_card_pn_sn,wtcwip.task_card,WTCWIP.WO,wtcwip.task_card_item,WTCWIP.AC,wtcwip.employee\r\n" + 
				"from wo_task_card_work_in_progress WTCWIP where employee = ?";
				
				String sqlItem = 
				"select mechanic,mechanic_status from wo_task_card_item\r\n" + 
				"where wo = ? and task_card = ? and task_card_item = ? and\r\n" + 
				"task_card_pn = ? and task_card_pn_sn = ? and ac= ?";
				
				PreparedStatement pstmt1 = null; 
				
				ResultSet rs = null;
				
				PreparedStatement pstmt2 = null; 
				
				ResultSet rs2 = null;
				
				logger.info("Setting Task Card work Status");
				try 
				{
					pstmt1 = con.prepareStatement(sqlEmployee);
					pstmt2 = con.prepareStatement(sqlItem);
					
					pstmt1.setString(1,item.getStaffNo() );
					rs = pstmt1.executeQuery();
					if (rs != null) 
					{
						while (rs.next()) // LOOP EACH rs LINE
						{
							
							
							logger.info("Processing: " + rs.getString(4));
							
							if(rs.getString(6).equalsIgnoreCase("0")) {
								Task t = new Task();
								
								
								t.setPn(rs.getString(2));
								t.setSn(rs.getString(3));
								t.setTaskCard(rs.getString(4));
								t.setWo(rs.getString(5));
								t.setAc(rs.getString(7));
								t.setEmployeeId(rs.getString(8));
								t.setWord(word);
								t.setSecretWord(secWord);
								t.setStatus("HOLD");
								
								t.setWorkAccomplished(null);
								
								ObjectMapper Obj = new ObjectMapper();
								String json;
								try {
									json = Obj.writeValueAsString(t);
								} catch (JsonProcessingException e) {
									json = "JsonProcessingException";
								}
								
								logger.info("Body: " + json);
								tasks.add(t);
								
								
							}else {
								Item i = new Item();
								
								
								i.setTaskCardPn(rs.getString(2));
								i.setTaskCardPnSn(rs.getString(3));
								i.setTaskCard(rs.getString(4));
								i.setWo(rs.getString(5));
								i.setTaskCardItem(rs.getString(6));
								i.setAc(rs.getString(7));
								i.setEmployeeId(rs.getString(8));
								i.setWord(word);
								i.setSecretWord(secWord);
								
								pstmt2.setString(1,i.getWo() );
								pstmt2.setString(2,i.getTaskCard() );
								pstmt2.setString(3,i.getTaskCardItem() );
								pstmt2.setString(4,i.getTaskCardPn() );
								pstmt2.setString(5,i.getTaskCardPnSn() );
								pstmt2.setString(6,i.getAc() );
								
								rs2 = pstmt2.executeQuery();
								if (rs2 != null) 
								{
									while (rs2.next()) // LOOP EACH rs2 LINE
									{
										if(rs2.getString(1) != null && !rs2.getString(1).isEmpty()	
												&&  rs2.getString(1).equalsIgnoreCase("Y")) {
											
												if(rs2.getString(2) != null && !rs2.getString(2).isEmpty()
													&&  rs2.getString(2).equalsIgnoreCase("COMPLETED")){
													i.setMechanicStatus("HOLDI");
												}else {
													i.setMechanicStatus("HOLD");
												}
										}else {
											i.setMechanicStatus("HOLDI");
										}
									}
								}	
								if(rs2 != null && !rs2.isClosed()) {
									rs2.close();
								}		
								
								
								i.setWorkAccomplished(null);
								
								ObjectMapper Obj = new ObjectMapper();
								String json;
								try {
									json = Obj.writeValueAsString(i);
								} catch (JsonProcessingException e) {
									json = "JsonProcessingException";
								}
								
								logger.info("Body: " + json);
								items.add(i);
								
								
								
								
								
								
							}
						}
					}
					
					if(tasks !=null && !tasks.isEmpty()) {
						for(Task t : tasks) {
							poster.postTask(t, urlTask);
						}
					}
					
					if(items !=null && !items.isEmpty()) {
						for(Item i : items) {
							poster.postItem(i, urlItem);
						}
					}
				}
				catch (Exception e) 
		        {
					logger.severe(e.toString());
		            exceuted = e.getMessage();
		            throw new Exception(exceuted);
				}
				finally {
					if(rs != null && !rs.isClosed())
						rs.close();
					if(pstmt1 != null && !pstmt1.isClosed())
						pstmt1.close();
					if(pstmt2 != null && !pstmt2.isClosed())
						pstmt2.close();
					
				}
				
				
			}
			
			public String getGroupByCostCenterEmployee( String costcenter) throws Exception{
				
				
				String query = "", group = "";
				query = " Select \"GROUP\" FROM SITE_GROUP_MASTER where cost_centre = ?";
				
				PreparedStatement ps = null;
					
				
				try
				{
					
					
					if(con == null || con.isClosed())
					{
						con = DataSourceClient.getConnection();
						logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
					}
					
					ps = con.prepareStatement(query);
					if(costcenter != null && !costcenter.isEmpty()) {
						ps.setString(1, costcenter);
					}
					
					ResultSet rs = ps.executeQuery();		
					
					if (rs != null) 
					{
						while (rs.next()) 
						{
							
						 return rs.getString(1);

						}
					}
					rs.close();
					
					
				}
				catch (SQLException sqle) 
				{
					logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
					throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
					throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
					throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(ps != null && !ps.isClosed())
							ps.close();
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement");
						
					}
				}
				return group;
				
			}	
			
			public String[] getLocationSiteByGroup(String group) throws Exception
			{		
				logger.info("Preparing the data ... group = " + group);
				
				String locationSite[] = new String[2];
				
				String query = " SELECT LOCATION,SITE FROM EMPLOYEE_SCHEDULE_GROUP where \"GROUP\" = '"+group+"'";
					
				PreparedStatement ps = null;
					
				
				try
				{
					logger.info("Preparing the connection and the data to get the site, location...");
					
					if(con == null || con.isClosed())
					{
						con = DataSourceClient.getConnection();
						logger.info("The connection was stablished successfully with status: " + String.valueOf(!con.isClosed()));
					}
					
					ps = con.prepareStatement(query);
					
					ResultSet rs = ps.executeQuery();		
					
					if(rs.next())
					{
						locationSite[0]= rs.getString(1);
						locationSite[1]= rs.getString(2);
					}
					
					rs.close();
				}
				catch (SQLException sqle) 
				{
					logger.severe("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + sqle.getMessage());
					throw new Exception("A SQLException" + " occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + sqle.getMessage());
				}
				catch (NullPointerException npe) 
				{
					logger.severe("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + npe.getMessage());
					throw new Exception("A NullPointerException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + npe.getMessage());
				}
				catch (Exception e) 
				{
					logger.severe("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + e.getMessage());
					throw new Exception("An Exception occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.INTERNAL_SERVER_ERROR + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + e.getMessage());
				}
				finally 
				{
					try 
					{
						if(ps != null && !ps.isClosed())
							ps.close();
					} 
					catch (SQLException e) 
					{ 
						logger.severe("An error ocurrer trying to close the statement");
						throw new Exception("A SQLException occurred executing the query to get the location site capacity. " + "\n error: " + ErrorType.BAD_REQUEST + "\ndata: {\ngroup: " + group + "\n}" + "\nmessage: " + e.getMessage());
					}
				}
				
				return locationSite;
			}
			
			private <T> void insertData( T data) 
			{
				try 
				{	
					if(!em.getTransaction().isActive())
						em.getTransaction().begin();
						em.merge(data);
					em.getTransaction().commit();
				}catch (Exception e)
				{
					logger.severe(e.toString());
				}
			}
			
			public boolean lockAvailable(String notificationType)
			{
				
				//em.getTransaction().begin();
				InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
						.setParameter("type", notificationType).getSingleResult();
				em.refresh(lock);
				//logger.info("lock " + lock.getLocked());
				if(lock.getLocked().intValue() == 1)
				{				
					LocalDateTime today = LocalDateTime.now();
					LocalDateTime locked = LocalDateTime.ofInstant(lock.getLockedDate().toInstant(), ZoneId.systemDefault());
					Duration diff = Duration.between(locked, today);
					if(diff.getSeconds() >= lock.getMaxLock().longValue())
					{
						lock.setLocked(new BigDecimal(1));
						insertData(lock);
						return true;
					}
					return false;
				}
				else
				{
					lock.setLocked(new BigDecimal(1));
					insertData(lock);
					return true;
				}
				
			}
			
			
			public void lockTable(String notificationType)
			{
				em.getTransaction().begin();
				InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
						.setParameter("type", notificationType).getSingleResult();
				lock.setLocked(new BigDecimal(1));
				//logger.info("lock " + lock.getLocked());
				
				lock.setLockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
				InetAddress address = null;
				try {
					address = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					
					logger.info(e.getMessage());
					//e.printStackTrace();
				}
				lock.setCurrentServer(address.getHostName());
				//em.lock(lock, LockModeType.NONE);
				em.merge(lock);
				em.getTransaction().commit();
			}
			
			public void unlockTable(String notificationType)
			{
				em.getTransaction().begin();
				
				InterfaceLockMaster lock = em.createQuery("SELECT i FROM InterfaceLockMaster i where i.interfaceType = :type", InterfaceLockMaster.class)
						.setParameter("type", notificationType).getSingleResult();
				lock.setLocked(new BigDecimal(0));
				//logger.info("lock " + lock.getLocked());
				
				lock.setUnlockedDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()) );
				//em.lock(lock, LockModeType.NONE);
				em.merge(lock);
				em.getTransaction().commit();
			}
			
			
			
}
