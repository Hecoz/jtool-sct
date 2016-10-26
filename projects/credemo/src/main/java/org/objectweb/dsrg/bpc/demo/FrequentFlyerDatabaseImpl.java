package org.objectweb.dsrg.bpc.demo;

import java.util.*;

public class FrequentFlyerDatabaseImpl implements IFreqFlyerAuth {

	private static final long TIME_UNITS_AFTER_ARRIVAL = 30 * 60;

	protected IFlyTicketDb iFlyTicketDb;
	protected IFlyTicketAuth iFlyTicketAuth;
	
	protected Hashtable database;

	public FrequentFlyerDatabaseImpl() {
		database = Demo_DatabaseGenerator.loadDatabase(DataRow_FrequentFlyerDatabase.DATABASE_NAME);
	}

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf)
	{
		if (cItf.equals("IFlyTicketDb")) {
			iFlyTicketDb = (IFlyTicketDb)sItf;
		} else if (cItf.equals("IFlyTicketAuth")) {
			iFlyTicketAuth = (IFlyTicketAuth)sItf;
		}
	}

	public void unbindFc(String cItf) 
	{
		if (cItf.equals("IFlyTicketDb")) {
			iFlyTicketDb=null;
		} else if (cItf.equals("IFlyTicketAuth")) {
			iFlyTicketAuth=null;
		}
	}

	//
	// Business methods
	//
	
	//
	// IFreqFlyerAuth interface
	//
	
	public IToken CreateToken(String FrequentFlyerId) {
		//System.out.println("..... FrequentFlyerDatabase.CreateToken(" + FrequentFlyerId + ") .....");
		
		if (FrequentFlyerId == null) return null;
		
		if (!database.containsKey(FrequentFlyerId)) {
			return null;
		}
	
		String[] ticketIds = iFlyTicketDb.GetFlyTicketsByFrequentFlyerId(FrequentFlyerId);
		
		//System.out.println("   .. tickets = " + ticketIds.length + ":");
		
		if (ticketIds == null || ticketIds.length == 0) {
			return null;
		}
		
		//System.out.println("   .. " + ticketIds[0] + " ..");
		
		int bestId = 0;
		Date bestValidity = iFlyTicketDb.GetFlyTicketValidity(ticketIds[0]);
		for (int i = 1; i < ticketIds.length; i++) {
		
			//System.out.println("   .. " + ticketIds[i] + " ..");
		
			Date validity = iFlyTicketDb.GetFlyTicketValidity(ticketIds[i]);
			if (validity.after(bestValidity)) {
				bestId = i;
				bestValidity = validity;
			}
		}
		
		IToken token = iFlyTicketAuth.CreateToken(ticketIds[bestId], false);
		
		if (token != null) {
			token.SetValidity(new Date(bestValidity.getTime() + TIME_UNITS_AFTER_ARRIVAL * 1000));

			// JPF does not like Date.toString
			//System.out.println("   .. chosen " + ticketIds[bestId] + " (" + (bestValidity.getTime() - System.currentTimeMillis())+ ") ..");
			//System.out.println("   .. chosen " + ticketIds[bestId] + " (" + bestValidity.toString() + ") ..");
		}

		return token;
	}

}
