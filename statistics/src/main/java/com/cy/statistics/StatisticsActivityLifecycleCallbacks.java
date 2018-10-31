package com.cy.statistics;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @Created by chenls on 2018/7/20.
 */
class StatisticsActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private StatisticsManager mStatisticsManager;

    public StatisticsActivityLifecycleCallbacks(StatisticsManager statisticsManager) {
        mStatisticsManager = statisticsManager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mStatisticsManager.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mStatisticsManager.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
