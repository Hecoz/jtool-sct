package org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.BankIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.CashDeskEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashDeskAppEventDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashDeskAppEventHandlerIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.*;
import org.objectweb.dsrg.cocome.fractal.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.*;
import static org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.CashDeskStates.*;

import org.objectweb.dsrg.cocome.fractal.Simulator;


public class CashDeskApplicationImpl implements CashDeskAppEventHandlerIf
{

	// Required interface
	protected BankIf BankIf;
	protected CashDeskConnectorIf CashDeskConnectorIf;
	protected CashDeskEventDispatcherIf CashDeskEventDispatcherIf;
	protected CashDeskAppEventDispatcherIf CashDeskAppEventDispatcherIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("BankIf")) {
			BankIf = (BankIf) serverItf;
			return;
		}
		if (clientItfName.equals("CashDeskConnectorIf")) {
			CashDeskConnectorIf = (CashDeskConnectorIf) serverItf;
			return;
		}
		if (clientItfName.equals("CashDeskEventDispatcherIf")) {
			CashDeskEventDispatcherIf = (CashDeskEventDispatcherIf) serverItf;
			return;
		}
		if (clientItfName.equals("CashDeskAppEventDispatcherIf")) {
			CashDeskAppEventDispatcherIf = (CashDeskAppEventDispatcherIf) serverItf;
			return;
		}
	}


	private CashDeskStates currState = INITIALIZED;

	private String cardInformation = null;

	private double runningtotal = 0.0;

	private TransactionID transactionid;

	private StringBuilder total;

	private List<ProductWithStockItemTO> products;

	private boolean expressModeEnabled = false;
	
	private final String cashDeskName;
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

  public CashDeskApplicationImpl(final Simulator simulator)
    {
    cashDeskName =
        "CashDesk "
            + Integer.toString(simulator.registerCashDeskApplication(this));

		total = new StringBuilder("");
		
		products = new ArrayList<ProductWithStockItemTO>();
    }
    
	// -----------------------------------------------------
	// Implementation of the CashDeskAppEventHandlerIf interface 'CashDeskAppEventHandlerIf'
	// -----------------------------------------------------
		
	public void onSaleStartedEvent(final SaleStartedEvent saleStartedEvent)  {
		//System.out.println(cashDeskName + ": Application: SaleStartedEvent received");
		if (currState.equals(INITIALIZED)) {
			reset();
			currState = SALE_STARTED;
		}
	}
	
	public void onProductBarcodeScannedEvent(final ProductBarcodeScannedEvent productBarcodeScannedEvent)  {
		//System.out.println(cashDeskName + ": Application: ProductBarcodeScannedEvent(" + productBarcodeScannedEvent.getScannedBarcode() + ") received");
		if (currState.equals(SALE_STARTED)) {
			// stop scanning if more than 8 products in express mode
			if (expressModeEnabled && products.size() == 8) {
				return;
			}
			ProductWithStockItemTO product = null;
			
			try {
				product = CashDeskConnectorIf.getProductWithStockItem(productBarcodeScannedEvent.getScannedBarcode());
			} catch (final NoSuchProductException e) {
				//System.out.println(cashDeskName + ": Application: " + e.toString());

				CashDeskAppEventDispatcherIf.sendProductBarcodeNotValidEvent(new ProductBarcodeNotValidEvent(productBarcodeScannedEvent.getScannedBarcode()));

				return;
			}

			final String productname = product.getName();
			double price = 0;
			if (product.getStockItemTO()!= null)
      {
        price = product.getStockItemTO().getSalesPrice();
      }

			// calculate running total
			runningtotal += (int) price;
			// round
			//runningtotal = Math.rint(100 * runningtotal) / 100;

			CashDeskAppEventDispatcherIf.sendRunningTotalChangedEvent(new RunningTotalChangedEvent(productname, price, runningtotal));

			// if crash here, abort anyway
			products.add(product);
		}
	}

	public void onCodeEnteredManuallyEvent(final CodeEnteredManuallyEvent codeEnteredManuallyEvent)  {
		//System.out.println(cashDeskName + ": Application: CodeEnteredManuallyEvent(" + codeEnteredManuallyEvent.getEnteredBarcode() + ") received");
		if (currState.equals(SALE_STARTED)) {
			// stop scanning if more than 8 products in express mode
			if (expressModeEnabled && products.size() == 8) {
				return;
			}
			ProductWithStockItemTO product = null;
			
			try {
				product = CashDeskConnectorIf.getProductWithStockItem(codeEnteredManuallyEvent.getEnteredBarcode());
			} catch (final NoSuchProductException e) {
				//System.out.println(cashDeskName + ": Application: " + e.getMessage());

				CashDeskAppEventDispatcherIf.sendProductBarcodeNotValidEvent(new ProductBarcodeNotValidEvent(codeEnteredManuallyEvent.getEnteredBarcode()));

				return;
			}

			final String productname = product.getName();
			final double price = product.getStockItemTO().getSalesPrice();
			// calculate running total
			runningtotal += (int) price;
			// round
			//runningtotal = Math.rint(100 * runningtotal) / 100;

			CashDeskAppEventDispatcherIf.sendRunningTotalChangedEvent(new RunningTotalChangedEvent(productname, price, runningtotal));

			// if crash here, abort anyway
			products.add(product);
		}
	}

	
	public void onSaleFinishedEvent(final SaleFinishedEvent saleFinishedEvent)  {
		//System.out.println(cashDeskName + ": Application: SaleFinishedEvent received");
		if (currState.equals(SALE_STARTED)) {
			currState = SALE_FINISHED;
		}
	}
	
	public void onCashAmountEnteredEvent(final CashAmountEnteredEvent cashAmountEnteredEvent)  {
		//System.out.println(cashDeskName + ": Application: CashAmountEnteredEvent received");
		if (currState.equals(PAYING_BY_CASH)) {
			switch (cashAmountEnteredEvent.getKeyStroke()) {
			case ONE:
				total = total.append("1");
				break;
			case TWO:
				total = total.append("2");
				break;
			case THREE:
				total = total.append("3");
				break;
			case FOUR:
				total = total.append("4");
				break;
			case FIVE:
				total = total.append("5");
				break;
			case SIX:
				total = total.append("6");
				break;
			case SEVEN:
				total = total.append("7");
				break;
			case EIGHT:
				total = total.append("8");
				break;
			case NINE:
				total = total.append("9");
				break;
			case ZERO:
				total = total.append("0");
				break;
			case COMMA:
				total = total.append(".");
				break;
			}
		}
	}

	public void onCashAmountCompletedEvent(final CashAmountCompletedEvent cashAmountCompletedEvent)  {
		// the ENTER key is pressed at the cash desk keyboard
		//System.out.println(cashDeskName + ": Application: CashAmountCompletedEvent received");
		if (currState.equals(PAYING_BY_CASH)) {
			try {
				// prevents NumberFormatException in case no CashAmountEntered event occured before this one
				if (total.length() == 0)
        {
          total = total.append("0");
        }
				
				final double amount = Double.parseDouble(total.toString());
				final double changeamount = (int) amount - runningtotal;
				// round
				//changeamount = Math.rint(100 * changeamount) / 100;
					
				CashDeskAppEventDispatcherIf.sendChangeAmountCalculatedEvent(new ChangeAmountCalculatedEvent(changeamount));
					
				currState = PAID;
				return;
			} catch (final NumberFormatException e) {
				// TODO: Handle properly
				//System.out.println("Application: " + e.toString());
			}
		}
	}
	
	public void onCashBoxClosedEvent(final CashBoxClosedEvent cashBoxClosedEvent)  {
		//System.out.println(cashDeskName + ": Application: CashBoxClosedEvent received");
		if (currState.equals(PAID)) {
			makeSale(PaymentMode.CASH);
			reset();
			currState = INITIALIZED;
		}
	}
	
	public void onCreditCardScannedEvent(final CreditCardScannedEvent creditCardScannedEvent)  {
		//System.out.println(cashDeskName + ": Application: CreditCardScannedEvent received");
		if (currState.equals(PAYING_BY_CREDITCARD) || currState.equals(CREDIT_CARD_SCANNED)) {
			cardInformation = creditCardScannedEvent.getCreditCardInformation();
			currState = CREDIT_CARD_SCANNED;
		}
	}
	
	public void onPINEnteredEvent(final PINEnteredEvent pinEnteredEvent)  {
		//System.out.println(cashDeskName + ": Application: PINEnteredEvent received");
		if (currState.equals(CREDIT_CARD_SCANNED)) {
			transactionid = BankIf.validateCard(cardInformation, pinEnteredEvent.getPIN());
			if (transactionid == null) {
				//System.out.println(cashDeskName + ": Application: issuing InvalidCreditCardEvent.");

				CashDeskAppEventDispatcherIf.sendInvalidCreditCardEvent(new InvalidCreditCardEvent());

				return;
			}
			final Debit info = BankIf.debitCard(transactionid);
			if (info.equals(Debit.OK)) {
				// make the sale
				//System.out.println(cashDeskName + ": Application: Credit card accepted.");
				makeSale(PaymentMode.CREDITCARD);
				reset();
				currState = INITIALIZED;
			}
			if (info.equals(Debit.TRANSACTION_ID_NOT_VALID)) {
				// we have to rescan the card
				//System.out.println(cashDeskName + ": Application: Credit card payment: Transaction id not valid");
				CashDeskAppEventDispatcherIf.sendInvalidCreditCardEvent(new InvalidCreditCardEvent());
				currState = PAYING_BY_CREDITCARD;
			}
			if (info.equals(Debit.NOT_ENOUGH_MONEY)) {
				// Other failures
				// TODO: this has to be refined
				//System.out.println(cashDeskName + ": Application: Credit card payment: Not enough money on the account");
				CashDeskAppEventDispatcherIf.sendInvalidCreditCardEvent(new InvalidCreditCardEvent());
			}
		}
	}
	
	public void onExpressModeEnabledEvent(final ExpressModeEnabledEvent expressModeEnabledEvent)  {
		//System.out.println(cashDeskName + ": Application: ExpressModeEnabledEvent received");
		if (!expressModeEnabled
				/* && expressModeEnabledEvent.getCashdesk().equals(...)*/ ) {

			CashDeskAppEventDispatcherIf.sendExpressModeEnabledEvent(expressModeEnabledEvent);
			// appPublisher.publish(topicSession
			//	.createObjectMessage(expressModeEnabledEvent));
				
			expressModeEnabled = true;
		}
	}
	
	public void onExpressModeDisabledEvent(final ExpressModeDisabledEvent expressModeDisabledEvent)  {
		//System.out.println(cashDeskName + ": Application: ExpressModeDisabledEvent received");
		if (expressModeEnabled) {
			expressModeEnabled = false;
		}
	}
	
	public void onPaymentModeEvent(final PaymentModeEvent paymentModeEvent)  {
		//System.out.println(cashDeskName + ": Application: PaymentModeEvent received");
		if (currState.equals(SALE_FINISHED) || currState.equals(PAYING_BY_CREDITCARD)) {
			final PaymentMode mode = paymentModeEvent.getMode();
			if (mode.equals(PaymentMode.CASH)) {
				currState = PAYING_BY_CASH;
			}
			if (mode.equals(PaymentMode.CREDITCARD) && !expressModeEnabled) {
				currState = PAYING_BY_CREDITCARD;
			}
		}
	}

	// -----------------------------------------------------
	// Private methods
	// -----------------------------------------------------
	
	private void reset() {
		runningtotal = 0.0;
		total = new StringBuilder("");
		products = new ArrayList<ProductWithStockItemTO>();
		cardInformation = null;
	}
	
	private void makeSale(final PaymentMode mode) {
		final SaleTO saleTO = new SaleTO();
		saleTO.setProductTOs(products);
		
		/* 
		// the old synchronous call 
		CashDeskConnectorIf.bookSale(saleTO);
		 */
		
		CashDeskAppEventDispatcherIf.sendSaleSuccessEvent(new SaleSuccessEvent());
		
		// goes to store
		CashDeskEventDispatcherIf.sendAccountSaleEvent(
			new AccountSaleEvent(saleTO)
		);
		
		// goes to coordinator
		CashDeskEventDispatcherIf.sendSaleRegisteredEvent(
			new SaleRegisteredEvent(
				cashDeskName,	// FIXME - fill in the correct value
				saleTO.getProductTOs().size(), mode
			)	
		);
	}
	
	
}

