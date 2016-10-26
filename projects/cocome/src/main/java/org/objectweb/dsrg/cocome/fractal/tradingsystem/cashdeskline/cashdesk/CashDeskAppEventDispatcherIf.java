package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

public interface CashDeskAppEventDispatcherIf {

	void sendProductBarcodeNotValidEvent(ProductBarcodeNotValidEvent productBarcodeNotValidEvent);
	
    void sendRunningTotalChangedEvent(RunningTotalChangedEvent runningTotalChangedEvent);
	
	void sendChangeAmountCalculatedEvent(ChangeAmountCalculatedEvent changeAmountCalculatedEvent);
	
	void sendSaleSuccessEvent(SaleSuccessEvent saleSuccessEvent);
	
	void sendInvalidCreditCardEvent(InvalidCreditCardEvent invalidCreditCardEvent);
	
	void sendExpressModeEnabledEvent(ExpressModeEnabledEvent expressModeEnabledEvent);
}
