package com.cy.omniknight.socket.transport;

/**
 * PUSH连接状态
 * Created by zhanmin on 18-4-21.
 */

public class ConnectStateMachine {

    public enum ConnectState {
        BROKEN, // 未连接状态
        CONNECTING, // 正在连接
        CONNECTED, // 已连接
        LOGINING, // 已经连接，正在登陆中，未完成登陆
        ESTABLISH, // 已经登陆成功
        LOGOUTING, // 正在注销
        CLOSING, // 正在关闭连接
        CHAOS; // 混乱状态，进入后，该连接不可用
    }

    private ConnectState mState = ConnectState.BROKEN;

    /**
     * 检测PUSH是否为某状态
     *
     * @param state
     * @return
     */
    public boolean isInState(ConnectState state) {
        synchronized (mState) {
            if (mState == state) {
                return true;
            }
            return false;
        }
    }

    /**
     * 设置PUSH状态接口
     *
     * @param state 新状态
     */
    public void setState(ConnectState state) {
        ConnectState oriState = this.mState; // 设置前的状态
        synchronized (mState) {
            this.mState = state;
        }

//        if (state == ConnectState.ESTABLISH) {
//            if (oriState != ConnectState.ESTABLISH) {
//                boolean reload = keepAliveFilter.needReload();
//                MProxy.onTransPointEnable(tpName, true, reload); // 连接建立
//                proxyTimeoutTimes = 0;
//            }
//        } else {
//            if (oriState == NetState.ESTABLISH) {
//                MProxy.onTransPointEnable(tpName, false, false); // 连接断开
//            }
//        }
    }

    public ConnectState getCurrState() {
        synchronized (mState) {
            return mState;
        }
    }
}
