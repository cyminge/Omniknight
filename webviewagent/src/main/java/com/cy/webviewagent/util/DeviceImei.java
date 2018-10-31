package com.cy.webviewagent.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

final class DeviceImei {

    private static final String TAG = DeviceImei.class.getSimpleName();

    // 手机系统属性集合
    private static final List<String> sPropList = new ArrayList<>();
    // 7523A03验证，用了深研的数据
    // GBL7553A02验证，用了深研的数据
    // BJ_G1602验证，用了深研的数据
    static {
        // 北研项目
        sPropList.add("persist.sys.imei_for_y3");
        sPropList.add("persist.sys.imei1_for_y3");
        sPropList.add("persist.sys.meid_for_y3");
        // 深研项目
        sPropList.add("persist.radio.imei");
        sPropList.add("persist.radio.imei1");
        sPropList.add("persist.radio.meid");
    }
    private static final String DEFAULT_VALUE = "";
    // 手机没有写IMEI的时候，用这个默认值
    private static final String DEFAULT_IMEI = "00000000000000";
    private static String sIMEI = DEFAULT_VALUE;

    /**
     * 获取手机IMEI号
     *
     * @param context
     * @return
     */
    static String getIMEI(final Context context) {
        // 上次获取过了，直接返回
        if (!DEFAULT_VALUE.equals(sIMEI)) {
            return sIMEI;
        }
        synchronized (DEFAULT_VALUE) {
            if (!DEFAULT_VALUE.equals(sIMEI)) {// NOSONAR
                return sIMEI;// NOSONAR
            }
            try {
                for (String prop : sPropList) {
                    String value = SystemProperties.get(prop, DEFAULT_VALUE);
                    if (TextUtils.isEmpty(value)) {
                        continue;
                    }
                    sIMEI = value;
                    return sIMEI;
                }
            } catch (Throwable e) {
                Log.w(TAG, "getImei error warn:"+ e.getMessage());
            }
            // 以前非属性值项目imei的获取
            String deviceId = getDeviceId(context);
            if (TextUtils.isEmpty(deviceId)) {
                return DEFAULT_IMEI;
            }
            sIMEI = deviceId;
            return sIMEI;
        }
    }

    /**
     * 获取IMEI，这个需要异步调用，否则可能导致anr
     */
    static String getDeviceId(Context context) {
        try {
            if (!hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                return DEFAULT_VALUE;
            }
            TelephonyManager telManage = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String deviceId = telManage.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = DEFAULT_IMEI;
            }
            return deviceId;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return DEFAULT_VALUE;
    }

    /**
     * 这个需要异步调用，否则可能导致anr
     * 
     * @param context
     * @param permission
     * @return
     */
    private static boolean hasPermission(Context context, String permission) {
        try {
            return context.checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid()) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
