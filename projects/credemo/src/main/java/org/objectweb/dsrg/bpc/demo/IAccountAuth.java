package org.objectweb.dsrg.bpc.demo;

public interface IAccountAuth {
	IToken CreateToken(String AccountId, String Password);
}
