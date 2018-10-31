package com.cy.downloader;

import com.cy.downloader.database.entity.DownloadInfo;
import com.cy.omniknight.tools.Utils;

public class DownloadConstant {
    public static final String APK_DOWNLOAD_TEMP_EXTENSION = ".ptemp";
    public static final String APK_EXTENSION = ".apk";

    public static String getApkDownloadFileName(DownloadInfo downloadInfo) {
        return downloadInfo.getPackageName()+"_"+downloadInfo.getFileMd5() + APK_EXTENSION;
    }

    public static String getApkDownloadTempFileName(DownloadInfo downloadInfo) {
        return downloadInfo.getPackageName()+"_"+downloadInfo.getFileMd5() + APK_EXTENSION + APK_DOWNLOAD_TEMP_EXTENSION;
    }

    public static String getDownloadRootDir() {
        return OkDownloadHelper.getInstance().getParentFile(Utils.getApp()).getPath();
    }
}
