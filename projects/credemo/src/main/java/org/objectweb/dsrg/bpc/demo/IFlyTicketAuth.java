package org.objectweb.dsrg.bpc.demo;

public interface IFlyTicketAuth {
	IToken CreateToken(String FlyTicketId, boolean RestrictValidity);
}
