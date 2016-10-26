package org.objectweb.dsrg.bpc.demo;

import java.util.Date;
import java.math.BigDecimal;

public class DataRow_CardCenter {
	public static final String DATABASE_NAME = "Database_CardCenter.serialized";
	
	public String CardNumber;
	public Date ExpirationDate;
	public BigDecimal Balance;
	
	public Object getKey() { return CardNumber; }
}

