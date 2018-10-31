package com.cy.omniknight.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class IntervalAlarm extends BroadcastReceiver {

    private static final int ALARM_ID = 107;

    public static final String ACTION = "gn.com.android.gamehall.interval_alarm";
    private static Context sContext;
    private static PendingIntent sPendingIntent;
    private static Intent sIntent;

    @SuppressLint("NewApi")
    public static void initial(Context context) {
        sContext = context.getApplicationContext();
        sIntent = new Intent(context, IntervalAlarm.class);
        if (null == sPendingIntent) {
            sPendingIntent = PendingIntent.getBroadcast(sContext, ALARM_ID, sIntent, 0);
        }

        AlarmManager am = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sPendingIntent);
//		long triggerAtTime = SystemClock.elapsedRealtime();
        long triggerAtTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC, triggerAtTime + getInterval(), sPendingIntent);
        } else {
            am.setRepeating(AlarmManager.RTC, triggerAtTime, getInterval(), sPendingIntent);
        }

    }

    @TargetApi(19)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            resetPendingIntent();
        } else {
        }

        doAlarmWakeup(context);
    }

    @TargetApi(19)
    private static void resetPendingIntent() {
        AlarmManager am = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
//		long triggerAtTime = SystemClock.elapsedRealtime();
        long triggerAtTime = System.currentTimeMillis();
        if (null == sPendingIntent) {
            sPendingIntent = PendingIntent.getBroadcast(sContext, ALARM_ID, sIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        am.cancel(sPendingIntent);
        am.setExact(AlarmManager.RTC, triggerAtTime + getInterval(), sPendingIntent);
    }

    private static int getInterval() {
        return 1000*60*60;
    }

    /**
     * 通过alarm广播唤醒
     * @param context
     */
    private void doAlarmWakeup(Context context) {
        // TODO doSomething
    }
}
