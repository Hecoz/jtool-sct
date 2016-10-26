package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.data.enterprise;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.EnterpriseQueryIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.PersistenceContext;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.ProductSupplier;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.TradingEnterprise;

public class EnterpriseImpl implements EnterpriseQueryIf
{
	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public EnterpriseImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the EnterpriseQueryIf interface 'EnterpriseQueryIf'
	// -----------------------------------------------------
		
	public TradingEnterprise queryEnterpriseById(final long arg0, final PersistenceContext arg1)  {
		// TODO Generated method
		return null;
	}
	
	public long getMeanTimeToDelivery(final ProductSupplier arg0, final TradingEnterprise arg1, final PersistenceContext arg2)  {
		// TODO Generated method
		return 0;
	}
	
}

