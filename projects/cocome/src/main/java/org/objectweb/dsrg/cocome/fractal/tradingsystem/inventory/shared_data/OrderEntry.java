package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;

/**
 * The class OrderEntry represents a single productorder entry in the database
 */
public class OrderEntry {
	private long id;
	private long amount;
	private Product product;
	private ProductOrder productOrder;
	
	/**
	 * Gets identifier value
	 * @return The id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets identifier.
	 * @param id Identifier value.
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	
	
	/**
	 * @return The amount of ordered products
	 */
	public long getAmount() {
		return amount;
	}
	
	/**
	 * @param amount The amount of ordered products
	 */
	public void setAmount(final long amount) {
		this.amount = amount;
	}
	
	/**
	 * @return The ProductOrder where the OrderEntry belongs to
	 */
	public ProductOrder getOrder() {
		return productOrder;
	}
	
	/**
	 * @param productOrder The ProductOrder where the OrderEntry belongs to
	 */
	public void setOrder(final ProductOrder productOrder) {
		this.productOrder = productOrder;
	}
	
	/**
	 * @return The product which is ordered
	 */
	public Product getProduct() {
		return product;
	}
	
	/**
	 * @param product The product which is ordered
	 */
	public void setProduct(final Product product) {
		this.product = product;
	}

	
}
