package com.cy.webviewagent.core;

/**
 * WebView生命周期管理接口
 * Created by JLB6088 on 2017/10/19.
 */

interface IWebViewLifeCycle {
    /**
     * WebView恢复
     */
    void onResume();

    /**
     * WebView暂停
     */
    void onPause();

    void clearView();

    /**
     * WebView销毁
     */
    void onDestroy();

    /**
     * WebView返回
     * @return
     */
    boolean onBack();
}
