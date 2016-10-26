package org.objectweb.dsrg.bpc.demo;

public interface ILogin {
	boolean LoginWithFlyTicketId(String IpAddress, String FlyTicketId);
	boolean LoginWithFrequentFlyerId(String IpAddress, String FrequentFlyerId);
	boolean LoginWithAccountId(String IpAddress, String AccountId, String Password);
	boolean Logout(String IpAddress);
	String GetTokenIdFromIpAddress(String IpAddress);
}
