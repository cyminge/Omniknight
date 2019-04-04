package com.cy.base.sp;


public interface ISP {
    void put(String key, String value);
    void put(String key, int value);
    void put(String key, long value);
    void put(String key, boolean value);

    String get(String key, String defValue);
    int get(String key, int defValue);
    long get(String key, long defValue);
    boolean get(String key, boolean defValue);


}
