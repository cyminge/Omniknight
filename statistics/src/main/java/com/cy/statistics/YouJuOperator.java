package com.cy.statistics;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

class YouJuOperator extends AbsStatistics {

    private static final String TAG = "YouJuOperator";

    private static boolean mYouJuEnabled = true;

    public YouJuOperator(Context context) {
        if (IS_OVERSEA_PROJECT) {
            init(context, "5C389D4590E9559D4EE7D2A19E86BBD9", "海外" + context.getApplicationInfo().processName);
        } else {
            init(context, "20A503C8E89441B64D67146AA08B50B8", "国内" + context.getApplicationInfo().processName);
        }
    }

    private static boolean isTrafficTip() {
//        // 需要考虑跑流量的问题
//        if (ConfirmFlow.isDenyAccessNetwork()) {
//            LogUtils.d(TAG, "还没有流量确认，不支持网络操作，异常返回");
//            return true;
//        }
        return false;
    }

    private static boolean checkFailed() {
//        if (isTrafficTip()) {
//            return true;
//        }
//        if (!mYouJuEnabled) {
//            return true;
//        }
        return false;
    }

    public void init(Context context, String appId, String channelId) {
//        YouJuAgent.init(context, appId, channelId);
    }

    @Override
    public boolean isAllowedUpload() {
        return !checkFailed();
    }

    public void onEvent(Context context, String eventId) {
//        YouJuAgent.onEvent(context, eventId);
    }

    public void onEvent(Context context, String eventId, String eventLabel) {
//        YouJuAgent.onEvent(context, eventId, eventLabel);
    }

    public void onEvent(Context context, String eventId, String eventLabel,
                        Map<String, Object> eventMap) {
//        YouJuAgent.onEvent(context, eventId, eventLabel, eventMap);
    }

    public void onError(Context context, Throwable throwable) {
//        YouJuAgent.onError(context, throwable);

    }

    public static final boolean IS_OVERSEA_PROJECT = getProp("ro.gn.oversea.product", "").toLowerCase(Locale.getDefault()).equals("yes");

    private static String getProp(String key, String defaultValue) {
        try {
            final Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            final Method get = systemProperties.getMethod("get", String.class, String.class);
            return (String) get.invoke(null, key, defaultValue);
        } catch (Exception e) {
            // This should never happen
            Log.e(TAG, "Exception while getting system property: ", e);
            return defaultValue;
        }
    }

    @Override
    public void onPageStart(Context context, String pageName) {
//        YouJuAgent.onPageStart(context,pageName);
    }

    @Override
    public void onResume(Context context) {
//        YouJuAgent.onResume(context);
    }

    @Override
    public void onPause(Context context) {
//        YouJuAgent.onPause(context);
    }

    @Override
    public void onPageEnd(Context context, String pageName) {
//        YouJuAgent.onPageEnd(context,pageName);
    }
}
