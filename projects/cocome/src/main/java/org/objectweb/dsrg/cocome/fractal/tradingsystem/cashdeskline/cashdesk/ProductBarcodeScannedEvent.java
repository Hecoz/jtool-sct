package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This event is raised by the scanner controller component after the barcode
 * scanner read a barcode.
 * 
 */
public class ProductBarcodeScannedEvent {

	private final long scannedBarcode;

	public ProductBarcodeScannedEvent(final long scannedBarcode) {
		this.scannedBarcode = scannedBarcode;
	}

	public long getScannedBarcode() {
		return scannedBarcode;
	}
}
