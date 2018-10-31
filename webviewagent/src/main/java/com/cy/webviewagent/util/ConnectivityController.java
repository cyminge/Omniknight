package com.cy.webviewagent.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by JLB6088 on 2017/7/12.
 */

public final class ConnectivityController {

    private static final String TAG = ConnectivityController.class.getSimpleName();

    private static ConnectivityController mConnectivityController;
    private Context mContext;
    private NetworkTracer mNetworkTracer;
    private String mNetworkType = "noset";
    private HandlerThread mHandlerThread;
    private volatile Handler mSubThreadHandler;

    private static int mWebViewCount = 0; // 可能一个WebView在没有被销毁的时候，又创建了另外一个WebView，导致销毁和创建出问题，所以用一个计数器统计当前有多少个WebView。

    public ConnectivityController(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context) {
        mWebViewCount ++;
        if(1 == mWebViewCount) {
            mConnectivityController = new ConnectivityController(context);
            mConnectivityController.initHandlerThread();
            mConnectivityController.startTracking();
        }
    }

    /**
     * 销毁
     */
    public static void destroy() {
        mWebViewCount --;
        if(mWebViewCount == 0) {
            if(null != mConnectivityController) {
                mConnectivityController.stopTracking();
                mConnectivityController.destroySubHandler();
                mConnectivityController = null;
            }
        }
    }

    public static ConnectivityController getInstance() {
        return mConnectivityController;
    }

    public String getCurrNetworkType() {
        if("noset".equals(mNetworkType)) {
            mNetworkType = getNetworkType();
        }
        return mNetworkType;
    }

    private void initHandlerThread() {
        if (mSubThreadHandler == null) {
            mHandlerThread = new HandlerThread("WebViewAgent-HandlerThread");
            mHandlerThread.start();
            mSubThreadHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    private Handler getSubHandler() {
        initHandlerThread();
        return mSubThreadHandler;
    }

    private void destroySubHandler() {
        if(null != mSubThreadHandler) {
            mSubThreadHandler.removeCallbacks(mNetWorkChangedRunnable);
        }
        if (null != mHandlerThread) {
            try {
                if (mHandlerThread.isAlive()) {
                    mHandlerThread.quit();
                    // mHandler.getLooper().quit();
                }
                mHandlerThread.interrupt();
                mHandlerThread = null;
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
            }
        }
    }

    private void startTracking() {
        mNetworkTracer = new NetworkTracer();
        mNetworkTracer.startTracking();
    }

    private void stopTracking() {
        if (null == mNetworkTracer) {
            return;
        }
        mNetworkTracer.stopTracking();
        mNetworkTracer = null;
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

        private void stopTracking() {
            mContext.unregisterReceiver(this);
        }

        private IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            return filter;
        }
    }

    private void onNetworkChanged() {
        getSubHandler().removeCallbacks(mNetWorkChangedRunnable);
        getSubHandler().post(mNetWorkChangedRunnable);
    }

    private Runnable mNetWorkChangedRunnable = new Runnable() {
        @Override
        public void run() {
            mNetworkType = getNetworkType();
        }
    };

    private String getNetworkType() {
        NetworkInfo info = getActiveNetworkInfo(mContext);
        if (info == null || !info.isConnectedOrConnecting()) {
            return "nonet";
        }
        String networkType;
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                networkType = "wifi";
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE: // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        networkType = "4G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        networkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        networkType = "2G";
                        break;
                    default:
                        networkType = "unknown";
                        break;
                }
                break;
            default:
                networkType = "unknown";
                break;
        }
        return networkType;
    }

    private NetworkInfo getActiveNetworkInfo(Context context) {
        try {
            ConnectivityManager connectivity = getConnectivityManager(context);
            return connectivity.getActiveNetworkInfo();
        } catch (Exception e) {
            return null;
        }
    }

    private ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
