package com.cy.statistics;

import android.content.Context;

/**
 * @Created by chenls on 2018/7/18.
 */
abstract class AbsStatistics implements ICustomStatistics, ICommonStatistics {
    void init(Context context, String appId, String channelId) {

    }

    boolean isAllowedUpload() {
        return true;
    }
}
