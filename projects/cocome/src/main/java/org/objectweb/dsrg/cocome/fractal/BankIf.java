package org.objectweb.dsrg.cocome.fractal;

/**
 * Interface to the Bank
 */
public interface BankIf {
	/**
	 * Used to validate a credit card
	 * 
	 * @param cardInformation
	 * @param pinnumber
	 * @return
	 * @throws RemoteException
	 */
	TransactionID validateCard(String cardInformation, int pinnumber);

	/**
	 * Used to debit an bank account
	 * 
	 * @param id
	 *            The corresponding TransactionID
	 * @return
	 * @throws RemoteException
	 */
	Debit debitCard(TransactionID id);
}
