package com.cy.omniknight.tools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.cy.omniknight.tools.NetworkUtils;
import com.cy.omniknight.tools.Utils;
import com.cy.omniknight.tools.runnable.TaskRunnable;

/**
 * Created by JLB6088 on 2017/7/12.
 */

public final class ConnectivityController extends ReceiverStateController {

    public static final int CUSTOM_NETWORK_TYPE_WIFI = 4;
    public static final int CUSTOM_NETWORK_TYPE_4G = 3;
    public static final int CUSTOM_NETWORK_TYPE_3G = 2;
    public static final int CUSTOM_NETWORK_TYPE_2G = 1;
    public static final int CUSTOM_NETWORK_TYPE_UNKNOWN = 0;
    public static final int CUSTOM_NETWORK_TYPE_NONE = -1;

    private NetworkTracer mNetworkTracer;
    private static volatile int mCustomNetworkType;

    public ConnectivityController(Context context) {
        super(context);
    }

    @Override
    public void startTracking(StateChangedListener stateChangedListener) {
        mStateChangedListenerArray.add(stateChangedListener);

        if (null == mNetworkTracer) {
            mNetworkTracer = new NetworkTracer();
            mNetworkTracer.startTracking();
        }
    }

    @Override
    public void stopTracking(StateChangedListener stateChangedListener) {
        mStateChangedListenerArray.remove(stateChangedListener);
        // 不需要反注册
        if(mStateChangedListenerArray.size() == 0) {
            if (null != mNetworkTracer) {
                mNetworkTracer.stopTracking();
                mNetworkTracer = null;
            }
        }
    }

    private class NetworkTracer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onNetworkChanged();
        }

        private void startTracking() {
            IntentFilter filter = getFilter();
            mContext.registerReceiver(this, filter);
        }

        private IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            return filter;
        }

        private void stopTracking() {
            mContext.unregisterReceiver(this);
        }
    }

    public void onNetworkChanged() {
        ReceiverManager.getSubThreadHandler().removeCallbacks(mNetWorkChangedRunnable);
        ReceiverManager.getSubThreadHandler().postDelayed(mNetWorkChangedRunnable, 1000);
    }

    private TaskRunnable mNetWorkChangedRunnable = new TaskRunnable("Check&Update NetWork State") {
        @Override
        public void runTask() {
            NetworkInfo info = NetworkUtils.getActiveNetworkInfo();
            int networkType = getCustomNetworkType(info);
            mCustomNetworkType = networkType;
            updateNetworkState(networkType, info);
        }
    };

    private int getCustomNetworkType(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            return CUSTOM_NETWORK_TYPE_NONE;
        }
        int customNetworkType;
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                customNetworkType = CUSTOM_NETWORK_TYPE_WIFI;
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE: // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        customNetworkType = CUSTOM_NETWORK_TYPE_4G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        customNetworkType = CUSTOM_NETWORK_TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        customNetworkType = CUSTOM_NETWORK_TYPE_2G;
                        break;
                    default:
                        customNetworkType = CUSTOM_NETWORK_TYPE_UNKNOWN;
                }
                break;
            default:
                customNetworkType = CUSTOM_NETWORK_TYPE_UNKNOWN;
        }
        return customNetworkType;
    }

    private void updateNetworkState(int customNetworkType, NetworkInfo info) {
        if(null == mStateChangedListenerArray || mStateChangedListenerArray.isEmpty()) {
            return;
        }

        synchronized (mStateChangedListenerArray) {
            for(int i=0, size = mStateChangedListenerArray.size(); i< size; i++) {
                mStateChangedListenerArray.get(i).onStateChanged(ReceiverManager.CHANGE_TYPE_NETWORK, customNetworkType, info);
            }
        }
    }

    /**
     * 是否网络处于连接状态
     * @return
     */
    public static boolean isNetworkConnected() {
        return mCustomNetworkType != CUSTOM_NETWORK_TYPE_NONE;
    }

    /**
     * 是否网络处于wifi状态
     * @return
     */
    public static boolean isWifiNetwork() {
        return mCustomNetworkType == CUSTOM_NETWORK_TYPE_WIFI;
    }

    public static String getNetworkType() {
        switch (mCustomNetworkType) {
            case CUSTOM_NETWORK_TYPE_WIFI :
                return "wifi";
            case CUSTOM_NETWORK_TYPE_4G :
                return "4G";
            case CUSTOM_NETWORK_TYPE_3G :
                return "3G";
            case CUSTOM_NETWORK_TYPE_2G :
                return "2G";
            case CUSTOM_NETWORK_TYPE_UNKNOWN :
                return "unknown";
            case CUSTOM_NETWORK_TYPE_NONE :
                return "none";
        }

        return "unknown";
    }
}
