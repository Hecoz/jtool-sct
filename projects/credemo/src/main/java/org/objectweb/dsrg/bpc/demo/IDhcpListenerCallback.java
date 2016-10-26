package org.objectweb.dsrg.bpc.demo;

public interface IDhcpListenerCallback {
	String RequestNewIpAddress(byte[] MacAddress);
	boolean RenewIpAddress(byte[] MacAddress, String IpAddress);
	boolean ReleaseIpAddress(byte[] MacAddress, String IpAddress);
}
