package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.data.store;

import java.util.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.StoreQueryIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.PersistenceContext;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.Product;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.ProductOrder;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.StockItem;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.*;


public class StoreImpl implements StoreQueryIf
{
	public static final int NUMBEROF_ENTERPRISES = 1;
	public static final int NUMBEROF_STORES = 1;
	public static final int NUMBEROF_STOCKITEMS = 5;
	public static final int NUMBEROF_SUPPLIERS = 2;
	public static final int NUMBEROF_PRODUCTS = 5;
	public static final int NUMBEROF_PRODUCTORDERS = 5;
	public static final int NUMBEROF_ORDERENTRIES = 2;
	
	List<TradingEnterprise> enterprises;
	List<Store> stores;
	List<ProductSupplier> suppliers;
	List<Product> products;
	List<ProductOrder> productorders;
	List<StockItem> stockitems;
	List<OrderEntry> orderentries;
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public StoreImpl()
    {
   		enterprises = new ArrayList<TradingEnterprise>();
		stores = new ArrayList<Store>();
		suppliers = new ArrayList<ProductSupplier>();
		products = new ArrayList<Product>();
		productorders = new ArrayList<ProductOrder>();
		stockitems = new ArrayList<StockItem>();
		orderentries = new ArrayList<OrderEntry>();

		//fill Enterprises
		for(int i = 0; i < NUMBEROF_ENTERPRISES; i++) {
			final TradingEnterprise enterprise = new TradingEnterprise();
			enterprise.setName("TradingEnterprise " + i);
			enterprises.add(enterprise);
		}
		
		//fill Stores and connect to Enterprises
		for(int i = 0; i < NUMBEROF_STORES; i++) {
			final Store store = new Store();
			store.setName("Store " + i);
			store.setLocation("StoreLocation " + i);
			//select randomly TradingEnterprise to connect to Store
			final TradingEnterprise enterprise = enterprises.get(0);
			store.setEnterprise(enterprise);
			stores.add(store);
		}
		//fill Suppliers
		for(int i = 0; i < NUMBEROF_SUPPLIERS; i++) {
			final ProductSupplier supplier = new ProductSupplier();
			supplier.setName("ProductSupplier " + i);
			suppliers.add(supplier);
		}
		//fill Products and connect to Suppliers
		for(int i = 0; i < NUMBEROF_PRODUCTS; i++) {
			final Product product = new Product();
			//each barcode is different
			product.setBarcode(i + 777);
			product.setName("Product " + i);
			product.setPurchasePrice(5);
			//select randomly ProductSupplier to connect to Product
			final ProductSupplier supplier = suppliers.get(i % NUMBEROF_SUPPLIERS);
			product.setSupplier(supplier);
			products.add(product);
		}
		//fill StockItems and connect to Stores and Products
		for(int i = 0; i < NUMBEROF_STOCKITEMS; i++) {
			final StockItem stockitem = new StockItem();
			stockitem.setSalesPrice(5);
			stockitem.setAmount(8);
			stockitem.setMinStock(2);
			stockitem.setMaxStock(12);
			//select randomly Store to connect to StockItem
			final Store store = stores.get(0);
			stockitem.setStore(store);
			//select randomly Product to connect to StockItem
			final Product product = products.get(i);
			stockitem.setProduct(product);
			stockitems.add(stockitem);
		}
		//connect TradingEnterprise and ProductSupplier (n:m)
		for(int i = 0; i < enterprises.size(); i++) {
			final TradingEnterprise enterprise = enterprises.get(i);
			final List<ProductSupplier> sup = new ArrayList<ProductSupplier>();
			for (int j = 0; j < suppliers.size(); j++) {
				final ProductSupplier supplier = suppliers.get(j);
				if ((j % 2) == (i % 2))
        {
          sup.add(supplier);
        }
			}
			enterprise.setSuppliers(sup);
		}
		//fill ProductOrders and connect to Store
		for(int i = 0; i < NUMBEROF_PRODUCTORDERS; i++) {
			final ProductOrder productorder = new ProductOrder();
			productorder.setOrderingDate(new Date(500 + i * 100));
			productorder.setDeliveryDate(new Date(1000 + i * 50));
			//select randomly Store to connect to ProductOrder
			final Store store = stores.get(0);
			productorder.setStore(store);
			productorders.add(productorder);
		}
		//fill OrderEntries and connect to Orders and Products
		for(int i = 0; i < NUMBEROF_ORDERENTRIES; i++) {
			final OrderEntry orderentry = new OrderEntry();
			orderentry.setAmount(6);
			//select randomly ProductOrder to connect to OrderEntry
			final ProductOrder productorder = productorders.get(((i+1)*2) % NUMBEROF_PRODUCTORDERS);
			orderentry.setOrder(productorder);
			//select randomly Product to connect to OrderEntry
			final Product product = products.get(((i+1)*3) % NUMBEROF_PRODUCTS);
			orderentry.setProduct(product);
			orderentries.add(orderentry);
		}
		
		System.out.println("StoreImpl: Objects initialized");
    }
	
	// -----------------------------------------------------
	// Implementation of the StoreQueryIf interface 'StoreQueryIf'
	// -----------------------------------------------------
		
	public Store queryStoreById(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public Collection<Product> queryProducts(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public Collection<StockItem> queryLowStockItems(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public Collection<StockItem> queryAllStockItems(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public ProductOrder queryOrderById(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public StockItem queryStockItem(final long storeId, final long productbarcode, final PersistenceContext pctx)  {
		Product foundProduct = null;
		
		for (final Product product : products) {
			if (product.getBarcode() == productbarcode) {
				foundProduct = product;
				break;
			}
		}
		if (foundProduct == null)
    {
      return null;
    }
		
		for (final StockItem item : stockitems) {
			if (item.getProduct().equals(foundProduct) && item.getStore().getId() == storeId) {
				return item;
			}
		}
		
		return null;
	}
	
	public StockItem queryStockItemById(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public Product queryProductById(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public Collection<StockItem> getStockItems(final long arg0, final long[] arg1, final PersistenceContext arg2)  {
		// TODO Generated method
		return null;
	}
	
}

