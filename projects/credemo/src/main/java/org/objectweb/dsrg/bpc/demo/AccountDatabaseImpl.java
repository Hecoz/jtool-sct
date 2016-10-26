package org.objectweb.dsrg.bpc.demo;

import java.math.BigDecimal;
import java.util.*;

public class AccountDatabaseImpl implements IAccountAuth, IAccount {
	private static final double TIME_UNIT_PRICE = 0.5;
	private static final long TOKEN_VALIDITY = 30 * 60;
	private static final String SECURITY_COOKIE = "++SecretCookie++";

	protected ICardCenter iCardCenter;

	private int nextAccountId = 2000;
	private final Hashtable database;
	private final Simulator simComp;
	
	public AccountDatabaseImpl(Simulator simComp) {
		database = Demo_DatabaseGenerator.loadDatabase(DataRow_AccountDatabase.DATABASE_NAME);
		this.simComp = simComp;
		// System.out.println(" ---- AccountDatabase.AccountDatabase(): loaded database -----");
		// for (Iterator it = database.values().iterator(); it.hasNext(); ) {
		// 	DataRow_AccountDatabase row = (DataRow_AccountDatabase) it.next();
		// 	System.out.println("   -- " + row.AccountId + ", " + row.PasswordHash);
		// }
	}

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("ICardCenter")) {
			iCardCenter = (ICardCenter)sItf;
		}
	}

	public void unbindFc(String cItf) 
	{
		if (cItf.equals("ICardCenter")) {
			iCardCenter=null;
		}
	}

	//
	// IAccountAuth interface
	//
	
	public IToken CreateToken(String AccountId, String Password) {
		//System.out.println(" ---- AccountDatabase.CreateToken(" + AccountId + ", " + Password + ") -----");
		
		if (AccountId == null || Password == null) return null;
		
		if (!database.containsKey(AccountId)) {
			//System.out.println("   ** Invalid AccountId **");
			return null;
		}
		
		DataRow_AccountDatabase row = (DataRow_AccountDatabase) database.get(AccountId);
		if ((!row.PasswordHash.equals(Password)) || row.PrepaidTime == 0) {
			//System.out.println("   ** Invalid Password or PrepaidTime is 0 **");
			return null;
		}
	
		IToken token = simComp.createAccountToken();
		
		long TokenValidity = Math.min(row.PrepaidTime, TOKEN_VALIDITY);
		row.PrepaidTime -= TokenValidity;
		
		token.SetValidity(new Date(TokenValidity * 1000));
		token.SetAccountCredentials(AccountId, SECURITY_COOKIE);
		
		//System.out.println(
		//	"   -- AccountId " + AccountId + ": " + Long.toString(row.PrepaidTime + TokenValidity) +
		//	" - " + Long.toString(TokenValidity) + " => " + Long.toString(row.PrepaidTime)
		//);
        
		return token;
	}
	
	//
	// IAccount interface
	//
    
	public void AdjustAccountPrepaidTime(String AccountId, String SecurityCookie, long PrepaidTime) {
		//System.out.print("  --- AccountDatabase.AdjustAccountPrepaidTime: ");
		//System.out.println(AccountId + ", " + SecurityCookie + ", " + Long.toString(PrepaidTime) + " -----");
	
		if (AccountId == null || SecurityCookie == null) return;	
		if (database.containsKey(AccountId) && SecurityCookie.equals(SECURITY_COOKIE)) {
			DataRow_AccountDatabase row = (DataRow_AccountDatabase) database.get(AccountId);
			row.PrepaidTime += PrepaidTime;
			
			//System.out.println(
			//	"   -- AccountId " + AccountId + ": " + Long.toString(row.PrepaidTime - PrepaidTime) +
			//	" + " + Long.toString(PrepaidTime) + " => " + Long.toString(row.PrepaidTime)
			//);
		}
	}

	public boolean CreateAccount(String AccountId, String Password) {
		if (AccountId == null || Password == null) return false;
	
		if (database.containsKey(AccountId)) {
			return false;
		}
		
		DataRow_AccountDatabase row = new DataRow_AccountDatabase();
		row.AccountId = AccountId;
		row.PasswordHash = Password;
		row.PrepaidTime = 0;
		
		return true;
	}
    
	public String GenerateRandomAccountId() {    
		String AccountId;
		
		do {
			AccountId = Integer.toString(nextAccountId++);
		} while (database.containsKey(AccountId));
		
		return AccountId;
	}
    
	public boolean RechargeAccount(String AccountId, String CreditCardId, Date CreditCardExpirationDate, long PrepaidTime) {
		if (AccountId == null || CreditCardId == null || CreditCardExpirationDate == null) return false;
		
		if (!database.containsKey(AccountId)) {
			return false;
		}
		
		boolean result = iCardCenter.Withdraw(
			CreditCardId, CreditCardExpirationDate, new BigDecimal(TIME_UNIT_PRICE * PrepaidTime)
		);
       
       		if (!result) {
			return false;
		} else {
			DataRow_AccountDatabase row = (DataRow_AccountDatabase) database.get(AccountId);
			row.PrepaidTime += PrepaidTime;
			
			//System.out.println(
			//	"   -- AccountId " + AccountId + ": " + Long.toString(row.PrepaidTime - PrepaidTime) +
			//	" + " + Long.toString(PrepaidTime) + " => " + Long.toString(row.PrepaidTime)
			//);
			
			return true;
		}
	}
}
