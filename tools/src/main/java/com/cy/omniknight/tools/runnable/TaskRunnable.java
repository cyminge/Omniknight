package com.cy.omniknight.tools.runnable;

/**
 * 设置当前任务在可排序调度器里的排序级别 Created by JLB6088 on 2017/6/19.
 */

public abstract class TaskRunnable extends ThreadPriorityRunnable {

    private int mSortPriority;

    public TaskRunnable(String threadName) {
        this(threadName, INVALID_THREAD_PRIORITY);
    }

    public TaskRunnable(String threadName, int threadPriority) {
        super(threadName, threadPriority);
    }

    public int getTaskSortPriority() {
        return mSortPriority;
    }
}
