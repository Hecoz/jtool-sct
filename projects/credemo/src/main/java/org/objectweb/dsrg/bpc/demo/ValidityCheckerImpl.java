package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public class ValidityCheckerImpl implements IToken, ITimerCallback, ILife {

	protected ITokenCallback iTokenCallback;
	protected ITimer iTimer;
	protected ICustomCallback iCustomCallback = null;
	
	protected Object evidence;
	protected Date validUntil;
	
	private final int tokenId;
	
	public ValidityCheckerImpl(int tokenId) {
	  this.tokenId = tokenId;
	}
	
	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("ITokenCallback")) {
			iTokenCallback = (ITokenCallback)sItf;
		} else if (cItf.equals("ITimer")) {
			iTimer = (ITimer)sItf;
		} else if (cItf.equals("ICustomCallback")) {
			iCustomCallback = (ICustomCallback)sItf;
		}
	}

	public void unbindFc(String cItf) 
	{
		if (cItf.equals("ITokenCallback")) {
			iTokenCallback=null;
		} else if (cItf.equals("ITimer")) {
			iTimer=null;
		} else if (cItf.equals("ICustomCallback")) {
			iCustomCallback=null;
		}
	}

	//
	// Business methods
	//
	
	//
	// IToken interface
	//

	public boolean InvalidateAndSave() {
		//System.out.println(" ---- ValidityChecker.InvalidateAndSave -----");

		iTimer.CancelTimeouts();
		callCustomCallback();		
		iTokenCallback.TokenInvalidated(evidence);
		
		return true;
	}
	
	public void SetValidity(Date ValidUntil) {
		validUntil = ValidUntil;
	}
    
	public void SetAccountCredentials(String AccountId, String SecurityCookie) {
		if (iCustomCallback != null) {
			iCustomCallback.SetAccountCredentials(AccountId, SecurityCookie);
		}
	}
	
	public void SetEvidence(Object TokenEvidence) {
		evidence = TokenEvidence;
	}

    
	public String GetUniqueId() {
		return "ID";
	}
    
	public void StartToken() {
		this.Start();
	}
	
	//
	// ITimerCallback interface
	//
    
	public void Timeout() {
		//System.out.println(" ---- ValidityChecker.Timeout -----");

		callCustomCallback();				
		iTokenCallback.TokenInvalidated(evidence);
	}
	
	//
	// ILife interface
	//

	public void Start() {
		iTimer.SetTimeout(validUntil);
	}

	//
	// Private methods
	//
	
	protected void callCustomCallback() {
		if (iCustomCallback != null) {
			long SecondsLeft = Math.max(validUntil.getTime() / 1000, 0);
			iCustomCallback.InvalidatingToken(SecondsLeft);
		}
	}

  @Override
  public int hashCode()
  {
    return tokenId;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof ValidityCheckerImpl))
      return false;
    ValidityCheckerImpl other = (ValidityCheckerImpl) obj;
    if (tokenId != other.tokenId)
      return false;
    return true;
  }
			
}
