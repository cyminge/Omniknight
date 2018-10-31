package com.cy.statistics;

import android.content.Context;

import java.util.Map;

/**
 * context 	当前宿主进程的ApplicationContext上下文。
 * eventId 	为当前统计的事件ID。
 * label 	事件的标签属性。
 */
interface ICustomStatistics {

    void onEvent(Context context, String eventId);

    void onEvent(Context context, String eventId, String eventLabel);

    void onEvent(Context context, String eventId, String eventLabel, Map<String, Object> eventMap);

    void onError(Context context, Throwable throwable);
}
