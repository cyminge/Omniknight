package com.cy.threadbus.scheduler.controller;

import android.util.Log;

import com.cy.threadbus.SchedulerFactory;
import com.cy.threadbus.TaskRunnable;
import com.cy.threadbus.ThreadBus;
import com.cy.threadbus.utils.Utils;

/**
 * Created by JLB6088 on 2017/7/7.
 */

final class CpuCheckHelper {

    private static final int CPU_USAGE_RATE_THRESHOLD = 50;
    private static final int MAX_CHECK_TIMES = 10;
    private int mCheckTimes;
    private volatile boolean isChecking;
    private int[] mCpuUsageRates = new int[MAX_CHECK_TIMES];
    private int cpuAbnormalTimes;
    private static final int DEFAULT_CPU_ABNORMAL_TIMES = 5;

    private CpuController mCpuController;

    public CpuCheckHelper(CpuController cpuController) {
        mCpuController = cpuController;
    }

    public void startCpuCheck() {
        if (isChecking) {
            return;
        }
        isChecking = true;
        ThreadBus.newAssembler().create(mTaskRunnable).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
    }

    private TaskRunnable mTaskRunnable = new TaskRunnable("Check&Update CPU State") {
        @Override
        public void runTask() {
            checkCpu();
        }
    };

    private void checkCpu() {
        while (true) {
            if (mCheckTimes >= MAX_CHECK_TIMES) {
                updateCPUState();
                isChecking = false;
                return;
            }
            long start = System.currentTimeMillis();
            String desc = Utils.getCPURateDesc();
            Log.d("cyTest", "checkCpu spend time:" + (System.currentTimeMillis() - start) + ", desc:" + desc);

            mCheckTimes++;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }

        }
    }

    private boolean updateCPUState() {
        for (int rate : mCpuUsageRates) {
            if (rate >= CPU_USAGE_RATE_THRESHOLD) {
                cpuAbnormalTimes++;
            }
        }

        if (cpuAbnormalTimes >= DEFAULT_CPU_ABNORMAL_TIMES) {
            mCpuController.updateCPUState(true);
            return true;
        }

        mCpuController.updateCPUState(false);
        return false;
    }

    // public static final int PROC_TERM_MASK = 0xff;
//    public static final int PROC_ZERO_TERM = 0;
//    public static final int PROC_SPACE_TERM = (int) ' ';
//    public static final int PROC_TAB_TERM = (int) '\t';
//    public static final int PROC_COMBINE = 0x100;
//    public static final int PROC_PARENS = 0x200;
//    public static final int PROC_QUOTES = 0x400;
//    public static final int PROC_CHAR = 0x800;
//    public static final int PROC_OUT_STRING = 0x1000;
//    public static final int PROC_OUT_LONG = 0x2000;
//    public static final int PROC_OUT_FLOAT = 0x4000;
//
//    private final long[] mSystemCpuData = new long[7];
//
//    private static final int[] PROCESS_STATS_FORMAT = new int[] {PROC_SPACE_TERM,
//            PROC_SPACE_TERM | PROC_PARENS, PROC_SPACE_TERM, PROC_SPACE_TERM, PROC_SPACE_TERM, PROC_SPACE_TERM,
//            PROC_SPACE_TERM, PROC_SPACE_TERM, PROC_SPACE_TERM, PROC_SPACE_TERM | PROC_OUT_LONG, // 10: minor faults
//            PROC_SPACE_TERM, PROC_SPACE_TERM | PROC_OUT_LONG, // 12: major faults
//            PROC_SPACE_TERM, PROC_SPACE_TERM | PROC_OUT_LONG, // 14: utime
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 15: stime
//    };
//
//    private static final int[] SYSTEM_CPU_FORMAT = new int[] {PROC_SPACE_TERM | PROC_COMBINE,
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 1: user time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 2: nice time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 3: sys time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 4: idle time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 5: iowait time
//            PROC_SPACE_TERM | PROC_OUT_LONG, // 6: irq time
//            PROC_SPACE_TERM | PROC_OUT_LONG // 7: softirq time
//    };
//
//    static final int PROCESS_STAT_MINOR_FAULTS = 0;
//    static final int PROCESS_STAT_MAJOR_FAULTS = 1;
//    static final int PROCESS_STAT_UTIME = 2;
//    static final int PROCESS_STAT_STIME = 3;
//
//    private long mBaseUserTime;
//    private long mBaseSystemTime;
//    private long mBaseIoWaitTime;
//    private long mBaseIrqTime;
//    private long mBaseSoftIrqTime;
//    private long mBaseIdleTime;
//    private int mRelUserTime;
//    private int mRelSystemTime;
//    private int mRelIoWaitTime;
//    private int mRelIrqTime;
//    private int mRelSoftIrqTime;
//    private int mRelIdleTime;

    /**
     * 获取cpu使用率的方法，待验证
     * 
     * @return
     */
