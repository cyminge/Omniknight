package com.cy.omniknight.tools.device;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.SensorManager;
import android.net.TrafficStats;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 设备相关
 * 
 * @author wkl
 * @time 2015-10-12
 */
public class DeviceUtil {

    /**
     * 获取屏幕刷新率
     * 
     * @return
     */
    public static int getScreenRefreshRate(Context context) {
        Display display = getDispaly(context);
        int refreshRate = (int) display.getRefreshRate();
        return refreshRate;
    }

    /**
     * 获取屏幕物理尺寸
     */
    public static int getScreenPhysicalDimensions(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        float density = dm.density;

        int screenWidth = (int) (dm.widthPixels + 0.5f); // 屏幕宽（px，如：480px）
        int screenHeight = (int) (dm.heightPixels + 0.5f); // 屏幕高（px，如：800px）
        double i = (Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight)) / 240;
        screenWidth = (int) (dm.widthPixels / density + 0.5f); // 屏幕宽（px，如：480px）
        screenHeight = (int) (dm.heightPixels / density + 0.5f); // 屏幕高（px，如：800px）
        double j = (Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight)) / 160;
        return (int) i;

    }

    /**
     * 获取Dispaly
     * 
     * @return
     */
    public static Display getDispaly(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return display;
    }

    /**
     * 获取屏幕分辨率
     * 
     * @return
     */
    public String getScreenResolution(Context context) {
        Display d = getDispaly(context);
        Method mGetRawW = null;
        Method mGetRawH = null;
        Method getRawExternalWidth = null;
        Method getRawExternalHeight = null;
        try {
            mGetRawW = d.getClass().getMethod("getRawWidth");
            mGetRawH = d.getClass().getMethod("getRawHeight");
            getRawExternalWidth = d.getClass().getMethod("getRawExternalWidth");
            getRawExternalHeight = d.getClass().getMethod("getRawExternalHeight");
            try {
                int nW = (Integer) mGetRawW.invoke(d);
                int nH = (Integer) mGetRawH.invoke(d);
                int nrW = (Integer) getRawExternalWidth.invoke(d);
                int nrH = (Integer) getRawExternalHeight.invoke(d);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取屏幕高度与宽度(其实就是分辨率)
     */
    public static int[] getScreenWidthAndHeight(Context context) {
        int[] screen = new int[2];
        DisplayMetrics dm = getDisplayMetrics(context);
        // 1、
        int screenWidth = (int) (dm.widthPixels + 0.5f); // 屏幕宽（px，如：480px）
        int screenHeight = (int) (dm.heightPixels + 0.5f); // 屏幕高（px，如：800px）
        // 2、
        // int screenWidth = dm.widthPixels;
        // int screenHeight = dm.heightPixels;
        // Log.i(TAG, "---screenWidth = " + screenWidth + "; screenHeight = " +
        // screenHeight);
        screen[0] = screenWidth;
        screen[1] = screenHeight;
        return screen;
    }

    /**
     * 获取屏幕密度值DPI
     */
    public static int getDesityDPI(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        int densityDPI = dm.densityDpi;
        // float xdpi = dm.xdpi;
        // float ydpi = dm.ydpi;
        // Log.i(TAG, "---xdpi=" + xdpi + "; ydpi=" + ydpi);
        // Log.i(TAG, "---densityDPI = " + densityDPI);
        return densityDPI;
    }

    /**
     * 获取屏幕密度百分比density值
     */
    public static float getDesityPixelScale(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        float density = dm.density; // （像素比例：0.75/1.0/1.5/2.0）
        return density;
    }

    /**
     * 获取DisplayMetrics
     * 
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        // ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    /**
     * 获取软件版本名称
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) throws Exception {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }

    /**
     * 获取软件版本号
     * 
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
//            Tracer.debugException(e);
        }
        return versionCode;
    }

    /**
     * 获取PackageInfo(包信息)
     * 
     * @param context
     *            Context
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pInfo = null;
        try {
            // pInfo =
            // context.getPackageManager().getPackageInfo(context.getPackageName(),PackageManager.GET_META_DATA);
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    /**
     * 获取cpu使用率
     */
    public static String getCPUUsageRate() throws Exception {
        String result;
        // top -m 10:取前十条，间隔刷新为1s. Top –d 1:间隔刷新为1s. Top -n 1:刷新次数为1
        Process p = Runtime.getRuntime().exec("top -n 1");
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((result = br.readLine()) != null) {
            if (result.trim().length() < 1) {
                continue;
            } else {
                String[] CPUusr = result.split(",");
                String user = ((CPUusr[0].trim().split("\\s"))[1].toString());
                String system = ((CPUusr[1].trim().split("\\s"))[1].toString());
                return "1.用户进程：" + user + ", " + "2.系统进程：" + system + ".";
            }
        }
        return null;
    }

    /**
     * 获取cpu信息
     * 
     * @return
     */
    public static String fetchCPUInfo() {
        String result = null;
        try {
            String[] args = { "/system/bin/cat", "/proc/cpuinfo" };
            result = run(args, "/system/bin/");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 获取cpu信息
     * 
     * @param cmd
     * @param workdirectory
     * @return
     * @throws IOException
     */
    private static String run(String[] cmd, String workdirectory) throws IOException {
        String result = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            InputStream in = null;
            // 设置一个路径
            if (workdirectory != null) {
                builder.directory(new File(workdirectory));
                builder.redirectErrorStream(true);
                Process process = builder.start();
                in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1)
                    result = result + new String(re);
            }
            if (in != null) {
                in.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 是否有温度传感器
     */
    public static boolean getTemperatureSensor(Context context) {
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            if (mSensorManager.getSensorList(7).size() > 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取可用内存
     */
    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long surplusMemory = outInfo.availMem / (1024 * 1024); // 空闲内存
        // Log.i(TAG, "---可用内存 = " + surplusMemory);
        return surplusMemory;
    }

    /**
     * 获取总的内存
     * 
     * @return
     */
    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
        } catch (IOException e) {
        }
        // return Formatter.formatFileSize(context, initial_memory);
        return initial_memory / (1024 * 1024);
    }

    /**
     * 得到每个应用从开机到当前所产生的流量，没区分3G、wifi流量
     */
    public static void getPackageFlow(Context context) {

        // static long getMobileRxBytes() //获取通过Mobile连接收到的字节总数，不包含WiFi
        // static long getMobileRxPackets() //获取Mobile连接收到的数据包总数,不包含WiFi
        // static long getMobileTxBytes() //Mobile发送的总字节数
        // static long getMobileTxPackets() //Mobile发送的总数据包数
        // static long getTotalRxBytes() //获取总的接受字节数，包含Mobile和WiFi等
        // static long getTotalRxPackets() //总的接受数据包数，包含Mobile和WiFi等
        // static long getTotalTxBytes() //总的发送字节数，包含Mobile和WiFi等
        // static long getTotalTxPackets() //发送的总数据包数，包含Mobile和WiFi等
        // static long getUidRxBytes( int uid) //获取某个网络UID的接受字节数
        // static long getUidTxBytes( int uid) //获取某个网络UID的发送字节数

        // 获取所有安装在手机上的应用软件的信息 ,并且获取这些软件里面的权限信息
        // packageInfo和applicationInfo的区别
        // 统计2G或者3G的流量
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packinfos) {
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {
                        int uid = info.applicationInfo.uid;
                        long rx = TrafficStats.getUidRxBytes(uid);
                        long tx = TrafficStats.getUidTxBytes(uid);
                        if (rx < 0 || tx < 0) {
                            // Log.d(TAG, "没有产生流量的包：" + info.packageName);
                        } else {
                            // Log.d(TAG, "" + info.packageName + "的流量信息:");
                            // Log.d(TAG, "----------------" + "下载的流量" +
                            // Formatter.formatFileSize(context, rx));
                            // Log.d(TAG, "----------------" + "上传的流量" +
                            // Formatter.formatFileSize(context, tx));
                        }
                    }
                }
            }
        }
    }
}
