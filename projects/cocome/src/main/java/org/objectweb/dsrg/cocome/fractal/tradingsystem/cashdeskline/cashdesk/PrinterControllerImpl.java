package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.PrinterEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashAmountEnteredEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashAmountCompletedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashBoxClosedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ChangeAmountCalculatedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.RunningTotalChangedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.SaleFinishedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.SaleStartedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.SaleSuccessEvent;

public class PrinterControllerImpl implements PrinterEventHandlerIf
{
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public PrinterControllerImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the PrinterEventHandlerIf interface 'PrinterEventHandlerIf'
	// -----------------------------------------------------
		
	public void onSaleStartedEvent(final SaleStartedEvent arg0)  {
		// TODO Generated method
		
	}
	
	public void onSaleFinishedEvent(final SaleFinishedEvent arg0)  {
		// TODO Generated method
		
	}
	
	public void onCashBoxClosedEvent(final CashBoxClosedEvent arg0)  {
		// TODO Generated method
		
	}
	
	public void onRunningTotalChangedEvent(final RunningTotalChangedEvent arg0)  {
		// TODO Generated method
		
	}
	
	public void onChangeAmountCalculatedEvent(final ChangeAmountCalculatedEvent arg0)  {
		// TODO Generated method
		
	}
	
	public void onSaleSuccessEvent(final SaleSuccessEvent arg0)  {
		// TODO Generated method
		
	}
	
	public void onCashAmountEnteredEvent(final CashAmountEnteredEvent arg0)  {
		// TODO Generated method
		
	}
	
	public void onCashAmountCompletedEvent(final CashAmountCompletedEvent arg0)  {
		// TODO Generated method
		
	}
}

