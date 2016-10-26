package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This event is raised by the cashdesk coordinator component.
 *
 */
public class ExpressModeEnabledEvent {

	private final String cashdesk;
	
	public ExpressModeEnabledEvent(final String cashdesk) {
		this.cashdesk = cashdesk;
	}

	public String getCashdesk() {
		return cashdesk;
	}
}
