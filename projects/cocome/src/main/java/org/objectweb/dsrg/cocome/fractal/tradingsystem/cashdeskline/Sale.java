package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline;

import java.util.Date;

/**
 * Helper class to keep track of the sales
 */
public class Sale {
	private final int numberofItems;
	private final PaymentMode paymentmode;
	private final Date timeofSale;
	
	protected Sale(final int numberofItems, final PaymentMode paymentmode, final Date timeofSale) {
		this.numberofItems = numberofItems;
		this.paymentmode = paymentmode;
		this.timeofSale = timeofSale;
	}

	public int getNumberofItems() {
		return numberofItems;
	}

	public PaymentMode getPaymentmode() {
		return paymentmode;
	}

	public Date getTimeofSale() {
		return timeofSale;
	}
}
