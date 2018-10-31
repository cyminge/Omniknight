package com.cy.statistics;

import android.content.Context;

/**
 * 公共的统计接口
 * Created by cy on 18-8-28.
 */

interface ICommonStatistics {
    // 统计应用时长
    void onResume(Context context);
    void onPause(Context context);

    // 统计页面停留时间
    void onPageStart(Context context, String pageName);
    void onPageEnd(Context context, String pageName);
}
