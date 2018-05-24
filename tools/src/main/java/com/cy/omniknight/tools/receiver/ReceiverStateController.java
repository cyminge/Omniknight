package com.cy.omniknight.tools.receiver;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by cy on 18-5-23.
 */

public abstract class ReceiverStateController {

    protected ArrayList<StateChangedListener> mStateChangedListenerArray = new ArrayList<>();
    protected Context mContext;

    public ReceiverStateController(Context context) {
        mContext = context;
    }

    public abstract void startTracking(StateChangedListener stateChangedListener);

    public abstract void stopTracking(StateChangedListener stateChangedListener);
}
