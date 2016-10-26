package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.AccountSaleEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.AccountSaleEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.CashDeskEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.CoordinatorEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.CoordinatorEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.SaleRegisteredEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashDeskAppEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ExpressModeEnabledEvent;


public class CashDeskLineBusImpl implements CashDeskEventDispatcherIf, CoordinatorEventDispatcherIf
{

	// Required interfaces
	protected CashDeskAppEventHandlerIf CashDeskAppEventHandlerIf;
	protected AccountSaleEventHandlerIf AccountSaleEventHandlerIf;
	protected CoordinatorEventHandlerIf CoordinatorEventHandlerIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("CashDeskAppEventHandlerIf")) {
			CashDeskAppEventHandlerIf = (CashDeskAppEventHandlerIf) serverItf;
			return;
		}
		if (clientItfName.equals("AccountSaleEventHandlerIf")) {
			AccountSaleEventHandlerIf = (AccountSaleEventHandlerIf) serverItf;
			return;
		}
		if (clientItfName.equals("CoordinatorEventHandlerIf")) {
			CoordinatorEventHandlerIf = (CoordinatorEventHandlerIf) serverItf;
			return;
		}
	}
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public CashDeskLineBusImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the CashDeskEventDispatcherIf interface 'CashDeskEventDispatcherIf'
	// -----------------------------------------------------
		
	public void sendSaleRegisteredEvent(final SaleRegisteredEvent saleRegisteredEvent)  {
		CoordinatorEventHandlerIf.onSaleRegisteredEvent(saleRegisteredEvent);		
	}
	
	public void sendAccountSaleEvent(final AccountSaleEvent accountSaleEvent)  {
		AccountSaleEventHandlerIf.onAccountSaleEvent(accountSaleEvent);
	}
	
	// -----------------------------------------------------
	// Implementation of the CoordinatorEventDispatcherIf interface 'CoordinatorEventDispatcherIf'
	// -----------------------------------------------------
		
	public void sendExpressModeEnabledEvent(final ExpressModeEnabledEvent expressModeEnabledEvent)  {
		CashDeskAppEventHandlerIf.onExpressModeEnabledEvent(expressModeEnabledEvent);
	}
	
}

