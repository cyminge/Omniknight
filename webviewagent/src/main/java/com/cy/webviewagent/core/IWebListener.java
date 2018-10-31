package com.cy.webviewagent.core;

import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 设置与WebView相关的辅助类
 * Created by JLB6088 on 2017/10/14.
 */

interface IWebListener {
    void setWebChromeClient(WebView webview, WebChromeClient webChromeClient);
    void setWebViewClient(WebView webView, WebViewClient webViewClient);
    void setDownLoader(WebView webView, DownloadListener downloadListener);
}
