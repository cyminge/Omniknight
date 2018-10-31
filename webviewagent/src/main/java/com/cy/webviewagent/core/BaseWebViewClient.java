package com.cy.webviewagent.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 帮助WebView处理各种通知、请求事件
 * 默认的WebViewClient, 对URL进行基本的拦截处理，调用方需要配置自己的拦截的逻辑
 * 注意内存泄露问题
 * Created by JLB6088 on 2017/10/14.
 */
public class BaseWebViewClient extends WebViewClient {

    private static final String TAG = BaseWebViewClient.class.getSimpleName();

    public BaseWebViewClient() {
    }

    /**
     * return true 表示应用拦截并由应用处理逻辑，建议使用return false，不然会重复调用WebView的loadUrl方法
     * return false 表示应用不拦截，由WebView进行处理
     * 默认 return false
     *
     * @param view
     * @param url
     * @return
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    @RequiresApi(api = 24)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    /**
     * 页面开始加载，可以在这个方法时显示进度条
     *
     * @param view
     * @param url
     * @param favicon
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    /**
     * 页面加载完成，可以在这个方法时隐藏进度条
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    /**
     * 页面加载失败
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    /**
     * 页面加载失败
     *
     * @param view
     * @param request
     * @param error
     */
    @RequiresApi(api = 23)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
//        handler.proceed();
//        handler.cancel();
//        Log.e("cyTest", "onReceivedSslError");
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("数字证书无效，打开网页存在风险，是否继续？");
        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
//        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        // 如果非法可以 view.stopLoading(); 停止加载
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }

    @RequiresApi(api = 21)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return super.shouldInterceptRequest(view, url);
    }

    /**
     * 适配androidO
     * 这个API处理一个WebView对象的渲染程序消失的情况，要么是因为系统杀死了渲染器以回收急需的内存，要么是因为渲染程序本身崩溃了。
     * 通过使用这个API，您可以让您的应用程序继续执行，即使渲染过程已经消失了。
     *
     * @param view
     * @param detail
     * @return
     */
    @RequiresApi(api = 26)
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        if (detail.didCrash()) {
            // Renderer crashed because of an internal error, such as a memory
            // access violation.
            Log.e(TAG, "The WebView rendering process crashed!");

            // In this example, the app itself crashes after detecting that the
            // renderer crashed. If you choose to handle the crash more gracefully
            // and allow your app to continue executing, you should 1) destroy the
            // current WebView instance, 2) specify logic for how the app can
            // continue executing, and 3) return "true" instead.
            return false;
        }

        // Renderer was killed because the system ran out of memory.
        // The app can recover gracefully by creating a new WebView instance
        // in the foreground.
        Log.e(TAG, "System killed the WebView rendering process " + "to reclaim memory. Recreating...");

        if (view != null) {
            ViewGroup webViewContainer = (ViewGroup) view.getParent();
            if (webViewContainer != null) {
                webViewContainer.removeView(view);
            }
            view.destroy();
            view = null;
        }

        // By this point, the instance variable "mWebView" is guaranteed
        // to be null, so it's safe to reinitialize it.
        return true; // The app continues executing.
    }
}
