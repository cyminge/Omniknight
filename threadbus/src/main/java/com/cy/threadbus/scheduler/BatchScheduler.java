package com.cy.threadbus.scheduler;

import com.cy.threadbus.ScheduledTask;
import com.cy.threadbus.core.IBatchScheduler;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by JLB6088 on 2017/5/15.
 */

public final class BatchScheduler<T> implements IBatchScheduler<T> {

    private ExecutorService mExecutorService;
    private BatchExecutorService<T> mCompletionService;

    public BatchScheduler() {
        mExecutorService = Executors.newCachedThreadPool();
        mCompletionService = new BatchExecutorService<>(mExecutorService);
    }

    @Override
    public void shutdown() {
        mExecutorService.shutdown();
        mCompletionService = null;
    }

    @Override
    public ArrayList<ScheduledTask<T>> schedule(ArrayList<? extends Callable<T>> callableList) {
        if (null == mExecutorService || mExecutorService.isShutdown()) {
            mExecutorService = Executors.newCachedThreadPool();
            mCompletionService = new BatchExecutorService<T>(mExecutorService);
        }

        ArrayList<ScheduledTask<T>> scheduledTasks = new ArrayList<>();
        for (Callable callable : callableList) {
            WorkerReal<T> workerReal = new WorkerReal<>(callable);
            mCompletionService.submit(workerReal);
            scheduledTasks.add(workerReal);
        }

        return scheduledTasks;
    }

}
