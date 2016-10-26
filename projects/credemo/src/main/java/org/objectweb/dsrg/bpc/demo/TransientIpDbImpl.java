package org.objectweb.dsrg.bpc.demo;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransientIpDbImpl implements IIpMacDb {

	protected Map ipMac = new HashMap();
	protected Map macIp = new HashMap();
	protected Map expTimes = new HashMap();
    
	public TransientIpDbImpl() {
	}

	
	private static class ByteArrayWrapper
	{
	  private final byte[] bytearray;

    public ByteArrayWrapper(byte[] bytearray)
    {
      this.bytearray = bytearray;
    }
    
    @Override
    public int hashCode()
    {
      return Arrays.hashCode(bytearray);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if(obj == null)
      {
        return false;
      }
      if(!(obj instanceof ByteArrayWrapper))
      {
        return false;
      }
      ByteArrayWrapper other = (ByteArrayWrapper) obj;
      return Arrays.equals(bytearray, other.bytearray);
    }

	}

	//
	// Business methods
	//
	
	//
	// IIpMacDb interface
	//
	
	public synchronized void Add(byte[] MacAddress, String IpAddress, Date ExpirationTime) {
		ipMac.put(IpAddress, MacAddress);
		macIp.put(new ByteArrayWrapper(MacAddress), IpAddress);
		expTimes.put(IpAddress, ExpirationTime);
	}

	public synchronized Date GetExpirationTime(String IpAddress) {
		Date expTime = (Date) expTimes.get(IpAddress);
      
		return expTime;
	}

	public synchronized String GetIpAddress(byte[] MacAddress) {
		String ip = (String) macIp.get(new ByteArrayWrapper(MacAddress));
		
		return ip;
	}
    
	public synchronized byte[] GetMacAddress(String IpAddress) {
		byte[] mac = (byte[]) ipMac.get(IpAddress);
        
       	return mac;
	}
    
	public synchronized void Remove(String IpAddress) {
		byte[] mac = (byte[]) ipMac.get(IpAddress);
	
		if (mac != null) {
			ipMac.remove(IpAddress);
			macIp.remove(new ByteArrayWrapper(mac));
			expTimes.remove(IpAddress);
		}
	}

	public synchronized void SetExpirationTime(String IpAddress, Date ExpirationTime) {
		if (expTimes.containsKey(IpAddress)) {
			expTimes.put(IpAddress, ExpirationTime);
		}
	}
}
