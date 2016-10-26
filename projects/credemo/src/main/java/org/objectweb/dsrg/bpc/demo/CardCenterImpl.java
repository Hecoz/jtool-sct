package org.objectweb.dsrg.bpc.demo;
import java.math.BigDecimal;
import java.util.Date;
import java.util.*;

public class CardCenterImpl implements ICardCenter {

	private final Hashtable database;

	public CardCenterImpl() {
		database = Demo_DatabaseGenerator.loadDatabase(DataRow_CardCenter.DATABASE_NAME);
	}

	//
	// Business methods
	//
	
	public boolean Withdraw(String CreditCardId, Date CreditCardExpirationDate, BigDecimal Amount) {
		//System.out.print("----- CardCenter.Withdraw: ");

		if (!database.containsKey(CreditCardId)) {
			//System.out.println("** Invalid CreditCardId **");
			return false;
		}
		
		DataRow_CardCenter row = (DataRow_CardCenter) database.get(CreditCardId);
		
		if ((!row.ExpirationDate.equals(CreditCardExpirationDate)) || row.Balance.compareTo(Amount) < 0) {
			//System.out.println("** Invalid ExpirationDate or not enough money **");
			return false;
		}
		
		//System.out.print(row.Balance.toString() + " - " + Amount.toString() + " => ");
		
		row.Balance = row.Balance.subtract(Amount);
		
		//System.out.println(row.Balance);
		
		return true;
	}

}
