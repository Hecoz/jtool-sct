package org.objectweb.dsrg.bpc.demo;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class IpAddressManagerImpl implements ITimerCallback, IDhcpListenerCallback, IManagement, ILife {

	private static final long INITIAL_LEASE_TIME = 5;
	private static final long RENEW_LEASE_TIME = 5;

	protected ITimer iTimer;
	protected IIpMacDb iIpMacTransientDb;
	protected IDhcpCallback iDhcpCallback;
	protected IIpMacDb iIpMacPermanentDb;
	protected ILife iListenerLifetimeController;

	private final LinkedList availableAddresses = new LinkedList();
	private final Set assignedAddresses = new HashSet();
	protected boolean usePermanentDb = false;
	protected boolean renewPermanentIps = false; 
	
	public IpAddressManagerImpl() {
		for (int lowestByte=1; lowestByte<8; lowestByte++) {
			availableAddresses.add("192.168.0."+lowestByte);
		}
	}

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("ITimer")) {
			iTimer = (ITimer)sItf;
		} else if (cItf.equals("IIpMacTransientDb")) {
			iIpMacTransientDb = (IIpMacDb)sItf;
		} else if (cItf.equals("IDhcpCallback")) {
			iDhcpCallback = (IDhcpCallback)sItf;
		} else if (cItf.equals("IIpMacPermanentDb")) {
			iIpMacPermanentDb = (IIpMacDb)sItf;
		} else if (cItf.equals("IListenerLifetimeController")) {
			iListenerLifetimeController = (ILife)sItf;
		}
	}

	public void unbindFc(String cItf)
	{
		if (cItf.equals("ITimer")) {
			iTimer=null;
		} else if (cItf.equals("IIpMacTransientDb")) {
			iIpMacTransientDb=null;
		} else if (cItf.equals("IDhcpCallback")) {
			iDhcpCallback=null;
		} else if (cItf.equals("IIpMacPermanentDb")) {
			iIpMacPermanentDb=null;
		} else if (cItf.equals("IListenerLifetimeController")) {
			iListenerLifetimeController=null;
		}
	}


	//
	// Business methods
	//
	
	//
	// ILife interface
	//
	
	public void Start() {
		iListenerLifetimeController.Start();
	}
	
	//
	// ITimerCallback interface
	//

	public void Timeout() {
		Date now = new Date(1000);
		
		synchronized (this)
		{
			// JPF can't handle Date.toString
			//System.out.println("+++++ IpAddressManager.Timeout() @ " + now.getTime() + " , " + assignedAddresses.size() + " +++++");
        
			Iterator it = assignedAddresses.iterator();
			while (it.hasNext()) {
				String addr = (String) it.next();
			
				Date expTime = iIpMacTransientDb.GetExpirationTime(addr);         
			
				// JPF can't handle Date.toString
				//System.out.println("   -- probing " + addr + " @ " + ((expTime != null) ? String.valueOf(expTime.getTime()) : "null") + ", now = " + ((now != null) ? String.valueOf(now.getTime()) : "null"));
				     
				if ((expTime != null) && (expTime.getTime() <= now.getTime() + 500)) {
					//System.out.println("   ++ lease expired for " + addr);
			
					iIpMacTransientDb.Remove(addr);
					assignedAddresses.remove(it);
					availableAddresses.addLast(addr);
					iDhcpCallback.IpAddressInvalidated(addr);                    
				}
			}
		}               
	}
	
	//
	// IDhcpListenerCallback interface
	//
    
	public boolean ReleaseIpAddress(byte[] MacAddress, String IpAddress) {
		//System.out.println("+++++ IpAddressManager.ReleaseIpAddress " + IpAddress + " +++++");
	
		String storedIp = iIpMacTransientDb.GetIpAddress(MacAddress);

		//if (storedIp == null || !storedIp.equals(IpAddress)) {
		if (storedIp == null) return false;
		if (IpAddress == null) return false;
		if (!storedIp.toString().equals(IpAddress.toString())) return false;

		iIpMacTransientDb.Remove(IpAddress);
        
		synchronized (this) 
		{
			releaseIpAddress(IpAddress);
			iDhcpCallback.IpAddressInvalidated(IpAddress);
	
			if (!haveAssignedAddresses()) {
				iTimer.CancelTimeouts();
			}
		}
		
		return true;
	}
    
	public boolean RenewIpAddress(byte[] MacAddress, String IpAddress) {
		//System.out.println("+++++ IpAddressManager.RenewIpAddress " + IpAddress + " +++++");
		
		String storedIp = iIpMacTransientDb.GetIpAddress(MacAddress);

		//if (storedIp == null || !storedIp.equals(IpAddress)) {
		if (storedIp == null) return false;
		if (IpAddress == null) return false;
		if (!storedIp.toString().equals(IpAddress.toString())) return false;
		
		synchronized (this)
		{
			if (usePermanentDb && !renewPermanentIps) {
				String permIp = iIpMacPermanentDb.GetIpAddress(MacAddress);
				if (permIp != null && permIp.equals(IpAddress)) return false;
			}
		}
		
		Date expTime = new Date(RENEW_LEASE_TIME * 1000);
        
		iIpMacTransientDb.SetExpirationTime(IpAddress, expTime);       
		iTimer.SetTimeout(expTime);

		return true;
	}
    
	public String RequestNewIpAddress(byte[] MacAddress) {
		//System.out.println("+++++ IpAddressManager.RequestIpAddress" + MacAddress + " +++++");

		String ipAddr = iIpMacTransientDb.GetIpAddress(MacAddress);
		if (ipAddr == null) {
			synchronized (this) // because of usePermanentDb field
			{
				if (usePermanentDb) {
					ipAddr = iIpMacPermanentDb.GetIpAddress(MacAddress);
				} else {
					ipAddr = allocNewIpAddress();
				}
			}
		}
		if (ipAddr == null) {
			return null;
		}
        
		Date expTime = new Date(INITIAL_LEASE_TIME * 1000);

		iIpMacTransientDb.Add(MacAddress, ipAddr, expTime);
		iTimer.SetTimeout(expTime);

		return ipAddr;
	}
    
	//
	// IManagement interface
	//
    
	public synchronized void StopRenewingPermanentIpAddresses() {
		renewPermanentIps = false;
	}
    
	public synchronized void StopUsingPermanentIpDatabase() {
		usePermanentDb = false;
	}
    
	public synchronized void UsePermanentIpDatabase() {
		usePermanentDb = true;
		renewPermanentIps = true;
	}
    
	//
	// Private methods
	//
	
	private String allocNewIpAddress() {
		String ipAddr = (String) availableAddresses.removeFirst();
		
		if (ipAddr != null) {
			assignedAddresses.add(ipAddr);
		}
    
		return ipAddr;
	}
    
	private void releaseIpAddress(String addr) {
		assignedAddresses.remove(addr);
		availableAddresses.addLast(addr);
	}
    
	private boolean haveAssignedAddresses() {
		return !assignedAddresses.isEmpty();
	}
}
