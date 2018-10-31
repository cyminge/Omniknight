package com.cy.threadbus.scheduler;

import android.os.Process;

import com.cy.threadbus.ScheduleListener;
import com.cy.threadbus.ScheduledTask;
import com.cy.threadbus.core.IDelayScheduledService;
import com.cy.threadbus.core.PriorityThreadFactory;
import com.cy.threadbus.utils.Constant;
import com.cy.threadbus.utils.ObjectHelper;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 支持延时周期执行任务的线程池 默认线程池数为1 注意：带返回值的任务不支持周期执行
 * 
 * Created by JLB6088 on 2017/5/17.
 */

public final class DelayedScheduler<T> extends AbstractScheduler<T> implements IDelayScheduledService<T> {

    private static final ThreadFactory DEFAULT_WORKER_THREAD_FACTORY = new PriorityThreadFactory(
            Process.THREAD_PRIORITY_BACKGROUND + 2);

    private static final int DEFAULT_POOL_SIZE = 1;

    public DelayedScheduler() {
        this(DEFAULT_POOL_SIZE);
    }

    public DelayedScheduler(int poolSize) {
        this(poolSize, null);
    }

    public DelayedScheduler(int poolSize, ThreadFactory threadFactory) {
        this(poolSize, threadFactory, Constant.THREAD_POOL_TYPE_IO);
    }

    public DelayedScheduler(int poolSize, ThreadFactory threadFactory, int threadPoolType) {
        super(poolSize, threadFactory, true, threadPoolType);
    }

    @Override
    protected ThreadPoolExecutor createThreadPool() {
        return new ScheduledThreadPoolExecutor(mPoolSize,
                ObjectHelper.getExistObject(mThreadFactory, DEFAULT_WORKER_THREAD_FACTORY));
    }

    @Override
    public ScheduledTask<T> schedule(Runnable runnable, long delayTime, TimeUnit timeUnit) {
        return schedule(runnable, delayTime, Constant.ZERO, timeUnit);
    }

    @Override
    public ScheduledTask<T> schedule(Runnable runnable, long delayTime, long period, TimeUnit timeUnit) {
        WorkerReal<T> workerReal = new WorkerReal<>(runnable, getSortPriorityByRunnable(runnable));
        if (delayTime <= Constant.ZERO || null == timeUnit) {
            execute(workerReal);
        } else {
            if (period <= Constant.ZERO) {
                ((ScheduledThreadPoolExecutor) mThreadPoolExecutor).schedule(workerReal, delayTime, timeUnit);
            } else {
                ((ScheduledThreadPoolExecutor) mThreadPoolExecutor).scheduleAtFixedRate(runnable, delayTime,
                        period, timeUnit);
            }
        }

        return workerReal;
    }

    @Override
    public ScheduledTask<T> schedule(Callable<T> callable, long delayTime, TimeUnit timeUnit) {
        return schedule(callable, null, delayTime, timeUnit);
    }

    @Override
    public ScheduledTask<T> schedule(Callable<T> callable, ScheduleListener scheduleListener, long delayTime,
                                     TimeUnit timeUnit) {
        WorkerReal<T> workerReal = new WorkerReal<>(callable, scheduleListener,
                getSortPriorityByCallable(callable));
        if (delayTime > Constant.ZERO && null != timeUnit) {
            ((ScheduledThreadPoolExecutor) mThreadPoolExecutor).schedule(workerReal, delayTime, timeUnit);
        } else {
            execute(workerReal);
        }

        return workerReal;
    }

}
