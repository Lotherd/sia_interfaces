package trax.aero.interfaces;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import trax.aero.controller.MaterialMovementController;
import trax.aero.pojo.MaterialMovementMaster;
import trax.aero.pojo.OpsLineEmail;
import trax.aero.utils.DataSourceClient;

public interface IMaterialMovementData {

	public String updateMaterial(MaterialMovementMaster materialMovementMaster);
	
public String getemailByOnlyOpsLine( String opsLine);

public OpsLineEmail getOpsLineStaffName(MaterialMovementMaster input);

	public byte[] getsharePointfile(String spurl);
		
	public int ordinalIndexOf(String str, String substr, int n);

	public boolean lockAvailable(String notificationType);
	

	
	public void lockTable(String notificationType);
	
	
	public void unlockTable(String notificationType);
	
}
