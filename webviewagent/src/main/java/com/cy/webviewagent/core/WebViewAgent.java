package com.cy.webviewagent.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cy.webviewagent.util.ConnectivityController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * WebView 管理类
 * Created by JLB6088 on 2017/10/14.
 */

public final class WebViewAgent {
    private Context mContext;
    private WebViewClient mWebViewClient;
    private WebChromeClient mWebChromeClient;
    private IWebViewSettingsAgent mWebSettingsAgent;
    private BaseWebView mWebView;
    private IBaseWebViewDownloadListener mDownloadListener;
    private HashMap<String, Object> mJavaObjects;
    private IWebViewLifeCycle mIWebViewLifeCycle;
    private String mCustomUserAgent;

    public WebViewAgent(WebViewAgentBuilder builder) {
        mContext = builder.mContext;
        mWebViewClient = builder.mWebViewClient;
        mWebChromeClient = builder.mWebChromeClient;
        mWebSettingsAgent = builder.mWebSettingsAgent;
        mWebView = builder.mWebView;
        mDownloadListener = builder.mDownloadListener;
        mJavaObjects = builder.mJavaObject;
        mIWebViewLifeCycle = builder.mIWebViewLifeCycle;
        mCustomUserAgent = builder.mCustomUserAgent;
        init();
    }

    private void init() {
        ConnectivityController.init(mContext);
        doSafeCheck();
    }

    /**
     * this method can be keep for future
     */
    private void doSafeCheck() {
        // if android4.2.2 version have safe loopholes, check WebView safe loopholes and fix they.
    }

    /**
     * 入口函数
     * @param context
     * @param webView
     * @return
     */
    public static WebViewAgentBuilder initWith(@NonNull Context context, @NonNull BaseWebView webView) {
        WebViewAgentBuilder builder = new WebViewAgentBuilder(context);
        builder.setWebView(webView);
        return builder;
    }

    /**
     * 生成WebViewAgent后，开始配置.
     * @return
     */
    public WebViewAgent ready() {

        mWebView.setCustomUserAgentString(mCustomUserAgent);

        mWebView.setWebViewClient(getDefaultWebViewClient());
        if (null != mWebChromeClient) {
//            mWebView.setWebChromeClient(new WrapperWebChromeClient(mWebChromeClient));
            mWebView.setWebChromeClient(mWebChromeClient);
        }

        if (null != mDownloadListener) {
            mWebView.setDownloadListener(new WrapperWebViewDownloadListener(mDownloadListener));
        }

        if(null != mWebSettingsAgent) {
            mWebSettingsAgent.toSetting(mContext, mWebView);
        }

        if(null == mIWebViewLifeCycle) {
            mIWebViewLifeCycle = new BaseWebViewLifeCycle(mWebView);
        }

        if (null != mJavaObjects) {
            addJavascriptInterface(mJavaObjects);
        }
        return this;
    }

    /**
     * 添加java与js交互的对象
     * @param maps
     */
    @SuppressLint("JavascriptInterface")
    private void addJavascriptInterface(HashMap<String, Object> maps) {
        Set<Map.Entry<String, Object>> sets = maps.entrySet();
        for (Map.Entry<String, Object> mEntry : sets) {
            Object v = mEntry.getValue();
            mWebView.addJavascriptInterface(v, mEntry.getKey());
        }
    }

    /**
     * 提供默认的WebViewClient, 对URL进行基本的拦截处理，调用方需要配置自己的拦截的逻辑
     * 注意内存泄露问题
     * @return
     */
    private WebViewClient getDefaultWebViewClient() {
        if (null == mWebViewClient) {
            mWebViewClient = new BaseWebViewClient();
        }
//        return new WrapperWebViewClient(mWebViewClient);
        return mWebViewClient;
    }

    /**
     * 恢复WebView
     */
    public void onResume() {
        mIWebViewLifeCycle.onResume();
    }

