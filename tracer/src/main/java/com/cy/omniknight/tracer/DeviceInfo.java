package com.cy.omniknight.tracer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

@SuppressLint("NewApi")
public class DeviceInfo {
	/* 设备产商 */
	private String manufature = "manufature";
	/* 硬件平台 */
	private String hardware = "hardware";
	/* Cpu信息 */
	private String cpuArch = "cpu_arch";
	/* 设备型号 */
	private String fingerPrint = "finger_print";
	/* 系统软件版本 */
	private String osVersion = "os_ver";
	/* SDK版本 android.os.Build.VERSION.SDK_INT */
	private String sdkVersion = "sdk_ver";
	/* 分辨率 */
	private String resolution = "resolution";
	/* 屏幕尺寸 */
	private String measurement = "measurement";
	/* JAVA虚拟机可用内存大小 */
	private String memCapacity = "mmem_cap";
	/* Rom大小 */
	private String mint_cap = "mint_cap";
	/* Sd卡大小（若有sd卡） */
	private String mext_cap = "mext_cap";
	/* Apk包名 */
	private String pkgName = "pkg_name";
	/* App程序名称 */
	private String appName = "app_name";
	/* Apk版本（对应svn） */
	private String apkVersion = "apk_ver";
	/* 安装方式（安装到rom/安装到sd卡） */
	private String installMode = "install_mode";

	private String getResolution(Context context) {
		int width = context.getResources().getDisplayMetrics().widthPixels;
		int height = context.getResources().getDisplayMetrics().heightPixels;
		return width + "x" + height;
	}
	
	private String getMeasurement(Context context) {
		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(dm.widthPixels/dm.xdpi,2);
		double y = Math.pow(dm.heightPixels/dm.ydpi,2);
		double screenInches = Math.sqrt(x+y);
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);            
		nf.setGroupingUsed(false);
//		Log.d("debug","Screen inches : " + screenInches);
		return nf.format(screenInches);
	}
	
	private int getTotalMemory() {
		Runtime info = Runtime.getRuntime();
		int memCapK = (int)info.totalMemory() / 1048576 ;
		return memCapK;
	}
	
	private long getTotalInteralStorage() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		return bytesAvailable / 1048576;
	}
	
	private long getTotalExtanalStorage() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		return bytesAvailable / 1048576;
	}
	
	private String getInstallMode(Context context) {
		String installLoc = "";
		ApplicationInfo ai = context.getApplicationInfo();
		
		if((ai.flags | ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
			installLoc = "external_storage";
		} else {
			installLoc = "internal_storage";
		}
		return installLoc;
	}
	
	public static String getApplicationVersion(Context context) {
		String version = "";
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			
			if(pInfo != null) {
				version = "" + pInfo.versionName + "-" + pInfo.versionCode;	// 版本名称-版本号
			}
		} catch (NameNotFoundException e) {
//			Tracer.debugException(e);
			e.printStackTrace();
		}
		return version;
	}

	public void parseDeviceInfo(Context context,JSONObject infoJson) {
		try {
			infoJson.putOpt(manufature, Build.MANUFACTURER);
			infoJson.putOpt(hardware, Build.HARDWARE);
			infoJson.putOpt(cpuArch, Build.CPU_ABI);
			infoJson.putOpt(fingerPrint, Build.FINGERPRINT);
			infoJson.putOpt(osVersion, Build.VERSION.RELEASE);
			infoJson.putOpt(sdkVersion, Build.VERSION.SDK_INT);
			infoJson.putOpt(resolution, getResolution(context));
			infoJson.putOpt(measurement, getMeasurement(context));
			infoJson.put(memCapacity, getTotalMemory());
			infoJson.putOpt(mint_cap, getTotalInteralStorage());
			infoJson.putOpt(mext_cap, getTotalExtanalStorage());
			infoJson.putOpt(pkgName, context.getPackageName());
//			infoJson.putOpt(appName, context.getString(R.string.app_name));
			infoJson.putOpt(apkVersion, getApplicationVersion(context));
			infoJson.putOpt(installMode, getInstallMode(context));
		} catch (JSONException e) {
//			Tracer.debugException(e);
			e.printStackTrace();
		}
	}
	
	public static String getDevIdString(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return Build.ID + "-" + tm.getDeviceId() + "-" + tm.getSimSerialNumber();
	}
}
