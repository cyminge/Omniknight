package com.gionee.threadbus.scheduler;

import android.support.annotation.NonNull;
import com.gionee.threadbus.ScheduledTask;

import java.util.concurrent.*;

/**
 * Created by JLB6088 on 2017/6/26.
 */

final class BatchExecutorService<V> implements CompletionService<V> {

    private final Executor mExecutor;
    private final BlockingQueue<Future<V>> mCompletionQueue;

    private static class QueueingFuture<V> extends FutureTask<Void> {
        private final Future<V> task;
        private final BlockingQueue<Future<V>> completionQueue;

        QueueingFuture(RunnableFuture<V> task, BlockingQueue<Future<V>> completionQueue) {
            super(task, null);
            this.task = task;
            this.completionQueue = completionQueue;
        }

        protected void done() {
            completionQueue.add(task);
        }
    }

    public BatchExecutorService(Executor executor) {
        if (executor == null)
            throw new NullPointerException();
        mExecutor = executor;
        mCompletionQueue = new LinkedBlockingQueue<>();
    }

    public ScheduledTask<V> submit(WorkerReal<V> workerReal) {
        if (workerReal == null)
            throw new NullPointerException();
        mExecutor.execute(new QueueingFuture<>(workerReal, mCompletionQueue));
        return workerReal;
    }

    @NonNull
    @Override
    public Future<V> submit(@NonNull Callable<V> task) {
        return null;
    }

    @NonNull
    @Override
    public Future<V> submit(@NonNull Runnable task, V result) {
        return null;
    }

    public Future<V> take() throws InterruptedException {
        return mCompletionQueue.take();
    }

    public Future<V> poll() {
        return mCompletionQueue.poll();
    }

    public Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException {
        return mCompletionQueue.poll(timeout, unit);
    }

}
