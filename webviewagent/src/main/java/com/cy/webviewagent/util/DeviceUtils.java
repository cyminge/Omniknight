package com.cy.webviewagent.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by JLB6088 on 2017/10/17.
 */

public final class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    private static final boolean MTK_GEMINI_SUPPORT = getProp("ro.mediatek.gemini_support", "")
            .equals("true");
    private static final boolean QC_MULTISIM_SUPPORT = getOperatorNum("").contains(",");
    private static final int ANDROID_M = 23;// Build.VERSION_CODES.M
    private static final String DEFAULT_OPERATOR = "**";
    private static final String STRING_COMMA = ",";

    private static String sGioneeUAString;
    private static String sOtherVendorUAString;

    /**
     * 提供默认的WebView UserAgent
     *
     * @param context
     * @return
     */
    public static String getUAString(WebView webView, Context context) {
        String networkType = "unknown";
        ConnectivityController controller = ConnectivityController.getInstance();
        if(null != controller) {
            networkType = controller.getCurrNetworkType();
        }
        return getGioneeUAString(webView, context) + " Network/" + networkType; //  + " Network/" + networkType
    }

    private static String getBrand() {
        return Build.BRAND;
    }

    private static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    private static String getSeries() {
        return Build.MODEL;
    }

    private static String getLanguage() {
        return Locale.getDefault().getLanguage(); // zh
    }

    private static String getCountry() {
        return Locale.getDefault().getCountry().toLowerCase(); // cn
    }

    /**
     * 非金立手机不设置IMEI
     * @param context
     * @return
     */
    public static String getNotGioneeUAString(WebView webView, Context context) {
        if (null != sOtherVendorUAString) {
            return sOtherVendorUAString;
        }

        try {
            String strImei = DeviceImei.getIMEI(context);
            String decodeImei = DecodeUtils.get(strImei);
            String romVer = SystemProperties.get("ro.gn.gnromvernumber", "GiONEE ROM4.0.1");
            String Ver = romVer.substring(romVer.indexOf("M") == -1 ? 0 : romVer.indexOf("M") + 1);
            String ua = "Mozilla/5.0 (Linux; U; Android "
                    + getAndroidVersion() + "; "
                    + getLanguage() + "-"
                    + getCountry() + "; "
                    + URLEncoder.encode(getBrand()) + "-"
                    + URLEncoder.encode(getSeries()) + "/Phone"
                    + " Build/IMM76D) AppleWebKit/534.30 (KHTML,like Gecko) Version/4.0 Mobile Safari/534.30"
                    + " Id/" + decodeImei
                    + " RV/" + Ver;
            sOtherVendorUAString = ua;
            return ua;
        } catch (Exception e){
            Log.w("WebViewAgent", "getUserAgent error : " + e.getMessage());
            return webView.getSettings().getUserAgentString();
        }
    }

    /**
     * 最好是在子线程调用，不然有可能出现ANR
     *
     * @return
     *
     *         Mozilla/5.0 (Linux; Android 5.1; GN8001 Build/LMY47D; wv) AppleWebKit/537.36 (KHTML, like
     *         Gecko) Version/4.0 Chrome/55.0.2883.91 Mobile Safari/537.36 Mozilla/5.0 (Linux; U; Android 5.1;
     *         zh-cn;GiONEE-GN8001/GN8001 Build/IMM76D) AppleWebKit534.30(KHTML,like Gecko)Version/4.0 Mobile
     *         Safari/534.30 Id/EDCDE1F6A83F67E84A6AAF5E1F66E9A2 RV/5.0.16
     */
    private static String getGioneeUAString(WebView webView, Context context) {
        if (null != sGioneeUAString) {
            return sGioneeUAString ;
        }
        String uaString;
        try {
            String brand = URLEncoder.encode(SystemProperties.get("ro.product.brand", "GiONEE"));
            String model = URLEncoder.encode(SystemProperties.get("ro.product.model", "Phone"));
            String extModel = URLEncoder.encode(SystemProperties.get("ro.gn.extmodel", "Phone"));
            String romVer = SystemProperties.get("ro.gn.gnromvernumber", "GiONEE ROM4.0.1");
            String Ver = romVer.substring(romVer.indexOf("M") == -1 ? 0 : romVer.indexOf("M") + 1);
            String language = Locale.getDefault().getLanguage();
            String country = Locale.getDefault().getCountry().toLowerCase();
            String strImei = DeviceImei.getIMEI(context);
            String decodeImei = DecodeUtils.get(strImei);
            String packageName = getPackageName(context);
            String packageVersionName = getPackageInfo(context).versionName;
            int packageVersionCode = getPackageInfo(context).versionCode;
            String optr = SystemProperties.get("ro.operator.optr");
            if (null != optr && optr.equals("OP02")) {
                uaString = "Mozilla/5.0 (Linux; U; Android " + Build.VERSION.RELEASE + "; " + language + "-"
                        + country + "; " + brand + "-" + model + "/" + extModel
                        + " Build/IMM76D) AppleWebKit/534.30 (KHTML,like Gecko) Version/4.0 Mobile Safari/534.30 Id/"
                        + decodeImei + " operator/" + getCurrentOperator(context)
                        + " RV/" + Ver + " PackageInfo/" + packageName + "_" + packageVersionCode + "_v"
                        + packageVersionName + " GNBR/" + "v1.5.1.h"
                        + " (securitypay,securityinstalled)";
            } else {
                uaString = "Mozilla/5.0 (Linux; U; Android " + Build.VERSION.RELEASE + "; " + language + "-"
                        + country + "; " + brand + "-" + model + "/" + extModel
                        + " Build/IMM76D) AppleWebKit/534.30 (KHTML,like Gecko) Version/4.0 Mobile Safari/534.30 Id/"
                        + decodeImei + " operator/" + getCurrentOperator(context)
                        + " RV/" + Ver + " PackageInfo/" + packageName + "_" + packageVersionCode + "_v"
                        + packageVersionName;
            }
            sGioneeUAString = uaString;
            return uaString;
        } catch (Exception e) {
            Log.w("WebViewAgent", "getGioneeUserAgent error : " + e.getMessage());
            return webView.getSettings().getUserAgentString();
        }
    }

    private static PackageInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(getPackageName(context), 0);
    }

    private static String getPackageVersionName(Context context) {
        try {
            return getPackageInfo(context).versionName;
        } catch (Exception e) {
            return "";
        }
    }

    private static int getPackageVersionCode(Context context) {
        try {
            return getPackageInfo(context).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    private static String getPackageName(Context context) {
        return context.getPackageName();
    }

    private static String getCurrentOperator(Context context) {
        TelephonyManager telephonyManager = getTelephonyManager(context);
        String operatorA = getOperatorA();
        String operatorB = getOperatorB();
        if (isStringNotEmpty(operatorA) && isStringNotEmpty(operatorB)) {
            try {
                if (hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                    @SuppressLint("MissingPermission")
                    String subscriberId = telephonyManager.getSubscriberId();
                    if (subscriberId.startsWith(operatorA)) {
                        return operatorA;
                    }
                    if (subscriberId.startsWith(operatorB)) {
                        return operatorB;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "error info : "+ e.getMessage());
            }

        }

        if (isStringNotEmpty(operatorA)) {
            return operatorA;
        }
        if (isStringNotEmpty(operatorB)) {
            return operatorB;
        }
        return "";
    }

    public static boolean hasPermission(Context context, String permission) {
        try {
            if (context.getPackageManager().checkPermission(permission,
                    context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            if (!checkPermission(context, permission)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.w(TAG, "error info : "+ e.getMessage());
        }
        return false;
    }

    public static boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= ANDROID_M) {
            return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private static final boolean isStringNotEmpty(String string) {
        return TextUtils.isEmpty(string) == false;
    }

    private static String getOperatorA() {
        String operatorAnum = DEFAULT_OPERATOR;

        switch (getMultiSImSupportPlatform()) {
            case PhoneType.MTK_MULTISIM:
                operatorAnum = getMtkOperatorForMutiSimCard(0);
                break;
            case PhoneType.QC_MULTISIM:
                operatorAnum = getQcOperatorForMutiSimCard(0);
                break;
            default:
                operatorAnum = getOperatorNum("");
                break;
        }

        if (isOperatorNotNull(operatorAnum)) {
            if (isOperatorsString(operatorAnum)) {
                operatorAnum = getOperatorFromOperatorsString(operatorAnum, 0);
            }
            return operatorAnum;
        } else {
            return DEFAULT_OPERATOR;
        }
    }

    public static String getOperatorB() {
        String operatorBnum = DEFAULT_OPERATOR;
        switch (getMultiSImSupportPlatform()) {
            case PhoneType.MTK_MULTISIM:
                operatorBnum = getMtkOperatorForMutiSimCard(1);
                break;
            case PhoneType.QC_MULTISIM:
                operatorBnum = getQcOperatorForMutiSimCard(1);
                break;
            default:
                break;
        }

        if (isOperatorNotNull(operatorBnum)) {
            return operatorBnum;
        } else {
            return DEFAULT_OPERATOR;
        }
    }

    private static boolean isOperatorNotNull(String operatorNum) {
        if (TextUtils.isEmpty(operatorNum)) {
            return false;
        }
        if ("null".equalsIgnoreCase(operatorNum)) {
            return false;
        }
        return true;
    }

    private static int getMultiSImSupportPlatform() {
        if (MTK_GEMINI_SUPPORT) {
            return PhoneType.MTK_MULTISIM;
        }
        if (QC_MULTISIM_SUPPORT) {
            return PhoneType.QC_MULTISIM;
        }
        return PhoneType.SINGLE_SIM;
    }

    private static String getProp(String key, String def) {
        try {
            return SystemProperties.get(key, def);
        } catch (Exception e) {
            return "";
        }
    }

    private static String getOperatorNum(String def) {
        return getProp("gsm.sim.operator.numeric", def);
        // return getProp("gsm.operator.numeric", def);
    }

    private static class PhoneType {
        public static final int QC_MULTISIM = 1;
        public static final int MTK_MULTISIM = 2;
        public static final int SINGLE_SIM = 3;

    }

    private static String getMtkOperatorForMutiSimCard(int cardId) {
        if (isNewMtkOperatorProp()) {
            return getOperatorFromOneProp(cardId);
        }
        String operators = "";
        switch (cardId) {
            case 0:
                operators = getOperatorNum("");
                break;

            case 1:
                operators = getOperatorNumOfCardB("");
                break;

            default:
                break;
        }
        return operators;
    }

    private static String getOperatorNumOfCardB(String def) {
        return getProp("gsm.sim.operator.numeric.2", def);
        // return getProp("gsm.operator.numeric.2", def);
    }

    private static boolean isNewMtkOperatorProp() {
        String operatorProp = getOperatorNum("");
        if (operatorProp.contains(STRING_COMMA)) {
            return true;
        }
        return false;
    }

    private static String getQcOperatorForMutiSimCard(int cardId) {
        return getOperatorFromOneProp(cardId);
    }

    private static String getOperatorFromOneProp(int cardId) {
        String operatorProp = getOperatorNum("");

        return getOperatorFromOperatorsString(operatorProp, cardId);
    }

    private static String getOperatorFromOperatorsString(String operatorsString, int index) {
        if (TextUtils.isEmpty(operatorsString)) {
            return "";
        }
        if (isOperatorsString(operatorsString)) {
            String[] operators = operatorsString.split(STRING_COMMA);
            if (operators == null || operators.length < index + 1) {
                return "";
            }

            return operators[index];
        }
        return operatorsString;
    }

    private static boolean isOperatorsString(String operatorString) {
        if (operatorString.contains(STRING_COMMA)) {
            return true;
        }
        return false;
    }
}
