package com.cy.threadbus.utils.listener;

/**
 *
 * Created by zf on 2017/5/21.
 */

public interface IThreadBusListener {
    void onEvent(int key, Object... params);
}
