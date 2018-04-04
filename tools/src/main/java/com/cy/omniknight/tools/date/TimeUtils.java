package com.cy.omniknight.tools.date;

import android.text.format.Time;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtils {
	public static String getFormatSecUYmd(long second) {
		Time time = new Time();
		time.set(second * 1000);
		return time.format("%Y/%m/%d");
	}

	public static String getFormatMillUYmd(long mills) {
		Time time = new Time();
		time.set(mills);
		return time.format("%Y/%m/%d");
	}
	
	public static long getCurrentTimeSecond(){
		return System.currentTimeMillis() / 1000;
	}
	
	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}
	
	public static long getGMTTimeMillis() {
		Calendar calendar = Calendar.getInstance();
		long unixTime = calendar.getTimeInMillis();
		return unixTime - TimeZone.getDefault().getRawOffset();
	}
}
