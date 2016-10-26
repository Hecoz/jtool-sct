package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline;

import java.io.Serializable;

/**
 * This event is raised by the cashdesk application component when a sale is
 * finished and registered in the inventory. It contains statistical information
 * about the sale (number of items, mode of payment).
 * 
 */

public class SaleRegisteredEvent implements Serializable {

	private static final long serialVersionUID = 6202472706841986582L;

	private final int numberOfItems;

	private final PaymentMode paymentMode;
	
	private final String cashdesk;

	public SaleRegisteredEvent(final String cashdesk, final int numberOfItems, final PaymentMode paymentMode) {
		this.numberOfItems = numberOfItems;
		this.paymentMode = paymentMode;
		this.cashdesk = cashdesk;
	}

	public int getNumberOfItems() {
		return numberOfItems;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public String getCashdesk() {
		return cashdesk;
	}
}
