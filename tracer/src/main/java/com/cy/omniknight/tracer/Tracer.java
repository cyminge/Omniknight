package com.cy.omniknight.tracer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import com.cy.omniknight.tracer.config.DefaultMaxLogFrameImpl;
import com.cy.omniknight.tracer.config.TraceConfiguration;
import com.cy.omniknight.tracer.export.IMaxLogFrame;
import com.cy.omniknight.tracer.util.GlobalListenerManager;
import com.cy.omniknight.tracer.util.StorageUtils;
import com.cy.omniknight.tracer.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * mDebugLevel 用于修改上报阀值 mGlobType取值如下 GLOB_DEVLOP = 0; // 开发时，log打印到logcat GLOB_DEBUG = 1; //
 * 内部测试，log既打到logcat，也上报日志 GLOB_RELS = 2; // release版本，只上报
 * 
 * @author cy
 * @time 2014-4-26
 */
public class Tracer {
    public static final String PERSI_BRC = "com.veclink.log.persistent";

    private static int MAX_CACHE_COUNT = 512; // 打印缓冲最大条数
    private static int MAX_CACHE_CAPACITY = (MAX_CACHE_COUNT + 64) * 512; // 每条打印预留512大小，并预留异常打印空间

    public static final String REPORT_TYPE_AUTO = "auto";
    public static final String REPORT_TYPE_MANUAL = "manual";
    /* 将log信息写入本地文件的缓冲，默认缓冲大小为20k */
    private static StringBuffer logStringBuilder;
    private static int logStringCount = 0;

    private static WifiConnectChangedReceiver wifiReceiver = null;
    private static PersistentReceiver mPersistentReceiver;

    public static Boolean isUpLoad = true;
    public static Boolean isSaveFile = true;

    private static TraceConfiguration mTraceConfiguration;
    private static IMaxLogFrame mOnMaxLogFrame;
    
    private static Context mContext;
    
    // 简单防止多线程重复调用上报和保存文件
    private static AtomicBoolean persisting = new AtomicBoolean(false);

    public static void init(Context context) {
        init(context, null, null);
    }

    public static void init(Context context, TraceConfiguration config) {
        init(context, config, null);
    }

    public static void init(Context context, IMaxLogFrame onMaxLogFrame) {
        init(context, null, onMaxLogFrame);
    }

    public static void init(Context context, TraceConfiguration config, IMaxLogFrame maxLogFrame) {
    	mContext = context;
    	TracerHelper.init(context);
        resetCache();
        
        if (null == config) {
            TraceConfiguration.Builder builder = new TraceConfiguration.Builder(context);
            builder.isPrintLogCat(true);
            config = builder.build();
        }
        mTraceConfiguration = config;
        
        if (maxLogFrame == null) {
        	maxLogFrame = new DefaultMaxLogFrameImpl();
        }
    	mOnMaxLogFrame = maxLogFrame;

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiReceiver = new WifiConnectChangedReceiver();
        context.getApplicationContext().registerReceiver(wifiReceiver, filter);

        mPersistentReceiver = new PersistentReceiver();
        filter = new IntentFilter(PERSI_BRC);
        context.getApplicationContext().registerReceiver(mPersistentReceiver, filter);

        Log.d("Tracer", "init done !");
    }

    public static void deInit() {
    	mContext.getApplicationContext().unregisterReceiver(wifiReceiver);
        logStringBuilder = null;
        logStringCount = 0;
    }

