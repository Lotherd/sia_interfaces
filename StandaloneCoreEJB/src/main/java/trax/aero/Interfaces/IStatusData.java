package trax.aero.Interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.persistence.EntityManager;

public interface IStatusData {
	public boolean dbHealthCheck();

	Date currentDateTime();

	Date combinedDateTime(Date s_date, int hour, int minute);

	void statementClose(Statement stmt);

	void resultSetClose(ResultSet rs);

	String getTranConfigFlag(String systemTransaction, String systemCode) throws SQLException;

	String marshallObject(Object o) throws Exception;

	Date nullDateFilter(Date inDate);

	String getCompanyProfile() throws Exception;

	void entityManagerClose(EntityManager em);

	long getNextSequenceVal(String sequenceName) throws Exception;

	Date removeTimeFromDate(Date dt);

	long getNextSequenceVal() throws Exception;

	int getConfigNumber(String string);
	
}
