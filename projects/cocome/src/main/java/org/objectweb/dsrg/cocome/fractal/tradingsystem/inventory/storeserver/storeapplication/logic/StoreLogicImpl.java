package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.logic;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.*;


public class StoreLogicImpl implements AccountSaleEventHandlerIf, CashDeskConnectorIf, MoveGoodsIf, StoreIf
{

	// Required interface
	protected ProductDispatcherIf ProductDispatcherIf;
	protected StoreQueryIf StoreQueryIf;
	protected PersistenceIf PersistenceIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("ProductDispatcherIf")) {
			ProductDispatcherIf = (ProductDispatcherIf) serverItf;
			return;
		}
		if (clientItfName.equals("StoreQueryIf")) {
			StoreQueryIf = (StoreQueryIf) serverItf;
			return;
		}
		if (clientItfName.equals("PersistenceIf")) {
			PersistenceIf = (PersistenceIf) serverItf;
			return;
		}
	}


	private long storeid;
	
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public StoreLogicImpl()
    {
		
    }
    
	// -----------------------------------------------------
	// Implementation of the CashDeskConnectorIf interface 'CashDeskConnectorIf'
	// -----------------------------------------------------
		
	public void bookSale(final SaleTO arg0)  {
		// TODO Generated method
		
	}
	
	public ProductWithStockItemTO getProductWithStockItem(final long productBarCode) throws org.objectweb.dsrg.cocome.fractal.tradingsystem.NoSuchProductException {
		ProductWithStockItemTO result = null;

		final StockItem stockitem = StoreQueryIf.queryStockItem(
				storeid, productBarCode, null);
		if (stockitem == null) {
			throw new NoSuchProductException(
					"There exists no product with barcode " + productBarCode);
		}

		result = FillTransferObjects.fillProductWithStockItemTO(stockitem);
		return result;
	}
	
	// -----------------------------------------------------
	// Implementation of the StoreIf interface 'StoreIf'
	// -----------------------------------------------------
		
	public ComplexOrderEntryTO[] getStockItems(final ProductTO[] arg0) {
		// TODO Generated method
		return null;
	}
	
	public StoreWithEnterpriseTO getStore()  {
		// TODO Generated method
		return null;
	}
	
	public List<ProductWithStockItemTO> getProductsWithLowStock()  {
		// TODO Generated method
		return null;
	}
	
	public List<ProductWithSupplierTO> getAllProducts()  {
		// TODO Generated method
		return null;
	}
	
	public List<ProductWithSupplierAndStockItemTO> getAllProductsWithOptionalStockItem()  {
		// TODO Generated method
		return null;
	}
	
	public List<ComplexOrderTO> orderProducts(final ComplexOrderTO arg0)  {
		// TODO Generated method
		return null;
	}
	
	public ComplexOrderTO getOrder(final long arg0)  {
		// TODO Generated method
		return null;
	}
	
	public void rollInReceivedOrder(final ComplexOrderTO arg0)  {
		// TODO Generated method
		
	}
	
	public ProductWithStockItemTO changePrice(final StockItemTO arg0)  {
		// TODO Generated method
		return null;
	}
	
	public void markProductsUnavailableInStock(final ProductMovementTO arg0) throws org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.ProductNotAvailableException {
		// TODO Generated method
		
	}
	
	// -----------------------------------------------------
	// Implementation of the AccountSaleEventHandlerIf interface 'AccountSaleEventHandlerIf'
	// -----------------------------------------------------
		
	public void onAccountSaleEvent(final AccountSaleEvent arg0)  {
		// TODO Generated method
		
	}
	
	// -----------------------------------------------------
	// Implementation of the MoveGoodsIf interface 'MoveGoodsIf'
	// -----------------------------------------------------
		
}

