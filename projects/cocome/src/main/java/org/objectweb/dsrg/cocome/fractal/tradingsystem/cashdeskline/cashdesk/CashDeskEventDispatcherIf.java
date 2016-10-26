package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.SaleRegisteredEvent;

/**
 * This interface defines an event handler for events related to the cash box.
 */
public interface CashDeskEventDispatcherIf {
	void sendSaleRegisteredEvent(SaleRegisteredEvent saleRegisteredEvent);
}
