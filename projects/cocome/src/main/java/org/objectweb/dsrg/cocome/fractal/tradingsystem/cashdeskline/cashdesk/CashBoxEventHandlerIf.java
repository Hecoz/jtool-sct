package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This interface defines an event handler for events related to the cash box.
 */
public interface CashBoxEventHandlerIf {
	void onChangeAmountCalculatedEvent(ChangeAmountCalculatedEvent changeAmountCalculatedEvent);
}
