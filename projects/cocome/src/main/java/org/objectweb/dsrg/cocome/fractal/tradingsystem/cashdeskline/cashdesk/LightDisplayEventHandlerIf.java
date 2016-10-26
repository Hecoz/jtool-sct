package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This interface defines event handlers related to switching between express
 * mode and normal mode.
 */
public interface LightDisplayEventHandlerIf {
	void onExpressModeEnabledEvent(ExpressModeEnabledEvent expressModeEnabledEvent);

	void onExpressModeDisabledEvent(ExpressModeDisabledEvent expressModeDisabledEvent);
}
