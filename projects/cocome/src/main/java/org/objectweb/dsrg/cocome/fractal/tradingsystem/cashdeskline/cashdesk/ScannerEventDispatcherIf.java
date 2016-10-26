package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

public interface ScannerEventDispatcherIf {
	public void sendProductBarcodeScannedEvent(
			ProductBarcodeScannedEvent productBarcodeScannedEvent);
}
