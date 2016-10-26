package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.LightDisplayEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ExpressModeDisabledEvent;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ExpressModeEnabledEvent;

public class LightDisplayControllerImpl implements LightDisplayEventHandlerIf
{
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public LightDisplayControllerImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the LightDisplayEventHandlerIf interface 'LightEventHandlerIf'
	// -----------------------------------------------------
		
	public void onExpressModeEnabledEvent(final ExpressModeEnabledEvent arg0)  {
		//System.out.println("LightDisplay: EXPRESS MODE ON");
	}
	
	public void onExpressModeDisabledEvent(final ExpressModeDisabledEvent arg0)  {
		//System.out.println("LightDisplay: EXPRESS MODE OFF");
	}
	
}

