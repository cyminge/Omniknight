package com.cy.base.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.cy.omniknight.tools.Utils;

public class CommonSP {

    public static final String COMMON_SP_FILE = "com.android.core.config";

    public static final String TAG = CommonSP.class.getSimpleName();

    private static CommonSP sCommonSP = new CommonSP();

    protected CommonSP() {
    }

    public static CommonSP getInstance() {
        return sCommonSP;
    }

    protected Context getApplicationContext() {
        return Utils.getApp();
    }

    protected SharedPreferences getSharedPreferences() {
        return getApplicationContext().getSharedPreferences(COMMON_SP_FILE, Context.MODE_PRIVATE);
    }

    public boolean getSharedPreferencesBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    public void setSharedPreferencesBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }


    public String getSharedPreferencesString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    public void setSharedPreferencesString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

}
