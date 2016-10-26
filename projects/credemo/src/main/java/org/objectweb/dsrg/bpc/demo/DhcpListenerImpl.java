package org.objectweb.dsrg.bpc.demo;

public class DhcpListenerImpl implements IDhcpListenerCallback, ILife {

	protected IDhcpListenerCallback iDhcpListenerCallback;

	public DhcpListenerImpl() {
	}

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("IDhcpListenerCallback")) {
			iDhcpListenerCallback = (IDhcpListenerCallback)sItf;
		}
	}

	public void unbindFc(String cItf)
	{
		if (cItf.equals("IDhcpListenerCallback")) {
			iDhcpListenerCallback=null;
		}
	}

	//	
	// Business methods
	//
	
	//
	// ILife interface
	//

	public void Start() {
	}
	
	//
	// IDhcpListenerCallback interface (needed only for the Simulator class)
	//
	
	public boolean ReleaseIpAddress(byte[] MacAddress, String IpAddress) {
		return iDhcpListenerCallback.ReleaseIpAddress(MacAddress, IpAddress);
	}

	public boolean RenewIpAddress(byte[] MacAddress, String IpAddress) {
		return iDhcpListenerCallback.RenewIpAddress(MacAddress, IpAddress);
	}

	public String RequestNewIpAddress(byte[] MacAddress) {
		return iDhcpListenerCallback.RequestNewIpAddress(MacAddress);
	}
}
