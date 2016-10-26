package org.objectweb.dsrg.cocome.fractal.bank;

import org.objectweb.dsrg.cocome.fractal.Debit;
import org.objectweb.dsrg.cocome.fractal.RandomNumberGenerator;
import org.objectweb.dsrg.cocome.fractal.TransactionID;
import org.objectweb.dsrg.cocome.fractal.BankIf;


public class BankImpl implements BankIf
{
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public BankImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the BankIf interface 'BankIf'
	// -----------------------------------------------------
		
	public TransactionID validateCard(final String cardInformation, final int cardnumber)  {
		
		//System.out.println("Bank: Validating credit card \"" + cardInformation + "\", PIN " + Integer.toString(cardnumber) + " ... (" + Integer.toString(requests) + " pending requests of " + Integer.toString(totalRequests) + " total)");
		
		if (cardInformation.equals("blablabla") && cardnumber == 7777) {
			return new TransactionID(23L);
		} else {
			return null;
		}
	}
	
	public Debit debitCard(final TransactionID id)  {
		if (!id.equals(new TransactionID(23L))) {
			return Debit.TRANSACTION_ID_NOT_VALID;
		}
		/*
		if (Verify.getBoolean()) {
			return Debit.NOT_ENOUGH_MONEY;
		}
		*/
		return Debit.OK;
	}
	
}

