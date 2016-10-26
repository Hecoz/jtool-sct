package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CardReaderEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CardReaderEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.*;
import org.objectweb.dsrg.cocome.fractal.Simulator;


public class CardReaderControllerImpl implements CardReaderEventHandlerIf
{

	// Required interface
	protected CardReaderEventDispatcherIf CardEventDispatcherIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("CardEventDispatcherIf")) {
			CardEventDispatcherIf = (CardReaderEventDispatcherIf) serverItf;
			return;
		}
	}

	private boolean expressModeEnabled = false;
	
	private final String cashDeskName;
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

  public CardReaderControllerImpl(final Simulator simulator)
    {
    cashDeskName =
        "CashDesk "
            + Integer.toString(simulator.registerCardReaderController(this));
    }
    
	// -----------------------------------------------------
	// Implementation of the CardReaderEventHandlerIf interface 'CardEventHandlerIf'
	// -----------------------------------------------------
		
	public void onExpressModeEnabledEvent(final ExpressModeEnabledEvent arg0)  {
		//System.out.println(cashDeskName + ": CardReaderController: ExpressModeEnabledEvent received");
		expressModeEnabled = true;
	}
	
	public void onExpressModeDisabledEvent(final ExpressModeDisabledEvent arg0)  {
		//System.out.println(cashDeskName + ": CardReaderController: ExpressModeDisabledEvent received");
		expressModeEnabled = false;
	}
	
	// -----------------------------------------------------
	// Simulation related methods
	// -----------------------------------------------------
	
	public void simulatedCreditCardScanned(final String creditInfo) {
		if (!expressModeEnabled) {
			//System.out.println(cashDeskName + ": CardReaderController: \"" + creditInfo + "\" credit card scanned.");
			this.CardEventDispatcherIf.sendCreditCardScannedEvent(
				new CreditCardScannedEvent(creditInfo)
			);
		}
	}
	
	public void simulatedPINEntered(final String pinText) {
		if (!expressModeEnabled) {
			final int pin = Integer.parseInt(pinText);
			
			/*
			// We simulate a hardware defect here
			if (Verify.getBoolean()) {
				// Read out wrong value
				pin++;
			}
			*/

			//System.out.println(cashDeskName + ": CardReaderController: PIN entered: " + Integer.toString(pin));
			this.CardEventDispatcherIf.sendPINEnteredEvent(
				new PINEnteredEvent(pin)
			);	
		}
	}
}

