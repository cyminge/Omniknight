package com.cy.omniknight.tracer;

public class ExceptionCaughted extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3197760023902261356L;
	public static int IGNORE = 0;
	public static int REBOOT = 1;

	public String mStrExternInfo = null;
	public int mOperation = IGNORE;
	
}
