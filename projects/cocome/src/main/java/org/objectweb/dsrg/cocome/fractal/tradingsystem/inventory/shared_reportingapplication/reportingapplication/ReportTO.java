package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication;

/**
 * Transfer object class for encapsulating report information in simple text format.
 *
 */
public class ReportTO {

	protected String reportText;

	/**
	 * Gets the report in text(html) format.
	 * @return the report
	 */
	public String getReportText()
	{
		return reportText;
	}

	/**
	 * Sets report text.
	 * @param reportText The report text.
	 */
	public void setReportText(final String reportText)
	{
		this.reportText  = reportText;
	}
}
