package com.cy.webviewagent.core;

import android.net.Uri;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
 * 注意内存泄露问题
 * Created by zf on 2017/10/15.
 */
public class BaseWebChromeClient extends WebChromeClient {

    private static final String TAG = "BaseWebChromeClient";

    public BaseWebChromeClient() {
    }

    /**
     * android3.0+ 打开文件选择器
     * @param uploadMsg
     * @param acceptType
     */
    // 3.0+
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
    }

    /**
     * android4.1+ 打开文件选择器
     * @param uploadMsg
     * @param acceptType
     * @param capture
     */
    // 4.1+
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
    }

    /**
     * android4.4+ 打开文件选择器
     * @param uploadFilePaths
     * @param acceptTypes
     * @param capture
     */
    // 4.4+
    @SuppressWarnings("unused")
    public void showFileChooser(ValueCallback<String[]> uploadFilePaths, String acceptTypes, boolean capture) {
    }

    /**
     * android5.0+ 打开文件选择器
     * @param webView
     * @param filePathCallback
     * @param fileChooserParams
     * @return
     */
    // 5.0
    @SuppressWarnings("unused")
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        return false;
    }

    /**
     * 接收到WebView的标题
     * @param view
     * @param title
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    /**
     * WebView加载进度发生改变
     * @param view
     * @param progress
     */
    @Override
    public void onProgressChanged(WebView view, int progress) {
        super.onProgressChanged(view, progress);
    }

    /**
     * WebView警告弹框
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

    /**
     * 当js调用java时会回调这个方法，我们在这里做处理拦截异常url
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
}
