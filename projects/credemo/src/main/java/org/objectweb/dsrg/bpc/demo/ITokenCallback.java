package org.objectweb.dsrg.bpc.demo;

public interface ITokenCallback {
	void TokenInvalidated(Object TokenEvidence);
	void Notify();
}
