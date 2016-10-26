package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.logic;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.ProductWithStockItemTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.StockItemTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.EnterpriseTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.ProductTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.StoreTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.OrderEntry;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.PersistenceContext;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.Product;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.ProductOrder;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.ProductSupplier;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.StockItem;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.Store;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.StoreQueryIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.TradingEnterprise;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.ComplexOrderEntryTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.ComplexOrderTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.OrderEntryTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.OrderTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.ProductWithSupplierAndStockItemTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.ProductWithSupplierTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.StoreWithEnterpriseTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.SupplierTO;

/**
 * Helper class to transfer the data from 
 * the persistent objects to the transfer objects
 */
public class FillTransferObjects {

	public static ComplexOrderEntryTO fillComplexOrderEntry(final OrderEntry orderentry) {
		final ComplexOrderEntryTO result = new ComplexOrderEntryTO();
		result.setAmount(orderentry.getAmount());
		result.setProductTO(fillProductWithSupplierTO(orderentry.getProduct()));
		return result;
	}

	public static ComplexOrderTO fillComplexOrderTO(final ProductOrder order) {
		final ComplexOrderTO result = new ComplexOrderTO();
		result.setId(order.getId());
		result.setDeliveryDate(order.getDeliveryDate());
		result.setOrderingDate(order.getOrderingDate());
		final List<ComplexOrderEntryTO> oeto = new ArrayList<ComplexOrderEntryTO>();
		for (final OrderEntry orderentry : order.getOrderEntries()) {
			oeto.add(fillComplexOrderEntry(orderentry));
		}
		result.setOrderEntryTO(oeto);
		return result;
	}

	public static EnterpriseTO fillEnterpriseTO(final TradingEnterprise enterprise) {
		final EnterpriseTO result = new EnterpriseTO();
		result.setId(enterprise.getId());
		result.setName(enterprise.getName());
		return result;
	}

	public static OrderEntryTO fillOrderEntryTO(final OrderEntry orderentry) {
		final OrderEntryTO result = new OrderEntryTO();
		result.setAmount(orderentry.getAmount());
		return result;
	}

	public static OrderTO fillOrderTO(final ProductOrder order) {
		final OrderTO result = new OrderTO();
		result.setId(order.getId());
		result.setDeliveryDate(order.getDeliveryDate());
		result.setOrderingDate(order.getOrderingDate());
		return result;
	}

	public static ProductTO fillProductTO(final Product product) {
		final ProductTO result = new ProductTO();
		result.setId(product.getId());
		result.setName(product.getName());
		result.setBarcode(product.getBarcode());
		result.setPurchasePrice(product.getPurchasePrice());
		return result;
	}

	public static ProductWithStockItemTO fillProductWithStockItemTO(
			final StockItem stockitem) {
		final ProductWithStockItemTO result = new ProductWithStockItemTO();
		final Product product = stockitem.getProduct();
		result.setId(product.getId());
		result.setName(product.getName());
		result.setBarcode(product.getBarcode());
		result.setPurchasePrice(product.getPurchasePrice());
		result.setStockItemTO(fillStockItemTO(stockitem));
		return result;
	}
	
	public static ProductWithSupplierAndStockItemTO fillProductWithSupplierAndStockItemTO(final StoreQueryIf storequery, final long storeid, final Product product, final PersistenceContext pctx) {
		final ProductWithSupplierAndStockItemTO result = new ProductWithSupplierAndStockItemTO();
		result.setSupplierTO(fillSupplierTO(product.getSupplier()));
		result.setId(product.getId());
		result.setName(product.getName());
		result.setBarcode(product.getBarcode());
		result.setPurchasePrice(product.getPurchasePrice());
		final StockItem stockitem = storequery.queryStockItem(storeid, product.getBarcode(), pctx);
		if (stockitem != null) {
			result.setStockItemTO(fillStockItemTO(stockitem));
		}
		return result;
	}

	public static ProductWithSupplierTO fillProductWithSupplierTO(final Product product) {
		final ProductWithSupplierTO result = new ProductWithSupplierTO();
		result.setId(product.getId());
		result.setBarcode(product.getBarcode());
		result.setName(product.getName());
		result.setPurchasePrice(product.getPurchasePrice());
		result.setSupplierTO(fillSupplierTO(product.getSupplier()));
		return result;
	}

	public static StockItemTO fillStockItemTO(final StockItem stockitem) {
		final StockItemTO result = new StockItemTO();
		result.setId(stockitem.getId());
		result.setAmount(stockitem.getAmount());
		result.setMinStock(stockitem.getMinStock());
		result.setMaxStock(stockitem.getMaxStock());
		result.setSalesPrice(stockitem.getSalesPrice());
		return result;
	}

	public static StoreTO fillStoreTO(final Store store) {
		final StoreTO result = new StoreTO();
		result.setId(store.getId());
		result.setName(store.getName());
		result.setLocation(store.getLocation());
		return result;
	}

	public static StoreWithEnterpriseTO fillStoreWithEnterpriseTO(final Store store) {
		final StoreWithEnterpriseTO result = new StoreWithEnterpriseTO();
		result.setId(store.getId());
		result.setName(store.getName());
		result.setLocation(store.getLocation());
		result.setEnterpriseTO(fillEnterpriseTO(store.getEnterprise()));
		return result;
	}

	public static SupplierTO fillSupplierTO(final ProductSupplier supplier) {
		final SupplierTO result = new SupplierTO();
		result.setId(supplier.getId());
		result.setName(supplier.getName());
		return result;
	}
}
