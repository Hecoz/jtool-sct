package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.PaymentMode;

public class PaymentModeEvent {

	private final PaymentMode mode;

	public PaymentModeEvent(final PaymentMode mode) {
		this.mode = mode;
	}

	public PaymentMode getMode() {
		return mode;
	}

}
