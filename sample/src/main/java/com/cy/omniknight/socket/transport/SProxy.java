package com.cy.omniknight.socket.transport;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.cy.omniknight.socket.message.MCSerFilter;
import com.cy.omniknight.socket.message.MCache;
import com.cy.omniknight.socket.message.MEndPoint;
import com.cy.omniknight.socket.message.MMessage;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhanmin on 18-4-11.
 */

public enum SProxy {

    INSTANCE;

    private static final String STR_TIMEOUT_THREAD_NAME = "SProxy.thread.timeout";

    private Context mContext;
    private MCache mMCache;
    private HashMap<String, MTransPoint> mTransPointMap = new HashMap<>();

    private THandlerTimeOut handlerTimeOut = null;
    private HandlerThread timeOutThread = null;

    private ACK mACKCallback;

    SProxy() {

    }

    public interface ACK {
        void handleACK(MMessage message, int errorCode);
    }

    public void initial(Context context, ACK callback) {
        if(null != mContext) {
            return;
        }
        mContext = context.getApplicationContext();
        mACKCallback = callback;
        mMCache = new MCache();
        mMCache.addFilter(new MCSerFilter());

        // 添加序列化工具


        registerTransPoint(new UdpProtoBufTransPoint(context, mMCache, "192.168.27.51", 9527, false));

        // 超时重发机制
        timeOutThread = new HandlerThread(STR_TIMEOUT_THREAD_NAME);
        timeOutThread.start();
        handlerTimeOut = new THandlerTimeOut(timeOutThread.getLooper(), SProxy.this);
    }

    public void registerTransPoint(MTransPoint transPoint) {
        if (transPoint == null || TextUtils.isEmpty(transPoint.getTypeName())) {
            return;
        }

        if (null == mTransPointMap.get(transPoint.getTypeName())) {
            transPoint.initital();
            mTransPointMap.put(transPoint.getTypeName(), transPoint);
        }
    }

    public MTransPoint getTransPoint(MEndPoint endPoint) {
        if (endPoint == null) {
            return null;
        }
        return getTransPoint(endPoint.getTransType());
    }

    public MTransPoint getTransPoint(String typeName) {
        if (typeName == null || typeName.length() == 0) {
            return null;
        }
        return mTransPointMap.get(typeName);
    }

    public void deInitial() {
        if(null == mContext) {
            return;
        }

        synchronized (mTransPointMap) {
            if (null != timeOutThread) {
                timeOutThread.quit();
                timeOutThread = null;
                handlerTimeOut = null;
            }

            Iterator<MTransPoint> iter = mTransPointMap.values().iterator();
            MTransPoint mtp = null;
            while (iter.hasNext()) {
                mtp = (MTransPoint) iter.next();
                mtp.deinitial();
            }
            mTransPointMap.clear();

			/*
			 * if (mInvalidFilterCleanReceiver != null) {
			 * mContext.unregisterReceiver(mInvalidFilterCleanReceiver);
			 * mInvalidFilterCleanReceiver = null; }
			 */

            mMCache = null;
            mContext = null;

        }
    }

    public int sendMessage(MMessage message) {
        if(null == message || null == message.getMessageObject()) {
            return TransResult.ERR_EMPTY_MSG;
        }

        MTransPoint transPoint = getTransPoint(message.getEndPoint());
        if(null == transPoint || !transPoint.isEnable()) {
            return TransResult.ERR_INVALID_ENDPOINT;
        }

        int result = 0;
        if(MMessage.MResponseDependent.NO_RESPONSE != message.getResponseDependent()){
            result = inCache(message);
        }

        if(result >= 0) {
            // 启动消息超时检测
            handlerTimeOut.sendMessageDelayed(handlerTimeOut.obtainMessage(result, message), message.getTransTimeout());
            delevery(message);
        }

        return -1;
    }

    public int inCache(MMessage message) {
        if (null != mMCache) {
            return mMCache.inCache(message);
        }
        return 0;
    }

    public MMessage outCache(String key) {
        if (null != mMCache) {
            return mMCache.outCache(key);
        }
        return null;
    }

    /**
     * 通过指定节点转发消息
     *
     * @param message
     */
    private boolean delevery(MMessage message) {
        if(null == message) {
//            MinaLog.e("send null message !!!");
            return false;
        }

        MTransPoint transPoint = getTransPoint(message.getEndPoint());
        if(null == transPoint) {
            return false;
        }

        if(!transPoint.isEnable()) {
            transPoint.establishTrigger();
            return false;
        }

        message.incTransTimes();
        /* 若传输节点有效，立刻转发 */
        transPoint.sendMessage(message);
        if (message.getDoubleSend()) { // 重复发送
            transPoint.sendMessage(message);
        }
        return true;
    }

    /**
     * 请求发送超时处理handler
     *
     */
    private class THandlerTimeOut extends Handler {

        private WeakReference<SProxy> wrProxy;

        public THandlerTimeOut(Looper looper, SProxy proxy) {
            super(looper);
            wrProxy = new WeakReference<SProxy>(proxy);
        }

        @Override
        public void handleMessage(Message msg) {
            if(null == wrProxy || null == wrProxy.get()) {
                return;
            }
            SProxy proxy = wrProxy.get();
            int msgId = msg.what;

//            MinaLog.i("当前缓冲信息 ： " + getCachedListString());

            MMessage mmsg = proxy.getCachedMessage(String.valueOf(msgId));
            if(null == mmsg) {
                return;
            }

            if (mmsg.getTransTimes() >= mmsg.getTransRetry() + 1) { // retry + 1 == transmit times，已经达到重发次数
                proxy.outCache(mmsg.getMsgId());
                proxy.handleACK(mmsg, TransResult.ERR_SEND_TIMEOUT);
//              MinaLog.e("等待回复包超时[" + msgId + "] ，删除消息");
            } else { // 重发
                proxy.resend(mmsg);
                sendMessageDelayed(obtainMessage(msg.what, mmsg), mmsg.getTransTimeout());
//                    MinaLog.i("等待回复包超时[" + msgId + "], 重发消息， 剩余重发次数 : " + (mmsg.getTransRetry() + 1 - mmsg.getTransTimes()));
            }
        }

    }

    private boolean resend(MMessage message) {
        if (null == mMCache || null == mMCache.getValue(message.getMsgId())) {
//            Tracer.e(THIS_FILE, "resend message but msg id is not in cache");
        }
        return delevery(message);
    }

    private void handleACK(MMessage message, int errorCode) {
        mACKCallback.handleACK(message, errorCode);
    }

    private String getCachedListString() {
        StringBuilder builder = new StringBuilder();
        Collection<MMessage> messages = mMCache.listMessage();
        for (MMessage mMessage : messages) {
            builder.append(mMessage.getMsgId() + ",");
        }
        return builder.toString();
    }

    public MMessage getCachedMessage(String key) {
        if (null != mMCache) {
            Object msg = mMCache.getValue(key);
            if (msg instanceof MMessage) {
                return (MMessage) msg;
            }
        }
        return null;
    }
}
