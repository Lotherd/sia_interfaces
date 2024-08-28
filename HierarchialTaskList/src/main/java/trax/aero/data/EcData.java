package trax.aero.data;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import trax.aero.logger.LogManager;
import trax.aero.model.EngineeringOrder;
import trax.aero.model.EngineeringOrderRv;
import trax.aero.model.EngineeringOrderRvPK;
import trax.aero.model.EngineeringScheduleControl;
import trax.aero.model.EngineeringScheduleControlPK;
import trax.aero.model.EngineeringScheduleCtlRv;
import trax.aero.model.EngineeringScheduleCtlRvPK;
import trax.aero.model.EngineeringTaskCardControl;
import trax.aero.model.EngineeringTaskCardControlPK;
import trax.aero.model.EngineeringTaskCardCtlRv;
import trax.aero.model.EngineeringTaskCardCtlRvPK;
import trax.aero.model.InterfaceLockMaster;
import trax.aero.pojo.Ec;
import trax.aero.pojo.SubEc;
import trax.aero.pojo.TaskCard;

public class EcData 
{

	Logger logger = LogManager.getLogger("EC_I19");
	
	EntityManagerFactory factory = null;
	@PersistenceContext(unitName = "ImportDS")	public EntityManager em = null;
	//public InterfaceLockMaster lock;
	public String error = "";
	public EcData(EntityManagerFactory factory)
	{
		this.factory = factory;
		em = factory.createEntityManager();
	}
	public boolean saveEcData(Ec data)
	{
		boolean success = true;
		if(data == null)
		{
			error = error.concat("Error Inserting EC " + System.lineSeparator() + "No data recieved" +
					 System.lineSeparator() +  System.lineSeparator());
			
			return false;
		}
		EngineeringOrder ec = null;
		
		try
		{
			ec = (EngineeringOrder) em.createQuery("select e from EngineeringOrder e where e.ex = :ec")
					.setParameter("ec", data.getEc())
					.getSingleResult();
		}
		catch(Exception e)
		{
			ec = new EngineeringOrder();
			ec.setEo(data.getEc());
			ec.setCreatedBy("TRAXIFACE");
			
			ec.setCreatedDate(new Date());
			ec = setDefaults(ec);
			
		}
		
		
		if(data.getDesc() != null)
			ec.setEoDescription(data.getDesc());
		
		if(data.getDelInd() != null && (data.getDelInd().equalsIgnoreCase("Y") || 
				data.getDelInd().equalsIgnoreCase("X")))
			ec.setStatus("CANCEL");
		else
			ec.setStatus("OPEN");
		
		if(data.getCategory() != null)
		{
			try
			{
				em.createQuery("select s from SystemTranCode s where s.id.systemTransaction = :tran and"
						+ " s.id.systemCode = :code")
				.setParameter("tran", "EOCATEGORY")
				.setParameter("code", data.getCategory())
				.getSingleResult();
				
				ec.setEoCategory(data.getCategory());
				
			}
			catch(Exception e)
			{
				error = error.concat("Error Inserting EC " + data.getEc() + System.lineSeparator() + "Category was not found" +
						 System.lineSeparator() +  System.lineSeparator());
				
				return false;
			}
		}
		else
		{
			
			ec.setEoCategory("NA");
//			error = error.concat("Error Inserting EC " + data.getEc() + System.lineSeparator() + "Category was not provided" +
//					 System.lineSeparator() +  System.lineSeparator());
//			
//			return false;
		}
		
		if(data.getSubEc() != null)
			ec.setEoSub(data.getSubEc());
		else
		{
			
			ec.setEoSub("NA");
//			error = error.concat("Error Inserting EC " + data.getEc() + System.lineSeparator() + "Sub EC was not provided" +
//					 System.lineSeparator() +  System.lineSeparator());
//			
//			return false;
		}
		
		try
		{
			String company = System.getProperty("profile_company");
			ec.setEoCompany(company);
		}
		catch(Exception e)
		{

			error = error.concat("Error Inserting EC " + data.getEc() + System.lineSeparator() + "Company could not be found" +
					 System.lineSeparator() +  System.lineSeparator());
			
			return false;
		}
		
		if(data.getClassif() != null)
		{
			ec.setManditory(data.getClassif());
		}
		else
		{
			ec.setManditory("MANDATORY");
		}
		
		if(data.getRevision() != null)
			ec.setRevision(data.getRevision());
		else if(ec.getRevision() == null)
			ec.setRevision("00");
		
		
		if(data.getMpd() != null)
		{
			//to do
		}
		
		if(data.getEffectivity() != null)
			ec.setEffectivityNo(data.getEffectivity());
		
		
		
		try
		{
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(ec);
			em.getTransaction().commit();
			em.clear();
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
			error = error.concat("Error Inserting EC " + ec.getEo() + System.lineSeparator() + e.toString() +
					 System.lineSeparator() +  System.lineSeparator());
			
			return false;
		}
		
		
		if(data.getSubEcs() != null && data.getSubEcs().size() > 0)
		{	
			if(ec.getEngineeringScheduleControls() == null)
				ec.setEngineeringScheduleControls(new ArrayList<EngineeringScheduleControl>());
			for(SubEc subEc : data.getSubEcs())
			{
				EngineeringScheduleControl esc = insertSub(subEc, ec);
				
				if(esc != null)
				{
					esc.setEngineeringOrder(ec);
					try
					{
						if(!em.getTransaction().isActive())
							em.getTransaction().begin();
						em.merge(esc);
						em.getTransaction().commit();
						em.clear();
					}
					catch(Exception e)
					{
						error = error.concat("Error Inserting SubEC " + subEc.getEc() + System.lineSeparator() + e.toString() +
								 System.lineSeparator() +  System.lineSeparator());
						success = false;
					}
				}
				else
				{
					success = false;
				}
//					ec.addEngineeringScheduleControl(esc);
			}
		}
		
		if(data.getTaskCards() != null && data.getTaskCards().size() > 0)
		{
			if(ec.getEngineeringTaskCardControls() == null)
				ec.setEngineeringTaskCardControls(new ArrayList<EngineeringTaskCardControl>());
			for(TaskCard card : data.getTaskCards())
			{
				List<EngineeringTaskCardControl> eCards = insertTaskCard(card, ec);
				
				if(eCards != null)
				{
					for(EngineeringTaskCardControl eCard : eCards)
					{
						if(eCard != null)
						{
							eCard.setEngineeringOrder(ec);
							try
							{
								if(!em.getTransaction().isActive())
									em.getTransaction().begin();
								em.merge(eCard);
								em.getTransaction().commit();
								em.clear();
							}
							catch(Exception e)
							{
								logger.severe(e.toString());
								error = error.concat("Error Inserting SubEC " + eCard.getId().getTaskCard() + System.lineSeparator() + e.toString() +
										 System.lineSeparator() +  System.lineSeparator());
								success = false;
							}
						}
						else
						{
							success = false;
						}
					}
				}
//					ec.addEngineeringTaskCardControl(eCard);
			}
		}
		
		
		
		if(success) {
			logger.info("INSERTING EC: " + data.getEc());
			saveEcDataRv(ec);
		}
		
		
		return success;
		
	}
	
	
	@SuppressWarnings("unchecked")
	private EngineeringScheduleControl insertSub(SubEc subEc, EngineeringOrder ec)
	{
		ArrayList<EngineeringScheduleControl> scheds = null;
		EngineeringScheduleControl newESC = null;
		long item = 0;
		try
		{
			scheds = (ArrayList<EngineeringScheduleControl>) em.createQuery("select  e from EngineeringScheduleControl e where e.id.eo = :ec")
					.setParameter("ec", subEc.getEc())
					.getResultList();
			if(scheds != null)
			{
				item = scheds.size() + 1;
			}
			else
			{
				item = 1;
			}
			newESC = new EngineeringScheduleControl();
			
			newESC.setId(new EngineeringScheduleControlPK());
			newESC.getId().setEo(ec.getEo());
			if(subEc.getEc() != null && subEc.getEc().length() > 0 && subEc.getPlanningPlant() != null && subEc.getPlanningPlant().length() > 0)
				newESC.getId().setScheduleEo(subEc.getEc());
			else
			{
				error = error.concat("Error Inserting EC " + ec.getEo() + System.lineSeparator() + "SubEc is null" +
						 System.lineSeparator() +  System.lineSeparator());
				return null;
			}
			newESC.getId().setItem(item);
			newESC.setScheduleControl("CALLEDON");
			
			newESC.setCreatedBy("TRAXIFACE");
			
			newESC.setCreatedDate(new Date());
			
			newESC.setModifiedBy("TRAXIFACE");
			
			newESC.setModifiedDate(new Date());
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
		}
		return newESC;
	}
	
