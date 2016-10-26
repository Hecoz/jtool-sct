package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public class DataRow_CsaDbConnection {
	public static final String DATABASE_NAME = "Database_CsaDbConnection.serialized";
	
	public String TicketId;
	public Date ArrivalTime;
	public String FrequentFlyerId;
	
	public Object getKey() { return TicketId; }	
}

