package com.cy.omniknight.tools.date;

import android.annotation.SuppressLint;

import java.text.*;
import java.util.Calendar;

public class TimeRandomString {
	/** .log */
	/** The FieldPosition. */
	private static final FieldPosition HELPER_POSITION = new FieldPosition(0);
	/** This Format for format the data to special format. */
	@SuppressLint("SimpleDateFormat")
	private final static Format dateFormat = new SimpleDateFormat("MMddHHmmssS");
	/** This Format for format the number to special format. */
	private final static NumberFormat numberFormat = new DecimalFormat("0000");
	/** This int is the sequence number ,the default value is 0. */
	private static int seq = 0;
	private static final int MAX = 9999;

	/**
	 * 
	 * @return
	 */
	public static synchronized String generateSequenceNo() {
		Calendar rightNow = Calendar.getInstance();
		StringBuffer sb = new StringBuffer();
		dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
		numberFormat.format(seq, sb, HELPER_POSITION);
		if (seq == MAX) {
			seq = 0;
		} else {
			seq++;
		}
		return sb.toString();
	}
}