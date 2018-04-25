package com.cy.omniknight.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cy.omniknight.socket.push.DispatchCenter;
import com.cy.omniknight.socket.transport.SProxy;

/**
 * Created by zhanmin on 18-4-13.
 */

public class SPushService extends Service {

    public static final String ACTION_START_HEART = "SOCKET_PUSH_START_HEART";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action;
        if(null == intent) {
            action = ACTION_START_HEART;
        } else {
            action = intent.getAction();
        }

        switch (action) {
            case ACTION_START_HEART:
                SProxy.INSTANCE.initial(this, new DispatchCenter());
                break;
        }

        return START_REDELIVER_INTENT;
    }

    private void startHeartBeat() {
        // 定时唤醒服务
    }

    private void stopHeartBeat() {

    }
}
