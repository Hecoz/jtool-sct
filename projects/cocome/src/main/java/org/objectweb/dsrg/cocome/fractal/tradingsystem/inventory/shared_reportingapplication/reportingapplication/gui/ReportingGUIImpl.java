package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication.gui;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication.ReportingIf;

public class ReportingGUIImpl
{
	// Required interface
	protected ReportingIf ReportingIf;
	
	public void bindFc(final String clientItfName, final Object serverItf) {
		if (clientItfName.equals("ReportingIf")) {
			ReportingIf = (ReportingIf) serverItf;
			return;
		}
	}

	// -----------------------------------------------------
	// Constructor without parameters
	// -----------------------------------------------------

    public ReportingGUIImpl()
    {
    }
    
}

