package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public interface IToken {
	boolean InvalidateAndSave();
    
	void SetValidity(Date ValidUntil);
	void SetAccountCredentials(String AccountId, String SecurityCookie);
	void SetEvidence(Object TokenEvidence);
	String GetUniqueId();

	void StartToken();
}
