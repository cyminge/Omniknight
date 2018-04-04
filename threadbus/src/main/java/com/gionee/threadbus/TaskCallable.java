package com.gionee.threadbus;

import com.gionee.threadbus.core.ThreadPriorityCallable;
import com.gionee.threadbus.utils.Constant;

/**
 * 设置当前任务在可排序调度器里的排序级别
 * Created by JLB6088 on 2017/6/20.
 */

public abstract class TaskCallable<T> extends ThreadPriorityCallable<T> {

    protected int mSortPriority;

    public TaskCallable(String threadName) {
        this(threadName, INVALID_THREAD_PRIORITY);
    }

    public TaskCallable(String threadName, int threadPriority) {
        this(threadName, threadPriority, Constant.USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT);
    }

    public TaskCallable(String threadName, int threadPriority, int sortPriority) {
        super(threadName, threadPriority);
        mSortPriority = sortPriority;
    }

    public int getTaskSortPriority() {
        return mSortPriority;
    }

}
