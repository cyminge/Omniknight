package com.cy.omniknight.tools.receiver;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;

/**
 * Created by JLB6088 on 2017/7/17.
 */

public final class ReceiverManager {

    public static final String CHANGE_TYPE_NETWORK = "networkChanged";

    private HandlerThread mHandlerThread;
    private volatile Handler mSubThreadHandler;

    private HashMap<String, ReceiverStateController> mStateControllers;

    public static ReceiverManager getInstance() {
        return ReceiverManagerHolder.RULER_SERVICE;
    }

    private static class ReceiverManagerHolder {
        private static final ReceiverManager RULER_SERVICE = new ReceiverManager();
    }

    public void init(Context context) {
        if(null != mStateControllers) {
            return;
        }

        mStateControllers = new HashMap<>();
        addController(CHANGE_TYPE_NETWORK, new ConnectivityController(context));
    }

    public void addController(String changeType, ReceiverStateController stateController) {
        if(null == mStateControllers) {
            return;
        }
        synchronized (mStateControllers) {
            if (mStateControllers.containsKey(changeType)) {
                return;
            }
            mStateControllers.put(changeType, stateController);
        }
    }

    public void startTracking(String changeType, StateChangedListener listener) {
        if(null == mStateControllers || !mStateControllers.containsKey(changeType)) {
            return;
        }
        synchronized (mStateControllers) {
            ReceiverStateController controller = mStateControllers.get(changeType);
            controller.startTracking(listener);
        }
    }

    public void stopTracking(String changeType, StateChangedListener listener) {
        if(null == mStateControllers || !mStateControllers.containsKey(changeType)) {
            return;
        }
        synchronized (mStateControllers) {
            ReceiverStateController controller = mStateControllers.get(changeType);
            controller.stopTracking(listener);
        }
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
