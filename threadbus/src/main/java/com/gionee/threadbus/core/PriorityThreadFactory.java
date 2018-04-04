package com.gionee.threadbus.core;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

/**
 * Created by JLB6088 on 2017/5/11.
 */

public final class PriorityThreadFactory implements ThreadFactory {

    private final int mThreadPriority;

    public PriorityThreadFactory(int priority) {
        mThreadPriority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r) {
            @Override
            public void run() {
                Process.setThreadPriority(mThreadPriority);
                super.run();
            }
        };
    }
}
