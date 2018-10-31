package com.cy.threadbus.utils.listener;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by zf on 2017/5/21.
 */

public final class ThreadBusListenerManager {

    public static final int STATE_CHANGED = 0x1;

    private static SparseArray<ArrayList<IThreadBusListener>> sListeners = new SparseArray<>();

    public static synchronized void addListener(IThreadBusListener listener, int... keys) {
        for (int key : keys) {
            addListener(listener, key);
        }
    }

    public static synchronized void addListener(IThreadBusListener listener, int key) {
        add(listener, key, sListeners);
    }

    private static void add(IThreadBusListener listener, int key,
            SparseArray<ArrayList<IThreadBusListener>> listeners) {
        if (listener == null) {
            return;
        }
        ArrayList<IThreadBusListener> list = listeners.get(key);
        if (list != null) {
            if (!list.contains(listener)) {
                list.add(listener);
            }
        } else {
            list = new ArrayList<>();
            list.add(listener);
            listeners.put(key, list);
        }
    }

    public static synchronized void removeListener(IThreadBusListener listener) {
        remove(listener, sListeners);
    }

    private static void remove(IThreadBusListener listener,
            SparseArray<ArrayList<IThreadBusListener>> listeners) {
        for (int i = 0, size = listeners.size(); i < size; i++) {
            ArrayList<IThreadBusListener> list = listeners.valueAt(i);
            list.remove(listener);
        }
    }

    public static synchronized void onEvent(int key, Object... params) {
        onEvent(key, sListeners.get(key), params);
    }

    private static void onEvent(int key, ArrayList<IThreadBusListener> target, Object... params) {
        if (target == null) {
            return;
        }

        for (IThreadBusListener listener : target) {
            listener.onEvent(key, params);
        }
    }
}
