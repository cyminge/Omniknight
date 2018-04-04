package com.gionee.threadbus;

import com.gionee.threadbus.core.IBatchScheduler;
import com.gionee.threadbus.core.IScheduler;
import com.gionee.threadbus.scheduler.DelayedScheduler;
import com.gionee.threadbus.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 装配工
 *
 * Created by JLB6088 on 2017/5/10.
 */

final class Assembler<T> implements IAssembler<T> {

    private IScheduler<T> mScheduler;
    private IBatchScheduler<T> mBatchScheduler;
    private long mDelayTime;
    private TimeUnit mTimeUnit;
    private long mPeriod;

    private TaskCallable<T> mTaskCallable;
    private ScheduleListener<T> mScheduleListener;
    private TaskRunnable mTaskRunnable;
    private ArrayList<TaskCallable<T>> mTaskList;

    Assembler() {

    }

    @Override
    public IAssembler create(TaskRunnable task) {
        mTaskRunnable = task;
        return this;
    }

    @Override
    public IAssembler create(TaskCallable<T> task) {
        return create(task, null);
    }

    @Override
    public IAssembler create(TaskCallable<T> task, ScheduleListener<T> scheduleListener) {
        mTaskCallable = task;
        mScheduleListener = scheduleListener;
        return this;
    }

    @Override
    public IAssembler groupCreate(ArrayList<TaskCallable<T>> tasks) {
        mTaskList = tasks;
        return this;
    }

    @Override
    public IAssembler delay(long delayTime, TimeUnit timeUnit) {
        mDelayTime = delayTime;
        mTimeUnit = timeUnit;
        return this;
    }

    @Override
    public IAssembler period(long period) {
        mPeriod = period;
        return this;
    }

    @Override
    public IAssembler scheduleOn(IScheduler scheduler) {
        mScheduler = scheduler;
        return this;
    }

    @Override
    public IAssembler scheduleOn(IBatchScheduler batchScheduler) {
        mBatchScheduler = batchScheduler;
        return this;
    }

    @Override
    public void start() {
        ObjectHelper.requireNonNull(mScheduler, "invoke start() with null IScheduler !");
        ObjectHelper.requireNonNull(mTaskRunnable, "invoke start() with null or wrong Task !");
        if(mScheduler instanceof DelayedScheduler) {
            DelayedScheduler delayedScheduler = (DelayedScheduler) mScheduler;
            delayedScheduler.schedule(mTaskRunnable, mDelayTime, mPeriod, mTimeUnit);
        } else {
            mScheduler.schedule(mTaskRunnable);
        }
    }

    @Override
    public ScheduledTask<T> startForResult() {
        ObjectHelper.requireNonNull(mScheduler, "invoke startForResult() with null IScheduler !");
        ObjectHelper.requireNonNull(mTaskCallable, "invoke startForResult() with null or wrong Task !");

        if(mScheduler instanceof DelayedScheduler) {
            DelayedScheduler delayedScheduler = (DelayedScheduler) mScheduler;
            return delayedScheduler.schedule(mTaskCallable, mScheduleListener, mDelayTime, mTimeUnit);
        } else {
            return mScheduler.schedule(mTaskCallable, mScheduleListener);
        }
    }

    @Override
    public ArrayList<ScheduledTask<T>> startForMultiResult() {
        ObjectHelper.requireNonNull(mBatchScheduler,
                "invoke startForMultiResult() with null IBatchScheduler !");
        ObjectHelper.requireNonNull(mTaskList, "invoke startForMultiResult() with null tasks !");
        return mBatchScheduler.schedule(mTaskList);
    }

}
