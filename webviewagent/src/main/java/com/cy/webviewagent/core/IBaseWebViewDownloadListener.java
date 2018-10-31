package com.cy.webviewagent.core;

/**
 * Created by JLB6088 on 2017/12/4.
 */

public interface IBaseWebViewDownloadListener {
    /**
     * 通知应用有文件需要下载
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength
     */
    void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);
}
