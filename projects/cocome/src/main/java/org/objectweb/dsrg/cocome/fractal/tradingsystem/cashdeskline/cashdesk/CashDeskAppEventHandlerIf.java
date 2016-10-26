package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

public interface CashDeskAppEventHandlerIf {
	/**
	 * Event handler for SaleStartedEvent events.
	 */
	public void onSaleStartedEvent(SaleStartedEvent saleStartedEvent);

	/**
	 * Event handler for ProductBarcodeScannedEvent events.
	 */
	public void onProductBarcodeScannedEvent(ProductBarcodeScannedEvent productBarcodeScannedEvent);

	/**
	 * Event handler for CodeEnteredManuallyEvent events.
	 */
	public void onCodeEnteredManuallyEvent(CodeEnteredManuallyEvent codeEnteredManuallyEvent);
	
	/**
	 * Event handler for SaleFinishedEvent events.
	 */
	public void onSaleFinishedEvent(SaleFinishedEvent saleFinishedEvent);

	/**
	 * Event handler for CashAmountEnteredEvent events.
	 */
	public void onCashAmountEnteredEvent(CashAmountEnteredEvent moneyAmountEnteredEvent);

	/**
	 * Event handler for CashAmountCompletedEvent events.
	 */
	public void onCashAmountCompletedEvent(CashAmountCompletedEvent cashAmountCompletedEvent);

	/**
	 * Event handler for CashBoxClosedEvent events.
	 */
	public void onCashBoxClosedEvent(CashBoxClosedEvent cashBoxClosedEvent);

	/**
	 * Event handler for CreditCardScannedEvent events.
	 */
	public void onCreditCardScannedEvent(CreditCardScannedEvent creditCardScannedEvent);

	/**
	 * Event handler for PINEnteredEvent events.
	 */
	public void onPINEnteredEvent(PINEnteredEvent pinEnteredEvent);

	/**
	 * Event handler for ExpressModeEnabledEvent events.
	 */
	public void onExpressModeEnabledEvent(ExpressModeEnabledEvent expressModeEnabledEvent);

	/**
	 * Event handler for ExpressModeEnabledEvent events.
	 */
	public void onExpressModeDisabledEvent(ExpressModeDisabledEvent expressModeDisabledEvent);
	
	/**
	 * Event handler for PaymentModeEvent events.
	 */
	public void onPaymentModeEvent(PaymentModeEvent paymentModeEvent);
}
