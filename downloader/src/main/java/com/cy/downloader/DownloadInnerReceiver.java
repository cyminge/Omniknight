package com.cy.downloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

public class DownloadInnerReceiver extends BroadcastReceiver {

    public static final String ACTION_DOWNLOAD_START = "perpetual.calendar.download.action.START";
    public static final String ACTION_DOWNLOAD_PAUSE = "perpetual.calendar.download.action.PAUSE";
    public static final String ACTION_DOWNLOAD_CONTINUE = "perpetual.calendar.download.action.DOWNLOAD_CONTINUE";
    public static final String ACTION_DOWNLOAD_INSTALL = "perpetual.calendar.download.action.DOWNLOAD_INSTALL";
    public static final String ACTION_DOWNLOAD_OPEN = "perpetual.calendar.download.action.DOWNLOAD_OPEN";
    public static final String ACTION_CANCEL_DOWNLOAD = "perpetual.calendar.download.action.CANCEL_DOWNLOAD";

    public static final String DOWNLOAD_URL = "downloadUrl";
    public static final String PACKAGE_NAME = "packageName";
    public static final String DEEP_LINK = "deepLink";
    public static final String ACTIVE_TRACKER = "activeTracker";
    public static final String LAUNCH_TRACKER = "launchTracker";
    public static final String NOTIFICATION_ID = "notificationId";

    private static DownloadInnerReceiver mReceiver = new DownloadInnerReceiver();
    private static IntentFilter mDownloadIntentFilter;

    public static synchronized void register(Context context) {
        try {
            if (mDownloadIntentFilter == null) {
                mDownloadIntentFilter = new IntentFilter();
                mDownloadIntentFilter.addAction(ACTION_DOWNLOAD_START);
                mDownloadIntentFilter.addAction(ACTION_DOWNLOAD_PAUSE);
                mDownloadIntentFilter.addAction(ACTION_DOWNLOAD_CONTINUE);
                mDownloadIntentFilter.addAction(ACTION_DOWNLOAD_INSTALL);
                mDownloadIntentFilter.addAction(ACTION_DOWNLOAD_OPEN);
                mDownloadIntentFilter.addAction(ACTION_CANCEL_DOWNLOAD);
                context.registerReceiver(mReceiver, mDownloadIntentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void unregister(Context context) {
        try {
            if (mDownloadIntentFilter != null) {
                mDownloadIntentFilter = null;
            }
            context.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        final String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        if(ACTION_CANCEL_DOWNLOAD.equals(action)) {
            DownloadHelper.getInstance().cancelDownloadAction(intent.getStringExtra(PACKAGE_NAME));
            return;
        }
        DownloadHelper.getInstance().handleDownloadActionNotification(context, intent.getStringExtra(PACKAGE_NAME), intent.getStringExtra(DEEP_LINK));
    }
}
