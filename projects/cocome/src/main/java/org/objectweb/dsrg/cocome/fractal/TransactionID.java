package org.objectweb.dsrg.cocome.fractal;

/**
 * Represents a transaction id issued to validate a pin number
 */
public class TransactionID {

	private final long id;
	
	public TransactionID(final long id) {
		this.id = id;
	}
	
	@Override
  public boolean equals(final Object o) {
		if (o instanceof TransactionID) {
			return ((TransactionID)o).id == id;
		}
		return false;
	}
}
