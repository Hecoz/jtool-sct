package org.objectweb.dsrg.bpc.demo;

import java.util.*;

public class AfDbConnectionImpl implements IFlyTicketDb {

	private static final String FIRST_CLASS_SUFFIX = "F";
	private static final String BUSINESS_CLASS_SUFFIX = "B";
	private static final String ECONOMY_CLASS_SUFFIX = "E";

	protected Hashtable database;

	public AfDbConnectionImpl() {
		database = Demo_DatabaseGenerator.loadDatabase(DataRow_AfDbConnection.DATABASE_NAME);
		
		// System.out.println(" ---- AfDbConnection.AfDbConnection(): loaded database -----");
		// for (Iterator it = database.values().iterator(); it.hasNext(); ) {
		// 	DataRow_AfDbConnection row = (DataRow_AfDbConnection) it.next();
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
		//System.out.println(" #### AfDbConnection.GetFlyTicketsByFrequentFlyerId ####");
		
		if (FrequentFlyerId == null) return null;
	
		ArrayList result = new ArrayList();
		
		for (Iterator it = database.values().iterator(); it.hasNext(); ) {
			DataRow_AfDbConnection row = (DataRow_AfDbConnection) it.next();
			
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
		
		return ((DataRow_AfDbConnection) database.get(FlyTicketId)).ArrivalTime;
	}
	
	public boolean IsEconomyFlyTicket(String FlyTicketId) {
		if (FlyTicketId == null) return true;
		
		if (!database.containsKey(FlyTicketId)) {
			return true;
		}
		
		return FlyTicketId.endsWith(ECONOMY_CLASS_SUFFIX);	
	}
}
