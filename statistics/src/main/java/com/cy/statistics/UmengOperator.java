package com.cy.statistics;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

class UmengOperator extends AbsStatistics {

    private static final String TAG = "UmengOperator";

    public UmengOperator(Context context) {
        if (IS_OVERSEA_PROJECT) {
            init(context, "5b9f1d5ff43e482781000106", "海外" + context.getApplicationInfo().processName);
        } else {
            init(context, "5b9f1d5ff43e482781000106", "国内" + context.getApplicationInfo().processName);
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
        if (isTrafficTip()) {
            return true;
        }
        return false;
    }

    public void init(Context context, String appId, String channelId) {
//        UMConfigure.init(context, appId, channelId, UMConfigure.DEVICE_TYPE_PHONE, GlobalConstant.EMPTY);
//        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
//        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_DUM_NORMAL);
    }

    @Override
    public boolean isAllowedUpload() {
        return !checkFailed();
    }

    public void onEvent(Context context, String eventId) {
//        YouJuAgent.onEvent(context, eventId);
    }

    public void onEvent(Context context, String eventId, String eventLabel) {
//        MobclickAgent.onEvent(context, eventId, eventLabel);
    }

    public void onEvent(Context context, String eventId, String eventLabel,
                        Map<String, Object> eventMap) {
//        Map<String, String> convertEventMap = new HashMap<>();
//        if (ObjectUtils.isNotEmpty(eventMap)) {
//            Iterator<Map.Entry<String, Object>> iterator = eventMap.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, Object> entry = iterator.next();
//                convertEventMap.put(entry.getKey(), String.valueOf(entry.getValue()));
//            }
//        }
//        MobclickAgent.onEvent(context, eventId, convertEventMap);
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
//        MobclickAgent.onPageStart(pageName);
    }

    @Override
    public void onResume(Context context) {
//        MobclickAgent.onResume(context);
    }

    @Override
    public void onPause(Context context) {
//        MobclickAgent.onPause(context);
    }

    @Override
    public void onPageEnd(Context context, String pageName) {
//        MobclickAgent.onPageEnd(pageName);
    }
}
