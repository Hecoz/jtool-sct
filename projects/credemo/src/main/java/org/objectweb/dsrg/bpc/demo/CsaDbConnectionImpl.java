package org.objectweb.dsrg.bpc.demo;

import java.util.*;

public class CsaDbConnectionImpl implements IFlyTicketDb {

	private static final String FIRST_CLASS_SUFFIX = "F";
	private static final String BUSINESS_CLASS_SUFFIX = "B";
	private static final String ECONOMY_CLASS_SUFFIX = "E";

	protected Hashtable database;

	public CsaDbConnectionImpl() {
		database = Demo_DatabaseGenerator.loadDatabase(DataRow_CsaDbConnection.DATABASE_NAME);
		
		// System.out.println(" ---- CsaDbConnection.CsaDbConnection(): loaded database -----");
		// for (Iterator it = database.values().iterator(); it.hasNext(); ) {
		// 	DataRow_CsaDbConnection row = (DataRow_CsaDbConnection) it.next();
		// 	System.out.println("   -- " + row.TicketId + ", " + row.ArrivalTime + ", " + row.FrequentFlyerId);
		// }
	}

	//
	// Business methods
	//
	
	//
	// IFlyTicketDb interface
	//
	
	public String[] GetFlyTicketsByFrequentFlyerId(String FrequentFlyerId) {
		//System.out.println(" #### CsaDbConnection.GetFlyTicketsByFrequentFlyerId ####");
	
		if (FrequentFlyerId == null) return null;
	
		ArrayList result = new ArrayList();
		
		for (Iterator it = database.values().iterator(); it.hasNext(); ) {
			DataRow_CsaDbConnection row = (DataRow_CsaDbConnection) it.next();
			
			if (row.FrequentFlyerId != null && FrequentFlyerId.equals(row.FrequentFlyerId)) {
				result.add(row.TicketId);
			}
		}
		
		return (String []) result.toArray(new String[result.size()]);
	}
    
	public Date GetFlyTicketValidity(String FlyTicketId) {
		if (FlyTicketId == null) return null;
		
		if (!database.containsKey(FlyTicketId)) {
			return null;
		}
		
		return ((DataRow_CsaDbConnection) database.get(FlyTicketId)).ArrivalTime;
	}
	
	public boolean IsEconomyFlyTicket(String FlyTicketId) {
		if (FlyTicketId == null) return true;
		
		if (!database.containsKey(FlyTicketId)) {
			return true;
		}
		
		return FlyTicketId.endsWith(ECONOMY_CLASS_SUFFIX);	
	}

}
