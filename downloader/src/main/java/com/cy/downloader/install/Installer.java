package com.cy.downloader.install;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.cy.omniknight.tools.ActivityUtils;
import com.cy.omniknight.tools.AppUtils;
import com.cy.omniknight.tools.ReflectionTools;
import com.cy.omniknight.tools.ToastUtils;
import com.cy.omniknight.tools.Utils;
import com.cy.omniknight.tools.VersionUtils;
import com.cy.threadbus.SchedulerFactory;
import com.cy.threadbus.TaskRunnable;
import com.cy.threadbus.ThreadBus;

import java.io.File;

/**
 * Created by cy on 18-9-26.
 */

public class Installer {

    private static final String TAG = "Installer";

    private static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    private static final int INSTALL_SUCCEEDED = 1;

    public interface SilentInstallCallback {
        void onSilentInstall(boolean isInstalled, String packageName);
    }


    public static void silentInstall(final Context context, final String packageName, final String homeDir, final String fileName, final SilentInstallCallback silentInstallCallback) {
        ThreadBus.newAssembler().create(new TaskRunnable("silentInstall") {
            @Override
            public void runTask() {
                systemSilentInstall(context, packageName, homeDir, fileName, silentInstallCallback);
            }
        })
        .scheduleOn(SchedulerFactory.getUnlimitedScheduler())
        .start();
    }

    public static boolean hasSignature(String localPath) {
        PackageManager pm = Utils.getApp().getPackageManager();
        return getPackageSignatureByPath(pm, localPath) != null;
    }

    private static String getPackageSignatureByPath(PackageManager pm, String path) {
        try {
            PackageInfo pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_SIGNATURES);
            return pkgInfo.signatures[0].toCharsString();
        } catch (Exception e) {
        }
        return null;
    }

    public static void popupInstall(Context context, String homeDir, String fileName) {
        if (homeDir == null) {
            ToastUtils.showShort("存储器未挂载或已损坏");
            return;
        }
        String apkAbsolutePath = homeDir + File.separator + fileName;
        startSystemInstaller(context, apkAbsolutePath);
    }

    public static void startSystemInstaller(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        startSystemInstallerForGames(context, file);
    }

    @Nullable
    public static Intent getInstallIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (VersionUtils.isTargetSdkN() && VersionUtils.isAndroidN()) {
            Uri uri = null;
            try {
                uri = FileProvider.getUriForFile(context, getProviderName(), file);
            } catch (Exception e) {
                Log.e(TAG, "getInstallIntent", e);
            }

            if (uri == null) {
                return null;
            }
            context.grantUriPermission(AppUtils.getAppPackageName(), uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        return intent;
    }

    private static String getProviderName() {
        return AppUtils.getAppPackageName() + ".installProviderName";
    }

    private static void startSystemInstallerForGames(Context context, File file) {
        Intent intent = getInstallIntent(context, file);
        if (intent == null) {
            return;
        }
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        ActivityUtils.startActivityNotFrequently(context, intent);
    }


    private static void dealInstallTimeOut(final Context context, final String pkgName, final String homeDir, final String fileName,  final String absolutePath, final SilentInstallCallback silentInstallCallback) {
        InstallTimeOutHelper.dealInstallTimeOut(pkgName, absolutePath, new InstallTimeOutHelper.InstallDelayCallback() {
            @Override
            public void onTimeOut(final String packageName) {
                ThreadBus.newAssembler().create(new TaskRunnable("dealInstallTimeOut") {
                    @Override
                    public void runTask() {
                        silentInstallFail(pkgName, silentInstallCallback);
                    }
                }).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
            }
        });
    }

    private static void removeDelayRunnable(String packageName) {
        InstallTimeOutHelper.removeDelayRunnable(packageName);
    }

    public static synchronized void systemSilentInstall(final Context context, final String pkgName, final String homeDir, final String fileName, SilentInstallCallback silentInstallCallback) {
        String apkAbsolutePath = homeDir + File.separator + fileName;

        try {
            dealInstallTimeOut(context, pkgName, homeDir, fileName, apkAbsolutePath, silentInstallCallback);
//            if(Utils.isAndroidN()) {
//                silentInstall(apkAbsolutePath, pkgName); // this code has some wrong, debug next time.
//            } else {
            int installFlags = 0;
            installFlags |= INSTALL_REPLACE_EXISTING;
            PackageManager instance = context.getPackageManager();
            IPackageInstallObserver observer = new PackageInstallObserver(silentInstallCallback);
            File file = new File(apkAbsolutePath);
            Uri packageUri = Uri.fromFile(file);
            String className = PackageManager.class.getName();
            String methodName = "installPackage";
            Class<?>[] parameterTypes = new Class<?>[] {Uri.class, IPackageInstallObserver.class, int.class,
                    String.class};
            Object[] args = new Object[] {packageUri, observer, installFlags, context.getPackageName()};
            ReflectionTools.getMethod(className, instance, methodName, parameterTypes, args);
//            }
        } catch (Exception e) {
            removeDelayRunnable(pkgName);
            silentInstallFail(pkgName, silentInstallCallback);
            e.printStackTrace();
        }
    }

    private static void silentInstallFail(String pkgName, SilentInstallCallback silentInstallCallback) {
        // TODO 静默安装失败
        if(null == silentInstallCallback) {
            return;
        }

        silentInstallCallback.onSilentInstall(false, pkgName);
    }

    private static class PackageInstallObserver extends IPackageInstallObserver.Stub {
        private SilentInstallCallback mSilentInstallCallback;

        public PackageInstallObserver(SilentInstallCallback silentInstallCallback) {
            mSilentInstallCallback = silentInstallCallback;
        }

        @Override
        public synchronized void packageInstalled(String pkgName, int returnCode) {
            removeDelayRunnable(pkgName);

            if (returnCode == INSTALL_SUCCEEDED) {
                installSuccess(pkgName, mSilentInstallCallback);
            } else {
                silentInstallFail(pkgName, mSilentInstallCallback);
            }
        }

        private void installSuccess(String pkgName, SilentInstallCallback silentInstallCallback) {
            if(null == silentInstallCallback) {
                return;
            }

            silentInstallCallback.onSilentInstall(true, pkgName);

        }
    }
}