    /**
     * 暂停WebView
     */
    public void onPause() {
        mIWebViewLifeCycle.onPause();
    }

    public void clearView() {
        mIWebViewLifeCycle.clearView();
    }

    public WebView getWebView() {
        return mWebView;
    }

    /**
     * 销毁WebView
     */
    public void onDestroy() {
        ConnectivityController.destroy();
        mIWebViewLifeCycle.onDestroy();
    }

    /**
     * 用户按返回键或虚拟返回键，判断是否在WebView内部进行跳转
     * @return
     */
    public boolean onBack() {
        return mIWebViewLifeCycle.onBack();
    }

    /**
     * 配置WebViewAgent
     */
    public static class WebViewAgentBuilder {
        private Context mContext;
        private BaseWebView mWebView;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;
        private IWebViewSettingsAgent mWebSettingsAgent;
        private IBaseWebViewDownloadListener mDownloadListener;
        private HashMap<String, Object> mJavaObject;
        private IWebViewLifeCycle mIWebViewLifeCycle;
        private String mCustomUserAgent;

        private void addJavaObject(String key, Object o) {
            if (mJavaObject == null) {
                mJavaObject = new HashMap<>();
            }
            mJavaObject.put(key, o);
        }

        private void addJavaObjects(HashMap<String, Object> map) {
            if (mJavaObject == null) {
                mJavaObject = new HashMap<>();
            }

            mJavaObject.putAll(map);
        }

        public WebViewAgentBuilder(@NonNull Context context) {
            mContext = context;
        }

        private WebViewAgentBuilder setWebView(@NonNull BaseWebView webView) {
            mWebView = webView;
            return this;
        }

        public WebViewAgentBuilder setWebSettingsAgent(IWebViewSettingsAgent webSettingsAgent) {
            mWebSettingsAgent = webSettingsAgent;
            return this;
        }

        public WebViewAgentBuilder setWebViewClient(WebViewClient webViewClient) {
            mWebViewClient = webViewClient;
            return this;
        }

        public WebViewAgentBuilder setWebChromeClient(WebChromeClient webChromeClient) {
            mWebChromeClient = webChromeClient;
            return this;
        }

        public WebViewAgentBuilder setDownloadListener(IBaseWebViewDownloadListener downloadListener) {
            mDownloadListener = downloadListener;
            return this;
        }

        public WebViewAgentBuilder setWebViewLifeCycle(IWebViewLifeCycle webViewLifeCycle) {
            mIWebViewLifeCycle = webViewLifeCycle;
            return this;
        }

        public WebViewAgentBuilder addJavascriptInterface(@NonNull String name, @NonNull Object object) {
            addJavaObject(name, object);
            return this;
        }

        public WebViewAgentBuilder addJavaScriptInterface(@NonNull HashMap<String, Object> map) {
            addJavaObjects(map);
            return this;
        }

        /**
         * 提供一个入口让调用者传入一些自定义的UserAgent，这个方法里的UserAgent参数会拼接到已有的UserAgent中，
         * 所以麻烦看下DeviceUtils的getUAString方法，不要传入重复的内容
         * 尽可能的不要传入一些设备敏感信息
         * @param customUserAgent
         * @return
         */
        public WebViewAgentBuilder setCustomUserAgent(String customUserAgent) {
            mCustomUserAgent = customUserAgent;
            return this;
        }

        public WebViewAgent builder() {
            return new WebViewAgent(this);
        }
    }

    /**
     * 调试Android WebView， 该方法只在Android4.4有效。
     * @param isNeedDebug 是否需要调试WebView， 比如在测试环境下可以开启。
     */
    public static void setWebContentsDebuggingEnabled(boolean isNeedDebug) {
        if(Build.VERSION.SDK_INT >= 19 && isNeedDebug) { // Build.VERSION_CODES.KITKAT
            BaseWebView.setWebContentsDebuggingEnabled(true);
        }
    }

}
