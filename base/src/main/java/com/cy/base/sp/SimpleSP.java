package com.cy.base.sp;

public class SimpleSP extends CommonSP implements ISP {

    public static final String TAG = SimpleSP.class.getSimpleName();

    private static SimpleSP sCommonSP = new SimpleSP();


    private SimpleSP() {
    }

    public static SimpleSP getInstance() {
        return sCommonSP;
    }

    @Override
    public void put(String key, String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    @Override
    public void put(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).commit();
    }

    @Override
    public String get(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    @Override
    public int get(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    @Override
    public void put(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).commit();
    }

    @Override
    public long get(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    @Override
    public void put(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    @Override
    public boolean get(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }
}
