package com.gionee.threadbus.core;

import android.text.TextUtils;

/**
 * 设置运行当前任务的线程名字 Created by JLB6088 on 2017/5/10.
 */

public abstract class NamedRunnable implements Runnable {
    private String mThreadName;

    public NamedRunnable(String threadName) {
        mThreadName = threadName;
    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(mThreadName)) {
            runTask();
            return;
        }

        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(mThreadName);
        try {
            runTask();
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    public String getThreadName() {
        return mThreadName;
    }

    public abstract void runTask();
}
