package com.gionee.threadbus;

/**
 * 任务处理完后的回调监听
 *
 * Created by JLB6088 on 2017/5/17.
 */

public interface ScheduleListener<T> {
    void onScheduleDone(ScheduledTask<T> scheduledTask);
}
