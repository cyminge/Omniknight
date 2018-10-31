package com.cy.threadbus.scheduler.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by JLB6088 on 2017/7/17.
 */

final class CpuController extends StateController {
    private CpuCheckHelper mCpuCheckHelper;
    private CpuTracker mCpuTracker;

    public CpuController(StateChangedListener stateChangedListener, Context context) {
        super(stateChangedListener, context);

    }

    @Override
    public void maybeStartTracking(Ruler ruler) {
        if (ruler.isRequiresCpuChange()) {
            if (null == mCpuTracker) {
                mCpuCheckHelper = new CpuCheckHelper(this);
                mCpuTracker = new CpuTracker();
                mCpuTracker.startTracking();

            }

            synchronized (mTrackingRulers) {
                mTrackingRulers.add(ruler);
            }
        }
    }

    void updateCPUState(boolean isCpuBusy) {
        boolean isChanged = false;
        synchronized (mTrackingRulers) {
            for (int i = 0, size = mTrackingRulers.size(); i < size; i++) {
                isChanged |= mTrackingRulers.get(i).setIsCpuBusy(isCpuBusy);
            }

            if (isChanged) {
                mStateChangedListener.onStateChanged();
            }
        }
    }

    private final class CpuTracker extends BroadcastReceiver {

        private void startTracking() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            mContext.registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            onBatteryChange();
        }

        private void onBatteryChange() {
            mCpuCheckHelper.startCpuCheck();
        }

    }

}
