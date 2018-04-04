package com.gionee.threadbus.scheduler;

import android.os.Process;
import com.gionee.threadbus.ScheduleListener;
import com.gionee.threadbus.ScheduledTask;
import com.gionee.threadbus.core.PriorityThreadFactory;
import com.gionee.threadbus.utils.Constant;
import com.gionee.threadbus.utils.DeviceUtil;
import com.gionee.threadbus.utils.ObjectHelper;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 可支持排序插队的线程池 默认线程池的优先级为Process.THREAD_PRIORITY_BACKGROUND Created by JLB6088 on 2017/5/17.
 */

public final class SortableScheduler<T> extends AbstractScheduler<T> {

    private static final ThreadFactory DEFAULT_WORKER_THREAD_FACTORY = new PriorityThreadFactory(
            Process.THREAD_PRIORITY_BACKGROUND);

    private WorkerCachePool<T> mWorkerCachePool;

    public SortableScheduler() {
        this(DeviceUtil.getDeviceCPUCounts());
    }

    public SortableScheduler(int poolSize) {
        this(poolSize, null);
    }

    public SortableScheduler(int poolSize, ThreadFactory threadFactory) {
        this(poolSize, threadFactory, Constant.THREAD_POOL_TYPE_IO);
    }

    public SortableScheduler(int poolSize, ThreadFactory threadFactory, int threadPoolType) {
        super(poolSize, threadFactory, true, threadPoolType);
        mWorkerCachePool = new WorkerCachePool<>();
    }

    @Override
    protected ThreadPoolExecutor createThreadPool() {
        return new SortableExecutor(mPoolSize,
                ObjectHelper.getExistObject(mThreadFactory, DEFAULT_WORKER_THREAD_FACTORY), this);
    }

    @Override
    public int getSortRule() {
        return Constant.SORT_RULE_BY_USER_DEFINED;
    }

    @Override
    public ScheduledTask<T> schedule(Runnable runnable) {
        synchronized (this) {
            WorkerReal workerReal = new WorkerReal(runnable, getSortPriorityByRunnable(runnable));
            workerReal.setSortRule(getSortRule());
            if (getActiveCount() >= mPoolSize) {
                mWorkerCachePool.addWorker(workerReal);
            } else {
                execute(workerReal);
            }

            return workerReal;
        }

    }

    @Override
    public ScheduledTask<T> schedule(Callable<T> callable, ScheduleListener<T> scheduleListener) {
        synchronized (this) {
            WorkerReal workerReal = new WorkerReal(callable, scheduleListener,
                    getSortPriorityByCallable(callable));
            workerReal.setSortRule(getSortRule());
            if (getActiveCount() >= mPoolSize) {
                mWorkerCachePool.addWorker(workerReal);
            } else {
                execute(workerReal);
            }

            return workerReal;
        }
    }

    @Override
    public long getTaskCount() {
        return super.getTaskCount() + mWorkerCachePool.getCacheSize();
    }

    @Override
    public void afterExecute(Runnable r, Throwable t) {
        synchronized (this) {
            if (null == mWorkerCachePool || mWorkerCachePool.isEmpty()) {
                return;
            }

            WorkerReal workerReal = mWorkerCachePool.getWorker();
            if (null == workerReal) {
                return;
            }

            execute(workerReal);
        }
    }

    @Override
    public boolean cancel(Runnable runnable) {
        if (null != mWorkerCachePool && !mWorkerCachePool.isEmpty()) {
            return false;
        }

        return mThreadPoolExecutor.getQueue().remove(runnable);
    }

    @Override
    public void shutdown() {
        super.shutdown();

        if (null == mWorkerCachePool || mWorkerCachePool.isEmpty()) {
            return;
        }
        mWorkerCachePool.removeAll();
    }

    @Override
    public void shutdownNow() {
        super.shutdownNow();

        if (null == mWorkerCachePool || mWorkerCachePool.isEmpty()) {
            return;
        }
        mWorkerCachePool.removeAll();
    }
}
