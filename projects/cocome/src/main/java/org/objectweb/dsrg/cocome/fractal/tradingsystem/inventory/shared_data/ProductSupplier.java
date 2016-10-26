package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;

import java.util.Collection;

/**
 * This class represents a ProductSupplier in the database.
 */
public class ProductSupplier {
	
	private long id;
	private String name;
	private Collection<Product> products;
	
	/**
	 * @return A unique identifier for ProductSupplier objects
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id A unique identifier for ProductSupplier objects
	 */
	public void setId(final long id) {
		this.id = id;
	}
	
	/**
	 * @return The name of the ProductSupplier
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name of the ProductSupplier
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * @return The list of Products provided by the ProductSupplier
	 */
	public Collection<Product> getProducts() {
		return products;
	}
	
	/**
	 * @param products The list of Products provided by the ProductSupplier
	 */
	public void setProducts(final Collection<Product> products) {
		this.products = products;
	}
	
}
