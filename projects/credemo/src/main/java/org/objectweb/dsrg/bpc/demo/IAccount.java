package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public interface IAccount {
	String GenerateRandomAccountId();
	boolean CreateAccount(String AccountId, String Password);
	boolean RechargeAccount(String AccountId, String CreditCardId, Date CreditCardExpirationDate, long PrepaidTime);
	void AdjustAccountPrepaidTime(String AccondId, String SecurityCookie, long PrepaidTime);
}
