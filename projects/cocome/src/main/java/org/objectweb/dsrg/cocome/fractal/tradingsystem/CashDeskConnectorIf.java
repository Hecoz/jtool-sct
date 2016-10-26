package org.objectweb.dsrg.cocome.fractal.tradingsystem;

/**
 * This interface is used by the cashdesk
 */
public interface CashDeskConnectorIf {
	/**
	 * Registers the selling of products contained in the stock of the store. Updates amount of stock items.
	 * Used for realization of UC 1.
	 * @param saleTO The sale to be registered in stock.
	 */
	void bookSale(SaleTO saleTO);
	
	/**
	 * Determines product and the item in the stock of the store by the given barcode. Used for realization
	 * of UC 1 and UC 4
	 * @param productBarCode Contains the given barcode
	 * @return Returns a ProductWithStockItemTO instance which contains
	 * the identified product which is linked to the stock item of the store. NULL, if barcode cannot be
	 * matched.
	 */
	ProductWithStockItemTO getProductWithStockItem(long productBarCode) throws NoSuchProductException;
}
