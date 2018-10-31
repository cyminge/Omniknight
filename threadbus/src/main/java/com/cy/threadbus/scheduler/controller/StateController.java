package com.cy.threadbus.scheduler.controller;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by JLB6088 on 2017/7/11.
 */

public abstract class StateController {

    protected final ArrayList<Ruler> mTrackingRulers = new ArrayList<>();

    protected StateChangedListener mStateChangedListener;
    protected Context mContext;

    public StateController(StateChangedListener stateChangedListener, Context context) {
        mStateChangedListener = stateChangedListener;
        mContext = context;
    }

    public abstract void maybeStartTracking(Ruler ruler);

}
