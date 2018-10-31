package com.cy.downloader.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.cy.downloader.R;
import com.cy.downloader.database.entity.DownloadInfo;
import com.cy.omniknight.tools.AppUtils;
import com.cy.omniknight.tools.Utils;
import com.cy.omniknight.tools.VersionUtils;

/**
 * Created by cy on 18-9-27.
 */

public class NotificationUtils {

    public static final int BROADCAST = 0;
    public static final int ACTIVITY = 1;
    private static final int SERVICE = 2;

    public static final int NOTIFICATION_ID_INSTALL = 8888;
    public static final String INSTALL_JUMP_ACTION = "gCalendar.intent.action.INSTALL_JUMP";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void installNotify(DownloadInfo downloadInfo) {
        try {
            String appName = getAppName(downloadInfo.getPackageName(), downloadInfo.getAppName());
            String pkgName = downloadInfo.getPackageName();
            String infoStr = "已经安装成功！";
            Intent intent = new Intent();
            intent.setAction(INSTALL_JUMP_ACTION);
            intent.putExtra("deepLink", downloadInfo.getDeepLink());
            intent.putExtra("package", pkgName);
            String contentStr = "点击立即体验游戏";
            RemoteViews contentView = getRemoteViews(appName + infoStr, contentStr);
            NotificationUtils.CustomNotifyParams data = new NotificationUtils.CustomNotifyParams(contentView, null, intent, NotificationUtils.BROADCAST);
            data.setTag(pkgName);
            data.setSingleNotify(NotificationUtils.NOTIFICATION_ID_INSTALL);
            NotificationUtils.showCustomViewNotification(data);
        } catch (Exception e) {
        }
    }

    private static String getAppName(String packageName, String appName) {
        if (appName.isEmpty()) {
            try {
                PackageManager pm = Utils.getApp().getPackageManager();
                PackageInfo p = getPackageInfoByName(packageName);
                return p.applicationInfo.loadLabel(pm).toString();
            } catch (Exception e) {
                return packageName;
            }
        }
        return appName;
    }

    private static PackageInfo getPackageInfoByName(String packageName) {
        return getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
    }

    private static PackageInfo getPackageInfo(String packageName, int flags) {
        PackageManager pm = Utils.getApp().getPackageManager();
        try {
            return pm.getPackageInfo(packageName, flags);
        } catch (Exception e) {
        }
        return null;
    }

    private static RemoteViews getRemoteViews(String title, String detail) {
        final RemoteViews expandedView = new RemoteViews(AppUtils.getAppPackageName(), R.layout.app_notification_item);
        expandedView.setViewVisibility(R.id.notify_progress, View.GONE);
        expandedView.setTextViewText(R.id.notify_title, title);
        expandedView.setTextViewText(R.id.notify_detail_percent, detail);
        expandedView.setImageViewResource(R.id.notify_icon, getAppIconId());
        return expandedView;
    }

    private static int getAppIconId() {
        try {
            ApplicationInfo info = Utils.getApp().getPackageManager().getApplicationInfo(AppUtils.getAppPackageName(), 0);
            return info.icon;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void showCustomViewNotification(CustomNotifyParams data) {
        Notification notification = getNotificationBuilder(Utils.getApp()).getNotification();
        notification.contentView = data.mSmallView;
        if (VersionUtils.isJellyBean()) {
            notification.bigContentView = data.mBigView;
        }
        notification.flags |= data.mFlags;
        notification.defaults |= data.mDefaultValue;
        notification.icon = getAppIconId();
        notification.contentIntent = data.mPendingIntent;
        show(data, notification);
    }

    private static void show(AbstractNotifyParams data, Notification notification) {
        show(data.mTag, data.mId, notification);
    }

    private static void show(String tag, int id, Notification notification) {
        try {
            NotificationManager manager = getNotificationManager();
            notification.defaults |= Notification.DEFAULT_LIGHTS;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            setLollipopIcon(notification);
            manager.notify(tag, id, notification);
        } catch (Exception e) {
            Log.w("cyTest", "NotificationUtils->show->" + e.getMessage());
        }
    }

    private static void setLollipopIcon(Notification notification) {
        if (VersionUtils.isLollipop()) {
            notification.icon = R.drawable.app_notification_small_icon;

            Bitmap bigIcon = BitmapFactory.decodeResource(Utils.getApp().getResources(), Utils.getApp().getApplicationInfo().icon);
            notification.largeIcon = bigIcon;
        }
    }

    private static NotificationManager getNotificationManager() {
        return (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static Notification.Builder getNotificationBuilder(Context context) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String id = Utils.getApp().getPackageName();
            String description = Utils.getApp().getPackageName();
            NotificationChannel channel = new NotificationChannel(id, description, NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getNotificationManager();
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(context, id);
        } else {
            builder = new Notification.Builder(context);
        }
        return builder;
    }

    private abstract static class AbstractNotifyParams {

        protected PendingIntent mPendingIntent;
        protected String mTag = "";
        protected int mId = this.hashCode();
        protected int mDefaultValue = Notification.DEFAULT_SOUND;
        protected int mFlags = Notification.FLAG_AUTO_CANCEL;

        private AbstractNotifyParams(Intent intent, int type) {
            mPendingIntent = getPendingIntent(intent, type);
        }

        private PendingIntent getPendingIntent(Intent intent, int type) {
            PendingIntent pendintIntent;
            Context context = Utils.getApp();
            int requestCode = intent.hashCode();
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (type == BROADCAST) {
                pendintIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);
            } else if (type == SERVICE) {
                pendintIntent = PendingIntent.getService(context, requestCode, intent, flags);
            } else {
                pendintIntent = PendingIntent.getActivity(context, requestCode, intent, flags);
            }
            return pendintIntent;
        }

        public void setSingleNotify(int id) {
            mId = id;
        }

        public void setTag(String tag) {
            mTag = tag;
        }
    }

    public static class CustomNotifyParams extends AbstractNotifyParams {
        protected RemoteViews mBigView;
        protected RemoteViews mSmallView;

        public CustomNotifyParams(RemoteViews smallView, RemoteViews bigView, Intent intent, int type) {
            super(intent, type);
            mSmallView = smallView;
            mBigView = bigView;
        }
    }
}
