package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;


/**
 * This class represents a Product in the database
 */
public class Product {

	private long id;
	private long barcode;
	private double purchasePrice;
	private String name;
	private ProductSupplier supplier;
	
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
	 * @return The barcode of the product
	 */
	public long getBarcode() {
		return barcode;
	}

	/**
	 * @param barcode The barcode of the product
	 */
	public void setBarcode(final long barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return The name of the product
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name of the product
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return The ProductSupplier of this product
	 */
	public ProductSupplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier The ProductSupplier of this product
	 */
	public void setSupplier(final ProductSupplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return The purchase price of this product
	 */
	public double getPurchasePrice() {
		return purchasePrice;
	}

	/**
	 * @param purchasePrice The purchase price of this product
	 */
	public void setPurchasePrice(final double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

}
