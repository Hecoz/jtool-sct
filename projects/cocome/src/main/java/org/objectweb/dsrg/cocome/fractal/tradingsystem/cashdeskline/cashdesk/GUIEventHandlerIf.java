package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This interface defines event handlers related to the Store at the cash desk
 */
public interface GUIEventHandlerIf {
	void onRunningTotalChangedEvent(RunningTotalChangedEvent runningTotalChangedEvent);

	void onCashAmountEnteredEvent(CashAmountEnteredEvent cashAmountEnteredEvent);
	
	void onCashAmountCompletedEvent(CashAmountCompletedEvent cashAmountCompletedEvent);
	
	void onChangeAmountCalculatedEvent(ChangeAmountCalculatedEvent changeAmountCalculatedEvent);

	void onExpressModeDisabledEvent(ExpressModeDisabledEvent expressModeDisabledEvent);

	void onExpressModeEnabledEvent(ExpressModeEnabledEvent expressModeEnabledEvent);

	void onInvalidCreditCardEvent(InvalidCreditCardEvent invalidCreditCardEvent);

	void onCreditCardScanFailedEvent(CreditCardScanFailedEvent creditCardScanFailedEvent);

	void onProductBarcodeNotValidEvent(ProductBarcodeNotValidEvent productBarcodeNotValidEvent);

	void onSaleSuccessEvent(SaleSuccessEvent saleSuccessEvent);

	void onSaleStartedEvent(SaleStartedEvent saleStartedEvent);
}
