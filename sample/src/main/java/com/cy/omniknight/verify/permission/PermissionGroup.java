package com.cy.omniknight.verify.permission;


import android.Manifest;

import java.util.Arrays;

public final class PermissionGroup {
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    private static final String[] CALENDAR = new String[]{
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};

    private static final String[] CAMERA = new String[]{
            Manifest.permission.CAMERA};

    private static final String[] CONTACTS = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS};

    private static final String[] LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final String[] MICROPHONE = new String[]{
            Manifest.permission.RECORD_AUDIO};

    private static final String[] PHONE = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS};

    private static final String[] SENSORS = new String[]{
            BODY_SENSORS};

    private static final String[] SMS = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS};

    private static final String[] STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static String[] getCalendar() {
        return Arrays.copyOf(CALENDAR, CALENDAR.length);
    }

    public static String[] getCamera() {
        return Arrays.copyOf(CAMERA, CALENDAR.length);
    }

    public static String[] getContacts() {
        return Arrays.copyOf(CONTACTS, CALENDAR.length);
    }

    public static String[] getLocation() {
        return Arrays.copyOf(LOCATION, CALENDAR.length);
    }

    public static String[] getMicrophone() {
        return Arrays.copyOf(MICROPHONE, CALENDAR.length);
    }

    public static String[] getPhone() {
        return Arrays.copyOf(PHONE, CALENDAR.length);
    }

    public static String[] getSensors() {
        return Arrays.copyOf(SENSORS, CALENDAR.length);
    }

    public static String[] getSms() {
        return Arrays.copyOf(SMS, CALENDAR.length);
    }

    public static String[] getStorage() {
        return Arrays.copyOf(STORAGE, CALENDAR.length);
    }
}
