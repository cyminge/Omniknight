package com.cy.omniknight.tracer.export;

import android.content.Context;

public abstract class AfterCrashRunnable implements Runnable {
	protected Context mContext;
	
	public AfterCrashRunnable (Context context) {
		mContext = context;
	}
	
}
