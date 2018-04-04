package com.gionee.threadbus.scheduler;

import com.gionee.threadbus.core.PriorityThreadFactory;
import com.gionee.threadbus.utils.Constant;
import com.gionee.threadbus.utils.ObjectHelper;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by JLB6088 on 2017/5/17.
 */

/**
 * 无限制的线程池，一般用于前端任务需要立即获取数据时调用 默认线程池的优先级为Process.THREAD_PRIORITY_FOREGROUND
 * 
 * @param <T>
 */
public final class UnlimitedScheduler<T> extends AbstractScheduler<T> {

    private static final ThreadFactory DEFAULT_WORKER_THREAD_FACTORY = new PriorityThreadFactory(
            android.os.Process.THREAD_PRIORITY_FOREGROUND);

    public UnlimitedScheduler() {
        this(DEFAULT_WORKER_THREAD_FACTORY);
    }

    public UnlimitedScheduler(ThreadFactory threadFactory) {
        super(Constant.ZERO, threadFactory, false, Constant.THREAD_POOL_TYPE_MIXED);
    }

    @Override
    protected ThreadPoolExecutor createThreadPool() {
        return new ThreadPoolExecutor(Constant.ZERO, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                ObjectHelper.getExistObject(mThreadFactory, DEFAULT_WORKER_THREAD_FACTORY));
    }

}
