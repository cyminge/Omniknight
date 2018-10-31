package com.cy.downloader.install;

import com.cy.omniknight.tools.tem.HandlerUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by cy on 18-9-27.
 */

public class InstallTimeOutHelper {
    public interface InstallDelayCallback {
        void onTimeOut(String packageName);
    }

    private static final int DEFALUT_INSTALL_WAIT_TIME = 30 * 1000;
    private static final int ADD_INSTALL_WAIT_TIME = 20 * 1000;

    private static int getSleepTime(String filePath) {
        int time = DEFALUT_INSTALL_WAIT_TIME;
        try {
            File file = new File(filePath);
            int mb = (int) file.length() / 1024 / 1024;
            if (mb > 50) {
                time += ADD_INSTALL_WAIT_TIME;
            }
            if (mb > 100) {
                time += ADD_INSTALL_WAIT_TIME;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    private static HashMap<String, Runnable> mRunnableMap = new HashMap<String, Runnable>();

    public static void dealInstallTimeOut(String packageName, String absolutePath,
                                          InstallDelayCallback callback) {
        long time = getSleepTime(absolutePath);
        dealInstallTimeOut(System.currentTimeMillis(), time, packageName, callback);
    }

    private static void dealInstallTimeOut(long startMs, long delayMs, final String packageName,
                                           final InstallDelayCallback callback) {
        synchronized (mRunnableMap) {
            long deltaTime = Math.abs(System.currentTimeMillis() - startMs);
            long remainTime = delayMs - deltaTime;
            remainTime = Math.max(remainTime, 0);
            remainTime = Math.min(remainTime, delayMs);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    callback.onTimeOut(packageName);
                }
            };
            mRunnableMap.put(packageName, runnable);
            HandlerUtils.postDelayed(runnable, remainTime);
        }
    }

    public static void removeDelayRunnable(String packageName) {
        synchronized (mRunnableMap) {
            Runnable runnable = mRunnableMap.get(packageName);
            if (null == runnable) {
                return;
            }
            HandlerUtils.removeRunnable(runnable);
            mRunnableMap.remove(packageName);
        }
    }
}
