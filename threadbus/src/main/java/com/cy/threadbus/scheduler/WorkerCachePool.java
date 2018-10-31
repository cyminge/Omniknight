package com.cy.threadbus.scheduler;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by JLB6088 on 2017/5/10.
 */

final class WorkerCachePool<T> {

    private PriorityBlockingQueue<WorkerReal<T>> mWorkerQueue = new PriorityBlockingQueue<>();

    public void addWorker(WorkerReal<T> worker) {
        synchronized (mWorkerQueue) {
            mWorkerQueue.add(worker);
            mWorkerQueue.notifyAll();
            worker.setWorkerCachePool(this);
        }
    }

    public WorkerReal<T> getWorker() {
        synchronized (mWorkerQueue) {
            WorkerReal worker;
            while (true) {
                if (mWorkerQueue.size() == 0) {
                    return null;
                }

                worker = mWorkerQueue.poll();
                if (worker != null) {
                    return worker;
                }
            }
        }
    }

    public boolean containsWorker(WorkerReal<T> workerReal) {
        synchronized (mWorkerQueue) {
            return mWorkerQueue.contains(workerReal);
        }
    }

    public void removeWorker(WorkerReal<T> workerReal) {
        synchronized (mWorkerQueue) {
            mWorkerQueue.remove(workerReal);
            mWorkerQueue.notifyAll();
        }
    }

    public void removeAll() {
        synchronized (mWorkerQueue) {
            mWorkerQueue.clear();
            mWorkerQueue.notifyAll();
        }
    }

    public boolean isEmpty() {
        return mWorkerQueue.isEmpty();
    }

    public int getCacheSize() {
        return mWorkerQueue.size();
    }

}
