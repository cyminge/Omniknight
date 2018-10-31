package com.cy.omniknight.tools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Calendar;

/**
 * Created by JLB6088 on 2017/7/12.
 */

public final class TimeChangeController extends ReceiverStateController {

    private TimeChangeTracer mTimeChangeTracer;
    private static int[] mCurrentDateArray;

    public TimeChangeController(Context context) {
        super(context);
        mCurrentDateArray = getCurrentDate();
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public int[] getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        return new int[]{year, month, date};
    }

    @Override
    public void startTracking(StateChangedListener stateChangedListener) {
        mStateChangedListenerArray.add(stateChangedListener);

        if (null == mTimeChangeTracer) {
            mTimeChangeTracer = new TimeChangeTracer();
            mTimeChangeTracer.startTracking();
        }
    }

    @Override
    public void stopTracking(StateChangedListener stateChangedListener) {
        mStateChangedListenerArray.remove(stateChangedListener);
        if(mStateChangedListenerArray.size() == 0) {
            if (null != mTimeChangeTracer) {
                mTimeChangeTracer.stopTracking();
                mTimeChangeTracer = null;
            }
        }

    }

    public class TimeChangeTracer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onTimeChanged(intent.getAction());
        }

        private void startTracking() {
            IntentFilter filter = getFilter();
            mContext.registerReceiver(this, filter);
        }

        private IntentFilter getFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_DATE_CHANGED);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_LOCALE_CHANGED);
            filter.addAction(Intent.ACTION_TIME_TICK);

            return filter;
        }

        private void stopTracking() {
            mContext.unregisterReceiver(this);
        }
    }

    public void onTimeChanged(String action) {
        if(null == mStateChangedListenerArray || mStateChangedListenerArray.isEmpty()) {
            return;
        }

        boolean isDateChanged = false;
        int[] currentDateArray = getCurrentDate();
        if((mCurrentDateArray[2] != currentDateArray[2]) || (mCurrentDateArray[1] != currentDateArray[1]) || (mCurrentDateArray[0] != currentDateArray[0])) {
            mCurrentDateArray = currentDateArray;
            isDateChanged = true;
        }

        synchronized (mStateChangedListenerArray) {
            for(int i=0, size = mStateChangedListenerArray.size(); i< size; i++) {
                mStateChangedListenerArray.get(i).onStateChanged(ReceiverManager.CHANGE_TYPE_TIME, action, isDateChanged);
            }
        }
    }

    public static boolean isLocaleChanged(String action) {
        if(Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            return true;
        }

        return false;
    }

    public static boolean isTimeZoneChanged(String action) {
        if(Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
            return true;
        }

        return false;
    }

    public static boolean isTimeChanged(String action) {
        if(Intent.ACTION_TIME_TICK.equals(action) || Intent.ACTION_TIME_CHANGED.equals(action)) {
            return true;
        }

        return false;
    }

}
