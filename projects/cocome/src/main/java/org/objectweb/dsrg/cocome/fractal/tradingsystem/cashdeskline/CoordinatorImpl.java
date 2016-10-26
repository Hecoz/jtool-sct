package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ExpressModeEnabledEvent;


public class CoordinatorImpl implements CoordinatorEventHandlerIf
{
	protected CoordinatorEventDispatcherIf CoordinatorEventDispatcherIf;
	
	// -----------------------------------------------------
	// Implementation of the BindingController interface
	// -----------------------------------------------------
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("CoordinatorEventDispatcherIf")) {
			CoordinatorEventDispatcherIf = (CoordinatorEventDispatcherIf) serverItf;
			return;
		}
	}

	
	private final List<Sale> sales = new ArrayList<Sale>();
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public CoordinatorImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the CoordinatorEventHandlerIf interface 'CoordinatorEventHandlerIf'
	// -----------------------------------------------------
		
	public void onSaleRegisteredEvent(final SaleRegisteredEvent saleRegisteredEvent)  {
		synchronized (sales) {
			updateStatistics(saleRegisteredEvent.getNumberOfItems(),
					saleRegisteredEvent.getPaymentMode());
			if (isExpressModeNeeded()) {
				//System.out.println("Coordinator: ExpressMode activated");
				CoordinatorEventDispatcherIf.sendExpressModeEnabledEvent(
					new ExpressModeEnabledEvent("???CashDeskName???")		// FIXME: Fill in correct string
				);
			} else {
				// The disabling of the ExpressMode is done manually by the cashier
				// at the CashDeskGUI
			}
		}
	}
	
	private void updateStatistics(final int numberofitems, final PaymentMode paymentmode) {
		sales.add(new Sale(numberofitems, paymentmode, new Date(800 + numberofitems * 50)));
	}

	/*
	 * Checks the condition for UC 2:ManageExpressCheckout:
	 * 50% of all sales during the last 60 minutes meet the requirements of an express checkout.
	 * That is: up to 8 products per sale and customer pays cash.
	 */
	private boolean isExpressModeNeeded() {
		final Date now = new Date(1000);
		int meetscondition = 0;
		int total = 0;
		// clean list of old (non-relevant) entries
		for (final Iterator<Sale> i = sales.iterator(); i.hasNext();) {
			final Sale s = i.next();
			if (s.getTimeofSale().getTime() + 100 < now.getTime()) {
				i.remove();
			} else {
				if (s.getPaymentmode().equals(PaymentMode.CASH)
						&& s.getNumberofItems() <= 8) {
					meetscondition++;
				}
				total++;
			}
		}
		if (((double) meetscondition) / ((double) total) > 0.5) {
			return true;
		}
		return false;
	}
}

