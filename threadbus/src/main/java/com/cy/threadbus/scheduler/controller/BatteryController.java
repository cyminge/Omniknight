package com.cy.threadbus.scheduler.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.cy.threadbus.TaskRunnable;

/**
 * Created by JLB6088 on 2017/7/17.
 */

final class BatteryController extends StateController {

    private static final int LOW_BATTERY_VALUES = 15;
    private BatteryTracker mBatteryTracker;

    public BatteryController(StateChangedListener stateChangedListener, Context context) {
        super(stateChangedListener, context);
    }

    @Override
    public void maybeStartTracking(Ruler ruler) {
        if (ruler.isRequiresBatteryChange()) {
            if(null == mBatteryTracker) {
                mBatteryTracker = new BatteryTracker();
                mBatteryTracker.startTracking();
            }

            synchronized (mTrackingRulers) {
                mTrackingRulers.add(ruler);
            }
        }
    }

    private final class BatteryTracker extends BroadcastReceiver {

        private void startTracking() {
            Log.d("cyTest", "BatteryController startTracking");
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            mContext.registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("cyTest", "Battery State Changed");
            onBatteryChange(intent);
        }

        private void onBatteryChange(Intent intent) {
            RulerService.getSubThreadHandler().post(new BatteryChangedRunnable(intent));
        }

        private class BatteryChangedRunnable extends TaskRunnable {
            private Intent mIntent;

            private BatteryChangedRunnable(Intent intent) {
                super("Check&Update Battery State");
                mIntent = intent;
            }

            @Override
            public void runTask() {
                if (mIntent == null) {
                    return;
                }
                updateBatteryState(isLowBattery(mIntent));
            }
        }

        private boolean isLowBattery(Intent intent) {
            // 获取当前电量
            int current = intent.getIntExtra("level", 0);
            // 电量的总刻度
            int total = intent.getIntExtra("scale", 100);
            int batteryLevelPercent = current * 100 / total;
            return batteryLevelPercent <= LOW_BATTERY_VALUES && batteryLevelPercent > 0;
        }

        private void updateBatteryState(boolean lowBattery) {
            boolean isChanged = false;
            synchronized (mTrackingRulers) {
                for (int i = 0, size = mTrackingRulers.size(); i < size; i++) {
                    isChanged |= mTrackingRulers.get(i).setIsLowBattery(lowBattery);
                }

                if (isChanged) {
                    mStateChangedListener.onStateChanged();
                }
            }
        }

    }

}
