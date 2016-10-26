package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.enterpriseserver.productdispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.MoveGoodsIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.ProductDispatcherIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.PersistenceIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.StoreQueryIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.EnterpriseTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.ProductAmountTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.StoreTO;


public class ProductDispatcherImpl implements ProductDispatcherIf
{

	// Required interface
	protected StoreQueryIf StoreQueryIf;
	protected PersistenceIf PersistenceIf;
	protected MoveGoodsIf MoveGoodsIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("StoreQueryIf")) {
			StoreQueryIf = (StoreQueryIf) serverItf;
			return;
		}
		if (clientItfName.equals("PersistenceIf")) {
			PersistenceIf = (PersistenceIf) serverItf;
			return;
		}
		if (clientItfName.equals("MoveGoodsIf")) {
			MoveGoodsIf = (MoveGoodsIf) serverItf;
			return;
		}
	}


	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public ProductDispatcherImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the ProductDispatcherIf interface 'ProductDispatcherIf'
	// -----------------------------------------------------
		
	public ProductAmountTO[] orderProductsAvailableAtOtherStores(final EnterpriseTO arg0, final StoreTO arg1, final Collection arg2)  {
		// TODO Generated method
		return null;
	}
	
}

