package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This interface defines an event handler for events related to card reader
 * controller.
 */
public interface CardReaderEventDispatcherIf {
	void sendPINEnteredEvent(PINEnteredEvent pINEnteredEvent);

	void sendCreditCardScannedEvent(
			CreditCardScannedEvent creditCardScannedEvent);
}
