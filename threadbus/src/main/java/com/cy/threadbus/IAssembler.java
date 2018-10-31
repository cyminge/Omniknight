package com.cy.threadbus;

import com.cy.threadbus.core.IBatchScheduler;
import com.cy.threadbus.core.IScheduler;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 装配器接口
 * 任务的封装及执行都是在此完成
 * Created by JLB6088 on 2017/6/19.
 */

public interface IAssembler<T> extends Initiator<T> {

    /**
     * 传入要执行的任务
     * @param task
     * @return
     */
    IAssembler create(TaskRunnable task);

    /**
     * 传入要执行的任务
     * @param task
     * @return
     */
    IAssembler create(TaskCallable<T> task);

    /**
     * 传入要执行的任务, ScheduleListener为任务执行完后的回调
     * @param task
     * @param futureListener
     * @return
     */
    IAssembler create(TaskCallable<T> task, ScheduleListener<T> futureListener);

    /**
     * 支持多任务同时返回时调用
     * 不与create同时使用
     * @param tasks
     * @return
     */
    IAssembler groupCreate(ArrayList<TaskCallable<T>> tasks);

    /**
     * 延时执行任务
     * @param delayTime
     * @param timeUnit
     * @return
     */
    IAssembler delay(long delayTime, TimeUnit timeUnit);

    /**
     * 周期执行任务，该函数需要与{@link #delay(long,TimeUnit)}配合使用
     * @param period
     * @return
     */
    IAssembler period(long period);

    /**
     * IScheduler：单任务执行时调用的线程
     * @param scheduler
     * @return
     */
    IAssembler scheduleOn(IScheduler scheduler);

    /**
     * IBatchScheduler：多任务执行时调用的线程池
     *
     * @param batchScheduler
     * @return
     */
    IAssembler scheduleOn(IBatchScheduler batchScheduler);

}
