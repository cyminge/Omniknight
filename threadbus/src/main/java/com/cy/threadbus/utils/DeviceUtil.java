package com.cy.threadbus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cy.threadbus.SchedulerFactory;
import com.cy.threadbus.TaskRunnable;
import com.cy.threadbus.ThreadBus;

/**
 * Created by zhanmin on 2016/12/30.
 */

public final class DeviceUtil {

    private static final String TAG = "ThreadBus";
    private static final String DEVICE_PREF_NAME = "device_pref";
    private static final String DEVICE_CPU_COUNTS_KEY = "cpu_counts_key";
    private static volatile boolean isCheck = false;
    private static final int INVALID_CPU_COUNTS = -1;
    private static final int DEFAULT_CPU_COUNTS = 2;
    private static int mCpuCounts = INVALID_CPU_COUNTS;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        checkDeviceCPUCounts();
    }

    private static void checkDeviceCPUCounts() {
        ThreadBus.newAssembler().create(mCheckDeviceCpuRunnable)
                .scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
    }

    private static TaskRunnable mCheckDeviceCpuRunnable = new TaskRunnable("Check Device CPU Counts") {
        @Override
        public void runTask() {
            if (isCheck) {
                return;
            }
            isCheck = true;

            SharedPreferences sp = mContext.getSharedPreferences(DEVICE_PREF_NAME, Context.MODE_PRIVATE);
            int cpuCounts = sp.getInt(DEVICE_CPU_COUNTS_KEY, INVALID_CPU_COUNTS);
            if (cpuCounts != INVALID_CPU_COUNTS) {
                mCpuCounts = cpuCounts;
                isCheck = false;
                return;
            }

            saveDeviceCPUCounts();
            isCheck = false;
        }
    };

    private static void saveDeviceCPUCounts() {
        int cpuCounts = INVALID_CPU_COUNTS;
        try {
            cpuCounts = Runtime.getRuntime().availableProcessors();
        } catch (Exception e) {
            Log.w(TAG, "get cpu counts error !!");
        }

        if (cpuCounts < DEFAULT_CPU_COUNTS) {
            return;
        }
        mCpuCounts = cpuCounts;

        SharedPreferences sp = mContext.getSharedPreferences(DEVICE_PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(DEVICE_CPU_COUNTS_KEY, cpuCounts).apply();
    }

    public static int getDeviceCPUCounts() {
        return mCpuCounts == INVALID_CPU_COUNTS ? DEFAULT_CPU_COUNTS : mCpuCounts;
    }

}
