package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.GUIEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashAmountEnteredEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashAmountCompletedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ChangeAmountCalculatedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CreditCardScanFailedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ExpressModeDisabledEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ExpressModeEnabledEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.InvalidCreditCardEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ProductBarcodeNotValidEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.RunningTotalChangedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.SaleStartedEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.SaleSuccessEvent;
import org.objectweb.dsrg.cocome.fractal.Simulator;

public class CashDeskGUIImpl implements GUIEventHandlerIf
{
	private Object lastEvent = null;
	
	private final String cashDeskName;
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

  public CashDeskGUIImpl(final Simulator simulator)
    {
    cashDeskName =
        "CashDesk " + Integer.toString(simulator.registerCashDeskGUI(this));
    }
    
	// -----------------------------------------------------
	// Implementation of the GUIEventHandlerIf interface 'GUIEventHandlerIf'
	// -----------------------------------------------------
		
	public void onSaleStartedEvent(final SaleStartedEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Sale started.");
		lastEvent = arg0;
	}
	
	public void onCashAmountEnteredEvent(final CashAmountEnteredEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Cash amount entered: " + arg0.getKeyStroke().toString());		
		lastEvent = arg0;
	}

	public void onCashAmountCompletedEvent(final CashAmountCompletedEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Cash amount completed.");		
		lastEvent = arg0;
	}
	
	public void onExpressModeEnabledEvent(final ExpressModeEnabledEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Express mode enabled on cash desk: " + arg0.getCashdesk());
		lastEvent = arg0;
	}
	
	public void onExpressModeDisabledEvent(final ExpressModeDisabledEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Express mode disabled.");		
		lastEvent = arg0;
	}
	
	public void onRunningTotalChangedEvent(final RunningTotalChangedEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Running total changed:");
		//System.out.println(cashDeskName + ":      Product name: " + arg0.getProductName());
		//System.out.println(cashDeskName + ":      Product price: " + arg0.getProductPrice());
		//System.out.println(cashDeskName + ":      Running total: " + arg0.getRunningTotal());
		lastEvent = arg0;
	}
	
	public void onChangeAmountCalculatedEvent(final ChangeAmountCalculatedEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Change amount calculated: " + arg0.getChangeAmount());
		lastEvent = arg0;
	}
	
	public void onInvalidCreditCardEvent(final InvalidCreditCardEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Invalid credit card.");		
		lastEvent = arg0;
	}
	
	public void onCreditCardScanFailedEvent(final CreditCardScanFailedEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Credit card scan failed.");		
		lastEvent = arg0;
	}
	
	public void onProductBarcodeNotValidEvent(final ProductBarcodeNotValidEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Product barcode not valid: " + arg0.getBarcode());
		lastEvent = arg0;
	}
	
	public void onSaleSuccessEvent(final SaleSuccessEvent arg0)  {
		//System.out.println(cashDeskName + ": GUI: Sale success.");		
		lastEvent = arg0;
	}
	
	// -----------------------------------------------------
	// Simulation related methods
	// -----------------------------------------------------
	
	public Object getLastEvent() {
		return lastEvent;
	}
}

