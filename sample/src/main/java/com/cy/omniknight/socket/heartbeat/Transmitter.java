package com.cy.omniknight.socket.heartbeat;

/**
 * Created by cy on 18-7-10.
 */

public class Transmitter {
    private static final int HEART_RETRY = 3; // 心跳重试次数
    private static final int DEFAULT_INTERVAL = 20;
    private static final int INCREASE_STEP = 5;
    private static final int INCREASE_THRESHHOLD = 20;

    private static final long DECREASE_KEEP_TIME = 1 * 60 * 60 * 1000;

    private int mAlarmCountdown = 0;

}
