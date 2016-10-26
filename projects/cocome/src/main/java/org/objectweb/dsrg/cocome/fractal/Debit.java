package org.objectweb.dsrg.cocome.fractal;

/**
 * Description of the possible return values of the debit operation at the Bank
 * interface
 */
public enum Debit {
	OK, 
	NOT_ENOUGH_MONEY, 
	TRANSACTION_ID_NOT_VALID
}
