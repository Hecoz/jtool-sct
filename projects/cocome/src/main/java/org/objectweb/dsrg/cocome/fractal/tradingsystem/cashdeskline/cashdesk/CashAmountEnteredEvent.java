package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This event signals the entering of a cash amount at the cash box keyboard
 * after taking cash from the customer. It is raised by the cash box controller
 * component after EVERY key stroke, <code>isFinalInput()
 * </code> is true if
 * the final input is entered.
 * 
 */
public class CashAmountEnteredEvent {

	private final KeyStroke keystroke;

	public CashAmountEnteredEvent(final KeyStroke keystroke) {
		this.keystroke = keystroke;
	}

	public KeyStroke getKeyStroke() {
		return keystroke;
	}
}
