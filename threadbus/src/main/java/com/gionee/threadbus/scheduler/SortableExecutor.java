package com.gionee.threadbus.scheduler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by JLB6088 on 2017/5/11.
 */

final class SortableExecutor extends ThreadPoolExecutor {

    private AbstractScheduler mScheduler;

    public SortableExecutor(int poolSize, ThreadFactory threadFactory, AbstractScheduler scheduler) {
        super(poolSize, poolSize, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
        mScheduler = scheduler;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        mScheduler.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        mScheduler.afterExecute(r, t);
    }
}
