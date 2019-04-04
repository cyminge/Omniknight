package com.cy.base;

import com.cy.threadbus.SchedulerFactory;
import com.cy.threadbus.TaskRunnable;
import com.cy.threadbus.ThreadBus;

import java.util.concurrent.TimeUnit;

/**

 */
public class ThreadBusUtil {
    private static final String TAG = "ThreadBusUtil";

    public static void enqueue(final Runnable runnable) {
        String threadName = TAG;
        if (runnable instanceof TaskRunnable) {
            threadName = ((TaskRunnable) runnable).getThreadName();
        }
        TaskRunnable taskRunnable = new TaskRunnable(threadName) {
            @Override
            public void runTask() {
                runnable.run();
            }
        };
        ThreadBus.newAssembler().create(taskRunnable).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).start();
    }

    public static void enqueue(final Runnable runnable, long delaySecond) {
        String threadName = TAG;
        if (runnable instanceof TaskRunnable) {
            threadName = ((TaskRunnable) runnable).getThreadName();
        }
        TaskRunnable taskRunnable = new TaskRunnable(threadName) {
            @Override
            public void runTask() {
                runnable.run();
            }
        };
        ThreadBus.newAssembler().create(taskRunnable).scheduleOn(SchedulerFactory.getUnlimitedScheduler()).delay(delaySecond, TimeUnit.SECONDS).start();
    }
}
