package trax.aero.interfaces;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import trax.aero.controller.ImportWarehouseController;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_BATCH;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_JSON;
import trax.aero.pojo.MT_TRAX_RCV_I46_4077_RES;
import trax.aero.pojo.MT_TRAX_SND_I46_4077_REQ;
import trax.aero.pojo.MaterialDetails;
import trax.aero.utils.ImportPoster;

public interface IImportWarehouseData {

	public EntityManager getEm();
	
	public String ProcessReqestBatch(MT_TRAX_RCV_I46_4077_BATCH i);
	
	public String ProcessReqest(MaterialDetails i, boolean invokeRequest);
	
	public void invokePOST();
	
	public void invokeRequest(MT_TRAX_RCV_I46_4077_JSON i);
	
	public boolean lockAvailable(String notificationType);
	
	
	public void lockTable(String notificationType);
	
	public void unlockTable(String notificationType);
	
	public ArrayList<MT_TRAX_SND_I46_4077_REQ> getWarehouse();
}
