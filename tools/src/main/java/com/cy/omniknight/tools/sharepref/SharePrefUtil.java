package com.cy.omniknight.tools.sharepref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("NewApi")
public class SharePrefUtil {

	private static final String SHARE_PREF_NAME = "cy_pref_name";
	
    private static SharedPreferences sSharePref;

    public static void init(Context context) {
        sSharePref = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void remove(String key) {
        sSharePref.edit().remove(key).apply();
    }

    public static void putInt(String key, int value) {
        sSharePref.edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        return sSharePref.getInt(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        sSharePref.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sSharePref.getBoolean(key, defValue);
    }

    public static void putString(String key, String value) {
        sSharePref.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static String getString(String key, String defValue) {
        return sSharePref.getString(key, defValue);
    }

    public static void putLong(String key, long value) {
        sSharePref.edit().putLong(key, value).apply();
    }

    public static long getLong(String key, long defValue) {
        return sSharePref.getLong(key, defValue);
    }

    public static void putHashTime(String key, long value) {
//        long randomTime = value + Utils.getRandomTimeMs();
//        putLong(key, randomTime);
    }

    public static void putHashTime(String key, long value, long maxMillis) {
//        long randomTime = value + Utils.getRandomTimeMs(maxMillis);
//        putLong(key, randomTime);
    }

    public static void putFloat(String key, float value) {
        sSharePref.edit().putFloat(key, value).apply();
    }

    public static float getFloat(String key, float defValue) {
        return sSharePref.getFloat(key, defValue);
    }

    public static void saveVersion(String prefKey, String jsonData) {
//        try {
//            JSONObject json = new JSONObject(jsonData);
//            String version = json.getString(JsonConstant.VERSION);
//            PreferenceManager.putString(prefKey, version);
//        } catch (Exception e) {
//        }
    }

    public static String getVersion(String prefKey) {
        return getString(prefKey, "");
    }
    
    public static void clearPreference(String key) {
    	sSharePref.edit().remove(key).apply();
    }
}
