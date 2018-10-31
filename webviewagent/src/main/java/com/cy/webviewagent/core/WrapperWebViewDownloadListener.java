package com.cy.webviewagent.core;

import android.webkit.DownloadListener;

import com.cy.webviewagent.util.Utils;

import java.lang.ref.WeakReference;

/**
 * WebView下载监听
 * Created by JLB6088 on 2017/10/16.
 */

final class WrapperWebViewDownloadListener implements DownloadListener {

    private WeakReference<IBaseWebViewDownloadListener> mGioneeWebViewDownloadListener;

    public WrapperWebViewDownloadListener(IBaseWebViewDownloadListener listener) {
        mGioneeWebViewDownloadListener = Utils.buildReference(listener);
    }

    /**
     * 通知应用有文件需要下载
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength
     */
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if(Utils.isWeakReferenceActive(mGioneeWebViewDownloadListener)) {
            Utils.getReferenceTarget(mGioneeWebViewDownloadListener).onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
        }
    }
}
