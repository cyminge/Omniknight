package com.cy.downloader.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cy.downloader.DownloadHelper;
import com.cy.downloader.DownloadInnerReceiver;
import com.cy.downloader.R;
import com.cy.downloader.database.entity.DownloadInfo;
import com.cy.omniknight.tools.AppUtils;
import com.cy.omniknight.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * TODO 下載是否支持DEEPLINK跳轉，上報等
 * TODO wifi下自动下载
 * TODO 非wifi自动暂停
 * TODO 用户主动要求移动网络下载的非wifi下不暂停
 */
public class DownloadNotificationManager implements IDownloadNotification {

    private HashMap<String, Notification.Builder> mNotificationBuilderMap = new HashMap<>();

    private static final String CHANNEL_ID = AppUtils.getAppPackageName() + "_download";

    public DownloadNotificationManager() {

    }

    @Override
    public void showNotification(ArrayList<DownloadInfo> downloadInfoList) {
        for(DownloadInfo downloadInfo : downloadInfoList) {
            showNotification(downloadInfo);
        }
    }

    @Override
    public void showNotification(DownloadInfo downloadInfo) {
        if (downloadInfo == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent contentIntent = new Intent();
                Intent deleteIntent = new Intent(DownloadInnerReceiver.ACTION_CANCEL_DOWNLOAD);
                String ticker = "";
                String contentTitle = "";
                int notificationId = downloadInfo.getUrl().hashCode();
                int progressMax = 0;
                float progress = DownloadHelper.getInstance().getDownloadProgress(downloadInfo.getPackageName());
                String progressStr = (progress * 100) + "%";
                switch (downloadInfo.getState()) {
                    case DownloadInfo.STATE_DOWNLOAD :
                        progressMax = 100;
                        contentTitle = Utils.getApp().getString(R.string.app_click_start_download);
                        ticker = Utils.getApp().getString(R.string.app_start_download);
                        contentIntent.setAction(DownloadInnerReceiver.ACTION_DOWNLOAD_START);
                        break;
                    case DownloadInfo.STATE_DOWNLOADING:
                        progressMax = 100;
                        contentTitle = Utils.getApp().getString(R.string.app_click_pause_download);
                        ticker = Utils.getApp().getString(R.string.app_downloading);
                        contentIntent.setAction(DownloadInnerReceiver.ACTION_DOWNLOAD_PAUSE);
                        break;
                    case DownloadInfo.STATE_DOWNLOAD_FAILED :
                        progressMax = 100;
                        contentTitle = Utils.getApp().getString(R.string.app_click_start_download);
                        ticker = Utils.getApp().getString(R.string.app_start_download);
                        contentIntent.setAction(DownloadInnerReceiver.ACTION_DOWNLOAD_START);
                        break;
                    case DownloadInfo.STATE_PAUSE:
                        progressMax = 100;
                        contentTitle = Utils.getApp().getString(R.string.app_click_continue_download);
                        ticker = Utils.getApp().getString(R.string.app_pause_download);
                        contentIntent.setAction(DownloadInnerReceiver.ACTION_DOWNLOAD_CONTINUE);
                        break;
                    case DownloadInfo.STATE_INSTALL:
                        contentTitle = Utils.getApp().getString(R.string.app_click_install);
                        ticker = Utils.getApp().getString(R.string.app_downloaded);
                        contentIntent.setAction(DownloadInnerReceiver.ACTION_DOWNLOAD_INSTALL);
                        break;
                    case DownloadInfo.STATE_INSTALLING :
//                        contentTitle = GlobalVariable.sAppContext.getString(R.string.click_install);
//                        ticker = GlobalVariable.sAppContext.getString(R.string.installing);
                        break;
                    case DownloadInfo.STATE_INSTALL_FAILED :
                        contentTitle = Utils.getApp().getString(R.string.app_click_install);
                        ticker = Utils.getApp().getString(R.string.app_downloaded);
                        contentIntent.setAction(DownloadInnerReceiver.ACTION_DOWNLOAD_INSTALL);
                        break;
                    case DownloadInfo.STATE_OPEN:
                        cancelNotification(notificationId);

                        contentTitle = Utils.getApp().getString(R.string.app_click_open_apk);
                        ticker = Utils.getApp().getString(R.string.app_apk_installed);
                        notificationId = downloadInfo.getPackageName().hashCode();
                        contentIntent.setAction(DownloadInnerReceiver.ACTION_DOWNLOAD_OPEN);
//                        contentIntent.putExtra(DownloadInnerReceiver.DEEP_LINK, downloadInfo.getDeeplink());
//                        contentIntent.putExtra(DownloadInnerReceiver.ACTIVE_TRACKER, downloadInfo.getOpenTracker());
//                        contentIntent.putExtra(DownloadInnerReceiver.LAUNCH_TRACKER, downloadInfo.getLaunchTrackers());
                        break;
                    default:
                        break;
                }
                contentIntent.putExtra(DownloadInnerReceiver.PACKAGE_NAME, downloadInfo.getPackageName());
                contentIntent.putExtra(DownloadInnerReceiver.DEEP_LINK, downloadInfo.getDeepLink());
                contentIntent.putExtra(DownloadInnerReceiver.DOWNLOAD_URL, downloadInfo.getUrl());
                contentIntent.putExtra(DownloadInnerReceiver.NOTIFICATION_ID, notificationId);
                contentIntent.setPackage(AppUtils.getAppPackageName());
                PendingIntent contentPendingIntent = PendingIntent.getBroadcast(Utils.getApp(), downloadInfo.getUrl().hashCode(), contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent deletePendingIntent = null;
                if (deleteIntent.getAction() != null) {
                    deleteIntent.putExtra(DownloadInnerReceiver.PACKAGE_NAME, downloadInfo.getPackageName());
                    deleteIntent.putExtra(DownloadInnerReceiver.DOWNLOAD_URL, downloadInfo.getUrl());
                    deleteIntent.setPackage(AppUtils.getAppPackageName());
                    deletePendingIntent = PendingIntent.getBroadcast(Utils.getApp(), downloadInfo.getUrl().hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                if (mNotificationBuilderMap.get(downloadInfo.getUrl()) == null) {
                    mNotificationBuilderMap.put(downloadInfo.getUrl(), new Notification.Builder(Utils.getApp()));
                    mNotificationBuilderMap.get(downloadInfo.getUrl()).setTicker(ticker)
                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                            .setWhen(System.currentTimeMillis()).setContentTitle(downloadInfo.getAppName())
                            .setContentText(contentTitle).setContentInfo(progressStr)
                            .setDefaults(Notification.FLAG_NO_CLEAR)
                            .setProgress(progressMax, (int)(progress*100), false).setContentIntent(contentPendingIntent)
                            .setDeleteIntent(deletePendingIntent);
                } else {
                    mNotificationBuilderMap.get(downloadInfo.getUrl()).setProgress(progressMax, (int)(progress*100), false).setTicker(ticker)
                            .setContentText(contentTitle).setContentIntent(contentPendingIntent)
                            .setDeleteIntent(deletePendingIntent).setDefaults(Notification.FLAG_NO_CLEAR)
                            .setContentInfo(progressStr).setContentTitle(downloadInfo.getAppName());
                }
                sendNotification(mNotificationBuilderMap.get(downloadInfo.getUrl()), notificationId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Notification.Builder builder, int notificationId) {
        NotificationManager manager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, Utils.getApp().getString(R.string.app_download_notification_tip),
                        NotificationManager.IMPORTANCE_LOW);
                channel.enableLights(false);
                channel.enableVibration(false);
                manager.createNotificationChannel(channel);
            }
            builder.setChannelId(CHANNEL_ID);
        }
        Notification notification = builder.build();
        manager.notify(notificationId, notification);
    }

    @Override
    public void cancelNotification(int id) {
        NotificationManager manager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }
}
