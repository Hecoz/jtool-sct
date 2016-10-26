package org.objectweb.dsrg.cocome.fractal.tradingsystem;

public class AccountSaleEvent {

	private final SaleTO sale;

	public AccountSaleEvent(final SaleTO sale) {
		this.sale = sale;
	}

	public SaleTO getSale() {
		return sale;
	}
}
