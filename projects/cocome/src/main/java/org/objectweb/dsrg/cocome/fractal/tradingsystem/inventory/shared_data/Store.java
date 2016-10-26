package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;

import java.util.Collection;


/**
 * The class Store represents a store in the database
 */
public class Store {

	private long id;
	private String name;
	private String location;
	private TradingEnterprise enterprise;
	private Collection<ProductOrder> productOrders;
	private Collection<StockItem> stockItems;
	
	
	
	/**
	 * @return A unique identifier for Store objects
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id A unique identifier for Store objects
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @return The enterprise which the Store belongs to
	 */
	public TradingEnterprise getEnterprise() {
		return enterprise;
	}
	
	/**
	 * @param enterprise The enterprise which the Store belongs to
	 */
	public void setEnterprise(final TradingEnterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	/**
	 * @return The location of the Store
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * @param location The location of the Store
	 */
	public void setLocation(final String location) {
		this.location = location;
	}
	
	/**
	 * @return The name of the Store
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name of the Store
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * @return All productOrders of the Store
	 */
	public Collection<ProductOrder> getOrders() {
		return productOrders;
	}
	
	/**
	 * @param productOrders All productOrders of the Store
	 */
	public void setOrders(final Collection<ProductOrder> productOrders) {
		this.productOrders = productOrders;
	}
	
	/**
	 * @return A list of StockItem objects.
	 *         A StockItem represents a concrete product in the store including sales price, ...
	 */
	public Collection<StockItem> getStockItems() {
		return stockItems;
	}
	
	/**
	 * @param stockItems A list of StockItem objects.
	 *        A StockItem represents a concrete product in the store including sales price, ...
	 */
	public void setStockItems(final Collection<StockItem> stockItems) {
		this.stockItems = stockItems;
	}
	
	
}
