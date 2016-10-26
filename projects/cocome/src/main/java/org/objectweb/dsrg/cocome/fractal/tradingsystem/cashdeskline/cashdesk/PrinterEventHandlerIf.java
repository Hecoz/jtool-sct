package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This interface defines event handlers related to printer-relevant topics
 * during process sale
 * 
 */
public interface PrinterEventHandlerIf {
	void onRunningTotalChangedEvent(RunningTotalChangedEvent runningTotalChangedEvent);

	void onCashAmountEnteredEvent(CashAmountEnteredEvent cashAmountEnteredEvent);
	
	void onCashAmountCompletedEvent(CashAmountCompletedEvent cashAmountCompletedEvent);
	
	void onChangeAmountCalculatedEvent(ChangeAmountCalculatedEvent changeAmountCalculatedEvent);

	void onSaleStartedEvent(SaleStartedEvent saleStartedEvent);

	void onSaleFinishedEvent(SaleFinishedEvent saleFinishedEvent);

	void onCashBoxClosedEvent(CashBoxClosedEvent cashBoxClosedEvent);

	void onSaleSuccessEvent(SaleSuccessEvent saleSuccessEvent);
}
