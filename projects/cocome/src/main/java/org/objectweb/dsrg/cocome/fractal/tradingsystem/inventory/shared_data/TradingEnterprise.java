package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;

import java.util.Collection;

/**
 * This class represents a TradingEnterprise in the database
 */
public class TradingEnterprise {

	private long id;
	private String name;
	private Collection<ProductSupplier> suppliers;
	private Collection<Store> stores;

	/**
	 * @return id A unique identifier for TradingEnterprise objects
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id A unique identifier for TradingEnterprise objects
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return Name of TradingEnterprise
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Name of TradingEnterprise
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return List of Stores related to the TradingEnterprise
	 */
	public Collection<Store> getStores() {
		return stores;
	}

	/**
	 * @param stores List of Stores related to the TradingEnterprise
	 */
	public void setStores(final Collection<Store> stores) {
		this.stores = stores;
	}

	/**
	 * @return List of Suppliers related to the TradingEnterprise
	 */
	public Collection<ProductSupplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers List of Suppliers related to the TradingEnterprise
	 */
	public void setSuppliers(final Collection<ProductSupplier> suppliers) {
		this.suppliers = suppliers;
	}
}
