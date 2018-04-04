package com.cy.omniknight.tracer.util;

import android.util.SparseArray;

import java.util.ArrayList;

public class GlobalListenerManager {
	public static final int REPORT_DONW_EVENT = 0x1;

    private static SparseArray<ArrayList<GlobalListener>> sListeners = new SparseArray<ArrayList<GlobalListener>>();
    private static SparseArray<ArrayList<GlobalListener>> sStaticListeners = new SparseArray<ArrayList<GlobalListener>>();

    public static synchronized void addListener(GlobalListener listener, int... keys) {
        for (int key : keys) {
            addListener(listener, key);
        }
    }

    public static synchronized void addListener(GlobalListener listener, int key) {
        add(listener, key, sListeners);
    }

    public static synchronized void addStaticListener(GlobalListener listener, int... keys) {
        for (int key : keys) {
            addStaticListener(listener, key);
        }
    }

    public static synchronized void addStaticListener(GlobalListener listener, int key) {
        add(listener, key, sStaticListeners);
    }

    public static synchronized void removeListener(GlobalListener listener) {
        remove(listener, sListeners);
        remove(listener, sStaticListeners);
    }

    private static void add(GlobalListener listener, int key, SparseArray<ArrayList<GlobalListener>> listeners) {
        if (listener == null) { // notify
            return;
        }
        ArrayList<GlobalListener> list = listeners.get(key);
        if (list != null) {
            if (!list.contains(listener)) {
                list.add(listener);
            }
        } else {
            list = new ArrayList<GlobalListener>();
            list.add(listener);
            listeners.put(key, list);
        }
    }

    private static void remove(GlobalListener listener, SparseArray<ArrayList<GlobalListener>> listeners) {
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            ArrayList<GlobalListener> list = listeners.valueAt(i);
            list.remove(listener);
        }
    }

    public static synchronized void onEvent(int key) {
        onEvent(key, new Object());
    }

    public static synchronized void onEvent(int key, Object... params) {
        onEvent(key, sListeners.get(key), params);
        onEvent(key, sStaticListeners.get(key), params);
    }

    private static void onEvent(int key, ArrayList<GlobalListener> target, Object... params) {

        if (target == null) {
            return;
        }
        for (GlobalListener listener : target) {
            listener.onEvent(key, params);
        }
    }

    public static synchronized void exit() {
        recycle(sListeners);
    }

    private static void recycle(SparseArray<ArrayList<GlobalListener>> listeners) {
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            ArrayList<GlobalListener> list = listeners.valueAt(i);
            list.clear();
        }
        listeners.clear();
    }
}
