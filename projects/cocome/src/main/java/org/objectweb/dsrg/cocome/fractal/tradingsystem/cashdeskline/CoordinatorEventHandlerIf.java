package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline;


/**
 * This interface defines an event handler for event related to the cash desk
 * coordination task.
 * 
 */
public interface CoordinatorEventHandlerIf {
	public void onSaleRegisteredEvent(SaleRegisteredEvent saleRegisteredEvent);
}
