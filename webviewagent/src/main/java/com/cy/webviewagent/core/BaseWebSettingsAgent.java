package com.cy.webviewagent.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 设置 WebView 配置项
 * you should to override this to match your app
 * Created by JLB6088 on 2017/10/14.
 */

public abstract class BaseWebSettingsAgent implements IWebViewSettingsAgent {
    private static final String TAG = BaseWebSettingsAgent.class.getSimpleName();

    public BaseWebSettingsAgent() {

    }

    @Override
    public void toSetting(@NonNull Context context, @NonNull WebView webView) {
        settings(context, webView);
    }

    /**
     * 如果要设置WebSettings，一定要重写这个方法！！！！
     * 因为发现接入方好多都是直接用的GioneeWebSettingsAgent，且并没有重写这个方法，这里改成abstract方法，强制要重写
     * @param context
     * @param webView
     */
    @SuppressLint("JavascriptInterface")
    protected abstract void settings(@NonNull Context context, @NonNull WebView webView);
//    {
//        webView.requestFocus();
//        webView.setSelected(true);
//        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
//        webView.setOverScrollMode(View.OVER_SCROLL_NEVER); // View.OVER_SCROLL_NEVER
//        webView.setInitialScale(0); // 0
//        webView.setVerticalScrollBarEnabled(false); // false
//        webView.setHorizontalScrollBarEnabled(false); // false
//        webView.requestFocusFromTouch();
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true); // true //支持js
//        webSettings.setSupportZoom(true);  // 支持缩放
//        webSettings.setDefaultZoom(selectZoomDensity(context));
//        webSettings.setTextZoom(Constant.WEBVIEW_TEXT_ZOOM); // Constant.WEBVIEW_TEXT_ZOOM
//        webSettings.setBlockNetworkImage(false); // false
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH); // WebSettings.RenderPriority.HIGH
////            if (AgentWebUtils.checkNetwork(webView.getContext())) {
////                //根据cache-control获取数据。
////                webSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
////            } else {
////                //没网，则从本地获取，即离线加载
////                webSettings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
////            }
//        webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
//        webSettings.setAppCacheEnabled(true); // true
//        webSettings.setAppCacheMaxSize(Constant.APPCACHE_MAXSIZE); // Constant.APPCACHE_MAXSIZE //缓存文件最大值
//        webSettings.setAppCachePath(context.getDir("web_appcache", Context.MODE_PRIVATE).getPath());
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // true //支持通过JS打开新窗口
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); // WebSettings.LayoutAlgorithm.NORMAL
//        webSettings.setDatabaseEnabled(true); // true
//        webSettings.setDatabasePath(context.getDir("web_databases", Context.MODE_PRIVATE).getPath());
//        webSettings.setDomStorageEnabled(true); // true
//        webSettings.setUseWideViewPort(true); // true //  将图片调整到适合webview大小
//
//        // 1. 允许加载本地html，或者file可能导致同源策略跨域访问对私有目录文件进行访问，导致隐私信息泄露，建议禁用file访问 cyminge
//        webSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议
//        if (Build.VERSION.SDK_INT >= 16) {
//            webSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
//        }
//        webSettings.setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
//        // 1. end cyminge
//        if (Build.VERSION.SDK_INT >= 19) {
//            webSettings.setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        } else {
//            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        }
//        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        webSettings.setSupportMultipleWindows(false); //多窗口
//        webSettings.setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
//
//        webSettings.setBuiltInZoomControls(false); // 设置支持缩放
//        webSettings.setSavePassword(false);
//        if (Build.VERSION.SDK_INT >= 21) {
//            //适配5.0不允许http和https混合使用情况
//            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else if (Build.VERSION.SDK_INT >= 19) {
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else if (Build.VERSION.SDK_INT < 19) {
//            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        webSettings.setNeedInitialFocus(true);
//        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//        webSettings.setGeolocationEnabled(true);
//        webSettings.setGeolocationDatabasePath(context.getDir("web_databases", Context.MODE_PRIVATE).getPath()); //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
//}

//    private WebSettings.ZoomDensity selectZoomDensity(Context context) {
//        int screenDensity = context.getApplicationContext().getResources().getDisplayMetrics().densityDpi;
//        WebSettings.ZoomDensity zoomDensity;
//        if (screenDensity >= 240) {
//            zoomDensity = WebSettings.ZoomDensity.FAR;
//        } else if (screenDensity < 240 && screenDensity >= 160) {
//            zoomDensity = WebSettings.ZoomDensity.MEDIUM;
//        } else {
//            zoomDensity = WebSettings.ZoomDensity.CLOSE;
//        }
//        return zoomDensity;
//    }

    @Override
    public void setWebChromeClient(WebView webview, WebChromeClient webChromeClient) {
        webview.setWebChromeClient(webChromeClient);
    }

    @Override
    public void setWebViewClient(WebView webView, WebViewClient webViewClient) {
        webView.setWebViewClient(webViewClient);
    }

    @Override
    public void setDownLoader(WebView webView, DownloadListener downloadListener) {
        webView.setDownloadListener(downloadListener);
    }

}
