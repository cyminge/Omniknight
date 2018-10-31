package com.cy.threadbus;

import com.cy.threadbus.core.IBatchScheduler;
import com.cy.threadbus.core.IScheduler;
import com.cy.threadbus.scheduler.BatchScheduler;
import com.cy.threadbus.scheduler.CustomScheduler;
import com.cy.threadbus.scheduler.DelayedScheduler;
import com.cy.threadbus.scheduler.SortableScheduler;
import com.cy.threadbus.scheduler.UnlimitedScheduler;

/**
 * 提供默认的线程池
 * Created by JLB6088 on 2017/5/10.
 */

public final class SchedulerFactory {

    /**
     * 默认线程数为cpu核心数的可排序（插队）的调度器，默认为提交任务的顺序
     */
    public static final IScheduler getSortableScheduler() {
        return SortableHolder.DEFAULT;
    }

    /**
     * 单个线程的可排序（插队）的调度器，默认为提交任务的顺序
     * @return
     */
    public static final IScheduler getSingleSortableScheduler() {
        return SingleHolder.DEFAULT;
    }

    /**
     * 无线程数限制的调度器
     */
    public static final IScheduler getUnlimitedScheduler() {
        return UnLimitedHolder.DEFAULT;
    }

    /**
     * 单个线程的可延时执行的调度器
     * @return
     */
    public static final IScheduler getSingleDelayedScheduler() {
        return SingleDelayedHolder.DEFAULT;
    }

    /**
     * 批量任务的调度器，用于多任务同时返回结果
     */
    public static IBatchScheduler getBatchScheduler() {
        return BatchHolder.DEFAULT;
    }

    /**
     * 自定义调度器
     * @param config
     * @return
     */
    public static IScheduler create(SchedulerConfiguration config) {
        return new CustomScheduler(config);
    }


    private static final class SortableHolder {
        private static final IScheduler DEFAULT = new SortableScheduler();
    }

    private static final class SingleDelayedHolder {
        private static final IScheduler DEFAULT = new DelayedScheduler();
    }

    private static final class UnLimitedHolder {
        private static final IScheduler DEFAULT = new UnlimitedScheduler();
    }

    private static final class SingleHolder {
        private static final IScheduler DEFAULT = new SortableScheduler(1);
    }

    private static final class BatchHolder {
        private static final IBatchScheduler DEFAULT = new BatchScheduler();
    }
}
