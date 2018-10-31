package com.cy.webviewagent.core;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;

/**
 * Created by JLB6088 on 2017/10/19.
 */

public class BaseWebViewLifeCycle implements IWebViewLifeCycle {

    private static final String BLANK_URL = "about:blank";
    private static final String UNNETWORK_URL = "data:text/html,";

    private WebView mWebView;

    public BaseWebViewLifeCycle(@NonNull WebView webView) {
        mWebView = webView;
    }

    @Override
    public void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public void clearView() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }

        if(mWebView != null) {
            mWebView.loadUrl(BLANK_URL);
        }
    }

    @Override
    public boolean onBack() {
        if (!mWebView.canGoBack()) {
            return false;
        }

        return goBackOrForward(mWebView);
    }

    private boolean isValidWebHistory(String url) {
        return isValidUrl(url);
    }

    private boolean isValidUrl(String url) {
        String curUrl = mWebView.getUrl();
        return !(url.equals(curUrl) || url.equals(UNNETWORK_URL) || curUrl.endsWith(UNNETWORK_URL));
    }

    /**
     * 返回逻辑，这个只是默认实现，如果涉及到重定向的问题，那么需要自己实现，如果所有应用的实现都一样，后面会统一。
     * @param webView
     * @return
     */
    protected boolean goBackOrForward(WebView webView) {
        WebBackForwardList list = webView.copyBackForwardList();
        int preIndex = list.getCurrentIndex() - 1;
        int step = 0;

        for (int i = preIndex; i >= 0; i--) {
            WebHistoryItem item = list.getItemAtIndex(i);
            if (isValidWebHistory(item.getUrl())) {
                step = (i - preIndex - 1);
                break;
            }
        }

        if (step == 0) {
            return false;
        } else {
            webView.goBackOrForward(step);
        }

        return true;
    }

    @Override
    public void onDestroy() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }

        if(null == mWebView) {
            return;
        }

        mWebView.loadUrl("about:blank");
        mWebView.stopLoading();
        if (mWebView.getHandler() != null) {
            mWebView.getHandler().removeCallbacksAndMessages(null);
        }
        mWebView.removeAllViews();
        ViewGroup viewGroup;
        if ((viewGroup = ((ViewGroup) mWebView.getParent())) != null) {
            viewGroup.removeView(mWebView);
        }
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.setTag(null);
        mWebView.clearHistory();
        mWebView.destroy();
        mWebView = null;
    }
}
