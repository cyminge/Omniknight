package com.cy.omniknight.tools.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private static final String FILE_TYPE_APK = ".apk";

    public static boolean delete(File file) {
        boolean succ = file.delete();
        String name = file.getName();
        if (succ && name.endsWith(FILE_TYPE_APK)) {
            notifyApkRemove(name);
        }
        return succ;
    }

    public static boolean delete(String pathName) {
        File file = new File(pathName);

        if (file != null && file.exists()) {
            return delete(file);
        }

        return false;
    }

    public static void deleteFileDir(File fileDir, boolean isNeedDeleteHideFile) {
        if (!fileDir.exists() || fileDir.isFile()) {
            return;
        }

        recursionDeleteFile(fileDir, isNeedDeleteHideFile);
    }

    private static void recursionDeleteFile(File fileDir, boolean isNeedDeleteHideFile) {
        if (fileDir == null || !fileDir.isDirectory()) {
            return;
        }
        File[] childFiles = fileDir.listFiles();
        if (childFiles == null) {
            return;
        }
        for (File file : childFiles) {
            if (file.isDirectory()) {
                recursionDeleteFile(file, isNeedDeleteHideFile);
            } else {
                if (isNeedDeleteHideFile || !file.isHidden()) {
                    FileUtils.delete(file);
                }
            }
        }
    }

    public static boolean setLastModified(File file, long time) {
        return file.setLastModified(time);
    }

    public static boolean renameTo(File file, File aimFile) {
        return file.renameTo(aimFile);
    }

    public static boolean mkdirs(File file) {
        return file.mkdirs();
    }

    public static boolean mkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }

        return true;
    }

    public static boolean isDirExist(File file) {
        return file.exists();
    }

    public static boolean isDirExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean createNewFile(File file) throws IOException {
        return file.createNewFile();
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                delete(destFile);
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
                return true;
            } finally {
                try {
                    out.flush();
                } catch (IOException e) {
                }
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static void notifyApkAdd(String fileName) {
        // File file = Utils.getFile(fileName);
        // if (file == null || !file.exists()) {
        // return;
        // }
        // Intent scanIntent = new
        // Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // scanIntent.setData(Uri.fromFile(file));
        // GNApplication.getInstance().sendBroadcast(scanIntent);
    }

    private static void notifyApkRemove(final String fileName) {
        // ThreadPoolUtil.post(new Runnable() {
        // @Override
        // public void run() {
        // ContentResolver cr =
        // GNApplication.getInstance().getContentResolver();
        // Uri baseUri = MediaStore.Files.getContentUri("external");
        // try {
        // cr.delete(baseUri, MediaStore.Files.FileColumns.DATA + " like ?", new
        // String[] { "%"
        // + fileName + "%" });
        // } catch (Exception e) {
        // LogUtils.loge(TAG, LogUtils.getFunctionName(), e);
        // }
        //
        // }
        // });
    }
}
