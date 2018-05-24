package com.cy.omniknight.tools;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {

	/**
	 * 测试完毕，删除本地文件
	 *
	 * @return
	 */
	public static void deleteFileFromDre(String dir, String filename) {
		File file = new File(dir, filename);
		if (file != null && file.exists()) {
			file.delete();
		}
	}

	/* Convert a object to string in base64 */
	public static String objectToString(Serializable obj) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(
					new Base64OutputStream(baos, Base64.NO_PADDING
							| Base64.NO_WRAP));
			oos.writeObject(obj);
			oos.close();
			return baos.toString("UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* convert a base64 String to Object */
	public static Object stringToObject(String str) {
		try {
			return new ObjectInputStream(new Base64InputStream(
					new ByteArrayInputStream(str.getBytes()), Base64.NO_PADDING
							| Base64.NO_WRAP)).readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * converts dip to px
	 */
	public static int dip2Px(Context context, float dip) {
		return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	/** * 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 特殊字符过滤
	 *
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String stringFilter(String str) throws PatternSyntaxException {
//        String regEx = "[/\\:*%?<>|\"\n\t]";  
		String regEx = "[/\\%<>|\"\n\t]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	/**
	 * 将字符串进行MD5加密
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

	/**
	 * 判断字符串中是否包含中文
	 *
	 * @param str
	 * @return
	 */
	public static boolean isContainGBK(String str) {
		int count = 0;
		String regEx = "[\\u4e00-\\u9fa5a]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				count = count + 1;
			}
		}
		System.out.println("共有 " + count + "个 ");
		return m.matches();
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatGivenDate(long givenDate, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String ret = sdf.format(new Date(givenDate));
		return ret;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatGivenDate(long givenDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ret = sdf.format(new Date(givenDate));
		return ret;
	}

	/**
	 * 把用秒数表示的时间转为年月日格式
	 *
	 * @param givenDate
	 * @return
	 */
	public static String getStrDate(long givenDate) {
		givenDate *= 1000;
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		String retStrFormatDate = sdFormat.format(givenDate);
		return retStrFormatDate;
	}

	/**
	 * 格式化当前时间
	 *
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatCurrDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String ret = sdf.format(new Date());
		return ret;
	}

	/**
	 * 格式化当前时间
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatCurrDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String ret = sdf.format(new Date());
		return ret;
	}

	/**
	 * 格式化当前日期（yyyy-MM-dd）
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatCurrDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String ret = sdf.format(new Date());
		return ret;
	}

	/**
	 * 创建文件夹
	 *
	 * @param path
	 */
	synchronized public static void createFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private static ConnectivityManager connectivity;

	/**
	 * 获取 ConnectivityManager
	 *
	 * @param context
	 * @return
	 */
	public static ConnectivityManager getConnectivityManager(Context context) {
		connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// connectivity = (ConnectivityManager)scontext.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivity;
	}

	/**
	 * 判断是否有可用网络
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		connectivity = getConnectivityManager(context);
		if (connectivity == null) {
			return false;
		}
		NetworkInfo[] info = connectivity.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; ++i) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取网络类型
	 *
	 * @param context
	 * @return
	 */
	public static String getNetWorkType(Context context) {
		connectivity = getConnectivityManager(context);
		if (connectivity == null) {
			return "-1";
		}
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		boolean connected = (info != null && info.isConnected());
		if (connected) {
			return String.valueOf(info.getType());
		}
		return "-1";
	}

	public static NetworkInfo getNetworkInfo(Context context) {
		connectivity = getConnectivityManager(context);
		if (connectivity == null) {
			return null;
		}
		try {
			return connectivity.getActiveNetworkInfo();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将两张位图拼接成一张
	 *
	 * @param first
	 * @param second
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable add2Bitmap(Context context, Bitmap first, Bitmap second) {
		int width = first.getWidth();
		int height = Math.max(first.getHeight(), second.getHeight());
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(first, 0, 0, null);
		canvas.drawBitmap(second, 4, 4, null);
		Drawable drawable = new BitmapDrawable(context.getResources(), result);
		return drawable;
	}

	/**
	 * 初始化PopupWindow
	 *
	 * @param context
	 * @param view 要显示的布局
	 * @param width PopupWindow宽度
	 * @param height PopupWindow高度
	 * @param drawable PopupWindow背景图片
	 * @param animStyle PopupWindow动画类型
	 * @return
	 */
	public static PopupWindow initPopupWindow(Context context, View view, int width, int height, Drawable drawable, int animStyle) {
		PopupWindow mPopupWindow = new PopupWindow(view, width, height);
		mPopupWindow.setBackgroundDrawable(drawable);
		mPopupWindow.setOutsideTouchable(true);
//		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.setAnimationStyle(animStyle);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		return mPopupWindow;
	}

	/**
	 * 初始化PopupWindow
	 *
	 * @param context
	 * @param view 要显示的布局
	 * @param width PopupWindow宽度
	 * @param height PopupWindow高度
	 * @param drawable PopupWindow背景图片
	 * @return
	 */
	public static PopupWindow initPopupWindow(Context context, View view, int width, int height, Drawable drawable) {
		PopupWindow mPopupWindow = new PopupWindow(view, width, height);
		mPopupWindow.setBackgroundDrawable(drawable);
		mPopupWindow.setOutsideTouchable(true);
//		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		return mPopupWindow;
	}

	/**
	 * 初始化PopupWindow
	 *
	 * @param context
	 * @param view 要显示的布局
	 * @param drawable PopupWindow背景图片
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static PopupWindow initPopupWindow(Context context, View view, Drawable drawable) {
		PopupWindow mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mPopupWindow.setBackgroundDrawable(drawable);
		mPopupWindow.setOutsideTouchable(true);
//		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		return mPopupWindow;
	}

	/**
	 * 显示PopupWindow
	 *
	 * @param view
	 */
	public static void showPopupWindowCenter(PopupWindow popupWindow, View view) {
		if (!popupWindow.isShowing()) {
			popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		}
	}

	/**
	 * 显示PopupWindow
	 *
	 * @param view
	 */
	public static void showPopupWindowBelowView(PopupWindow popupWindow, View view) {
		if (!popupWindow.isShowing()) {
			popupWindow.showAsDropDown(view, 0, 0);
		}
	}

	/**
	 * 解析html标签
	 * 这个方法不正确，别使用
	 *
	 * @param str
	 * @return
	 */
	public static String parseLabel2Null(String str) {
		str = str.replace("^</?[a-z]+>$", "");
		return str;
	}

	/**
	 * 匹配汉字数字字母
	 *
	 * @param str
	 * @return
	 */
	public static boolean isRegularMatch_Ch_num_Let(String str) {
		String regular = "^[\u4e00-\u9fa5a-zA-Z0-9]+$";
		Pattern p = Pattern.compile(regular);
		Matcher m = p.matcher(str);

		return m.matches();
	}

	/**
	 * 匹配字母斜杠
	 *
	 * @param str
	 * @return
	 */
	public static boolean isRegularMatch_Let_sprit(String str) {
		String regular = "^</?[a-z]+>$";
		Pattern p = Pattern.compile(regular);
		Matcher m = p.matcher(str);

		return m.matches();
	}

	/**
	 * Toast 显示
	 *
	 * @param context
	 * @param toast
	 * @param showStr
	 */
	public static Toast toast(Context context, Toast toast, String showStr) {
		if (toast != null) {
			toast.setText(showStr);
			toast.show();
		} else {
			toast = Toast.makeText(context, showStr, Toast.LENGTH_SHORT);
			toast.show();
		}

		return toast;
	}

	/**
	 * Toast 显示
	 *
	 * @param context
	 * @param toast
	 * @param showStr
	 * @param showTime
	 */
	public static Toast toast(Context context, Toast toast, String showStr, int showTime) {
		if (toast != null) {
			toast.setText(showStr);
			toast.show();
		} else {
			toast = Toast.makeText(context, showStr, showTime);
			toast.show();
		}

		return toast;
	}

	/**
	 * 程序是否前置显示
	 *
	 * @param context
	 * @return
	 */
	public static boolean isAppForeground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		String topAppPackage = foregroundTaskInfo.topActivity.getPackageName();

		if (topAppPackage != null && topAppPackage.contentEquals(context.getPackageName())) {
			return true;
		}
		return false;
	}

	public static void showToastForeground(final Context context, final String text, final int duration) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		String topAppPackage = foregroundTaskInfo.topActivity.getPackageName();

		if (topAppPackage != null && topAppPackage.contentEquals(context.getPackageName())) {
			if ((Looper.myLooper() != Looper.getMainLooper())) {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, text, duration).show();
					}
				});
			} else {
				Toast.makeText(context, text, duration).show();
			}
		}
	}

	/**
	 * 得到当前应用显示的activity
	 *
	 * @param context
	 * @return
	 */
	public static Activity getAppTopActivity(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		ComponentName cn = foregroundTaskInfo.topActivity;
		String topAppPackage = cn.getPackageName();

		if (null != topAppPackage && topAppPackage.contentEquals(context.getPackageName())) {
			try {
				return (Activity) Class.forName(cn.getClassName()).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static void showToastForeground(final Context context, final int resId, final int duration) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		String topAppPackage = foregroundTaskInfo.topActivity.getPackageName();

		if (topAppPackage != null && topAppPackage.contentEquals(context.getPackageName())) {
			if ((Looper.myLooper() != Looper.getMainLooper())) {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, resId, duration).show();
					}
				});
			} else {
				Toast.makeText(context, resId, duration).show();
			}
		}
	}

	/**
	 * Judge parameter string is a numeric string or not
	 *
	 * @param string
	 * @return
	 */
	public static boolean isNumeric(String string) {
		for (int i = string.length(); --i >= 0;) {
			if (!Character.isDigit(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Judge parameter string is empty, null or a numeric string
	 *
	 * @param
	 * @return
	 */
	public static boolean isNumericOrEmpty(String string) {
		if (string == null || string.trim().length() == 0) {
			return true;
		}

		return isNumeric(string);
	}

	public static String fixNullStringAndTrim(String string) {
		if (string == null) {
			return "";
		} else {
			return string.trim();
		}
	}

	public static boolean equalNoThrow(String str1, String str2) {
		if (str1 != null && str2 != null) {
			if (str1.contentEquals(str2)) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * judge string is null or empty
	 *
	 * @param string
	 * @return
	 */
	public static boolean emptyString(String string) {
		if (string == null || string.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static int parseIntNotEmpty(String numberString, int defValue) {
		if (numberString != null && numberString.trim().length() > 0) {
			return Integer.parseInt(numberString);
		}
		return defValue;
	}

	public static int parseIntNotEmpty(String numberString) {
		return parseIntNotEmpty(numberString, 0);
	}

	public static long parseLongNotEmpty(String numberString, long defValue) {
		if (numberString != null && numberString.trim().length() > 0) {
			return Long.parseLong(numberString);
		}
		return defValue;
	}

	public static long parseLongNotEmpty(String numberString) {
		return parseLongNotEmpty(numberString, 0);
	}

	public static boolean stringCompare(String str1, String str2) {
		boolean bRet = false;
		if (null == str1) {
			if (null == str2) {
				bRet = true;
			}
		}
		else {
			if (null != str2) {
				bRet = str1.equals(str2);
			}
		}
		return bRet;
	}

	public static boolean integerCompare(Integer integer1, Integer integer2) {
		boolean bRet = false;
		if (null == integer1) {
			if (null == integer2) {
				bRet = true;
			}
		}
		else {
			if (null != integer2) {
				bRet = (0 == integer1.compareTo(integer2));
			}
		}
		return bRet;
	}

	public static boolean longCompare(Long integer1, Long integer2) {
		boolean bRet = false;
		if (null == integer1) {
			if (null == integer2) {
				bRet = true;
			}
		}
		else {
			if (null != integer2) {
				bRet = (0 == integer1.compareTo(integer2));
			}
		}
		return bRet;
	}

	public static String commonDecode(String encrypted) {
		String decoded = "";
		try {
			String base64Dec = new String(Base64.decode(encrypted, Base64.NO_WRAP | Base64.NO_PADDING));
			decoded = URLDecoder.decode(base64Dec, "UTF-8");
		} catch (UnsupportedEncodingException e) {
//			Tracer.debugException(e);
		} catch (Exception e) {
//			Tracer.debugException(e);
		}
		return decoded;
	}

	/**
	 * modify by zhanmin 2013.10.24
	 *
	 * @param srcString
	 * @return
	 */
	public static String commonEncode(String srcString) {
		try {
			String urlEncoded = URLEncoder.encode(srcString, "UTF-8");
			String base64Enc = Base64.encodeToString(urlEncoded.getBytes(), Base64.NO_WRAP | Base64.NO_PADDING);
			return base64Enc;
		} catch (UnsupportedEncodingException e) {
//			Tracer.debugException(e);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * @param srcString
	 * @return
	 */
	public static String commonEncodeBase64(String srcString) {
		try {
			String base64Enc = Base64.encodeToString(srcString.getBytes(), Base64.NO_WRAP | Base64.NO_PADDING);
//			String urlEncoded = URLEncoder.encode(base64Enc, "UTF-8");
			return base64Enc;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取带参数的字符串资源
	 *
	 * @param context
	 * @param params
	 * @return
	 */
	public static String getResourceStringWithParam(Context context, int id, Object... params) {
		String formatString = context.getResources().getString(id);
		return String.format(formatString, params);
	}

	/**
	 * 解析转义字符为符号
	 *
	 * @param str
	 * @return
	 */
	public static String parseESC2Symbol(String str) {
		str = str.replace("&amp;", "&");
		str = str.replace("&lt;", "<");
		str = str.replace("&gt;", ">");
		str = str.replace("&quot;", "\"");
		str = str.replace("&nbsp;", " ");
		str = str.replace("&copy;", "©");
		str = str.replace("&reg;", "®");
		str = str.replace("&apos;", "'");
		return str;
	}

	public static String getStringNotNull(String str) {
		if (null == str) {
			str = "";
		}
		return str;
	}

	/**
	 * 获取软件版本号
	 *
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context) {
		String clientVersion = null;
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (pInfo != null) {
				clientVersion = String.valueOf(pInfo.versionCode);
			}
		} catch (NameNotFoundException e) {
			clientVersion = null;
		}
		return clientVersion;
	}

	/**
	 * 获取设备型号
	 *
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public static String getDeviceModel(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceSoftwareVersion();
	}

	/**
	 * 获取imei
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("MissingPermission")
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

}
