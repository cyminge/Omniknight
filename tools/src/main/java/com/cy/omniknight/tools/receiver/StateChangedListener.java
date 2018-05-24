package com.cy.omniknight.tools.receiver;

/**
 * Created by JLB6088 on 2017/7/17.
 */

public interface StateChangedListener {

    void onStateChanged(String changeType, Object... params);
}
