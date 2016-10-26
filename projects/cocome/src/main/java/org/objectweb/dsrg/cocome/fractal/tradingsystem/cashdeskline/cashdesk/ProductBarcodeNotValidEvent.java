package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

public class ProductBarcodeNotValidEvent {

	private final long barcode;

	public ProductBarcodeNotValidEvent(final long barcode) {
		this.barcode = barcode;
	}

	public long getBarcode() {
		return barcode;
	}
}
