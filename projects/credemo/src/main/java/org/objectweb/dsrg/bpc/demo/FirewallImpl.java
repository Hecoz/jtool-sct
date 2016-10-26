package org.objectweb.dsrg.bpc.demo;

public class FirewallImpl implements IFirewall {

	public FirewallImpl() {
	}

	//
	// Business methods
	//
	
	public boolean DisablePortBlock(String IpAddress) {
		return true;
	}

	public boolean EnablePortBlock(String IpAddress) {
		return true;
	}
}
