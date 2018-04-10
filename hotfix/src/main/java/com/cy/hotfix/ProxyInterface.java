/*
 * Copyright (C) 2014 Huanju Inc. All rights reserved.
 */
package com.cy.hotfix;

import android.content.Context;


public interface ProxyInterface {
	
	/**
	 * 初始化
	 * @param context
	 */
	public void initialize(Context context);

	/**
	 * 释放资源
	 */
	public void deinitialize();

}
