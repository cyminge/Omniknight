package com.gionee.threadbus.scheduler;

/**
 * Created by JLB6088 on 2017/5/16.
 */

import android.os.Process;
import com.gionee.threadbus.ScheduleListener;
import com.gionee.threadbus.ScheduledTask;
import com.gionee.threadbus.SchedulerConfiguration;
import com.gionee.threadbus.core.PriorityThreadFactory;
import com.gionee.threadbus.utils.Constant;
import com.gionee.threadbus.utils.ObjectHelper;

import java.util.concurrent.*;

/**
 * 创建自定义线程池
 *
 * 支持两种任务排序方式， 时间跟自定义排序。
 * 
 * @param <T>
 */
public final class CustomScheduler<T> extends AbstractScheduler<T> {

    private static final ThreadFactory DEFAULT_WORKER_THREAD_FACTORY = new PriorityThreadFactory(
            Process.THREAD_PRIORITY_DEFAULT);

    private SchedulerConfiguration mConfig;
    private WorkerCachePool<T> mWorkerCachePool;

    public CustomScheduler(SchedulerConfiguration config) {
        super(config.getPoolSize(), config.getThreadFactory(), true, config.getThreadPoolType());
        mConfig = config;
        if (getSortRule() != Constant.NO_SORT_RULE) {
            mWorkerCachePool = new WorkerCachePool<>();
        }
    }

    @Override
    protected ThreadPoolExecutor createThreadPool() {
        return new ThreadPoolExecutor(mPoolSize, mPoolSize, 30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                ObjectHelper.getExistObject(mThreadFactory, DEFAULT_WORKER_THREAD_FACTORY));
    }

    @Override
    public long getTaskCount() {
        return super.getTaskCount() + mWorkerCachePool.getCacheSize();
    }

    @Override
    public int getSortRule() {
        return mConfig.getSortRule();
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
    public void afterExecute(Runnable r, Throwable t) {
        synchronized (this) {
            if (getSortRule() == Constant.NO_SORT_RULE || null == mWorkerCachePool
                    || mWorkerCachePool.isEmpty()) {
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
        if (getSortRule() == Constant.NO_SORT_RULE) {
            return super.cancel(runnable);
        }

        if (null == mWorkerCachePool || mWorkerCachePool.isEmpty()) {
            return super.cancel(runnable);
        }

        return false;
    }

    @Override
    public void shutdown() {
        super.shutdown();

        if (getSortRule() == Constant.NO_SORT_RULE) {
            return;
        }

        if (null == mWorkerCachePool || mWorkerCachePool.isEmpty()) {
            return;
        }
        mWorkerCachePool.removeAll();
    }

    @Override
    public void shutdownNow() {
        super.shutdownNow();

        if (getSortRule() == Constant.NO_SORT_RULE) {
            return;
        }

        if (null == mWorkerCachePool || mWorkerCachePool.isEmpty()) {
            return;
        }
        mWorkerCachePool.removeAll();
    }

}
