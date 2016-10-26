package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication.logic;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.EnterpriseQueryIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.PersistenceIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.StoreQueryIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication.ReportingIf;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.EnterpriseTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.StoreTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication.ReportTO;


public class ReportingLogicImpl implements ReportingIf
{

	// Required interface
	protected StoreQueryIf StoreQueryIf;
	protected PersistenceIf PersistenceIf;
	protected EnterpriseQueryIf EnterpriseQueryIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("StoreQueryIf")) {
			StoreQueryIf = (StoreQueryIf) serverItf;
			return;
		}
		if (clientItfName.equals("PersistenceIf")) {
			PersistenceIf = (PersistenceIf) serverItf;
			return;
		}
		if (clientItfName.equals("EnterpriseQueryIf")) {
			EnterpriseQueryIf = (EnterpriseQueryIf) serverItf;
			return;
		}
	}


	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public ReportingLogicImpl()
    {
    }
    
	// -----------------------------------------------------
	// Implementation of the ReportingIf interface 'ReportingIf'
	// -----------------------------------------------------
		
	public ReportTO getStockReport(final StoreTO arg0)  {
		// TODO Generated method
		return null;
	}
	
	public ReportTO getStockReport(final EnterpriseTO arg0)  {
		// TODO Generated method
		return null;
	}
	
	public ReportTO getMeanTimeToDeliveryReport(final EnterpriseTO arg0)  {
		// TODO Generated method
		return null;
	}
	
}