//    public long getCpuTotalTime() {
//        while (true) {
//            final long[] sysCpu = new long[7];
//            try {
//                Class process = Class.forName("android.os.Process");
//                Method read = process.getMethod("readProcFile", String.class, int[].class, String[].class,
//                        long[].class, float[].class);
//                if ((Boolean) read.invoke(null, "/proc/stat", SYSTEM_CPU_FORMAT, null, sysCpu, null)) {
//
//                    // Total user time is user + nice time.
//                    final long usertime = sysCpu[0] + sysCpu[1];
//                    // Total system time is simply system time.
//                    final long systemtime = sysCpu[2];
//                    // Total idle time is simply idle time.
//                    final long idletime = sysCpu[3];
//                    // Total irq time is iowait + irq + softirq time.
//                    final long iowaittime = sysCpu[4];
//                    final long irqtime = sysCpu[5];
//                    final long softirqtime = sysCpu[6];
////                    long totalTime = usertime + systemtime + idletime + irqtime + softirqtime;
//                    // Log.e("CpuTime", String.valueOf(totalTime));
////                    Log.e("cyTest", "totalTime:"+totalTime);
////                    return totalTime;
//
//                    if (true || (usertime >= mBaseUserTime && systemtime >= mBaseSystemTime
//                            && iowaittime >= mBaseIoWaitTime && irqtime >= mBaseIrqTime
//                            && softirqtime >= mBaseSoftIrqTime && idletime >= mBaseIdleTime)) {
//                        mRelUserTime = (int) (usertime - mBaseUserTime);
//                        mRelSystemTime = (int) (systemtime - mBaseSystemTime);
//                        mRelIoWaitTime = (int) (iowaittime - mBaseIoWaitTime);
//                        mRelIrqTime = (int) (irqtime - mBaseIrqTime);
//                        mRelSoftIrqTime = (int) (softirqtime - mBaseSoftIrqTime);
//                        mRelIdleTime = (int) (idletime - mBaseIdleTime);
//
//                        mBaseUserTime = usertime;
//                        mBaseSystemTime = systemtime;
//                        mBaseIoWaitTime = iowaittime;
//                        mBaseIrqTime = irqtime;
//                        mBaseSoftIrqTime = softirqtime;
//                        mBaseIdleTime = idletime;
//
//                        long one = mRelUserTime + mRelSystemTime + mRelIoWaitTime + mRelIrqTime
//                                + mRelSoftIrqTime;
//                        long two = mRelUserTime + mRelSystemTime + mRelIoWaitTime + mRelIrqTime
//                                + mRelSoftIrqTime + mRelIdleTime;
//
////                        mCpuUsageRates[mCheckTimes] = one/two;
//
//                        Log.e("cyTest", "one:" + one + "two:" + two + "one/two->" + 100 * one / two);
//
//                    } else {
//                        Log.e("cyTest", "error2");
//                        mRelUserTime = 0;
//                        mRelSystemTime = 0;
//                        mRelIoWaitTime = 0;
//                        mRelIrqTime = 0;
//                        mRelSoftIrqTime = 0;
//                        mRelIdleTime = 0;
//                    }
//
//                } else {
//                    Log.e("cyTest", "error1");
//                }
//
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
////            return 0;
//        }
//    }

}
