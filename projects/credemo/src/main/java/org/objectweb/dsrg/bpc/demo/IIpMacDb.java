package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public interface IIpMacDb {
	void Add(byte[] MacAddress, String IpAddress, Date ExpirationTime);
	void Remove(String IpAddress);
	byte[] GetMacAddress(String IpAddress);
	String GetIpAddress(byte[] MacAddress);
	java.util.Date GetExpirationTime(String IpAddress);
	void SetExpirationTime(String IpAddress, Date ExpirationTime);
}
