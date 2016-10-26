package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ExpressModeEnabledEvent;

public interface CoordinatorEventDispatcherIf {
    void sendExpressModeEnabledEvent(ExpressModeEnabledEvent expressModeEnabledEvent);;

}
