package com.gionee.threadbus.core;

/**
 * Created by JLB6088 on 2017/6/23.
 */

public abstract class ThreadPriorityCallable<T> extends NamedCallable<T> {
    protected static final int INVALID_THREAD_PRIORITY = -1;

    private int mThreadPriority;

    public ThreadPriorityCallable(String threadName, int threadPriority) {
        super(threadName);
        mThreadPriority = threadPriority;
    }

    @Override
    public T call() throws Exception {
        if (mThreadPriority != INVALID_THREAD_PRIORITY) {
            android.os.Process.setThreadPriority(mThreadPriority);
        }
        return super.call();
    }

    public int getThreadPriority() {
        return mThreadPriority;
    }
}
