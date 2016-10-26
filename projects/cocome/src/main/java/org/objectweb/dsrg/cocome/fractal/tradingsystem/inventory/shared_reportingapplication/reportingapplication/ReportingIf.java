package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication;

import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.EnterpriseTO;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.StoreTO;

/**
 * This interface provides methods for generating reports. It corresponds to the interface
 * ReportingIf in figure xx.
 *
 */
public interface ReportingIf {
	/**
	 * Generates report of available stocks in the specified store.
	 * @param storeTO Store for which report should be generated.
	 * @return Report transfer object containing stock information.
	 */
	public ReportTO getStockReport(StoreTO storeTO);
	
	/**
	 * Generates report of cumulated available stocks of specified enterprise.
	 * @param enterpriseTO The enterprise for which the report should be generated.
	 * @return Report transfer object containing cumulated stock information.
	 */
	public ReportTO getStockReport(EnterpriseTO enterpriseTO);

	/**
	 * Genrates report which informs about the mean time to delivery
	 * for each supplier of the specified enterprise.
	 * @param enterpriseTO TradingEnterprise for which the report should be generated.
	 * @return Report transfer object containing mean time to delivery information.
	 */
	public ReportTO getMeanTimeToDeliveryReport(EnterpriseTO enterpriseTO);
}
