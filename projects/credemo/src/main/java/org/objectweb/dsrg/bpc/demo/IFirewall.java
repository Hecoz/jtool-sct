package org.objectweb.dsrg.bpc.demo;

public interface IFirewall {
	boolean DisablePortBlock(String IpAddress);
	boolean EnablePortBlock(String IpAddress);
}
