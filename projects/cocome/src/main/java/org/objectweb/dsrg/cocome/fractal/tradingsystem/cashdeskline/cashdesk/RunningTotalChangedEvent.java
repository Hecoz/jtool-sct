package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This event is raised by the cashdesk application component after another item
 * has been scanned, identified and internally added to the current sale. It
 * contains information about the current item, its price and the running total.
 * 
 */
public class RunningTotalChangedEvent {

	private final String productName;

	private final double productPrice;

	private final double runningTotal;

	public RunningTotalChangedEvent(final String productName, final double productPrice,
			final double runningTotal) {
		this.productName = productName;
		this.productPrice = productPrice;
		this.runningTotal = runningTotal;
	}

	public String getProductName() {
		return productName;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public double getRunningTotal() {
		return runningTotal;
	}
}
