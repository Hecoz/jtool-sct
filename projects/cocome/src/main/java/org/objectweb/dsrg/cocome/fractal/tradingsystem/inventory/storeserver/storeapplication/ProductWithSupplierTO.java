package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.ProductTO;

/**
 * <code>ProductWithSupplierTO</code> is used as transfer object class for transferring basic product and additional supplier
 * information between client and the service-oriented application layer. It contains either copies of persisted
 * data which are transferred to the client, or data which is transferred from the client to the
 * application layer for being processed and persisted.
 *
 */
public class ProductWithSupplierTO extends ProductTO {

	protected SupplierTO supplierTO;

	/**
	 * Gets transfer object for supplier which offers this product.
	 * @return Transfer object for supplier.
	 */
	public SupplierTO getSupplierTO() {
		return supplierTO;
	}

	/**
	 * Sets transfer object for supplier
	 * @param supplierTO New supplier transfer object.
	 */
	public void setSupplierTO(final SupplierTO supplierTO) {
		this.supplierTO = supplierTO;
	}
}
