package com.cy.threadbus.core;

import com.cy.threadbus.ScheduleListener;
import com.cy.threadbus.ScheduledTask;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 这个接口是用于延时、周期执行任务 Created by JLB6088 on 2017/6/22.
 */

public interface IDelayScheduledService<T> {

    /**
     * 延时执行无返回值任务
     * @param runnable
     * @param delayTime
     * @param timeUnit
     * @return
     */
    ScheduledTask<T> schedule(Runnable runnable, long delayTime, TimeUnit timeUnit);

    /**
     * 周期执行无返回任务
     * @param runnable
     * @param delayTime
     * @param period
     * @param timeUnit
     * @return
     */
    ScheduledTask<T> schedule(Runnable runnable, long delayTime, long period, TimeUnit timeUnit);

    /**
     * 延时执行有返回值任务
     * @param callable
     * @param delayTime
     * @param timeUnit
     * @return
     */
    ScheduledTask<T> schedule(Callable<T> callable, long delayTime, TimeUnit timeUnit);

    /**
     * 延时执行有返回值任务，提供结果回调
     * @param callable
     * @param scheduleListener
     * @param delayTime
     * @param timeUnit
     * @return
     */
    ScheduledTask<T> schedule(Callable<T> callable, ScheduleListener scheduleListener, long delayTime,
                              TimeUnit timeUnit);


}
