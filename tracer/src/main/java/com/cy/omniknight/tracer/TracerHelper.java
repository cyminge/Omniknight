package com.cy.omniknight.tracer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.cy.omniknight.tracer.util.FileUtils;

public class TracerHelper {
    
    public static final String BROADCAST_ACTION_LOG_EXPORT = "com.gionee.gamehall.log.export";
    public static final String BROADCAST_EXTRA_IS_EXPORT_LOG_KEY = "isExportLog";
    
    private static final String LOG_FILE = "gionee0123456789logdebug";
    
    private static boolean mIsNeedExportLog = false;
    
    private static LogExportReceiver mLogExportReceiver;
    
    static {
    	mIsNeedExportLog = FileUtils.isFileExisting(LOG_FILE);
    	Log.e("cyTest", "mIsNeedExportLog:"+mIsNeedExportLog);
    }
    
    public static void init(Context context) {
        mLogExportReceiver = new LogExportReceiver();
        IntentFilter filter = new IntentFilter(BROADCAST_ACTION_LOG_EXPORT);
        context.registerReceiver(mLogExportReceiver, filter);
    }

    public static boolean isNeedExportLog() {
        return mIsNeedExportLog;
    }
    
    private static class LogExportReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.e("cyTest", "接收到广播");
            if (!BROADCAST_ACTION_LOG_EXPORT.equals(intent.getAction())) {
            	return;
            }
            
            // 1. 开始用户上报日志开关
            mIsNeedExportLog = intent.getBooleanExtra(BROADCAST_EXTRA_IS_EXPORT_LOG_KEY, false);
            if(!mIsNeedExportLog) {
            	return;
            }
            
            // 2. 上报已缓存的日志
            Tracer.persisteLog(true, Tracer.REPORT_TYPE_MANUAL, "logup" + System.currentTimeMillis());
        }
    }
    
}
