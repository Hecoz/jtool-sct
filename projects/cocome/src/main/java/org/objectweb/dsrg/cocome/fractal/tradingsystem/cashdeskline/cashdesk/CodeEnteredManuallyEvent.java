package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This event is raised when the cashdesk operator manually enters the 
 * product's barcode
 * 
 */
public class CodeEnteredManuallyEvent {

	private final long enteredBarcode;

	public CodeEnteredManuallyEvent(final long enteredBarcode) {
		this.enteredBarcode = enteredBarcode;
	}

	public long getEnteredBarcode() {
		return enteredBarcode;
	}
}
