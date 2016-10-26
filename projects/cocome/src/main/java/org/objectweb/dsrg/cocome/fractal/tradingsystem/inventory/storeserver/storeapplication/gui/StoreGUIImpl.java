package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.gui;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.StoreIf;

public class StoreGUIImpl 
{

	// Required interface
	protected StoreIf StoreIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("StoreIf")) {
			StoreIf = (StoreIf) serverItf;
			return;
		}
	}

	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public StoreGUIImpl()
    {
    }
    
}

