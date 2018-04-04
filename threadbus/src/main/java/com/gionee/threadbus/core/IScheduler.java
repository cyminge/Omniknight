package com.gionee.threadbus.core;

import com.gionee.threadbus.ScheduleListener;
import com.gionee.threadbus.ScheduledTask;

import java.util.concurrent.Callable;

/**
 * Created by JLB6088 on 2017/6/19.
 */

public interface IScheduler<T> {

    /**
     * 执行任务
     * 
     * @param runnable
     */
    void execute(Runnable runnable);

    /**
     * @param runnable
     *            执行无需返回值的任务
     * @return
     */
    ScheduledTask<T> schedule(Runnable runnable);

    /**
     * @param callable
     *            执行有返回值的任务
     * @return
     */
    ScheduledTask<T> schedule(Callable<T> callable);

    /**
     * @param callable
     * @param scheduleListener
     *            任务执行完成回调
     * @return
     */
    ScheduledTask<T> schedule(Callable<T> callable, ScheduleListener<T> scheduleListener);

    /**
     * 取消线程池中某个还未执行的任务 这个方法只有当你是通过{@link #execute(Runnable)}方法执行任务时才有效
     * 如果你是通过schedule方法执行的任务，可以通过其返回值{@link ScheduledTask#cancel(boolean)}进行任务取消操作
     * 
     * @param runnable
     * @return
     */
    boolean cancel(Runnable runnable);

    /**
     * 平缓关闭线程池，但是会确保所有已经加入的任务都将会被执行完毕才关闭
     */
    void shutdown();

    /**
     * 立刻关闭线程池，正在执行的任务也可能会被中断
     */
    void shutdownNow();

}