	@SuppressWarnings("unchecked")
	private List<EngineeringTaskCardControl> insertTaskCard(TaskCard card, EngineeringOrder ec)
	{
		List <trax.aero.model.TaskCard> taskcards = new ArrayList<trax.aero.model.TaskCard>();
		
		
		List<EngineeringTaskCardControl> engCards = new ArrayList<EngineeringTaskCardControl>();
		
		String prefix = card.getTaskCard() + "_" + card.getGroupNo() + "_";
		
		if(prefix.length() > 31)
		{
			//int max = 42 - ("_" + card.getGroupNo() + "_").length();
			
			prefix = card.getTaskCard().substring(0, 21) + "_" + card.getGroupNo() + "_";
		}
	
		if(card == null || card.getTaskCard() == null || card.getPlanningPlant() == null)
		{
			error = error.concat("Error Inserting TaskCard " + card.getTaskCard() + card.getGroupNo() + 
					System.lineSeparator() + "TaskCard data is null" +
					 System.lineSeparator() +  System.lineSeparator());
			return null;
		}
		EngineeringTaskCardControl newETC = null;
		try
		{
			taskcards = (List<trax.aero.model.TaskCard>) em.createQuery("select t from TaskCard t where t.taskCard LIKE :card")
			.setParameter("card", prefix + "%")
			.getResultList();
		}
		catch(Exception e)
		{
			error = error.concat("Error Inserting TaskCard " + card.getTaskCard() + "_" + card.getGroupNo() + 
					System.lineSeparator() + "TaskCard not found on TASK_CARD table" +
					 System.lineSeparator() +  System.lineSeparator());
			return null;
		}
		
		if(taskcards.size() == 0)
		{
			error = error.concat("Error Inserting TaskCard " + prefix + 
					System.lineSeparator() + "TaskCard not found on TASK_CARD table" +
					 System.lineSeparator() +  System.lineSeparator());
			return null;
		}
		
		for(trax.aero.model.TaskCard ecCard : taskcards)
		{
			try
			{
				newETC = (EngineeringTaskCardControl) em.createQuery("select e from EngineeringTaskCardControl e where e.id.eo = :eo "
						+ "and e.id.category = :cat and e.id.taskCard = :card and e.id.acType = :type "
						+ "and e.id.acSeries = :series and e.id.ac = :ac and e.id.pn = :pn and e.id.sn = :sn "
						+ "and e.id.taskCardItem = :item")
						.setParameter("eo", ec.getEo())
						.setParameter("cat", "TASKCARD")
						.setParameter("card", ecCard.getTaskCard())
						.setParameter("type", "          ")
						.setParameter("series", "          ")
						.setParameter("ac", "          ")
						.setParameter("pn", "                                   ")
						.setParameter("sn", "                                   ")
						.setParameter("item", 0L)
						.getSingleResult();
			}
			catch(Exception e)
			{
				newETC = new EngineeringTaskCardControl();
				newETC.setId(new EngineeringTaskCardControlPK());
				newETC.getId().setAc("          ");
				newETC.getId().setEo(ec.getEo());
				newETC.getId().setCategory("TASKCARD");
				newETC.getId().setTaskCard(ecCard.getTaskCard());
				newETC.getId().setAcType("          ");
				newETC.getId().setAcSeries("          ");
				newETC.getId().setAc("          ");
				newETC.getId().setPn("                                   ");
				newETC.getId().setSn("                                   ");
				newETC.getId().setTaskCardItem(0L);
				
				newETC.setCreatedBy("TRAXIFACE");
				
				newETC.setCreatedDate(new Date());
				
				newETC.setSortItem(new BigDecimal(0));
				newETC.setGroup(new BigDecimal(0));
				newETC.setSubGroup(new BigDecimal(0));
			}
			
			newETC.setModifiedBy("TRAXIFACE");
			newETC.setModifiedDate(new Date());
			
			engCards.add(newETC);
		}
		
		return engCards;
	}
	
