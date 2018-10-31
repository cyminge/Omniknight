package com.cy.threadbus.scheduler;

import android.util.Log;

import com.cy.threadbus.scheduler.controller.Ruler;
import com.cy.threadbus.scheduler.controller.RulerService;
import com.cy.threadbus.utils.Constant;
import com.cy.threadbus.utils.listener.IThreadBusListener;
import com.cy.threadbus.utils.listener.ThreadBusListenerManager;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态调整线程池大小 ps：用于计算的线程池调整策略不一样。 Created by zf on 2017/5/21.
 */

final class PoolSizeChanger implements IThreadBusListener {
    private static final String TAG = "ThreadBus";
    private static final int MIN_POOL_SIZE = 2;
    private static final long CHANGE_DELAY_MILLIS = 1000;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private int mPoolSize;
    private boolean mIsChangePoolSize = false;
    private int mCurrentPoolSize;
    private int mThreadPoolType;
    private Ruler mRuler;

    public PoolSizeChanger(int poolSize, boolean isChangePoolSize, ThreadPoolExecutor threadPoolExecutor,
            int threadPoolType) {
        mPoolSize = poolSize;
        mIsChangePoolSize = isChangePoolSize;
        mThreadPoolExecutor = threadPoolExecutor;
        mThreadPoolType = threadPoolType;
        init();
    }

    private void init() {
        if (isChangePoolSize()) {
            Ruler.Builder builder = new Ruler.Builder();
            if (Constant.THREAD_POOL_TYPE_COMPUTE == mThreadPoolType) {
                builder.setRequiredNetwork(false);
                builder.setRequiresBattery(false);
                builder.setRequiresActivityLifecycle(false);
            }
            mRuler = builder.build();
            RulerService.getInstance().startTrackingRuler(mRuler);
            ThreadBusListenerManager.addListener(this, ThreadBusListenerManager.STATE_CHANGED);
        }
    }

    private boolean isChangePoolSize() {
        return mIsChangePoolSize && mPoolSize > MIN_POOL_SIZE;
    }

    @Override
    public void onEvent(int key, Object... params) {
        if (ThreadBusListenerManager.STATE_CHANGED == key) {
            RulerService.getSubThreadHandler().removeCallbacks(mRunnable);
            RulerService.getSubThreadHandler().postDelayed(mRunnable, CHANGE_DELAY_MILLIS);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            adjustPoolSize();
        }
    };

    /**
     * 区分io跟cpu，判断是IO型的还是cpu型的，如果是cpu型则不需要通过网络做调整
     */
    private void adjustPoolSize() {
        // TODO 1.电量 2.CPU 3.网络 4.前后台 6.用户操作中 // 不关注的: 5.屏灭屏亮
        // 1. 如果电量低，则调整线程池。
        // 2. 如果是在后台（根据网络、cpu）， 则调整线程池。
        // 3. 如果CPU占用率高（根据用户操作中），则调整线程池。
        // 4. 如果网络不好则调整线程池。

        Log.d(TAG, "PoolSizeChanger.adjustPoolSize--> " + mRuler.toString());

        if (mPoolSize <= MIN_POOL_SIZE) {
            return;
        }

        // 如果是计算密集型，则只根据cpu状态进行调整
        if (Constant.THREAD_POOL_TYPE_COMPUTE == mThreadPoolType) {
            if (mRuler.isCpuBusy()) {
                setPoolSize(Math.max(mPoolSize / 2, MIN_POOL_SIZE));
            } else {
                setPoolSize(mPoolSize);
            }
            mRuler.setIsCpuBusy(false);
            return;
        }

        if (mRuler.isLowBattery() || !mRuler.isOnForeground()) {
            if (mCurrentPoolSize != MIN_POOL_SIZE) {
                setPoolSize(MIN_POOL_SIZE);
            }
            return;
        }

        if (mRuler.isCpuBusy()) {
            if (mCurrentPoolSize != MIN_POOL_SIZE) {
                setPoolSize(MIN_POOL_SIZE);
            }
            mRuler.setIsCpuBusy(false);
            return;
        }

        int networkType = mRuler.getNetworkType();
        if (Ruler.NETWORK_TYPE_GOOD == networkType) {
            setPoolSize(mPoolSize);
        } else if (Ruler.NETWORK_TYPE_COMMON == networkType) {
            setPoolSize(Math.max(mPoolSize / 2, MIN_POOL_SIZE));
        } else {
            setPoolSize(MIN_POOL_SIZE);
        }
    }

    private void setPoolSize(int poolSize) {
        if (null == mThreadPoolExecutor || mThreadPoolExecutor.isShutdown()
                || mThreadPoolExecutor.isTerminated()) {
            return;
        }

        mThreadPoolExecutor.setCorePoolSize(poolSize);
        mCurrentPoolSize = poolSize;
    }

}
