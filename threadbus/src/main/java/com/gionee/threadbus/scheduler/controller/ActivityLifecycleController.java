package com.gionee.threadbus.scheduler.controller;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import com.gionee.threadbus.TaskRunnable;

/**
 * Created by JLB6088 on 2017/7/17.
 */

final class ActivityLifecycleController extends StateController {

    private static final long DEFAULT_DELAY_MILLIS = 5000;
    private ActivityLifecycleListener mActivityLifecycleListener;
    private boolean mIsFirstLoad = true;

    public ActivityLifecycleController(StateChangedListener stateChangedListener, Application application) {
        super(stateChangedListener, application);
    }

    @Override
    public void maybeStartTracking(Ruler ruler) {
        if (ruler.isRequiresActivityLifecycleChange()) {
            if(null == mActivityLifecycleListener) {
                mActivityLifecycleListener = new ActivityLifecycleListener();
                ((Application)mContext).registerActivityLifecycleCallbacks(mActivityLifecycleListener);
            }

            synchronized (mTrackingRulers) {
                mTrackingRulers.add(ruler);
            }
        }
    }

    private final class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
        public int mActivityCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ViewTreeObserver vto = activity.getWindow().getDecorView().getViewTreeObserver();
            if (!vto.isAlive()) {
                return;
            }
            vto.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    // TODO 当需要根据当前是否有滑动事件来调整线程池大小的时候可以通过监听这个事件
                }
            });
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (mActivityCount == 0 && !mIsFirstLoad) {
                mIsFirstLoad = false;
                RulerService.getSubThreadHandler().removeCallbacks(mActivityLifecycleRunnable);
                RulerService.getSubThreadHandler().postDelayed(mActivityLifecycleRunnable, DEFAULT_DELAY_MILLIS);
            }
            mActivityCount++;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            mActivityCount--;
            if (mActivityCount == 0) {
                RulerService.getSubThreadHandler().removeCallbacks(mActivityLifecycleRunnable);
                RulerService.getSubThreadHandler().postDelayed(mActivityLifecycleRunnable, DEFAULT_DELAY_MILLIS);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        private TaskRunnable mActivityLifecycleRunnable = new TaskRunnable("Update Activity Lifecycle State") {

            @Override
            public void runTask() {
                updateForeBackGroundState();
            }
        };

        private void updateForeBackGroundState() {
            boolean isOnForeground = mActivityCount > 0;
            boolean isChanged = false;
            synchronized (mTrackingRulers) {
                for (int i = 0, size = mTrackingRulers.size(); i < size; i++) {
                    isChanged |= mTrackingRulers.get(i).setIsOnForeground(isOnForeground);
                }

                if (isChanged) {
                    mStateChangedListener.onStateChanged();
                }
            }
        }
    }

}
