package org.objectweb.dsrg.bpc.demo;

import java.util.*;
import java.math.BigDecimal;

public class Demo_DatabaseGenerator {
	public static final int ACCOUNTS = 3;
	public static final int CREDIT_CARDS = 3;
	public static final int AF_TICKETS = 3;
	public static final int CSA_TICKETS = 3;
	public static final int FREQ_FLYERS = 2;
	
	public static Hashtable loadDatabase(String DatabaseName) {
		Hashtable list = new Hashtable();
	
		if (DatabaseName.equals(DataRow_AccountDatabase.DATABASE_NAME)) {
			
			//
			// AccountDatabase
			//
		
			DataRow_AccountDatabase row;
		
			for (int i = 1; i <= ACCOUNTS; i++) {
				row = new DataRow_AccountDatabase();
				row.AccountId = Integer.toString(1000 + i);
				row.PasswordHash = "Password" + Integer.toString(i);
				row.PrepaidTime = 0;
				list.put(row.getKey(), row);
			}
	
		} else if (DatabaseName.equals(DataRow_CardCenter.DATABASE_NAME)) {
			
			//
			// CardCenter
			//
		
			DataRow_CardCenter row2;
		
			for (int i = 1; i <= CREDIT_CARDS; i++) {
				row2 = new DataRow_CardCenter();
				row2.CardNumber = "1111-2222-3333-" + Integer.toString(4400 + i);
				row2.ExpirationDate = new Date(Simulator.CARD_EXPDATE_MS);
				row2.Balance = new BigDecimal("500.00");
				list.put(row2.getKey(), row2);
			}
			
		} else if (DatabaseName.equals(DataRow_AfDbConnection.DATABASE_NAME)) {
			
			//
			// AfDbConnection
			//
		
			DataRow_AfDbConnection row;
		
			for (int i = 1; i <= AF_TICKETS; i++) {
				row = new DataRow_AfDbConnection();
				row.TicketId = FlyTicketClassifierImpl.AF_TICKET_PREFIX + 
					Integer.toString(1000 + i) +
					((i % 2 == 0) ? "B" : "E");
				row.ArrivalTime = new Date(i * 60 * 1000);
				row.FrequentFlyerId = ((i % 5 == 0) ? "SKY1001" : null);
				if (i % 10 == 0) row.FrequentFlyerId = "SKY1002";
				list.put(row.getKey(), row);
			}
			
		} else if (DatabaseName.equals(DataRow_CsaDbConnection.DATABASE_NAME)) {
			
			//
			// CsaDbConnection
			//
		
			DataRow_CsaDbConnection row;
		
			for (int i = 1; i <= CSA_TICKETS; i++) {
				row = new DataRow_CsaDbConnection();
				row.TicketId = FlyTicketClassifierImpl.CSA_TICKET_PREFIX + 
					Integer.toString(1000 + i) +
					((i % 2 == 0) ? "B" : "E");
				row.ArrivalTime = new Date(i * 60 * 1000);
				row.FrequentFlyerId = ((i % 5 == 0) ? "SKY1002" : null);
				if (i % 10 == 0) row.FrequentFlyerId = "SKY1001";
				list.put(row.getKey(), row);
			}
			
		} else if (DatabaseName.equals(DataRow_FrequentFlyerDatabase.DATABASE_NAME)) {
			
			//
			// FrequentFlyerDatabase
			//
		
			DataRow_FrequentFlyerDatabase row;
		
			for (int i = 1; i <= FREQ_FLYERS; i++) {
				row = new DataRow_FrequentFlyerDatabase();
				row.FlyerId = "SKY" + Integer.toString(1000 + i);
				list.put(row.getKey(), row);
			}
		}
	
		return list;
	}

	/*
	public static Hashtable loadSerializedDatabase(String DatabaseName) {
		try {
			FileInputStream fis = new FileInputStream(DatabaseName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			Hashtable list = (Hashtable) ois.readObject();
			
			ois.close();
			fis.close();
			
			return list;
		} catch (IOException ex) {
			return null;
		} catch (ClassNotFoundException ex) {
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println("Generating demo databases ...");
	
		FileOutputStream fos;
		ObjectOutputStream oos;	
		Hashtable list;
	
		try {
			//
			// AccountDatabase
			//
		
			fos = new FileOutputStream(DataRow_AccountDatabase.DATABASE_NAME);
			oos = new ObjectOutputStream(fos);
			list = new Hashtable();
			DataRow_AccountDatabase row;
		
			for (int i = 1; i <= ACCOUNTS; i++) {
				row = new DataRow_AccountDatabase();
				row.AccountId = Integer.toString(1000 + i);
				row.PasswordHash = "Password" + Integer.toString(i);
				row.PrepaidTime = 0;
				list.put(row.getKey(), row);
			}
	
			oos.writeObject(list);
			oos.flush();
			oos.close();
			fos.close();
			
			//
			// CardCenter
			//
		
			fos = new FileOutputStream(DataRow_CardCenter.DATABASE_NAME);
			oos = new ObjectOutputStream(fos);
			list = new Hashtable();
			DataRow_CardCenter row2;
		
			for (int i = 1; i <= CREDIT_CARDS; i++) {
				row2 = new DataRow_CardCenter();
				row2.CardNumber = "1111-2222-3333-" + Integer.toString(4400 + i);
				row2.ExpirationDate = new Date(2006, 10, 1);
				row2.Balance = new BigDecimal("500.00");
				list.put(row2.getKey(), row2);
			}
		
			oos.writeObject(list);
			oos.flush();
			oos.close();
			fos.close();
			
		} catch (IOException ex) {
		}
		
		System.out.println("Done.");
	}
	*/
}
