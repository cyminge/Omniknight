package com.cy.webviewagent.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.WebView;

/**
 * 设置WebView参数
 * Created by JLB6088 on 2017/10/14.
 */

interface IWebViewSettingsAgent extends IWebListener {

    /**
     * 设置WebView各种配置参数
     * @param context
     * @param webView
     * @return
     */
    void toSetting(@NonNull Context context, @NonNull WebView webView);
}
