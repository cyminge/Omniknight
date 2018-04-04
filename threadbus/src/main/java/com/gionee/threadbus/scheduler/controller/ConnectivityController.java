package com.gionee.threadbus.scheduler.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.gionee.threadbus.TaskRunnable;
import com.gionee.threadbus.utils.Utils;

/**
 * Created by JLB6088 on 2017/7/12.
 */

final class ConnectivityController extends StateController {

    private NetworkTracer mNetworkTracer;

    public ConnectivityController(StateChangedListener stateChangedListener, Context context) {
        super(stateChangedListener, context);
    }

    @Override
    public void maybeStartTracking(Ruler ruler) {
        if (ruler.isRequiresNetworkChange()) {
            if (null == mNetworkTracer) {
                mNetworkTracer = new NetworkTracer();
                mNetworkTracer.startTracking();
            }

            synchronized (mTrackingRulers) {
                mTrackingRulers.add(ruler);
            }
        }
    }

    private class NetworkTracer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("cyTest", "NetWork State Changed");
            onNetworkChanged();
        }

        private void startTracking() {
            Log.d("cyTest", "ConnectivityController startTracking");
            IntentFilter filter = getFilter();
            mContext.registerReceiver(this, filter);
        }

        private IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            return filter;
        }
    }

    private void onNetworkChanged() {
        RulerService.getSubThreadHandler().removeCallbacks(mNetWorkChangedRunnable);
        RulerService.getSubThreadHandler().postDelayed(mNetWorkChangedRunnable, 1000);
    }

    TaskRunnable mNetWorkChangedRunnable = new TaskRunnable("Check&Update NetWork State") {
        @Override
        public void runTask() {
            int networkType = getNetworkType();
            updateNetworkState(networkType);
        }
    };

    private int getNetworkType() {
        NetworkInfo info = Utils.getActiveNetworkInfo(mContext);
        if (info == null || !info.isConnectedOrConnecting()) {
            return Ruler.NETWORK_TYPE_NONE;
        }
        int networkType;
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                networkType = Ruler.NETWORK_TYPE_GOOD;
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE: // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        networkType = Ruler.NETWORK_TYPE_GOOD;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        networkType = Ruler.NETWORK_TYPE_COMMON;
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        networkType = Ruler.NETWORK_TYPE_POOR;
                        break;
                    default:
                        networkType = Ruler.NETWORK_TYPE_POOR;
                }
                break;
            default:
                networkType = Ruler.NETWORK_TYPE_POOR;
        }
        return networkType;
    }

    private void updateNetworkState(int networkType) {
        boolean isChanged = false;
        synchronized (mTrackingRulers) {
            for (int i = 0, size = mTrackingRulers.size(); i < size; i++) {
                isChanged |= mTrackingRulers.get(i).setNetworkType(networkType);
            }

            if (isChanged) {
                mStateChangedListener.onStateChanged();
            }
        }
    }
}
