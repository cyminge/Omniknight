package com.cy.downloader;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.cy.downloader.database.DownloadDBOperator;
import com.cy.downloader.database.entity.DownloadInfo;
import com.cy.downloader.install.Installer;
import com.cy.downloader.notify.DownloadNotificationManager;
import com.cy.downloader.notify.IDownloadNotification;
import com.cy.omniknight.tools.Utils;
import com.cy.omniknight.tools.receiver.ConnectivityController;
import com.cy.omniknight.tools.receiver.ReceiverManager;
import com.cy.omniknight.tools.receiver.StateChangedListener;
import com.cy.omniknight.tools.tem.HandlerUtils;
import com.cy.threadbus.SchedulerFactory;
import com.cy.threadbus.TaskRunnable;
import com.cy.threadbus.ThreadBus;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by cy on 18-9-25.
 */

public class DownloadHelper {

    public static final int NOTIFY_PROGRESS_DELAY_TIME = 2000;
    private static final float MAX_PROGRESS = 0.99f;

    private Object mProgressUpdateLock = new Object();

    private static final DownloadHelper sInstance = new DownloadHelper();

    public static DownloadHelper getInstance() {
        return sInstance;
    }

    private static volatile boolean sIsSynced = false;
    private ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<>();
    private ArrayList<String> mInstalledApp = new ArrayList<>();  // 已安装应用列表
    private ArrayList<String> mDownloadedAPK = new ArrayList<>(); // 已下载apk列表

    private IDownload mDownloadManager;
    private long mLastNotifyTime = 0;
    private IDownloadNotification mDownloadNotification;

    private DownloadHelper() {
        mDownloadManager = new DownloadManager(mDownloadListener1);
    }

    public void init() {
        mDownloadNotification = new DownloadNotificationManager();
        ThreadBus.newAssembler().create(new TaskRunnable("download init") {
            @Override
            public void runTask() {
                initInstalledApp();
                initDownloadedAPK();
                initDownloadTask();
                sIsSynced = true;
                mDownloadNotification.showNotification(new ArrayList<>(mDownloadInfoMap.values()));
                EventBus.getDefault().post(new DownloadChangeMessage(true));
            }
        })
        .scheduleOn(SchedulerFactory.getUnlimitedScheduler())
        .start();

        ReceiverManager.getInstance().startTracking(ReceiverManager.CHANGE_TYPE_PACKAGE, mStateChangedListener);
        ReceiverManager.getInstance().startTracking(ReceiverManager.CHANGE_TYPE_NETWORK, mStateChangedListener);
    }

    private StateChangedListener mStateChangedListener = new StateChangedListener() {
        @Override
        public void onStateChanged(String changeType, Object... params) {
            if(ReceiverManager.CHANGE_TYPE_PACKAGE.equals(changeType)) {
                String packageName = String.valueOf(params[1]);

                String action = String.valueOf(params[0]);
                if(Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                    mInstalledApp.remove(packageName);
                    DownloadInfo downloadInfo = mDownloadInfoMap.get(packageName);
                    if(null != downloadInfo) {
                        DownloadDBOperator.getInstance().delete(downloadInfo);
                        mDownloadInfoMap.remove(packageName);
                    }
                } else if(Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                    onInstallSuccess(packageName);
                }
                EventBus.getDefault().post(new DownloadChangeMessage(true));

            } else if(ReceiverManager.CHANGE_TYPE_NETWORK.equals(changeType)) {
                if(!isSynced()){
                    return;
                }

                if(ConnectivityController.isWifiNetwork()) {
                    switchDownloadSate(RESUME_DOWNLOAD_TASK);
                } else {
                    switchDownloadSate(PAUSE_DOWNLOAD_TASK);
                }
            }
        }
    };

    private static final String RESUME_DOWNLOAD_TASK = "resumeDownloadTask";
    private static final String PAUSE_DOWNLOAD_TASK = "pauseDownloadTask";

