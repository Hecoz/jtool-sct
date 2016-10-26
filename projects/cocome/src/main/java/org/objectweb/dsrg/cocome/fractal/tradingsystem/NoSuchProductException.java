package org.objectweb.dsrg.cocome.fractal.tradingsystem;

/**
 * This exception is thrown if there is no product for a specific barcode
 * in the database
 */
public class NoSuchProductException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6026652539932418410L;

	public NoSuchProductException() {
		// TODO Auto-generated constructor stub
	}

	public NoSuchProductException(final String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NoSuchProductException(final Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NoSuchProductException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
