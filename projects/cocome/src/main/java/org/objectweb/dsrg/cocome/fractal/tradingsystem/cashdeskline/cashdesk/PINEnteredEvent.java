package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This event is raised by the credit card reader component after entering a
 * PIN.
 * 
 */
public class PINEnteredEvent {

	private final int PIN;

	public PINEnteredEvent(final int pin) {
		this.PIN = pin;
	}

	public int getPIN() {
		return PIN;
	}
}
