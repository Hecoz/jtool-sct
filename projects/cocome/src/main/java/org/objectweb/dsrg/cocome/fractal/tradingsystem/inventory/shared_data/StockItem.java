package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;


/**
 * A StockItem represents a concrete product in the store including sales price, amount, ...
 */
public class StockItem {
	private long id;
	private double salesPrice;
	private long amount;
	private long minStock;
	private long maxStock;
	private long incomingAmount;
	private Store store;
	private Product product;
	
	
	/**
	 * @return A unique identifier for StockItem objects
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id A unique identifier
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @return The currently available amount of items of a product
	 */
	public long getAmount() {
		return amount;
	}
	
	/**
	 * @param amount The currently available amount of items of a product
	 */
	public void setAmount(final long amount) {
		this.amount = amount;
	}
	
	/**
	 * This method will be used while computing the low stock item list
	 * 
	 * @return The maximum capacity of a product in a store
	 */
	public long getMaxStock() {
		return maxStock;
	}
	
	/**
	 * This method enables the definition of the maximum capacity of a product in a store 
	 * 
	 * @param maxStock The maximum capacity of a product in a store
	 */
	public void setMaxStock(final long maxStock) {
		this.maxStock = maxStock;
	}
	
	/**
	 * @return The minimum amount of products which has to be available in a store
	 */
	public long getMinStock() {
		return minStock;
	}
	
	/**
	 * @param minStock The minimum amount of products which has to be available in a store
	 */
	public void setMinStock(final long minStock) {
		this.minStock = minStock;
	}
	
	/**
	 * @return The Product of a StockItem.
	 */
	public Product getProduct() {
		return product;
	}
	
	/**
	 * @param product The Product of a StockItem
	 */
	public void setProduct(final Product product) {
		this.product = product;
	}
	
	/**
	 * @return The sales price of the StockItem
	 */
	public double getSalesPrice() {
		return salesPrice;
	}
	
	/**
	 * @param salesPrice The sales price of the StockItem
	 */
	public void setSalesPrice(final double salesPrice) {
		this.salesPrice = salesPrice;
	}
	
	/**
	 * Required for UC 8
	 * @return incomingAmount An amount of products that will
	 * be delivered in the near future.
	 */
	public long getIncomingAmount() {
		return this.incomingAmount;
	}
	
	/**
	 * Set the amount of products that will
	 * be delivered in the near future.
	 * <p>
	 * Required for UC 8
	 * @param incomingAmount The absolute amount (no delta) of
	 * incoming products.
	 */
	public void setIncomingAmount(final long incomingAmount) {
		this.incomingAmount = incomingAmount;
	}
	
	/**
	 * @return The store where the StockItem belongs to
	 */
	public Store getStore() {
		return store;
	}
	
	/**
	 * @param store The store where the StockItem belongs to
	 */
	public void setStore(final Store store) {
		this.store = store;
	}
}
