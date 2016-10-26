package org.objectweb.dsrg.bpc.demo;

public interface ICustomCallback {
	void SetAccountCredentials(String AccountId, String SecurityCookie);
	boolean InvalidatingToken(long TimeLeft);
}
