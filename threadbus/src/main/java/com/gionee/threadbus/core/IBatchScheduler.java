package com.gionee.threadbus.core;

import com.gionee.threadbus.ScheduledTask;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * 批量执行任务调用接口
 * Created by JLB6088 on 2017/5/24.
 */

public interface IBatchScheduler<T> {

    void shutdown();

    ArrayList<ScheduledTask<T>> schedule(ArrayList<? extends Callable<T>> callableList);
}