	private EngineeringOrder setDefaults(EngineeringOrder ec)
	{
		ec.setScheduleHours(new BigDecimal(0));
		ec.setBlobNo(new BigDecimal(0));
		ec.setScheduleCycles(new BigDecimal(0));
		ec.setScheduleDays(new BigDecimal(0));
		ec.setScheduleHoursRepeat(new BigDecimal(0));
		ec.setScheduleCyclesRepeat(new BigDecimal(0));
		ec.setScheduleDaysRepeat(new BigDecimal(0));
		ec.setTotalFrequency(new BigDecimal(1));
		
		ec.setWeightOn(new BigDecimal(0));
		
		ec.setWeightOff(new BigDecimal(0));
		
		ec.setWarningAtPercentDue(new BigDecimal(0));
		
		ec.setScheduleAcHours(new BigDecimal(0));
		
		ec.setScheduleAcCycles(new BigDecimal(0));
		
		ec.setAuthorization("ACCEPTED");
		
		ec.setRevisedBy("TRAXIFACE");
		
		ec.setAuthorizedBy("TRAXIFACE");
		
		ec.setApplicable("Y");
		
		ec.setEoType("EC");
		ec.setPressureCycles("NO");
		ec.setSchedule("EARLIEST");
		ec.setScheduleAc("N");
		
		ec.setInternalCapability("YES");
		
		ec.setPlanEo("YES");
		
		ec.setLowerLevel("NO");
		
		ec.setLowerLevel2("NO");
		ec.setLowerLevel3("NO");
		ec.setLowerLevel4("NO");
		ec.setLowerLevel5("NO");
		ec.setLowerLevel6("NO");
		ec.setLowerLevel7("NO");
		ec.setLowerLevel8("NO");
		ec.setLowerLevel9("NO");
		ec.setLowerLevel10("NO");
		ec.setLowerLevel11("NO");
		ec.setLowerLevel12("NO");
		ec.setLowerLevel13("NO");
		ec.setLowerLevel14("NO");
		ec.setLowerLevel15("NO");
		ec.setLowerLevel16("NO");
		
		ec.setCapabilityArea("ALL");
		
		ec.setControlArea("E/C");
		
		ec.setStructureRepair("OTHER");
		
		ec.setStructureRepairClass("NONE");
		
		ec.setCalendarControl("NO");
		
		ec.setWeightBalance("N");
		
		ec.setNotDoAllowConcession("NO");
		
		ec.setScheduleDaysEom("NO");
		
		ec.setScheduleDaysEomRepeat("NO");
		
		ec.setRedoEc("NO");
		
		ec.setHourCalendarControl("NO");
		
		ec.setPlanLeadTime(new BigDecimal(0));
		return ec;
	}
	
