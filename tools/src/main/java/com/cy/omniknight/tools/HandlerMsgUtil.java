package com.cy.omniknight.tools;

import android.os.Handler;
import android.os.Message;

public class HandlerMsgUtil {

    public static void sendEmptyHandlerMsg(Handler handler, int what) {
        if (null == handler) {
            return;
        }

        handler.obtainMessage(what).sendToTarget();
    }

    public static void sendEmptyHandlerMsgDelay(Handler handler, int what, long delayMillis) {
        if (null == handler) {
            return;
        }

        handler.sendEmptyMessageDelayed(what, delayMillis);
    }

    public static void resendEmptyHandlerMsg(Handler handler, int what) {
        if (null == handler) {
            return;
        }

        if (handler.hasMessages(what)) {
            handler.removeMessages(what);
        }

        handler.obtainMessage(what).sendToTarget();
    }

    public static void resendEmptyHandlerMsgDelay(Handler handler, int what, long delayMillis) {
        if (null == handler) {
            return;
        }

        if(handler.hasMessages(what)) {
            handler.removeMessages(what);
        }

        handler.sendEmptyMessageDelayed(what, delayMillis);
    }

    public static void sendHandlerMsg(Handler handler, Message message) {
        if (null == handler || null == message) {
            return;
        }

        handler.sendMessage(message);
    }

    public static void resendHandlerMsg(Handler handler, Message message) {
        if (null == handler || null == message) {
            return;
        }

        if (handler.hasMessages(message.what)) {
            handler.removeMessages(message.what);
        }

        handler.sendMessage(message);
    }

    public static void sendHandlerMsgDelay(Handler handler, Message message, long delayMillis) {
        if (null == handler || null == message) {
            return;
        }

        handler.sendMessageDelayed(message, delayMillis);
    }

    public static void resendHandlerMsgDelay(Handler handler, Message message, long delayMillis) {
        if(null == handler || null == message) {
            return;
        }

        if (handler.hasMessages(message.what)) {
            handler.removeMessages(message.what);
        }

        handler.sendMessageDelayed(message, delayMillis);
    }

    public static void sendHandlerMsg(Handler handler, int what, Object obj) {
        if (null == handler) {
            return;
        }

        handler.obtainMessage(what, obj).sendToTarget();
    }

    public static void resendHandlerMsg(Handler handler, int what, Object obj) {
        if (null == handler) {
            return;
        }

        if(handler.hasMessages(what)) {
            handler.removeMessages(what);
        }

        handler.obtainMessage(what, obj).sendToTarget();
    }

    public static void sendHandlerMsgDelay(Handler handler, int what, Object obj, long delayMillis) {
        if (null == handler) {
            return;
        }

        handler.obtainMessage(what, obj).sendToTarget();
    }

    public static void resendHandlerMsgDelay(Handler handler, int what, Object obj, long delayMillis) {
        if (null == handler) {
            return;
        }

        if(handler.hasMessages(what)) {
            handler.removeMessages(what);
        }

        handler.obtainMessage(what, obj).sendToTarget();
    }


    public static void removeHandlerMsg(Handler handler, int... what) {
        if (null == handler) {
            return;
        }

        for (int item : what) {
            if (handler.hasMessages(item)) {
                handler.removeMessages(item);
            }
        }

    }
}
