package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashBoxEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashBoxEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashAmountEnteredEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ChangeAmountCalculatedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.KeyStroke;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.*;
import org.objectweb.dsrg.cocome.fractal.Simulator;


public class CashBoxControllerImpl implements CashBoxEventHandlerIf
{
	// Required interface
	protected CashBoxEventDispatcherIf CashBoxEventDispatcherIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("CashBoxEventDispatcherIf")) {
			CashBoxEventDispatcherIf = (CashBoxEventDispatcherIf) serverItf;
			return;
		}
	}

	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

  public CashBoxControllerImpl(final Simulator simulator)
    {
    simulator.registerCashBoxController(this);
    }
    
	// -----------------------------------------------------
	// Implementation of the CashBoxEventHandlerIf interface 'CashBoxEventHandlerIf'
	// -----------------------------------------------------
		
	public void onChangeAmountCalculatedEvent(final ChangeAmountCalculatedEvent arg0)  {
		// TODO Generated method
	}
	
	// -----------------------------------------------------
	// Simulation related methods
	// -----------------------------------------------------
	
	public void simulatedCashAmountEntered(final KeyStroke keystroke) {
		CashBoxEventDispatcherIf.sendCashAmountEnteredEvent(
			new CashAmountEnteredEvent(keystroke)
		);
	}
	
	public void simulatedSaleStarted() {
		CashBoxEventDispatcherIf.sendSaleStartedEvent(
			new SaleStartedEvent()
		);
	}
	
	public void simulatedSaleFinished() {
		CashBoxEventDispatcherIf.sendSaleFinishedEvent(
			new SaleFinishedEvent()
		);
	}
	
	public void simulatedPaymentMode(final PaymentMode mode) {
		CashBoxEventDispatcherIf.sendPaymentModeEvent(
			new PaymentModeEvent(mode)
		);
	}
}

