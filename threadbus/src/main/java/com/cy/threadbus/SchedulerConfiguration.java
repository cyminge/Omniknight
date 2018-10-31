package com.cy.threadbus;

import com.cy.threadbus.utils.Constant;

import java.util.concurrent.ThreadFactory;

/**
 * 配置自定义线程池所要传入的参数
 *
 * Created by JLB6088 on 2017/6/1.
 */

public final class SchedulerConfiguration {
    // 排序规则
    private int mSortRule;
    // 线程池大小
    private int mPoolSize;
    // 创建线程的工厂
    private ThreadFactory mThreadFactory;
    // 线程池类型（IO，CPU，混合）
    private int mThreadPoolType;

    public int getSortRule() {
        return mSortRule;
    }

    public int getPoolSize() {
        return mPoolSize;
    }

    public ThreadFactory getThreadFactory() {
        return mThreadFactory;
    }

    public int getThreadPoolType() {
        return mThreadPoolType;
    }

    private SchedulerConfiguration(Builder builder) {
        mSortRule = builder.mSortRule;
        mPoolSize = builder.mPoolSize;
        mThreadFactory = builder.mThreadFactory;
        mThreadPoolType = builder.mThreadPoolType;
    }

    public static final class Builder {
        private int mSortRule = Constant.NO_SORT_RULE;
        private int mPoolSize = Constant.ZERO;
        private ThreadFactory mThreadFactory;
        private int mThreadPoolType = Constant.THREAD_POOL_TYPE_IO;

        public Builder confSortRule(int sortRule) {
            if (sortRule < Constant.NO_SORT_RULE || sortRule > Constant.SORT_RULE_COUNT) {
                mSortRule = Constant.NO_SORT_RULE;
            } else {
                mSortRule = sortRule;
            }
            return this;
        }

        public Builder confPoolSize(int poolSize) {
            mPoolSize = poolSize < Constant.ZERO ? Constant.ZERO : poolSize;
            return this;
        }

        public Builder confThreadFactory(ThreadFactory threadFactory) {
            mThreadFactory = threadFactory;
            return this;
        }

        public Builder confThreadPoolType(int type) {
            mThreadPoolType = type;
            return this;
        }

        public SchedulerConfiguration builder() {
            return new SchedulerConfiguration(this);
        }

    }

}
