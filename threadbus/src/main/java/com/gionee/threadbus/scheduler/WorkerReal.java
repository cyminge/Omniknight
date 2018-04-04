package com.gionee.threadbus.scheduler;

import android.support.annotation.NonNull;
import com.gionee.threadbus.ScheduleListener;
import com.gionee.threadbus.ScheduledTask;
import com.gionee.threadbus.utils.Constant;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by JLB6088 on 2017/5/18.
 */
final class WorkerReal<T> extends ScheduledTask<T> implements Comparable<WorkerReal<T>> {

    private int mSortPriority;
    private WorkerCachePool mWorkerCachePool;
    private ScheduleListener mScheduledListener;
    private Callable<T> mCallableTask;
    private long mSortTime;
    private int mSortRule;

    public WorkerReal(@NonNull Callable<T> callable) {
        this(callable, null);
    }

    public WorkerReal(@NonNull Callable<T> callable, ScheduleListener<T> listener) {
        this(callable, listener, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
    }

    public WorkerReal(@NonNull Callable<T> callable, ScheduleListener<T> listener, int sortPriority) {
        super(callable);
        mCallableTask = callable;
        mScheduledListener = listener;
        mSortPriority = sortPriority;
        mSortTime = System.currentTimeMillis();
    }

    public WorkerReal(@NonNull Runnable runnable) {
        this(runnable, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
    }

    public WorkerReal(@NonNull Runnable runnable, int priority) {
        super(runnable);
        mSortPriority = priority;
        mSortTime = System.currentTimeMillis();
    }

    public void setSortRule(int sortRule) {
        mSortRule = sortRule;
    }

    public Callable getCallableTask() {
        return mCallableTask;
    }

    public void setWorkerCachePool(WorkerCachePool workerCachePool) {
        mWorkerCachePool = workerCachePool;
    }

    @Override
    protected void done() {
        super.done();

        if (null == mScheduledListener) {
            return;
        }

        mScheduledListener.onScheduleDone(this);
    }

    public int getSortPriority() {
        return mSortPriority;
    }

    @Override
    public int compareTo(@NonNull WorkerReal<T> another) {
        if (mSortRule == Constant.NO_SORT_RULE) {
            return 0;
        }

        WorkerReal anotherArgs = (WorkerReal) another;
        if (mSortRule == Constant.SORT_RULE_BY_USER_DEFINED) {
            // 数字大，优先级高
            if (mSortPriority == anotherArgs.mSortPriority) {
                return 0;
            } else if (mSortPriority < anotherArgs.mSortPriority) {
                return 1;
            } else {
                return -1;
            }
        } else if (mSortRule == Constant.SORT_RULE_BY_TIME_INVERTED_ORDER) {
            // 时间越新，优先级高
            if (mSortTime == anotherArgs.mSortTime) {
                return 0;
            } else if (mSortTime < anotherArgs.mSortTime) {
                return 1;
            } else {
                return -1;
            }
        }

        return 0;
    }

    @Override
    public boolean cancel(boolean b) {
        if (null != mWorkerCachePool && mWorkerCachePool.containsWorker(this)) {
            mWorkerCachePool.removeWorker(this);
            return true;
        }

        if (null != mScheduledListener) {
            mScheduledListener = null;
        }

        return super.cancel(b);
    }

    @Override
    public boolean isCancelled() {
        if (null != mWorkerCachePool && mWorkerCachePool.containsWorker(this)) {
            mWorkerCachePool.removeWorker(this);
            return true;
        }

        return super.isCancelled();
    }

    @Override
    public boolean isDone() {
        if (null != mWorkerCachePool && mWorkerCachePool.containsWorker(this)) {
            return false;
        }

        return super.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        if (null != mWorkerCachePool && mWorkerCachePool.containsWorker(this)) {
            return null;
        }

        return super.get();
    }

    @Override
    public T get(long l, @NonNull TimeUnit timeUnit)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (null != mWorkerCachePool && mWorkerCachePool.containsWorker(this)) {
            return null;
        }

        return super.get(l, timeUnit);
    }
}
