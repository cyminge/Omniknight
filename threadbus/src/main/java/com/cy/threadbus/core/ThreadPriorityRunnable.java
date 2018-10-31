package com.cy.threadbus.core;

/**
 * 设置运行当前任务的线程优先级 Created by JLB6088 on 2017/6/23.
 */

public abstract class ThreadPriorityRunnable extends NamedRunnable {

    protected static final int INVALID_THREAD_PRIORITY = -1;

    private int mThreadPriority;

    public ThreadPriorityRunnable(String threadName, int threadPriority) {
        super(threadName);
        mThreadPriority = threadPriority;
    }

    @Override
    public void run() {
        if (mThreadPriority != INVALID_THREAD_PRIORITY) {
            android.os.Process.setThreadPriority(mThreadPriority);
        }
        super.run();
    }

    public int getThreadPriority() {
        return mThreadPriority;
    }
}