    static public class WifiConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    State state = networkInfo.getState();
                    boolean isConnected = (state == State.CONNECTED);
                    if (isConnected) {
                    }
                }
            }
        }
    }

    static public class PersistentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PERSI_BRC.equals(intent.getAction())) {
                if (!persisteLog(true, REPORT_TYPE_MANUAL, "logup" + System.currentTimeMillis())) {
                    if (persisting.get()) {
                        GlobalListenerManager.onEvent(GlobalListenerManager.REPORT_DONW_EVENT, new ReportDoneEvent(false, -2)); // 正在上传
                    } else {
                    	GlobalListenerManager.onEvent(GlobalListenerManager.REPORT_DONW_EVENT, new ReportDoneEvent(false, -3));
                    }
                }
            }
        }
    }

    public static void v(String tag, String msg) {
    	log(tag, msg, TraceConfiguration.LOG_LEVEL_VERBOSE);
    }

    public static void d(String tag, String msg) {
    	log(tag, msg, TraceConfiguration.LOG_LEVEL_DEBUG);
    }

    public static void i(String tag, String msg) {
    	log(tag, msg, TraceConfiguration.LOG_LEVEL_INFO);
    }

    public static void w(String tag, String msg) {
    	log(tag, msg, TraceConfiguration.LOG_LEVEL_WARN);
    }

    public static void e(String tag, String msg) {
    	log(tag, msg, TraceConfiguration.LOG_LEVEL_ERROR);
    }
    
    private static void log(String tag, String msg, int logLevel) {
    	if (TracerHelper.isNeedExportLog() || mTraceConfiguration.isPrintLogCat()) {
    		if(logLevel == TraceConfiguration.LOG_LEVEL_VERBOSE) {
    			Log.v(tag, msg == null ? "null" : msg);
        	} else if(logLevel == TraceConfiguration.LOG_LEVEL_DEBUG) {
        		Log.d(tag, msg == null ? "null" : msg);
        	} else if(logLevel == TraceConfiguration.LOG_LEVEL_INFO) {
        		Log.i(tag, msg == null ? "null" : msg);
        	} else if(logLevel == TraceConfiguration.LOG_LEVEL_WARN) {
        		Log.w(tag, msg == null ? "null" : msg);
        	} else if(logLevel == TraceConfiguration.LOG_LEVEL_ERROR) {
        		Log.e(tag, msg == null ? "null" : msg);
        	}
    	}
    	
    	if (logLevel < mTraceConfiguration.getSaveLevel()) {
        	return;
        }
    	
    	appendSimpleLog(TraceConfiguration.convertLevel(logLevel), tag, msg);
        
        if (logLevel >= mTraceConfiguration.getPersistTriggerLevel() || logStringCount >= MAX_CACHE_COUNT) {
            persisteLog(false);
        }
    }
    
    public static void handleCatchedException(String tag, Throwable throwable) {
    	if(null == throwable) {
    		return;
		}
    	
    	if (TracerHelper.isNeedExportLog() || mTraceConfiguration.isPrintLogCat()) {
			throwable.printStackTrace();
    	}
    	
    	handleUncatchException(Thread.currentThread(), throwable, null);
    }
    
    public static void handleUncatchException(Thread thread, Throwable throwable) {
    	if(null == throwable) {
    		return;
    	}
    	
    	throwable.printStackTrace();
    	handleUncatchException(thread, throwable, null);
    }

    public static boolean handleUncatchException(Thread thread, Throwable throwable, String exterInfo) {
        if (TraceConfiguration.LOG_LEVEL_UNCATCHED < mTraceConfiguration.getSaveLevel()) {
        	return false;
        }
        
        String exceptionInfo = getStackTrace(thread, throwable);
        String threadName = getThreadName(thread);
        
        appendSimpleLog(TraceConfiguration.LOG_TYPE_UNCATCHED, threadName, "********** Uncatched Exception **********" + "\n"
                + exceptionInfo);
        
        return persisteLog(true);
    }
    
    private static String getThreadName(Thread thread) {
    	String threadName = "uncatch thread info";
        try {
            if (null != thread) {
                threadName = thread.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            threadName += e.toString();
        }
        
        return threadName;
    }
    
    private static String getStackTrace(Thread thread, Throwable throwable) {
        String exceptionInfo = "printStackTrace err:";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = null;
        try {
            ps = new PrintStream(baos);
            throwable.printStackTrace(ps);
            ps.flush();
            baos.flush();
            exceptionInfo = baos.toString(); // 输出异常打印信息
        } catch (Exception e) {
            e.printStackTrace();
            exceptionInfo += e.toString();
        } finally {
        	String error = Utils.closeIO(ps);
        	exceptionInfo += error == null ? "" : error;
        	error = Utils.closeIO(baos);
        	exceptionInfo += error == null ? "" : error;
        }
        
        return exceptionInfo;
    }
    
    private static StringBuffer resetCache() {
        StringBuffer oldStringBuffer = logStringBuilder;
        logStringBuilder = new StringBuffer(MAX_CACHE_CAPACITY);
        logStringCount = 0;
        return oldStringBuffer;
    }

    public static synchronized boolean persisteLog(boolean report, String reportType, String logName) {
        if (persisting.get()) {
            return false;
        }
        persisting.set(true);
        
        StringBuffer persBuffer = resetCache();
        if (persBuffer == null || persBuffer.length() <= 10 || mOnMaxLogFrame == null) {
        	persisting.set(false);
            return false;
        }
        
        String logStr = persBuffer.toString();
        if(mTraceConfiguration.isSaveFile()) {
        	mOnMaxLogFrame.saveLogToFile(logStr);
        }
        
        if(report && mTraceConfiguration.isReportLog()) {
        	mOnMaxLogFrame.reportToServer(reportType, logName, logStr);
        }
        
        return true;
    }

    public static boolean persisteLog(boolean report) {
        return persisteLog(report, REPORT_TYPE_AUTO, "log" + System.currentTimeMillis());
    }

    /**
     * 将log添加到文件缓冲
     * 
     * @param level
     *            log级别简写
     * @param tag
     * @param msg
     */
    @SuppressLint("SimpleDateFormat") 
    public static void appendSimpleLog(String level, String tag, String msg) {
        String timeStr = new SimpleDateFormat("HH:mm:ss").format(new Date());
        logStringBuilder.append(String.format("%s %s\\%-20s\t%s\n", timeStr, level, tag, msg));
        logStringCount++;
    }

    /*
    public static void writeThowableInfo(Throwable throwable) {
    	StringBuffer excep = new StringBuffer();
    	excep.append(">>> " + throwable.getMessage() + "\n");
    	StackTraceElement[] stackFrames = throwable.getStackTrace();
    	for (int j = stackFrames.length - 1; j >= 0; j--) {
    		String error = stackFrames[j].getFileName() + ":" + stackFrames[j].getClassName() + "." + stackFrames[j].getMethodName() + ":" + stackFrames[j].getLineNumber() + "\n";
    		excep.append(error);
    	}
    	writeToFile(excep.toString());
    }
    */

    public static String getLogPath() {
        String dir = StorageUtils.getOwnCacheDirectory(mContext, "trace").getPath();
//		return populatePath(dir, TimeRandomString.generateSequenceNo() + ".log");
//		return populatePath(dir, StringUtil.formatCurrDate("yyyy_MM_dd_HH")+".log");
        return populatePath(dir, Utils.formatCurrDate("yyyy_MM_dd") + ".log");
    }

    public static String populatePath(String... spliteds) {
        if (spliteds.length <= 0) {
            return "";
        }
        String fullPath = spliteds[0];
        for (int i = 1; i < spliteds.length; i++) {
            fullPath += "/";
            fullPath += spliteds[i];
        }
        return fullPath;
    }
    
}
