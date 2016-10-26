package org.objectweb.dsrg.bpc.demo;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

public class FlyTicketClassifierImpl implements IFlyTicketDb, IFlyTicketAuth {

	public static final String AF_TICKET_PREFIX = "AFR";
	public static final String CSA_TICKET_PREFIX = "CSA";
	private static final long TIME_UNITS_AFTER_ARRIVAL = 30 * 60;

	protected IFlyTicketDb iAfFlyTicketDb;
	protected IFlyTicketDb iCsaFlyTicketDb;
	
	private final Simulator simComp;

	public FlyTicketClassifierImpl(Simulator simComp) {
	  this.simComp = simComp;
	}

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("IAfFlyTicketDb")) {
			iAfFlyTicketDb = (IFlyTicketDb)sItf;
		} else if (cItf.equals("ICsaFlyTicketDb")) {
			iCsaFlyTicketDb = (IFlyTicketDb)sItf;
		}
	}

	public void unbindFc(String cItf) 
	{
		if (cItf.equals("IAfFlyTicketDb")) {
			iAfFlyTicketDb=null;
		} else if (cItf.equals("ICsaFlyTicketDb")) {
			iCsaFlyTicketDb=null;
		}
	}

	//
	// Business methods
	//
	
	//
	// IFlyTicketDb
	//
	
	public String[] GetFlyTicketsByFrequentFlyerId(String FrequentFlyerId) {
		if (FrequentFlyerId == null) return null;
	
		Vector result = new Vector();
        
		// JPF can't handle the Vector.addAll method
		String[] array1 = iAfFlyTicketDb.GetFlyTicketsByFrequentFlyerId(FrequentFlyerId);
		for (int i = 0; i < array1.length; i++) result.add(array1[i]);
	
		String[] array2 = iCsaFlyTicketDb.GetFlyTicketsByFrequentFlyerId(FrequentFlyerId);
		for (int i = 0; i < array2.length; i++) result.add(array2[i]);
    
		return (String[]) result.toArray(new String[result.size()]);
	}
    
	public Date GetFlyTicketValidity(String FlyTicketId) {
		IFlyTicketDb db = getTicketDbByTicketId(FlyTicketId);
		if (db == null) return null;
		
		return db.GetFlyTicketValidity(FlyTicketId);
	}

	public boolean IsEconomyFlyTicket(String FlyTicketId) {
		IFlyTicketDb db = getTicketDbByTicketId(FlyTicketId);
		if (db == null) return true;
		
		return db.IsEconomyFlyTicket(FlyTicketId);
	}
	
	//
	// IFlyTicketAuth
	//
    
	public IToken CreateToken(String FlyTicketId, boolean RestrictValidity) {
		//System.out.println("..... FlyTicketClassifier.CreateToken(" + FlyTicketId + ") .....");

		if (FlyTicketId == null) return null;
		
		IFlyTicketDb db = getTicketDbByTicketId(FlyTicketId);
		if (db == null) return null;
		
		Date ArrivalTime = null;
		if (RestrictValidity) {
			ArrivalTime = db.GetFlyTicketValidity(FlyTicketId);
			if (ArrivalTime == null) return null;
		
			if (db.IsEconomyFlyTicket(FlyTicketId)) return null;
		}
		
		IToken token = simComp.createToken();
		
		if (RestrictValidity) {
			token.SetValidity(new Date(ArrivalTime.getTime() + TIME_UNITS_AFTER_ARRIVAL * 1000));
		}
		
		return token;
	}
	
	//
	// Private methods
	//
	
	protected IFlyTicketDb getTicketDbByTicketId(String FlyTicketId) {
		if (FlyTicketId == null) return null;
		
		if (FlyTicketId.startsWith(AF_TICKET_PREFIX)) {
			return iAfFlyTicketDb;
		} else if (FlyTicketId.startsWith(CSA_TICKET_PREFIX)) {
			return iCsaFlyTicketDb;
		} else {
			return null;
		}
	}
}
