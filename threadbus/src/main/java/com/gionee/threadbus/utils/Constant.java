package com.gionee.threadbus.utils;

/**
 * Created by JLB6088 on 2017/5/27.
 */

public class Constant {

    public static final int ZERO = 0;

    // 可排序任务调度器的任务执行顺序，数字越高，执行越快
    public static final int USER_DEFINED_TASK_SORT_PRIORITY_HIGH = 10;
    public static final int USER_DEFINED_TASK_SORT_PRIORITY_NORMAL = 6;
    public static final int USER_DEFINED_TASK_SORT_PRIORITY_LOW = 0;
    public static final int USER_DEFINED_TASK_SORT_PRIORITY_DEFAULT = 3;

    // 可排序任务的排序规则
    public static final int NO_SORT_RULE = 0x0;
    public static final int SORT_RULE_BY_TIME_INVERTED_ORDER = 0x1;
    public static final int SORT_RULE_BY_USER_DEFINED = 0x2;
    public static final int SORT_RULE_COUNT = SORT_RULE_BY_USER_DEFINED; // 有多少种排序规则

    // 线程池类型
    public static final int THREAD_POOL_TYPE_IO = 0x1;
    public static final int THREAD_POOL_TYPE_MIXED = 0x2;
    public static final int THREAD_POOL_TYPE_COMPUTE = 0x3;

}
