package org.objectweb.dsrg.bpc.demo;

public class CustomTokenImpl implements ICustomCallback {

	protected IAccount iAccount;
	
	private String accountId = null;
	private String securityCookie = null;

	public CustomTokenImpl() {
	}


	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("IAccount")) {
			iAccount = (IAccount)sItf;
		}
	}

	public void unbindFc(String cItf) 
	{
		if (cItf.equals("IAccount")) {
			iAccount=null;
		}
	}


	//
	// Business methods
	//
	
	//
	// ICustomCallback interface
	//

	public void SetAccountCredentials(String AccountId, String SecurityCookie) {
		accountId = AccountId;
		securityCookie = SecurityCookie;
	}

	public boolean InvalidatingToken(long TimeLeft) {
		iAccount.AdjustAccountPrepaidTime(accountId, securityCookie, TimeLeft);
		return true;
	}
}
