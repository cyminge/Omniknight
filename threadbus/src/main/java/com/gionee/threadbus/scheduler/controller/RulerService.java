package com.gionee.threadbus.scheduler.controller;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import com.gionee.threadbus.TaskRunnable;
import com.gionee.threadbus.utils.listener.ThreadBusListenerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态调整线程池大小的规则管理
 * Created by JLB6088 on 2017/7/17.
 */

public final class RulerService implements StateChangedListener {

    private HandlerThread mHandlerThread;
    private volatile Handler mSubThreadHandler;

    private List<StateController> mStateControllers;

    public static RulerService getInstance() {
        return RulerServiceHolder.RULER_SERVICE;
    }

    private static class RulerServiceHolder {
        private static final RulerService RULER_SERVICE = new RulerService();
    }

    public void init(Context context) {
        mStateControllers = new ArrayList<>();
        addController(new ConnectivityController(this, context));
        addController(new BatteryController(this, context));
        addController(new ActivityLifecycleController(this, (Application) context));
        addController(new CpuController(this, context));
    }

    public void addController(StateController stateController) {
        synchronized (mStateControllers) {
            if (mStateControllers.contains(stateController)) {
                return;
            }
            mStateControllers.add(stateController);
        }
    }

    public void startTrackingRuler(Ruler ruler) {
        getSubHandler().post(new RulerRunnable(ruler));
    }

    private class RulerRunnable extends TaskRunnable {
        private static final String THREAD_NAME = "Start Tracking Ruler";
        private Ruler mRuler;

        private RulerRunnable(Ruler ruler) {
            super(THREAD_NAME);
            mRuler = ruler;
        }

        @Override
        public void runTask() {
            startTracking(mRuler);
        }
    }

    private void startTracking(final Ruler ruler) {
        synchronized (mStateControllers) {
            for (int i = 0, size = mStateControllers.size(); i < size; i++) {
                StateController controller = mStateControllers.get(i);
                controller.maybeStartTracking(ruler);
            }
        }
    }

    @Override
    public void onStateChanged() {
        ThreadBusListenerManager.onEvent(ThreadBusListenerManager.STATE_CHANGED);
    }

    /**
     * 获取子线程的Handler
     * @return
     */
    public static Handler getSubThreadHandler() {
        return getInstance().getSubHandler();
    }

    private Handler getSubHandler() {
        initHandlerThread();
        return mSubThreadHandler;
    }

    private void initHandlerThread() {
        if (mSubThreadHandler == null) {
            mHandlerThread = new HandlerThread("ThreadBus-HandlerThread");
            mHandlerThread.start();
            mSubThreadHandler = new Handler(mHandlerThread.getLooper());
        }
    }
}