    private void switchDownloadSate(String state) {
        Collection<DownloadInfo> downloadInfos = getDownloadInfoList();
        Iterator<DownloadInfo> iterator = downloadInfos.iterator();
        while(iterator.hasNext()) {
            DownloadInfo downloadInfo = iterator.next();
            if(null == downloadInfo) {
                return;
            }

            if(RESUME_DOWNLOAD_TASK.equals(state)) {
                if(DownloadInfo.STATE_DOWNLOAD == downloadInfo.getState()
                        || DownloadInfo.STATE_DOWNLOAD_FAILED == downloadInfo.getState()
                        || DownloadInfo.STATE_PAUSE == downloadInfo.getState()) {
                    mDownloadManager.startDownload(downloadInfo);
                    downloadInfo.setState(DownloadInfo.STATE_DOWNLOADING);
                    DownloadDBOperator.getInstance().update(downloadInfo);
                    mDownloadNotification.showNotification(downloadInfo);
                    EventBus.getDefault().post(new DownloadChangeMessage(true));
                }
            } else if(PAUSE_DOWNLOAD_TASK.equals(state)) {
                if(DownloadInfo.STATE_DOWNLOADING == downloadInfo.getState()) {
                    mDownloadManager.pauseDownload(downloadInfo);
                    downloadInfo.setState(DownloadInfo.STATE_PAUSE);
                    DownloadDBOperator.getInstance().update(downloadInfo);
                    mDownloadNotification.showNotification(downloadInfo);
                    EventBus.getDefault().post(new DownloadChangeMessage(true));
                }
            }
        }
    }

