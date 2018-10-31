package com.cy.threadbus;

import java.util.ArrayList;

/**
 * 启动器 Created by JLB6088 on 2017/5/10.
 */

interface Initiator<T> {
    /**
     * 无返回值单任务开始执行
     */
    void start();

    /**
     * 有返回值单任务开始执行
     * 
     * @return
     */
    ScheduledTask<T> startForResult();

    /**
     * 多任务开始执行
     * this method meanwhile returns results by multi request if you want to use it, you should run it not on
     * main thread.
     * @return
     */
    ArrayList<ScheduledTask<T>> startForMultiResult();
}
