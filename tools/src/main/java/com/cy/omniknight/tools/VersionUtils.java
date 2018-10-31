package com.cy.omniknight.tools;

import android.content.pm.ApplicationInfo;
import android.os.Build;

/**
 * Created by cy on 18-9-27.
 */

public class VersionUtils {
    private static final int VERSION_CODES_ICE_CREAM_SANDWICH = 14;
    private static final int VERSION_CODES_JELLY_BEAN = 16;
    private static final int VERSION_CODES_JELLY_BEAN_MR2 = 18;
    private static final int VERSION_CODES_LOLLIPOP = 21;
    private static final int VERSION_CODES_LOLLIPOP_PLUS = 22;
    private static final int VERSION_CODES_ANDROID_M = 23;
    private static final int VERSION_CODES_ANDROID_N = 24;

    public static boolean isAndroidM() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_ANDROID_M;
    }

    public static boolean isAndroidN() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_ANDROID_N;
    }

    public static boolean isTargetSdkN() {
        ApplicationInfo info = Utils.getApp().getApplicationInfo();
        return info != null && info.targetSdkVersion >= VERSION_CODES_ANDROID_N;
    }

    public static boolean isIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_ICE_CREAM_SANDWICH;
    }

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_JELLY_BEAN;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_LOLLIPOP;
    }

    public static boolean isLollipopPlus() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_LOLLIPOP_PLUS;
    }

    public static boolean isJellyBeanMr2() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_JELLY_BEAN_MR2;
    }
}
