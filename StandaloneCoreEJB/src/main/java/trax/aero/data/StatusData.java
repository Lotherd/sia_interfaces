package trax.aero.data;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import trax.aero.Interfaces.IStatusData;

@Stateless(name="StatusData", mappedName="StatusData")
@TransactionManagement(value=TransactionManagementType.BEAN)
public class StatusData implements IStatusData{


	protected EntityManagerFactory emf = Persistence.createEntityManagerFactory("TraxCorePersistence");

	public static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY HH:mm:ss:SSS");

	private Logger logger = LogManager.getLogger(getClass());

	@Override
	public boolean dbHealthCheck() {
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			//em.getTransaction().begin();
			em.createNativeQuery("select 1 from dual").getSingleResult();
			//em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("healthcheck failed: " + e.getMessage());
			return false;
		}
		finally
		{
			if (em != null && em.isOpen())
				em.close();
		}

		return true;
	}
	
	@Override
	public long getNextSequenceVal() throws Exception
	{
		return getNextSequenceVal("SEQ_INTERFACE_AUDIT");
	}
	
	@Override
	public Date removeTimeFromDate(Date dt)
	{
		Date d = null;
		final Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		d = cal.getTime();
		return d;
	}

	@Override
	public Date currentDateTime()
	{
		Date currentTime ;
		EntityManager em = null;
		try {
			em = emf.createEntityManager();

			currentTime = (Date) em.createNativeQuery("select pkg_application_function.currentDateTime from dual").getSingleResult();

		} catch (Exception e) {

			return null; 
		}
		finally
		{
			if (em != null && em.isOpen())
				em.close();
		}

		return currentTime;
	}

	@Override
	public Date combinedDateTime(Date s_date, int hour, int minute)
	{
		Date combinedTime ;
		EntityManager em = null;
		try {
			em = emf.createEntityManager();

			combinedTime = (Date) em.createNativeQuery("select pkg_application_function.getcombineddate(?, ?, ?) from dual")
					.setParameter(1, s_date)
					.setParameter(2, hour)
					.setParameter(3, minute)
					.getSingleResult();

		} catch (Exception e) {

			return null; 
		}
		finally
		{
			if (em != null && em.isOpen())
				em.close();
		}

		return combinedTime;
	}

	@Override
	public void statementClose(Statement stmt)
	{
		try
		{
			if (stmt != null)
			{
				stmt.close();
			}
		} catch (final SQLException ex)
		{
			logger.warn("statement.close threw SQLException", ex);
		}
	}

	@Override
	public void resultSetClose(ResultSet rs)
	{
		try
		{
			if (rs != null)
			{
				rs.close();
			}
		} catch (final SQLException ex)
		{
			logger.warn("resultSet.close threw SQLException", ex);
		} catch (final Exception ex)
		{
			logger.warn("resultSet.close threw Exception", ex);
		}
	}
	
	@Override
	public void entityManagerClose (EntityManager em)
	{
		try
		{
			if (em != null && em.isOpen())
				em.close();
		}catch (Exception ex)
		{
			logger.warn("Error closing Entity", ex);
		}
	}

	@Override
	public String getTranConfigFlag(String systemTransaction,
			String systemCode) throws SQLException
	{
		String flag = null;

		EntityManager em = null;
		try
		{
			em = emf.createEntityManager();

			flag = (String) em.createNativeQuery("SELECT CONFIG_FLAG FROM SYSTEM_TRAN_CONFIG WHERE SYSTEM_TRANSACTION = ? AND SYSTEM_CODE = ?")
					.setParameter(1, systemTransaction)
					.setParameter(2, systemCode)
					.getSingleResult();


			if (flag == null || flag.length() <= 0)
				return "N";
			else
				return flag;


		} catch (final Exception e)
		{
			return "N";
		} finally
		{
			if (em != null && em.isOpen())
				em.close();
		}

	}
	
	@Override
	public String marshallObject(Object o)
			throws Exception
	{
		final StringWriter stringWriter = new StringWriter();
		try
		{
			JAXBContext jc = JAXBContext.newInstance(o.getClass());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(o, stringWriter);
			
		} catch (final Exception e)
		{
			logger.warn("Exception", e);
		}
		return stringWriter.toString();
	}
	
	@Override
	public Date nullDateFilter(Date inDate)
	{
		
		if (inDate != null)
		{
			final Calendar calHelper = Calendar.getInstance();
			int month, day, year;
			calHelper.setTime(inDate);
			month = calHelper.get(Calendar.MONTH);
			day = calHelper.get(Calendar.DAY_OF_MONTH);
			year = calHelper.get(Calendar.YEAR);
			if (month == 0 && day == 01 && year == 0001)
				return null;
		}
		return inDate;
	}
	
	@Override
	public String getCompanyProfile() throws Exception
	{
		String companyProfile = null;
		EntityManager em = null;


		try
		{
			em = emf.createEntityManager();
			
			companyProfile = (String) em.createNativeQuery("SELECT \"PKG_APPLICATION_FUNCTION\".getCompanyProfile from dual")
			.getSingleResult();

		} catch (final Exception e)
		{
			logger.error("Exception thrown", e);
		}finally
		{
			if (em != null && em.isOpen())
				em.close();
		}
		
		logger.debug("Returning profile: " + companyProfile + ".");
		return companyProfile;
	}
	
	@Override
	public long getNextSequenceVal(String sequenceName)
			throws Exception
	{
		BigDecimal sequenceValue = new BigDecimal(0);
		EntityManager em = null;
		try
		{
			em = emf.createEntityManager();
			em.getTransaction().begin();
			
			sequenceValue = (BigDecimal) em.createNativeQuery(" SELECT \"" + sequenceName
					+ "\".NEXTVAL " + " FROM DUAL ")
					.getSingleResult();
			
			logger.info("Transaction successfully marked");
			
		} catch (final Exception e)
		{
			logger.warn("Transaction marking failed!");
			
		} finally
		{
			if (em != null && em.isOpen())
			{
				em.getTransaction().commit();
				em.close();
			}
		}
		return sequenceValue.longValue();
	}

	@Override
	public int getConfigNumber(String switchName) {
		BigDecimal configNumber ;
		EntityManager em = null;
		try {
			em = emf.createEntityManager();
			em.getTransaction().begin();

			configNumber = (BigDecimal) em.createNativeQuery("select pkg_application_function.config_number(?) from dual")
					.setParameter(1, switchName)
					.getSingleResult();

		} catch (Exception e) {

			return 0; 
		}
		finally
		{
			if (em != null && em.isOpen())
			{
				em.getTransaction().commit();
				em.close();
			}
		}

		return configNumber.intValue();
	}

}
