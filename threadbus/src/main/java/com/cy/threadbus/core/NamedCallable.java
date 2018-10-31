package com.cy.threadbus.core;

import android.text.TextUtils;

import java.util.concurrent.Callable;

/**
 * Created by JLB6088 on 2017/5/16.
 */

public abstract class NamedCallable<T> implements Callable<T> {

    private String mThreadName;

    public NamedCallable(String threadName) {
        mThreadName = threadName;
    }

    @Override
    public T call() throws Exception {
        if (TextUtils.isEmpty(mThreadName)) {
            return callTask();
        }

        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(mThreadName);
        try {
            return callTask();
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    public String getThreadName() {
        return mThreadName;
    }

    protected abstract T callTask();
}
