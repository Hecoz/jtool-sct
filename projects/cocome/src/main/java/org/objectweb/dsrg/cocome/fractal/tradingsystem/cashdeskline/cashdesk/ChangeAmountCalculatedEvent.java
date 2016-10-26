package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This event is raised by the cashdesk application component after having
 * calculated the change amount during cash payment.
 * 
 */
public class ChangeAmountCalculatedEvent {

	private final double changeAmount;

	public ChangeAmountCalculatedEvent(final double changeAmount) {
		this.changeAmount = changeAmount;
	}

	public double getChangeAmount() {
		return changeAmount;
	}
}
