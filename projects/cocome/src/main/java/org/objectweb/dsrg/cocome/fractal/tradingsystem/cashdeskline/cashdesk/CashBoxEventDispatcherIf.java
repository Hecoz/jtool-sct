package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

/**
 * This interface defines an event handler for events related to the cash box.
 */
public interface CashBoxEventDispatcherIf {
	void sendSaleStartedEvent(SaleStartedEvent saleStartedEvent);

	void sendSaleFinishedEvent(SaleFinishedEvent saleFinishedEvent);

	void sendPaymentModeEvent(PaymentModeEvent paymentModeEvent);

	void sendCashAmountEnteredEvent(
			CashAmountEnteredEvent cashAmountEnteredEvent);

	void sendCashAmountCompletedEvent(
			CashAmountCompletedEvent cashAmountCompletedEvent);

	void sendCashBoxClosedEvent(CashBoxClosedEvent cashBoxClosedEvent);

	void sendExpressModeDisabledEvent(
			ExpressModeDisabledEvent expressModeDisabledEvent);
}
