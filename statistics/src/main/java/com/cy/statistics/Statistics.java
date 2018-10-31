package com.cy.statistics;

import android.content.Context;

import com.cy.omniknight.tools.Utils;

import java.util.Map;
import java.util.Vector;

/**
 * 统计接口统一封装，支持多种统计方式，只需要在构造函数add 即可
 *
 * @Created by chenls on 2018/7/18.
 */
final class Statistics implements ICustomStatistics, ICommonStatistics {

    private Vector<AbsStatistics> mObserver;

    public Statistics() {
        mObserver = new Vector<>();
        mObserver.add(new YouJuOperator(Utils.getApp()));
        mObserver.add(new UmengOperator(Utils.getApp()));
    }

    @Override
    public void onEvent(Context context, String eventId) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onEvent(context, eventId);
        }
    }

    @Override
    public void onEvent(Context context, String eventId, String eventLabel) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onEvent(context, eventId, eventLabel);
        }
    }

    @Override
    public void onEvent(Context context, String eventId, String eventLabel, Map<String, Object> eventMap) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onEvent(context, eventId, eventLabel, eventMap);
        }
    }

    @Override
    public void onError(Context context, Throwable throwable) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onError(context, throwable);
        }
    }

    @Override
    public void onPageStart(Context context, String pageName) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onPageStart(context, pageName);
        }
    }

    @Override
    public void onResume(Context context) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onResume(context);
        }
    }

    @Override
    public void onPause(Context context) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onPause(context);
        }
    }

    @Override
    public void onPageEnd(Context context, String pageName) {
        if (mObserver.isEmpty()) {
            return;
        }
        for (AbsStatistics statistics : mObserver) {
            if (!statistics.isAllowedUpload()) {
                return;
            }
            statistics.onPageEnd(context, pageName);
        }
    }
}
