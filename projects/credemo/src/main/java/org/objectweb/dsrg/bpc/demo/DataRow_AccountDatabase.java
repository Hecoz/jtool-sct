package org.objectweb.dsrg.bpc.demo;

public class DataRow_AccountDatabase {
	public static final String DATABASE_NAME = "Database_AccountDatabase.serialized";

	public String AccountId;
	public String PasswordHash;
	public long PrepaidTime;	// Prepaid time in seconds
	
	public Object getKey() { return AccountId; }
}

