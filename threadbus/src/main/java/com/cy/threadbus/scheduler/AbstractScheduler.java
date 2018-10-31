package com.cy.threadbus.scheduler;

import com.cy.threadbus.ScheduleListener;
import com.cy.threadbus.ScheduledTask;
import com.cy.threadbus.TaskCallable;
import com.cy.threadbus.TaskRunnable;
import com.cy.threadbus.core.IScheduler;
import com.cy.threadbus.utils.Constant;
import com.cy.threadbus.utils.ObjectHelper;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by JLB6088 on 2017/5/10.
 */

abstract class AbstractScheduler<T> implements IScheduler<T> {

    protected int mPoolSize;
    protected ThreadFactory mThreadFactory;
    protected ThreadPoolExecutor mThreadPoolExecutor;

    protected AbstractScheduler(int poolSize, ThreadFactory threadFactory, boolean isChangePoolSize,
            int threadPoolType) {
        mPoolSize = ObjectHelper.requireNonNegative(poolSize);
        mThreadFactory = threadFactory;
        mThreadPoolExecutor = createThreadPool();
        new PoolSizeChanger(mPoolSize, isChangePoolSize, mThreadPoolExecutor, threadPoolType);
    }

    /**
     * 创建线程池
     * 
     * @return
     */
    protected abstract ThreadPoolExecutor createThreadPool();

    /**
     * 任务排序的规则。
     * 
     * @return
     */
    public int getSortRule() {
        return Constant.NO_SORT_RULE;
    }

    public long getTaskCount() {
        return mThreadPoolExecutor.getTaskCount();
    }

    public long getActiveCount() {
        return mThreadPoolExecutor.getActiveCount();
    }

    public void beforeExecute(Thread t, Runnable r) {
    }

    public void afterExecute(Runnable r, Throwable t) {
    }

    @Override
    public void execute(Runnable runnable) {
        if (null == mThreadPoolExecutor || mThreadPoolExecutor.isShutdown()) {
            mThreadPoolExecutor = createThreadPool();
        }
        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public ScheduledTask<T> schedule(Runnable runnable) {
        WorkerReal workerReal = new WorkerReal(runnable, getSortPriorityByRunnable(runnable));
        execute(workerReal);
        return workerReal;
    }

    @Override
    public ScheduledTask<T> schedule(Callable<T> callable) {
        return schedule(callable, null);
    }

    @Override
    public ScheduledTask<T> schedule(Callable<T> callable, ScheduleListener<T> scheduleListener) {
        WorkerReal workerReal = new WorkerReal(callable, scheduleListener,
                getSortPriorityByCallable(callable));
        execute(workerReal);
        return workerReal;
    }

    @Override
    public boolean cancel(Runnable runnable) {
        if (null == mThreadPoolExecutor || mThreadPoolExecutor.isShutdown()) {
            return true;
        }

        return mThreadPoolExecutor.getQueue().remove(runnable);
    }

    @Override
    public void shutdown() {
        if (null == mThreadPoolExecutor || mThreadPoolExecutor.isShutdown()) {
            return;
        }
        mThreadPoolExecutor.shutdown();
    }

    @Override
    public void shutdownNow() {
        if (null == mThreadPoolExecutor || mThreadPoolExecutor.isShutdown()) {
            return;
        }

        mThreadPoolExecutor.shutdownNow();
    }

    /**
     * 根据Runnable任务拿到排序优先级
     * 
     * @param runnable
     * @return
     */
    protected int getSortPriorityByRunnable(Runnable runnable) {
        int sortPriority = Constant.NO_SORT_RULE;
        if (runnable instanceof TaskRunnable) {
            TaskRunnable taskRunnable = (TaskRunnable) runnable;
            sortPriority = taskRunnable.getTaskSortPriority();
        }
        return sortPriority;
    }

    /**
     * 根据Callable任务拿到排序优先级
     * 
     * @param callable
     * @return
     */
    protected int getSortPriorityByCallable(Callable callable) {
        int sortPriority = Constant.NO_SORT_RULE;
        if (callable instanceof TaskCallable) {
            TaskCallable taskCallable = (TaskCallable) callable;
            sortPriority = taskCallable.getTaskSortPriority();
        }
        return sortPriority;
    }
}