	private EngineeringOrderRv setDefaultsRv(EngineeringOrderRv ec)
	{
		ec.setScheduleHours(new BigDecimal(0));
		ec.setBlobNo(new BigDecimal(0));
		ec.setScheduleCycles(new BigDecimal(0));
		ec.setScheduleDays(new BigDecimal(0));
		ec.setScheduleHoursRepeat(new BigDecimal(0));
		ec.setScheduleCyclesRepeat(new BigDecimal(0));
		ec.setScheduleDaysRepeat(new BigDecimal(0));
		ec.setTotalFrequency(new BigDecimal(1));
		
		ec.setWeightOn(new BigDecimal(0));
		
		ec.setWeightOff(new BigDecimal(0));
		
		ec.setWarningAtPercentDue(new BigDecimal(0));
		
		ec.setScheduleAcHours(new BigDecimal(0));
		
		ec.setScheduleAcCycles(new BigDecimal(0));
		
		ec.setAuthorization("ACCEPTED");
		
		ec.setRevisedBy("TRAXIFACE");
		
		ec.setAuthorizedBy("TRAXIFACE");
		
		ec.setApplicable("Y");
		
		ec.setEoType("EC");
		ec.setPressureCycles("NO");
		ec.setSchedule("EARLIEST");
		ec.setScheduleAc("N");
		
		ec.setInternalCapability("YES");
		
		ec.setPlanEo("YES");
		
		ec.setLowerLevel("NO");
		
		ec.setLowerLevel2("NO");
		ec.setLowerLevel3("NO");
		ec.setLowerLevel4("NO");
		ec.setLowerLevel5("NO");
		ec.setLowerLevel6("NO");
		ec.setLowerLevel7("NO");
		ec.setLowerLevel8("NO");
		ec.setLowerLevel9("NO");
		ec.setLowerLevel10("NO");
		ec.setLowerLevel11("NO");
		ec.setLowerLevel12("NO");
		ec.setLowerLevel13("NO");
		ec.setLowerLevel14("NO");
		ec.setLowerLevel15("NO");
		ec.setLowerLevel16("NO");
		
		ec.setCapabilityArea("ALL");
		
		ec.setControlArea("E/C");
		
		ec.setStructureRepair("OTHER");
		
		ec.setStructureRepairClass("NONE");
		
		ec.setCalendarControl("NO");
		
		ec.setWeightBalance("N");
		
		ec.setNotDoAllowConcession("NO");
		
		ec.setScheduleDaysEom("NO");
		
		ec.setScheduleDaysEomRepeat("NO");
		
		ec.setRedoEc("NO");
		
		ec.setHourCalendarControl("NO");
		
		ec.setPlanLeadTime(new BigDecimal(0));
		return ec;
	}
	
	
	public void saveEcDataRv(EngineeringOrder ec)
	{
		
		EngineeringOrderRv ecr = null;
		
		try
		{
			ecr = (EngineeringOrderRv) em.createQuery("select e from EngineeringOrderRv e where e.id.eo = :ec and e.id.revision = :rev")
					.setParameter("ec", ec.getEo())
					.setParameter("rev", ec.getRevision())
					.getSingleResult();
		}
		catch(Exception e)
		{
			ecr = new EngineeringOrderRv();
			EngineeringOrderRvPK pk = new EngineeringOrderRvPK();
			ecr.setId(pk);
			ecr.getId().setEo(ec.getEo());
			ecr.getId().setRevision(ec.getRevision());
			ecr.setCreatedBy("TRAXIFACE");
			
			ecr.setCreatedDate(new Date());
			ecr = setDefaultsRv(ecr);
			
		}
			
		ecr.setEoDescription(ec.getEoDescription());	
		ecr.setStatus(ec.getStatus());				
		ecr.setEoCategory(ec.getEoCategory());		
		ecr.setEoSub(ec.getEoSub());
		ecr.setEoCompany(ec.getEoCompany());
		ecr.setManditory(ec.getManditory());
		ecr.setEffectivityNo(ec.getEffectivityNo());
		
		try
		{
			logger.info("INSERTING EC RV: " + ecr.getId().getEo());
			if(!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(ecr);
			em.getTransaction().commit();
			em.clear();
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
		}
		
		
		if(ec.getEngineeringScheduleControls() != null && ec.getEngineeringScheduleControls().size() > 0)
		{	
			for(EngineeringScheduleControl subEc : ec.getEngineeringScheduleControls())
			{
				EngineeringScheduleCtlRv escr = insertSubRv(subEc, ecr);
				
				if(escr != null)
				{
					try
					{
						if(!em.getTransaction().isActive())
							em.getTransaction().begin();
						em.merge(escr);
						em.getTransaction().commit();
						em.clear();
					}
					catch(Exception e)
					{
						logger.severe(e.toString());
					}
				}
				else
				{
					
				}
			}
		}
		
		if(ec.getEngineeringTaskCardControls() != null && ec.getEngineeringTaskCardControls().size() > 0)
		{
			for(EngineeringTaskCardControl card : ec.getEngineeringTaskCardControls())
			{
				EngineeringTaskCardCtlRv eCard = insertTaskCardRv(card, ecr);
				
				if(eCard != null)
				{
					try
						{
							if(!em.getTransaction().isActive())
								em.getTransaction().begin();
							em.merge(eCard);
							em.getTransaction().commit();
							em.clear();
						}
						catch(Exception e)
						{
							logger.severe(e.toString());
								
						}
				}
					
			}
		}
		return;
		
	}
	
	
	private EngineeringScheduleCtlRv insertSubRv(EngineeringScheduleControl subEc, EngineeringOrderRv ec)
	{
		ArrayList<EngineeringScheduleCtlRv> scheds = null;
		EngineeringScheduleCtlRv newESC = null;
		long item = 0;
		try
		{
			scheds = (ArrayList<EngineeringScheduleCtlRv>) em.createQuery("select  e from EngineeringScheduleCtlRv e where e.id.eo = :ec and e.id.revision = :rev")
					.setParameter("ec", subEc.getId().getEo())
					.setParameter("rev", ec.getId().getRevision())
					.getResultList();
			if(scheds != null)
			{
				item = scheds.size() + 1;
			}
			else
			{
				item = 1;
			}
			newESC = new EngineeringScheduleCtlRv();
			
			newESC.setId(new EngineeringScheduleCtlRvPK());
			newESC.getId().setEo(ec.getId().getEo());
			
			newESC.getId().setScheduleEo(subEc.getId().getEo());
			
			
			newESC.getId().setItem(item);
			newESC.setScheduleControl("CALLEDON");
			
			newESC.setCreatedBy("TRAXIFACE");
			
			newESC.setCreatedDate(new Date());
			
			newESC.setModifiedBy("TRAXIFACE");
			
			newESC.setModifiedDate(new Date());
		}
		catch(Exception e)
		{
			logger.severe(e.toString());
		}
		return newESC;
	}
	
	private EngineeringTaskCardCtlRv insertTaskCardRv(EngineeringTaskCardControl card, EngineeringOrderRv ec)
	{
		
		
		EngineeringTaskCardCtlRv newETC = null;
		
		
		try
			{
				newETC = (EngineeringTaskCardCtlRv) em.createQuery("select e from EngineeringTaskCardControl e where e.id.eo = :eo "
						+ "and e.id.category = :cat and e.id.taskCard = :card and e.id.acType = :type "
						+ "and e.id.acSeries = :series and e.id.ac = :ac and e.id.pn = :pn and e.id.sn = :sn "
						+ "and e.id.taskCardItem = :item")
						.setParameter("eo", ec.getId().getEo())
						.setParameter("cat", "TASKCARD")
						.setParameter("card", card.getId().getTaskCard())
						.setParameter("type", "          ")
						.setParameter("series", "          ")
						.setParameter("ac", "          ")
						.setParameter("pn", "                                   ")
						.setParameter("sn", "                                   ")
						.setParameter("item", 0L)
						.getSingleResult();
			}
			catch(Exception e)
			{
				newETC = new EngineeringTaskCardCtlRv();
				newETC.setId(new EngineeringTaskCardCtlRvPK());
				newETC.getId().setAc("          ");
				newETC.getId().setEo(ec.getId().getEo());
				newETC.getId().setCategory("TASKCARD");
				newETC.getId().setTaskCard(card.getId().getTaskCard());
				newETC.getId().setAcType("          ");
				newETC.getId().setAcSeries("          ");
				newETC.getId().setAc("          ");
				newETC.getId().setPn("                                   ");
				newETC.getId().setSn("                                   ");
				newETC.getId().setTaskCardItem(0L);
				newETC.getId().setRevision(ec.getId().getRevision());
				
				newETC.setCreatedBy("TRAXIFACE");
				
				newETC.setCreatedDate(new Date());
				
				newETC.setSortItem(new BigDecimal(0));
				newETC.setGroup(new BigDecimal(0));
				newETC.setSubGroup(new BigDecimal(0));
			}
			
			newETC.setModifiedBy("TRAXIFACE");
			newETC.setModifiedDate(new Date());
			
			
		
		
		return newETC;
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
