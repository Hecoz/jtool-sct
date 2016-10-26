package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CardReaderEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CardReaderEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashBoxEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashBoxEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashDeskAppEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashDeskAppEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.GUIEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.LightDisplayEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.PrinterEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ScannerEventDispatcherIf;


public class CashDeskBusImpl implements CardReaderEventDispatcherIf, CashBoxEventDispatcherIf, CashDeskAppEventDispatcherIf, ScannerEventDispatcherIf
{
	// Required interface
	protected CashDeskAppEventHandlerIf CashDeskAppEventHandlerIf;
	protected LightDisplayEventHandlerIf LightEventHandlerIf;
	protected CardReaderEventHandlerIf CardEventHandlerIf;
	protected GUIEventHandlerIf GUIEventHandlerIf;
	protected CashBoxEventHandlerIf CashBoxEventHandlerIf;
	protected PrinterEventHandlerIf PrinterEventHandlerIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("CashDeskAppEventHandlerIf")) {
			CashDeskAppEventHandlerIf = (CashDeskAppEventHandlerIf) serverItf;
			return;
		}
		if (clientItfName.equals("LightEventHandlerIf")) {
			LightEventHandlerIf = (LightDisplayEventHandlerIf) serverItf;
			return;
		}
		if (clientItfName.equals("CardEventHandlerIf")) {
			CardEventHandlerIf = (CardReaderEventHandlerIf) serverItf;
			return;
		}
		if (clientItfName.equals("GUIEventHandlerIf")) {
			GUIEventHandlerIf = (GUIEventHandlerIf) serverItf;
			return;
		}
		if (clientItfName.equals("CashBoxEventHandlerIf")) {
			CashBoxEventHandlerIf = (CashBoxEventHandlerIf) serverItf;
			return;
		}
		if (clientItfName.equals("PrinterEventHandlerIf")) {
			PrinterEventHandlerIf = (PrinterEventHandlerIf) serverItf;
			return;
		}
	}


	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public CashDeskBusImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the CardReaderEventDispatcherIf interface 'CardEventDispatcherIf'
	// -----------------------------------------------------
		
	public void sendPINEnteredEvent(final PINEnteredEvent arg0)  {
		CashDeskAppEventHandlerIf.onPINEnteredEvent(arg0);
	}
	
	public void sendCreditCardScannedEvent(final CreditCardScannedEvent arg0)  {
		CashDeskAppEventHandlerIf.onCreditCardScannedEvent(arg0);		
	}
	
	// -----------------------------------------------------
	// Implementation of the CashBoxEventDispatcherIf interface 'CashBoxEventDispatcherIf'
	// -----------------------------------------------------
		
	public void sendSaleStartedEvent(final SaleStartedEvent arg0)  {
		CashDeskAppEventHandlerIf.onSaleStartedEvent(arg0);
	}
	
	public void sendSaleFinishedEvent(final SaleFinishedEvent arg0)  {
		CashDeskAppEventHandlerIf.onSaleFinishedEvent(arg0);
	}
	
	public void sendPaymentModeEvent(final PaymentModeEvent arg0)  {
		CashDeskAppEventHandlerIf.onPaymentModeEvent(arg0);
	}
	
	public void sendCashAmountEnteredEvent(final CashAmountEnteredEvent arg0)  {
		CashDeskAppEventHandlerIf.onCashAmountEnteredEvent(arg0);
	}

	public void sendCashAmountCompletedEvent(final CashAmountCompletedEvent arg0)  {
		CashDeskAppEventHandlerIf.onCashAmountCompletedEvent(arg0);
	}
	
	public void sendCashBoxClosedEvent(final CashBoxClosedEvent arg0)  {
		CashDeskAppEventHandlerIf.onCashBoxClosedEvent(arg0);		
	}
	
	public void sendExpressModeDisabledEvent(final ExpressModeDisabledEvent arg0)  {
		CashDeskAppEventHandlerIf.onExpressModeDisabledEvent(arg0);		
	}
	
	// -----------------------------------------------------
	// Implementation of the ScannerEventDispatcherIf interface 'ScannerEventDispatcherIf'
	// -----------------------------------------------------
		
	public void sendProductBarcodeScannedEvent(final ProductBarcodeScannedEvent arg0)  {
		CashDeskAppEventHandlerIf.onProductBarcodeScannedEvent(arg0);
	}
	
	// -----------------------------------------------------
	// Implementation of the CashDeskAppEventDispatcherIf interface 'CashDeskAppEventDispatcherIf'
	// -----------------------------------------------------
	public void sendProductBarcodeNotValidEvent(final ProductBarcodeNotValidEvent arg0)
	{
		GUIEventHandlerIf.onProductBarcodeNotValidEvent(arg0);
	}
	
    public void sendRunningTotalChangedEvent(final RunningTotalChangedEvent arg0)
	{
		GUIEventHandlerIf.onRunningTotalChangedEvent(arg0);
	}
	
	public void sendChangeAmountCalculatedEvent(final ChangeAmountCalculatedEvent arg0)
	{
		GUIEventHandlerIf.onChangeAmountCalculatedEvent(arg0);
	}
	
	public void sendSaleSuccessEvent(final SaleSuccessEvent arg0)
	{
		GUIEventHandlerIf.onSaleSuccessEvent(arg0);
	}
	
	public void sendInvalidCreditCardEvent(final InvalidCreditCardEvent arg0)
	{
		GUIEventHandlerIf.onInvalidCreditCardEvent(arg0);
	}
	
	public void sendExpressModeEnabledEvent(final ExpressModeEnabledEvent arg0)
	{
		GUIEventHandlerIf.onExpressModeEnabledEvent(arg0);
	}
	
}

