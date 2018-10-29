package com.cy.omniknight.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/1
 *     desc  : utils about device
 * </pre>
 */
public final class DeviceUtils {

    private DeviceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether device is rooted.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether ADB is enabled.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAdbEnabled() {
        return Settings.Secure.getInt(
                Utils.getApp().getContentResolver(),
                Settings.Global.ADB_ENABLED, 0
        ) > 0;
    }

    /**
     * Return the version name of device's system.
     *
     * @return the version name of device's system
     */
    public static String getSDKVersionName() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    public static int getSDKVersionCode() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        String id = Settings.Secure.getString(
                Utils.getApp().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return id == null ? "" : id;
    }

    /**
     * Return the MAC address.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static String getMacAddress() {
        return getMacAddress((String[]) null);
    }

    /**
     * Return the MAC address.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, INTERNET})
    public static String getMacAddress(final String... excepts) {
        String macAddress = getMacAddressByWifiInfo();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByNetworkInterface();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByInetAddress();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        return "";
    }

    private static boolean isAddressNotInExcepts(final String address, final String... excepts) {
        if (excepts == null || excepts.length == 0) {
            return !"02:00:00:00:00:00".equals(address);
        }
        for (String filter : excepts) {
            if (address.equals(filter)) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    private static String getMacAddressByWifiInfo() {
        try {
            Context context = Utils.getApp().getApplicationContext();
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByNetworkInterface() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni == null || !ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02x:", b));
                    }
                    return sb.substring(0, sb.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByInetAddress() {
        try {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress != null) {
                NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddress);
                if (ni != null) {
                    byte[] macBytes = ni.getHardwareAddress();
                    if (macBytes != null && macBytes.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : macBytes) {
                            sb.append(String.format("%02x:", b));
                        }
                        return sb.substring(0, sb.length() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(':') < 0) return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    String address = result.successMsg;
                    if (address != null && address.length() > 0) {
                        return address;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }

    /**
     * Return the manufacturer of the product/hardware.
     * <p>e.g. Xiaomi</p>
     *
     * @return the manufacturer of the product/hardware
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * Return the model of device.
     * <p>e.g. MI2SC</p>
     *
     * @return the model of device
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * Return an ordered list of ABIs supported by this device. The most preferred ABI is the first
     * element in the list.
     *
     * @return an ordered list of ABIs supported by this device
     */
    public static String[] getABIs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS;
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
            return new String[]{Build.CPU_ABI};
        }
    }

    /**
     * Shutdown the device
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.SHUTDOWN/>}
     * in manifest.</p>
     */
    public static void shutdown() {
        ShellUtils.execCmd("reboot -p", true);
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        Utils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * Reboot the device.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"} in manifest.</p>
     */
    public static void reboot() {
        ShellUtils.execCmd("reboot", true);
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        Utils.getApp().sendBroadcast(intent);
    }

    /**
     * Reboot the device.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.REBOOT" />}</p>
     *
     * @param reason code to pass to the kernel (e.g., "recovery") to
     *               request special boot modes, or null.
     */
    public static void reboot(final String reason) {
        PowerManager pm = (PowerManager) Utils.getApp().getSystemService(Context.POWER_SERVICE);
        //noinspection ConstantConditions
        pm.reboot(reason);
    }

    /**
     * Reboot the device to recovery.
     * <p>Requires root permission.</p>
     */
    public static void reboot2Recovery() {
        ShellUtils.execCmd("reboot recovery", true);
    }

    /**
     * Reboot the device to bootloader.
     * <p>Requires root permission.</p>
     */
    public static void reboot2Bootloader() {
        ShellUtils.execCmd("reboot bootloader", true);
    }

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
        } catch (PackageManager.NameNotFoundException e) {
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
        } catch (PackageManager.NameNotFoundException e) {
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

    /***
     * 获取Android Linux内核版本信息
     */
    public void getLinuxKernalInfo() {
        Process process = null;
        String mLinuxKernal = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
                // result += "\n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (result != "") {
            String Keyword = "version ";
            int index = result.indexOf(Keyword);
            Log.v("cyTest", "----"+result);
            line = result.substring(index + Keyword.length());
            index = line.indexOf(" ");
            // tv01.setText(line.substring(0,index));
            mLinuxKernal = line.substring(0, index);

            Log.d("cyTest", "----Linux Kernal is : " + mLinuxKernal);
        }
    }
}
