package com.cy.downloader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.cy.omniknight.tools.ActivityUtils;
import com.cy.omniknight.tools.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cy on 18-9-27.
 */

public class CommonUtil {

    public static void launchApp(String packageName) {
        try {
            Context context = Utils.getApp();
            PackageManager pm = context.getPackageManager();
            Intent targetIntent = pm.getLaunchIntentForPackage(packageName);
            targetIntent.setPackage(null);
            ActivityUtils.startActivityNotFrequently(context, targetIntent);
        } catch (Exception e) {
            Log.w("cyTest", "launchApp-error:" + e.getMessage());
        }
    }

    public static void launchApp(String packageName, String deepLink) {
        if (ActivityUtils.isTimeTooShort()) {
            return;
        }
        try {
            if(!TextUtils.isEmpty(deepLink)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ActivityUtils.startActivity(intent);
                return;
            }
        } catch (Exception e) {
        }

        try {
            Context context = Utils.getApp();
            PackageManager pm = context.getPackageManager();
            Intent targetIntent = pm.getLaunchIntentForPackage(packageName);
            targetIntent.setPackage(null);
            ActivityUtils.startActivity(targetIntent);
        } catch (Exception e) {
            Log.w("cyTest", "launchApp-error:" + e.getMessage());
        }
    }

    public static ArrayList<String> getInstalledPackageNames() {
        ArrayList<PackageInfo> res = getPackageInfo();
        ArrayList<String> packageNames = new ArrayList<String>();
        for (PackageInfo r : res) {
            packageNames.add(r.packageName);
        }
        return packageNames;
    }

    public static ArrayList<PackageInfo> getPackageInfo() {
//        ArrayList<PackageInfo> packageInfos = getPackageInfos(PackageManager.GET_UNINSTALLED_PACKAGES); // why use this flag
        ArrayList<PackageInfo> packageInfos = getPackageInfos(0);
        PackageInfo gameHallInfo = getPackageInfoByName(Utils.getApp().getPackageName());
        if (null != gameHallInfo) {
            packageInfos.add(gameHallInfo);
        }
        return packageInfos;
    }

    public static ArrayList<PackageInfo> getPackageInfos(int flags) {
        PackageManager packageManger = Utils.getApp().getPackageManager();
        ArrayList<PackageInfo> res = new ArrayList<PackageInfo>();
        try {
            List<PackageInfo> packs = packageManger.getInstalledPackages(flags);
            for (PackageInfo p : packs) {
                if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    res.add(p);
                }
            }
        } catch (Exception e) {
            Log.w("cyTest", "Package manager has died:" + e.getMessage());
        }
        return res;
    }

    public static PackageInfo getPackageInfoByName(String packageName) {
        return getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
    }

    public static PackageInfo getPackageInfo(String packageName, int flags) {
        PackageManager pm = Utils.getApp().getPackageManager();
        try {
            return pm.getPackageInfo(packageName, flags);
        } catch (Exception e) {
        }
        return null;
    }

    public static ArrayList<String> getDownloadedApkNames() {
        ArrayList<String> packageNames = new ArrayList<String>();
        String apkDir = DownloadConstant.getDownloadRootDir();
        if (apkDir == null) {
            return packageNames;
        }

        File appDirFile = new File(apkDir);
        File[] files = appDirFile.listFiles();
        if (files == null) {
            return packageNames;
        }

        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".apk")) {
                packageNames.add(getPackageName(fileName));
            }
        }
        return packageNames;
    }

    private static String getPackageName(String fileName) {
        return fileName.substring(0, fileName.indexOf("_"));
    }

    public static boolean renameFile(String appDir, String srcName, String destName) {
        String srcFilePath = appDir + File.separator + srcName;
        File srcFile = new File(srcFilePath);
        if (srcFile.exists()) {
            String destFilePath = appDir + File.separator + destName;
            File destFile = new File(destFilePath);
            if (srcFile.renameTo(destFile)) {
                return true;
            }
        }
        return false;
    }

    public static String getFileMD5(final File file) {
        if (file == null) {
            return "";
        }
        synchronized (file) {
            byte[] digest = null;
            FileInputStream in = null;
            try {
                MessageDigest digester = MessageDigest.getInstance("MD5");
                byte[] bytes = new byte[8192];
                in = new FileInputStream(file);
                int byteCount;
                while ((byteCount = in.read(bytes)) > 0) {
                    digester.update(bytes, 0, byteCount);
                }
                digest = digester.digest();
            } catch (Exception cause) {

            } finally {
                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    } catch (Exception e) {
                    }

                }
            }
            return (digest == null) ? null : byteArrayToString(digest);
        }
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            ret.append(Character.forDigit((aByte >> 4) & 0xf, 16));
            ret.append(Character.forDigit(aByte & 0xf, 16));
        }
        return ret.toString();
    }

    public static String getCurrStackExceptionInfo() {
        return "\n" + getExceptionInfo(new Exception());
    }

    public static String getExceptionInfo(Throwable ex) {
        if (null == ex) {
            return "";
        }
        PrintWriter pw = null;
        try {
            Writer errorWriter = new StringWriter();
            pw = new PrintWriter(errorWriter);
            ex.printStackTrace(pw);
            return errorWriter.toString();
        } catch (Exception e) {
            return "get exception info error " + e;
        } finally {
            closeIO(pw);
        }
    }

    public static void closeIO(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
        }
    }
}
