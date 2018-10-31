package com.cy.webviewagent.core;

/**
 * Created by zf on 2017/10/17.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cy.webviewagent.util.DeviceUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 建议在布局里通过new BaseWebView或其子View的方式add到布局里。
 */
public class BaseWebView extends WebView {
    private static final String TAG = BaseWebView.class.getSimpleName();
    private boolean mIsInited = false;
    private String mCustomUserAgent;
    private boolean mIsUAHasSet = false;

    @MainThread
    public BaseWebView(Context context) {
        this(context, null);
    }

    @MainThread
    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsInited = true;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    @MainThread
    @CallSuper
    @Override
    public void loadUrl(@NonNull String url) {
        setUserAgent(url);
        super.loadUrl(url);
    }

    private void setUserAgent(String url) {
        // 如果是Gionee的链接，则加上gionee标识的userAgent
        if(mIsUAHasSet) {
            return;
        }
        if (!TextUtils.isEmpty(url) && isGioneeUrl(url)) {
            getSettings().setUserAgentString(DeviceUtils.getUAString(this, getContext()) + getCustomUserAgentString());
        } else {
            getSettings().setUserAgentString(DeviceUtils.getNotGioneeUAString(this, getContext()) + getCustomUserAgentString());
        }
        mIsUAHasSet = true;
    }

    private String getCustomUserAgentString() {
        return mCustomUserAgent;
    }

    void setCustomUserAgentString(String customUserAgent) {
        mCustomUserAgent = customUserAgent;
    }

    /**
     * 你需要重写该方法，如果还有其他url是可以表示是gionee标志的
     * @param url
     * @return
     */
    protected boolean isGioneeUrl(String url) {
        if(url.contains(".gionee.") || url.contains(".amigo.")) {
            return true;
        }

        return false;
    }


    @MainThread
    @CallSuper
    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        setUserAgent(url);
        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void destroy() {
        setVisibility(View.GONE);
        removeAllViewsInLayout();
        fixedStillAttached();
        releaseConfigCallback();
        if (mIsInited) {
            super.destroy();
        }
        mIsInited = false;
        mIsUAHasSet = false;
        mCustomUserAgent = null;
    }

    /**
     * 经过大量的测试，按照以下方式才能保证JS脚本100%注入成功：
     * 1、在第一次loadUrl之前注入JS（在addJavascriptInterface里面注入即可，setWebViewClient和setWebChromeClient要在addJavascriptInterface之前执行）；
     * 2、在webViewClient.onPageStarted中都注入JS；
     * 3、在webChromeClient.onProgressChanged中都注入JS，并且不能通过自检查（onJsPrompt里面判断）JS是否注入成功来减少注入JS的次数，因为网页中的JS可以同时打开多个url导致无法控制检查的准确性；
     *
     *
     * @deprecated Android4.2.2及以上版本的addJavascriptInterface方法已经解决了安全问题，如果不使用“网页能将JS函数传到Java层”功能，不建议使用该类，毕竟系统的JS注入效率才是最高的；
     */
    @SuppressLint("JavascriptInterface")
    @Override
    @Deprecated
    public void addJavascriptInterface(Object interfaceObj, String interfaceName) {
        super.addJavascriptInterface(interfaceObj, interfaceName);
    }

    @Override
    public void clearHistory() {
        if (mIsInited) {
            super.clearHistory();
        }
    }

    public static Pair<Boolean, String> isWebViewPackageException(Throwable e) {
        String messageCause = e.getCause() == null ? e.toString() : e.getCause().toString();
        String trace = Log.getStackTraceString(e);
        if (trace.contains("android.content.pm.PackageManager$NameNotFoundException")
                || trace.contains("java.lang.RuntimeException: Cannot load WebView") || trace.contains(
                        "android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed")) {

//            LogUtils.safeCheckCrash(TAG, "isWebViewPackageException", e);
            return new Pair<Boolean, String>(true, "WebView load failed, " + messageCause);
        }
        return new Pair<Boolean, String>(false, messageCause);
    }

    @Override
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Throwable e) {
            Pair<Boolean, String> pair = isWebViewPackageException(e);
            if (pair.first) {
                Toast.makeText(getContext(), pair.second, Toast.LENGTH_SHORT).show();
                destroy();
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean isPrivateBrowsingEnabled() {
        if (Build.VERSION.SDK_INT == 15 && getSettings() == null) {
            return false; // getSettings().isPrivateBrowsingEnabled()
        } else {
            return super.isPrivateBrowsingEnabled();
        }
    }

    /*
     * Activity在onDestroy时调用webView的destroy，可以停止播放页面中的音频
     */
    private void fixedStillAttached() {
        // java.lang.Throwable: Error: WebView.destroy() called while still attached!
        // at android.webkit.WebViewClassic.destroy(WebViewClassic.java:4142)
        // at android.webkit.WebView.destroy(WebView.java:707)
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) { // 由于自定义webView构建时传入了该Activity的context对象，因此需要先从父容器中移除webView，然后再销毁webView；
            ViewGroup mWebViewContainer = (ViewGroup) getParent();
            mWebViewContainer.removeAllViewsInLayout();
        }
    }

    // 解决WebView内存泄漏问题；
    private void releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < 16) { // JELLY_BEAN
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                Log.w(TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                Log.w(TAG, e.getMessage());
            }
        } else if (Build.VERSION.SDK_INT < 19) { // KITKAT
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame")
                        .getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
                Log.w(TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.w(TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                Log.w(TAG, e.getMessage());
            }
        }
    }

    /**
     * Android 4.4 KitKat 使用Chrome DevTools 远程调试WebView WebView.setWebContentsDebuggingEnabled(true);
     * http://blog.csdn.net/t12x3456/article/details/14225235
     */
    @TargetApi(19)
    protected void trySetWebDebuggEnabled() {
//        if (LogUtils.isDebug() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            try {
//                Class<?> clazz = WebView.class;
//                Method method = clazz.getMethod("setWebContentsDebuggingEnabled", boolean.class);
//                method.invoke(null, true);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
    }
}
