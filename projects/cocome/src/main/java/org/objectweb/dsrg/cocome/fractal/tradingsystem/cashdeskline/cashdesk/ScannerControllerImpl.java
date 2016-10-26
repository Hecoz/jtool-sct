package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ScannerEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.ProductBarcodeScannedEvent;
import org.objectweb.dsrg.cocome.fractal.Simulator;

public class ScannerControllerImpl 
{
	// Required interface
	protected ScannerEventDispatcherIf ScannerEventDispatcherIf;

	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("ScannerEventDispatcherIf")) {
			ScannerEventDispatcherIf = (ScannerEventDispatcherIf) serverItf;
			return;
		}
	}
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

  public ScannerControllerImpl(final Simulator simulator)
    {
    simulator.registerScannerController(this);
    }
    
	// -----------------------------------------------------
	// Simulation related methods
	// -----------------------------------------------------
	
	public void simulatedProductBarcodeScanned(final long scannedBarcode) {
		ScannerEventDispatcherIf.sendProductBarcodeScannedEvent(
			new ProductBarcodeScannedEvent(scannedBarcode)
		);
	}
}

