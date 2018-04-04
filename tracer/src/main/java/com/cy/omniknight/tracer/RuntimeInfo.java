package com.cy.omniknight.tracer;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RuntimeInfo {
//	private static final int CURRENT_DROP_FRAMES = 2;
	private static final int CURRENT_DROP_FRAMES = 0;

	/* 时间 */
	private static String time = "time";
	/* 可用外部存储空间 */
	private static String mext_aval = "mext_aval";
	/* 可用内部存储空间 */
	private static String mint_aval = "mint_aval";
	/* 应用程序占用内存空间 */
	private static String kmem_aval = "kmem_aval";
	/* 应用程序可用内存大小 */
	private static String kmem_used = "kmem_used";
	/* 应用程序线程栈信息 */
	private static String threads_info = "threads_info";
	/* 线程名字 */
	private static String thread_name = "thread_name";
	/* 栈信息 */
	private static String stack_info = "stack_info";
	/* 栈帧函数名 */
	private static String frame_sig = "frame_sig";
	/* 应用程序当前界面 */
	private static String curr_comp = "curr_comp";
	/* 当前exception名字 */
	private static String excep = "excep";
	/* 当前线程栈所在文件 */
	private static String file = "file";
	/* 当前线程栈所在行数 */
	private static String line = "line";
	/* 应用程序线程栈信息 */
	private static String extern_info = "extern_info";

	private static int getKAvailableMemory() {
		Runtime mRuntime = Runtime.getRuntime();
		return (int) mRuntime.freeMemory() / 1024;
	}

	private static int getKUsedMemory() {
		Runtime mRuntime = Runtime.getRuntime();
		int usedMemory = (int) ((mRuntime.totalMemory() - mRuntime.freeMemory()) / 1024);
		return usedMemory;
	}

	private static long getMAvailableInternalStorage(Context context) {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
		return bytesAvailable >> 20;
	}

	private static long getMAvailableExternalStorage(Context context) {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
		return bytesAvailable >> 20;
	}

	private static String getTopActivityName(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		return cn.getClassName();
	}

	private static JSONArray getThreadsInfo() throws JSONException {
		JSONArray threasArray = new JSONArray();

		int activeCount = Thread.activeCount();
		Thread[] threads = new Thread[activeCount];
		Thread.enumerate(threads);

		for (int i = 0; i < threads.length; i++) {
			JSONArray frameArray = new JSONArray();
			JSONObject threadObj = new JSONObject();
			int dropFrames = 0;

			if (Thread.currentThread().getId() == threads[i].getId()) {
				dropFrames = CURRENT_DROP_FRAMES;
			}

			StackTraceElement[] stackFrames = threads[i].getStackTrace();
			for (int j = stackFrames.length - 1 - dropFrames; j >= 0; j--) {
				JSONObject frameObj = new JSONObject();
				frameObj.put(frame_sig, stackFrames[j].getClassName() + "." + stackFrames[j].getMethodName());
				frameArray.put(frameObj);
			}
			threadObj.put(thread_name, threads[i].getName());
			threadObj.put(stack_info, frameArray);
			threasArray.put(threadObj);
		}
		return threasArray;
	}

	private static JSONArray getThowableInfo(Thread thread, Throwable throwable) throws JSONException {
		JSONArray threasArray = new JSONArray();
		JSONArray frameArray = new JSONArray();
		JSONObject threadObj = new JSONObject();
		StackTraceElement[] stackFrames = throwable.getStackTrace();
		for (int j = stackFrames.length - 1; j >= 0; j--) {
			JSONObject frameObj = new JSONObject();
			frameObj.put(frame_sig, stackFrames[j].getClassName() + "." + stackFrames[j].getMethodName());
			frameObj.put(file, stackFrames[j].getFileName());
			frameObj.put(line, stackFrames[j].getLineNumber());
			frameArray.put(frameObj);
		}
		threadObj.put(excep, throwable.getMessage());
		if (null != thread) {
			threadObj.put(thread_name, thread.getName());
		}
		else {
			threadObj.put(thread_name, "null");
		}
		threadObj.put(stack_info, frameArray);
		threasArray.put(threadObj);
		return threasArray;
	}

	@SuppressLint("SimpleDateFormat")
	public static JSONObject parseRuntimeInfo(Context context, JSONObject jsonObject, Thread thread, Throwable throwable) throws JSONException {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String dateString = sDateFormat.format(new Date());
		jsonObject.put(time, dateString);
		jsonObject.put(kmem_aval, getKAvailableMemory() + "k");
		jsonObject.put(kmem_used, getKUsedMemory() + "k");
		jsonObject.put(mext_aval, getMAvailableExternalStorage(context) + "m");
		jsonObject.put(mint_aval, getMAvailableInternalStorage(context) + "m");
		jsonObject.put(curr_comp, getTopActivityName(context));

		jsonObject.put(threads_info, getThowableInfo(thread, throwable));
		getExternInfo(jsonObject, thread, throwable);
		
		return null;
	}

	public static JSONObject getExternInfo(JSONObject jsonObject, Thread thread, Throwable throwable) {
		if (null != jsonObject) {
			if (throwable instanceof ExceptionCaughted) {
				try {
					ExceptionCaughted exceptionCaughted = (ExceptionCaughted) throwable;
					if (null != exceptionCaughted.mStrExternInfo) {
						jsonObject.put(extern_info, exceptionCaughted.mStrExternInfo);
					}
				} catch (JSONException e) {
					Tracer.handleCatchedException("cyTest", throwable);
				} catch (Exception e) {
					Tracer.handleCatchedException("cyTest", throwable);
				}
			}
		}
		return jsonObject;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static JSONObject parseRuntimeInfo(Context context, JSONObject jsonObject) throws JSONException {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String dateString = sDateFormat.format(new Date());

		jsonObject.put(time, dateString);
		jsonObject.put(kmem_aval, getKAvailableMemory() + "k");
		jsonObject.put(kmem_used, getKUsedMemory() + "k");
		jsonObject.put(mext_aval, getMAvailableExternalStorage(context) + "m");
		jsonObject.put(mint_aval, getMAvailableInternalStorage(context) + "m");
		jsonObject.put(curr_comp, getTopActivityName(context));
		jsonObject.put(threads_info, getThreadsInfo());
		return jsonObject;
	}
}
