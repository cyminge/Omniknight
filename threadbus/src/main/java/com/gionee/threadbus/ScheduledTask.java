package com.gionee.threadbus;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 *
 * Created by zf on 2017/5/14.
 */

public class ScheduledTask<T> extends FutureTask<T> {
    public ScheduledTask(@NonNull Callable<T> callable) {
        super(callable);
    }

    public ScheduledTask(@NonNull Runnable runnable) {
        super(runnable, null);
    }
}
