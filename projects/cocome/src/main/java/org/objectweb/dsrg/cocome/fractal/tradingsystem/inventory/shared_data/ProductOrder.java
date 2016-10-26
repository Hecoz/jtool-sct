package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;

import java.util.Collection;
import java.util.Date;

/**
 * The ProductOrder class represents an ProductOrder of a Store in the database.
 */
public class ProductOrder {
	
	private long id;
	private Date deliveryDate;
	private Date orderingDate;
	private Collection<OrderEntry> orderEntries;
	private Store store;
	
	/**
	 * @return A unique identifier for ProductOrder objects
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id A unique identifier for ProductOrder objects
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @return A list of OrderEntry objects (pairs of Product-Amount-pairs)
	 */
	public Collection<OrderEntry> getOrderEntries() {
		return orderEntries;
	}
	
	/**
	 * @param orderEntries A list of OrderEntry objects (pairs of Product-Amount-pairs)
	 */
	public void setOrderEntries(final Collection<OrderEntry> orderEntries) {
		this.orderEntries = orderEntries;
	}
	
	/**
	 * @return The date of ordering
	 */
	public Date getOrderingDate() {
		return orderingDate;
	}
	
	/**
	 * @param orderingDate The date of order
	 */
	public void setOrderingDate(final Date orderingDate) {
		this.orderingDate = orderingDate;
	}
	
	/**
	 * The delivery date is used for computing the mean time to delivery
	 * 
	 * @return The date of order fulfillment
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	
	/**
	 * The delivery date is used for computing the mean time to delivery
	 * 
	 * @param deliveryDate The date of order fulfillment
	 */
	public void setDeliveryDate(final Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	/**
	 * @return The store where the order is placed
	 */
	public Store getStore() {
		return store;
	}
	
	/**
	 * @param store The store where the order is placed
	 */
	public void setStore(final Store store) {
		this.store = store;
	}
}