    private synchronized void onInstallSuccess(String packageName) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(packageName);
        if(null == downloadInfo) {
            return;
        }
//        if(SwitchConfig.getInstance().isDeleteDownloadedApk()) { // TODO
//            downloadInfo.setState(DownloadInfo.STATE_OPEN);
//            mDownloadNotification.showNotification(downloadInfo);
//            long result = DownloadDBOperator.getInstance().delete(downloadInfo);
//            if(result == -1) {
//                DownloadDBOperator.getInstance().update(downloadInfo);
//            }
//            mDownloadManager.cancelDownload(downloadInfo);
//            mDownloadInfoMap.remove(packageName);
//        } else {
            downloadInfo.setState(DownloadInfo.STATE_OPEN);
            DownloadDBOperator.getInstance().update(downloadInfo);
            mDownloadNotification.showNotification(downloadInfo);
//        }
        if(!mInstalledApp.contains(packageName)) {
            mInstalledApp.add(packageName);
        }
    }

    private void initDownloadedAPK() {
        mDownloadedAPK.addAll(CommonUtil.getDownloadedApkNames());
    }

    private void initInstalledApp() {
        mInstalledApp.addAll(CommonUtil.getInstalledPackageNames());
    }

    public boolean isInstalled(String pkgName) {
        return mInstalledApp.contains(pkgName);
    }

    public boolean isDownloaded(String packageName) {
        return mDownloadedAPK.contains(packageName);
    }

    private void initDownloadTask() {
        ArrayList<DownloadInfo> downloadInfos = DownloadDBOperator.getInstance().queryAll();
        if(null == downloadInfos || downloadInfos.isEmpty()) {
            return;
        }
        for(DownloadInfo downloadInfo:downloadInfos) {
            mDownloadInfoMap.put(downloadInfo.getPackageName(), downloadInfo);
        }
    }

    public static boolean isSynced() {
        return sIsSynced;
    }

    private DownloadInfo getDownloadInfo(String packageName) {
        return mDownloadInfoMap.get(packageName);
    }

    private Collection<DownloadInfo> getDownloadInfoList() {
        return mDownloadInfoMap.values();
    }

    public int getDownloadStatus(String packageName) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(packageName);
        if(null == downloadInfo) {
            if(isInstalled(packageName)) {
                return DownloadInfo.STATE_OPEN;
            }
            return DownloadInfo.STATE_DOWNLOAD;
        }

        return downloadInfo.getState();
    }

    public float getDownloadProgress(String packageName) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(packageName);
        if(null == downloadInfo) {
            return 0;
        }

        long total = 0;
        if (downloadInfo.getTotalSize() > 0) {
            total = downloadInfo.getTotalSize() ;
        }

        if(total == 0) {
            return 0;
        }

        float progress = ((float) downloadInfo.getProgress()) / total;
        progress = Math.max(0, progress);
        progress = Math.min(MAX_PROGRESS, progress);
        return progress;
    }

    private DownloadListener1 mDownloadListener1 = new DownloadListener1() {

        @Override
        public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
            updateDownloadState(task, DownloadInfo.STATE_DOWNLOADING);
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
            // TODO 下载失败需要删除已下载文件
            updateDownloadState(task, DownloadInfo.STATE_DOWNLOAD_FAILED);
        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            // TODO Nothing
        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            String fileName = task.getFilename();
            String packageName = fileName.substring(0, fileName.indexOf("_"));
            DownloadInfo downloadInfo = getDownloadInfo(packageName);
            downloadInfo.setTotalSize(totalLength);
            downloadInfo.setProgress(currentOffset);

            long delay = (NOTIFY_PROGRESS_DELAY_TIME - (System.currentTimeMillis() - mLastNotifyTime));
            delay = Math.max(0, delay);
            delay = Math.min(NOTIFY_PROGRESS_DELAY_TIME, delay);

            HandlerUtils.postDelayed(new OnProgressChangeRunnable(downloadInfo), delay);
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
            synchronized (mProgressUpdateLock) {
                String fileName = task.getFilename();
                String packageName = fileName.substring(0, fileName.indexOf("_"));
                DownloadInfo downloadInfo = getDownloadInfo(packageName);

                if(0 != downloadInfo.getTotalSize() && downloadInfo.getTotalSize() == downloadInfo.getProgress()) {
                    if(CommonUtil.getFileMD5(task.getFile()).equals(downloadInfo.getFileMd5())) {
                        downloadInfo.setState(DownloadInfo.STATE_INSTALL);
                        CommonUtil.renameFile(DownloadConstant.getDownloadRootDir(), fileName, fileName.substring(0, fileName.lastIndexOf(".")));
                        installApp(downloadInfo);
                        EventBus.getDefault().post(new DownloadChangeMessage(true));
                    }
                }
            }
        }
    };

    private class OnProgressChangeRunnable implements Runnable {

        private DownloadInfo mDownloadInfo;

        private OnProgressChangeRunnable(DownloadInfo downloadInfo) {
            mDownloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            ThreadBus.newAssembler().create(new TaskRunnable("OnProgressChangeRunnable") {
                @Override
                public void runTask() {
                    synchronized (mProgressUpdateLock) {
                        int result = DownloadDBOperator.getInstance().update(mDownloadInfo);
                        if(result <= 0) {
                            return;
                        }
                        EventBus.getDefault().post(new DownloadChangeMessage(false));
                        mLastNotifyTime = System.currentTimeMillis();
                        mDownloadNotification.showNotification(mDownloadInfo);
                    }
                }
            }).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
        }
    }

    private void installApp(DownloadInfo downloadInfo) {
//        if(SwitchConfig.getInstance().isAllowSilentInstall()) { // TODO
//            downloadInfo.setState(DownloadInfo.STATE_INSTALLING);
//            mDownloadNotification.showNotification(downloadInfo);
//            Installer.silentInstall(Utils.getApp(), downloadInfo.getPackageName(), DownloadConstant.getDownloadRootDir(),
//                    DownloadConstant.getApkDownloadFileName(downloadInfo), mSilentInstallCallback);
//        } else {
//            Installer.popupInstall(Utils.getApp(), DownloadConstant.getDownloadRootDir(), DownloadConstant.getApkDownloadFileName(downloadInfo));
//        }
    }

    private Installer.SilentInstallCallback mSilentInstallCallback = new Installer.SilentInstallCallback() {
        @Override
        public void onSilentInstall(boolean isInstalled, String packageName) {
            DownloadInfo downloadInfo = getDownloadInfo(packageName);
            if(isInstalled) {
//                NotificationUtils.installNotify(downloadInfo);
                onInstallSuccess(packageName);
            } else {
                downloadInfo.setState(DownloadInfo.STATE_INSTALL_FAILED);
                Installer.popupInstall(Utils.getApp(), DownloadConstant.getDownloadRootDir(), DownloadConstant.getApkDownloadFileName(downloadInfo));
            }
            EventBus.getDefault().post(new DownloadChangeMessage(true));
        }
    };

    private void updateDownloadState(DownloadTask task, int state) {
        String fileName = task.getFilename();
        String packageName = fileName.substring(0, fileName.indexOf("_"));
        DownloadInfo downloadInfo = getDownloadInfo(packageName);
        downloadInfo.setState(state);
        DownloadDBOperator.getInstance().update(downloadInfo);
        mDownloadNotification.showNotification(downloadInfo);
        EventBus.getDefault().post(new DownloadChangeMessage(true));
    }

    public class DownloadChangeMessage {
        private boolean mIsStateChange;

        public DownloadChangeMessage(boolean isStateChange) {
            this.mIsStateChange = isStateChange;
        }

        public boolean isStateChange() {
            return mIsStateChange;
        }

    }

    private boolean isShouldShowTrafficDialog(DownloadInfo downloadInfo) {
        if(ConnectivityController.isWifiNetwork()) {
            return false;
        }

        if(null == downloadInfo) {
            return true;
        }
        if(DownloadInfo.STATE_DOWNLOAD == downloadInfo.getState() || DownloadInfo.STATE_DOWNLOAD_FAILED == downloadInfo.getState() || DownloadInfo.STATE_PAUSE == downloadInfo.getState()) {
            return true;
        }

        return false;
    }

    public void handleDownloadActionUse(final Context context, final DownloadBean downloadBean) {
        ThreadBus.newAssembler().create(new TaskRunnable("handleDownloadAction") {
            @Override
            public void runTask() {
                if(!isSynced()) {
                    return;
                }

                String packageName = downloadBean.getPackageName();
                if(isInstalled(packageName)) {
                    CommonUtil.launchApp(packageName, downloadBean.deepLink);
                    return;
                }

                // TODO 因为存放的路径是在package/cache目录下，清除应用数据也会把这下面的数据清除，所以这种情况基本不会出现，暂时不处理这种情况
//                    if(isDownloaded(downloadBean.getPackageName())) {
//                        Installer.popupInstall(GlobalVariable.sAppContext, DownloadConstant.getDownloadRootDir(), DownloadConstant.getApkDownloadFileName(downloadBean));
//                        // TODO 创建并存储DownloadInfo
//                        return;
//                    }

                final DownloadInfo downloadInfo = mDownloadInfoMap.get(packageName);
                if(isShouldShowTrafficDialog(downloadInfo)) {
                    HandlerUtils.post(new Runnable() {
                        @Override
                        public void run() {
                            TrafficRemindDialog trafficRemindDialog = new TrafficRemindDialog(downloadInfo, new TrafficRemindDialog.OnTrafficRemindCallBack() {
                                @Override
                                public void onMobileNetStart(DownloadInfo dlInfo) {
                                    handleDownloadActionUse(context, dlInfo, downloadBean);
                                }

                                @Override
                                public void onMobileNetCancel(DownloadInfo downloadInfo) {
                                }
                            }, context);

                            trafficRemindDialog.setTitle(R.string.app_traffic_remind_title);
                            String content;
                            if(null == downloadInfo || (downloadInfo.getTotalSize() == 0 || downloadInfo.getProgress() == 0)) {
                                if(TextUtils.isEmpty(downloadBean.getSize())) {
                                    content = context.getString(R.string.app_traffic_remind_content, "");
                                } else {
                                    content = context.getString(R.string.app_traffic_remind_content, "将消耗约"+downloadBean.getSize()+"的流量，");
                                }
                            } else {
                                long remainSizeLong = downloadInfo.getTotalSize() - downloadInfo.getProgress();
                                int remainSizeInt = (int)(remainSizeLong / (1024 * 1024));
                                if(remainSizeInt < 1) {
                                    remainSizeInt = 1;
                                }
                                String remainSizeStr = String.valueOf(remainSizeInt) + "M";
                                content = context.getString(R.string.app_traffic_remind_content, "将消耗约"+remainSizeStr+"的流量，");
                            }
                            trafficRemindDialog.setContent(content);
                            trafficRemindDialog.setPositiveText(R.string.app_traffic_remind_positive_text);
                            trafficRemindDialog.setNegativeText(R.string.app_traffic_remind_negative_text);
                            dialogShow(trafficRemindDialog);
                        }
                    });

                    return;
                }

                handleDownloadActionUse(context, downloadInfo, downloadBean);
            }
        })
        .scheduleOn(SchedulerFactory.getUnlimitedScheduler())
        .start();
    }

    private void handleDownloadActionUse(final Context context, final DownloadInfo downloadInfo, final DownloadBean downloadBean) {
        DownloadInfo dlInfo = downloadInfo;
        if(null == downloadInfo) {
            long result = DownloadDBOperator.getInstance().insert(downloadBean);
            if(result < 0) {
                return;
            }

            dlInfo = DownloadDBOperator.getInstance().queryDownloadInfo(downloadBean.getPackageName());
            mDownloadInfoMap.put(downloadBean.getPackageName(), dlInfo);
        }

        handleDownloadAction(context, dlInfo, false);
    }


    public void cancelDownloadAction(final String packageName) {
        if(TextUtils.isEmpty(packageName)) {
            return;
        }

        final DownloadInfo downloadInfo = getDownloadInfo(packageName);
        if(null == downloadInfo) {
            return;
        }

        ThreadBus.newAssembler().create(new TaskRunnable("handleDownloadAction") {
            @Override
            public void runTask() {
                if(!isSynced()) {
                    return;
                }
                mDownloadManager.cancelDownload(downloadInfo);
                DownloadDBOperator.getInstance().delete(downloadInfo);
                mDownloadNotification.cancelNotification(downloadInfo.getUrl().hashCode());
                mDownloadInfoMap.remove(packageName);
                EventBus.getDefault().post(new DownloadChangeMessage(true));
            }
        }).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
    }

    public void handleDownloadActionNotification(final Context context, final String packageName, final String deepLink) {
        if(TextUtils.isEmpty(packageName)) {
            return;
        }

        if(isInstalled(packageName)) {
            CommonUtil.launchApp(packageName, deepLink);
            return;
        }

        final DownloadInfo downloadInfo = getDownloadInfo(packageName);
        if(null == downloadInfo) {
            return;
        }

        if(isShouldShowTrafficDialog(downloadInfo)) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    TrafficRemindDialog trafficRemindDialog = new TrafficRemindDialog(downloadInfo, new TrafficRemindDialog.OnTrafficRemindCallBack() {
                        @Override
                        public void onMobileNetStart(DownloadInfo dlInfo) {
                            handleDownloadActionNotification(context, downloadInfo);
                        }

                        @Override
                        public void onMobileNetCancel(DownloadInfo downloadInfo) {
                        }
                    }, context);

                    trafficRemindDialog.setTitle(R.string.app_traffic_remind_title);
                    String content;
                    if(downloadInfo.getTotalSize() == 0 || downloadInfo.getProgress() == 0) {
                        if(TextUtils.isEmpty(downloadInfo.getSize())) {
                            content = context.getString(R.string.app_traffic_remind_content, "");
                        } else {
                            content = context.getString(R.string.app_traffic_remind_content, "将消耗约"+downloadInfo.getSize()+"的流量，");
                        }
                    } else {
                        long remainSizeLong = downloadInfo.getTotalSize() - downloadInfo.getProgress();
                        int remainSizeInt = (int)(remainSizeLong / (1024 * 1024));
                        if(remainSizeInt < 1) {
                            remainSizeInt = 1;
                        }
                        String remainSizeStr = String.valueOf(remainSizeInt) + "M";
                        content = context.getString(R.string.app_traffic_remind_content, "将消耗约"+remainSizeStr+"的流量，");
                    }
                    trafficRemindDialog.setContent(content);
                    trafficRemindDialog.setPositiveText(R.string.app_traffic_remind_positive_text);
                    trafficRemindDialog.setNegativeText(R.string.app_traffic_remind_negative_text);
                    dialogShow(trafficRemindDialog);
                }
            });
            return;
        }

        handleDownloadActionNotification(context, downloadInfo);
    }

    public void dialogShow(Dialog dialog) {
        try {
            if(isNotNull(dialog)) {
                dialog.show();
            }
        } catch (Exception var2) {
            Log.w("cyTest", var2.getMessage());
        }

    }

    private boolean isNotNull(Object object) {
        return !isNull(object);
    }

    private boolean isNull(Object object) {
        return object == null || "".equals(object.toString().trim()) || "null".equals(object.toString().trim().toLowerCase(Locale.getDefault()));
    }

    private void handleDownloadActionNotification(final Context context, final DownloadInfo downloadInfo) {
        ThreadBus.newAssembler().create(new TaskRunnable("handleDownloadActionNotification") {
            @Override
            public void runTask() {
                if(!isSynced()) {
                    return;
                }
                handleDownloadAction(context, downloadInfo, true);
            }
        }).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
    }

    private void handleDownloadStart(DownloadInfo downloadInfo) {
        mDownloadManager.startDownload(downloadInfo);
        downloadInfo.setState(DownloadInfo.STATE_DOWNLOADING);
        DownloadDBOperator.getInstance().update(downloadInfo);
        mDownloadNotification.showNotification(downloadInfo);
        EventBus.getDefault().post(new DownloadChangeMessage(true));
    }

    private void handleDownloadAction(final Context context, final DownloadInfo downloadInfo, boolean isFromNotification) {
        switch (downloadInfo.getState()) {
            case DownloadInfo.STATE_DOWNLOAD:
            case DownloadInfo.STATE_DOWNLOAD_FAILED:
            case DownloadInfo.STATE_PAUSE:
                handleDownloadStart(downloadInfo);
                break;
            case DownloadInfo.STATE_DOWNLOADING:
                if(!isFromNotification) {
                    return;
                }
                mDownloadManager.pauseDownload(downloadInfo);
                downloadInfo.setState(DownloadInfo.STATE_PAUSE);
                DownloadDBOperator.getInstance().update(downloadInfo); // TODO update会不会有冲突
                mDownloadNotification.showNotification(downloadInfo);
                EventBus.getDefault().post(new DownloadChangeMessage(true));
                break;
            case DownloadInfo.STATE_INSTALL: // TODO 静默安装失败，或者用户手动安装点击取消的时候，再次点击是否需要静默安装
                Installer.popupInstall(Utils.getApp(), DownloadConstant.getDownloadRootDir(), DownloadConstant.getApkDownloadFileName(downloadInfo));
                break;
            case DownloadInfo.STATE_INSTALLING:
                break;
            case DownloadInfo.STATE_INSTALL_FAILED:
                Installer.popupInstall(Utils.getApp(), DownloadConstant.getDownloadRootDir(), DownloadConstant.getApkDownloadFileName(downloadInfo));
                break;
            case DownloadInfo.STATE_OPEN:
                CommonUtil.launchApp(downloadInfo.getPackageName(), downloadInfo.getDeepLink());
                break;
            case DownloadInfo.STATE_UPGRADE: // TODO 是否需要处理更新？ 如果要处理该怎么做？数据上要做怎样的调整？
                break;
        }
    }

}
