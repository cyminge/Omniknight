package com.cy.omniknight.tools.storage;

import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;

public class GNStorageUtils {
//    private static final String STORAGE_PATH = "storage_path";
//    private static final String OLD_SDCARD_1_PATH = "/mnt/sdcard";
//    private static final String OLD_SDCARD_2_PATH = "/mnt/sdcard2";
//    private static final String NEW_SDCARD_1_PATH = "/storage/sdcard0";
//    private static final String NEW_SDCARD_2_PATH = "/storage/sdcard1";
//
//    private static String sGameFolder;
//    private static String sSDCardDir;
//
//    static {
//        init("cyTest");
//    }
//
//    public static void init(String folderName) {
//        String path = SharePrefUtil.getString(STORAGE_PATH);
//        if (TextUtils.isEmpty(path) || !isSDCard2Exsit()) {
//            sSDCardDir = getRootPath();
//        } else {
//            sSDCardDir = path;
//        }
//        sGameFolder = folderName;
//    }
//
//    public static boolean switchSDCard() {
//        if (!isSDCard2Exsit()) {
//            return false;
//        }
//
//        if (isOldAndroidSystem()) {
//            sSDCardDir = sSDCardDir.equals(OLD_SDCARD_1_PATH) ? OLD_SDCARD_2_PATH : OLD_SDCARD_1_PATH;
//        } else {
//            sSDCardDir = sSDCardDir.equals(NEW_SDCARD_1_PATH) ? NEW_SDCARD_2_PATH : NEW_SDCARD_1_PATH;
//        }
//
//        SharePrefUtil.putString(STORAGE_PATH, sSDCardDir);
////        BitmapManager.onMediaChange();
////        DownloadStatusMgr.getNormalInstance().onSdChanged();
//        return true;
//    }
//
//    public static boolean isSDCard2Exsit() {
//        if (!GNStorageUtils.isSDCardMounted()) {
//            return false;
//        }
//
//        String sdcardPath;
//        if (isOldAndroidSystem()) {
//            sdcardPath = OLD_SDCARD_2_PATH;
//        } else {
//            sdcardPath = NEW_SDCARD_2_PATH;
//        }
//
//        File sdcard2 = new File(sdcardPath);
//        return sdcard2 != null && sdcard2.exists() && (getSDCardBlockCount(sdcardPath) != 0);
//    }
//
//    private static boolean isOldAndroidSystem() {
//        return OLD_SDCARD_1_PATH.equals(getRootPath());
//    }
//
//    @SuppressWarnings("deprecation")
//    private static int getSDCardBlockCount(String sdcardDir) {
//        try {
//            StatFs stat = new StatFs(sdcardDir);
//            return stat.getBlockCount();
//        } catch (Exception e) {
//        }
//        return 0;
//    }
//
//    public static String getAnotherHomeDir() {
//        if (!isSDCard2Exsit()) {
//            return null;
//        }
//
//        String sdCardDir = sSDCardDir;
//        if (isOldAndroidSystem()) {
//            sdCardDir = sdCardDir.equals(OLD_SDCARD_1_PATH) ? OLD_SDCARD_2_PATH : OLD_SDCARD_1_PATH;
//        } else {
//            sdCardDir = sdCardDir.equals(NEW_SDCARD_1_PATH) ? NEW_SDCARD_2_PATH : NEW_SDCARD_1_PATH;
//        }
//
//        return sdCardDir + File.separator + sGameFolder;
//    }
//
//    public static boolean isSDCardMounted() {
//        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
//    }
//
//    public static void onMediaChange() {
//        sSDCardDir = getRootPath();
//    }
//
//    public static String getSDCardDir() {
//        if (isSDCardMounted()) {
//            return sSDCardDir;
//        }
//        return null;
//    }
//
//    public static String getGameDir() {
//        return sGameFolder;
//    }
//
//    public static boolean isSDCardChange() {
//        String rootPath = getRootPath();
//        return !rootPath.equals(sSDCardDir);
//    }
//
//    private static String getRootPath() {
//        return Environment.getExternalStorageDirectory().getAbsolutePath();
//    }
//
//    public static String getHomeDirAbsolute() {
//        if (isSDCardMounted()) {
//            String retPath = sSDCardDir + File.separator + sGameFolder;
//            File file = new File(retPath);
//            if (!file.exists()) {
//                FileUtils.mkdirs(file);
//            }
//            return retPath;
//        }
//        return null;
//    }
//
//    public static void deleteFileDir(File fileDir, boolean isNeedDeleteHideFile) {
//        if (!fileDir.exists() || fileDir.isFile()) {
//            return;
//        }
//        recursionDeleteFile(fileDir, isNeedDeleteHideFile);
//    }
//
//    private static void recursionDeleteFile(File fileDir, boolean isNeedDeleteHideFile) {
//        if (fileDir == null || !fileDir.isDirectory()) {
//            return;
//        }
//        File[] childFiles = fileDir.listFiles();
//        if (childFiles == null) {
//            return;
//        }
//        for (File file : childFiles) {
//            if (file.isDirectory()) {
//                recursionDeleteFile(file, isNeedDeleteHideFile);
//            } else {
//                if (isNeedDeleteHideFile || !file.isHidden()) {
//                    FileUtils.delete(file);
//                }
//            }
//        }
//    }
}
