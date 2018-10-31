package com.cy.omniknight.tools.tem;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class HandlerUtils {

    private static Handler mMainHandler = new Handler(Looper.getMainLooper());
    private static HandlerThread mHandlerThread;
    private static volatile Handler mLoopHandler;

    /**
     * 主线程post消息
     *
     * @param runnable
     */
    public static void post(Runnable runnable) {
        mMainHandler.post(runnable);
    }


    /**
     * 获取一个子线程Handler
     *
     * @return
     */
    public static Handler getSubThreadHandler() {
        initHandlerThread();
        return mLoopHandler;
    }

    /**
     * 主线程post消息
     *postDelayed(
     * @param runnable
     * @param delayMillis
     */
    public static void postDelayed(Runnable runnable, long delayMillis) {
        mMainHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * 移除主线程消息
     *
     * @param runnable
     */
    public static void removeRunnable(Runnable runnable) {
        mMainHandler.removeCallbacks(runnable);
    }


    public static void startDelay(long startMs, long delayMs, final DelayCallback callback) {
        long deltaTime = Math.abs(System.currentTimeMillis() - startMs);
        long remainTime = delayMs - deltaTime;
        remainTime = Math.max(remainTime, 0);
        remainTime = Math.min(remainTime, delayMs);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                callback.onTimeOut();
            }
        };
        postDelayed(runnable, remainTime);
    }

    private static void initHandlerThread() {
        if (mLoopHandler == null) {
            mHandlerThread = new HandlerThread("WatchDobSubThread");
            mHandlerThread.start();
            mLoopHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    public interface DelayCallback {
        void onTimeOut();
    }
}
