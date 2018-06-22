package com.cy.omniknight.socket.transport;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.cy.omniknight.socket.message.MMessage;
import com.cy.omniknight.socket.netty.NettyClientInitializer;
import com.cy.omniknight.socket.netty.NettyListener;
import com.cy.omniknight.tools.StringUtil;
import com.cy.omniknight.tools.receiver.ReceiverManager;
import com.cy.omniknight.tools.receiver.StateChangedListener;
import com.cy.omniknight.tracer.Tracer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by zhanmin on 18-4-13.
 */

public class LongConnectCenter extends HandlerThread implements Handler.Callback, NettyListener {

    private static final String THREAD_NAME = "netty-connect-center";

    private static final int MSG_CONNECT = 10001;	 // 发起连接
    private static final int MSG_RECONNECT = 10002;  // 重连
    private static final int MSG_SHUTDOWN = 10003;   // 关闭连接
    private static final int MSG_SEND = 10004;		 // 发送消息
    private static final int MSG_LOGIN = 10005;		 // 登陆
    private static final int MSG_LOGOUT = 10006;	 // 注销
    private static final int MSG_KEEPALIVE = 11000;   // 连接保持
    private static final int MSG_KEEPALIVE_TICK = 9527;   // 连接动态保持

    private Context mContext;

    private IPAddress mIPAddress;
    /* android 线程事件处理器 */
    private static Handler mThreadHandler;

    private EventLoopGroup mEventLoopGroup;

    private ConnectStateMachine mConnectStateMachine;

    private final Object mConnectLock = new Object();

    private Channel mChannel;


    @Override
    public synchronized void start() {
        super.start();
    }

    public LongConnectCenter(Context context, String address, int port, boolean isSSL) {
        super(THREAD_NAME);

        mContext = context;
        mIPAddress = new IPAddress(address, port, isSSL);
        mConnectStateMachine = new ConnectStateMachine();
    }

    @Override
    public void onMessageResponse(String byteBuf) {
        Log.e("cyTest", "-- onMessageResponse --> "+byteBuf);
    }

    @Override
    public void onServiceStatusConnectChanged(ChannelHandlerContext ctx, int statusCode) {
        Log.e("cyTest", "-- onServiceStatusConnectChanged -- statusCode:"+statusCode);
        if(NettyListener.STATUS_CONNECT_SUCCESS == statusCode) {
            mChannel = ctx.channel();
            if(isNeedLogin) {
                mConnectStateMachine.setState(ConnectStateMachine.ConnectState.CONNECTED);
            } else {
                mConnectStateMachine.setState(ConnectStateMachine.ConnectState.ESTABLISH);
            }

            // TODO　测试发送消息
            ctx.channel().writeAndFlush("i am client !");
            sendMessage(null);
        } else {
            // TODO 连接断开，重试？
        }
    }

    private class IPAddress {
        private String mAddress;
        private int mPort;
        private boolean mIsSSL;

        public IPAddress(String address, int port, boolean isSSL) {
            mAddress = address;
            mPort = port;
            mIsSSL = isSSL;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_CONNECT :
                connectInternal();
                break;
            case MSG_SHUTDOWN :
                disConnectInternal();
                break;
            case MSG_SEND :
                if(!mConnectStateMachine.isInState(ConnectStateMachine.ConnectState.ESTABLISH)) {
                    return false;
                }
                mChannel.writeAndFlush("i am client by manual operation !");
                mThreadHandler.sendEmptyMessageDelayed(MSG_SEND, 30000);
                break;
        }
        return false;
    }

    private boolean isNeedLogin = false;

    /**
     * 连接服务器
     */
    private void connectInternal() {
        synchronized (mConnectLock) {
            if(!mConnectStateMachine.isInState(ConnectStateMachine.ConnectState.BROKEN)) {
                return;
            }

            mConnectStateMachine.setState(ConnectStateMachine.ConnectState.CONNECTING);

            try {
                Tracer.d("cyTest", "---- 客户端开始连接服务器 ----");
                mEventLoopGroup = new NioEventLoopGroup();
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(mEventLoopGroup)
                        .option(ChannelOption.SO_KEEPALIVE,true)
                        .channel(NioSocketChannel.class)
                        .handler(new NettyClientInitializer(this, mIPAddress.mIsSSL));
                // Make the connection attempt.
                ChannelFuture future = bootstrap.connect(mIPAddress.mAddress, mIPAddress.mPort)
                        // 可以通过异步的方式来获取连接的状态，
                        // 但是从逻辑上来看，如果没有连接的话不应该有下一步动作，所以这里不设置监听
                        // 采用同步的方式等待连接是否成功。
//                        .addListener(new ChannelFutureListener() {
//                            @Override
//                            public void operationComplete(ChannelFuture future) throws Exception {
//                            }
//                        })
                        .sync();
//                future.awaitUninterruptibly(); // 等待异步操作完成。
//                Channel channel = future.channel();
                // Wait until the connection is closed.
//                channel.closeFuture().sync();
            } catch (Exception e) {
                if(null !=  mEventLoopGroup) {
                    mEventLoopGroup.shutdownGracefully();
                }
                mConnectStateMachine.setState(ConnectStateMachine.ConnectState.BROKEN);
                Tracer.w("cyTest", "连接异常－－> " + e.getMessage());
            } finally {
                Tracer.d("cyTest", "-- over --");
            }
        }

    }

    private void disConnectInternal() {
        synchronized (mConnectLock) {
            if (mConnectStateMachine.isInState(ConnectStateMachine.ConnectState.BROKEN)
                    || mConnectStateMachine.isInState(ConnectStateMachine.ConnectState.CLOSING)) {
                return;
            }

            mConnectStateMachine.setState(ConnectStateMachine.ConnectState.CLOSING);

            if(null != mChannel && mChannel.isActive()) {
//                mChannel.close();
            }

            if(null !=  mEventLoopGroup) {
                mEventLoopGroup.shutdownGracefully();
            }
        }

        mThreadHandler.removeMessages(MSG_CONNECT);
        mThreadHandler.removeMessages(MSG_SHUTDOWN);

        mConnectStateMachine.setState(ConnectStateMachine.ConnectState.BROKEN);
    }

    private void connect() {
        mThreadHandler.obtainMessage(MSG_CONNECT).sendToTarget();
    }

    private void disconnect(boolean immediately) {
        mThreadHandler.obtainMessage(MSG_SHUTDOWN).sendToTarget();
    }

    public void startWork() {
        start(); // 启动线程
        mThreadHandler = new Handler(getLooper(), this);// 创建子线程的Handler

        keepEstablish(); // 保持连接
        ReceiverManager.getInstance().startTracking(ReceiverManager.CHANGE_TYPE_NETWORK, mStateChangedListener);
    }

    private String mLastNetworkTypeName = null;
    private String mLastNetworkSubTypeName = null;
    private int mCustomNetworkType = -1;

    private StateChangedListener mStateChangedListener = new StateChangedListener() {
        @Override
        public void onStateChanged(String changeType, Object... params) {
            switch (changeType) {
                case ReceiverManager.CHANGE_TYPE_NETWORK : // 網絡狀態切換
                    mCustomNetworkType = Integer.parseInt(params[0].toString());
                    NetworkInfo info = (NetworkInfo) params[1];
                    dealWithNetworkStateChange(info);
                    break;
                default :
                    break;
            }
        }
    };

    private void dealWithNetworkStateChange(NetworkInfo info) {
        if(null == info || !info.isConnected()) {
            mLastNetworkTypeName = null;
            mLastNetworkSubTypeName = null;
            disconnect(true);
            return;
        }

        if(isSameNetwork(info.getTypeName(), info.getSubtypeName())) {
            return;
        }

        mLastNetworkTypeName = info.getTypeName();
        mLastNetworkSubTypeName = info.getSubtypeName();

        if(mConnectStateMachine.isInState(ConnectStateMachine.ConnectState.BROKEN)) {
            disconnect(true);
        }

        keepEstablish();
    }

    private boolean isSameNetwork(String networkTypeName, String networkSubTypeName) {
        if (StringUtil.equalNoThrow(networkTypeName, mLastNetworkTypeName) &&
                StringUtil.equalNoThrow(networkSubTypeName, mLastNetworkSubTypeName)) {
            return true;
        }
        return false;
    }

    public void stopWork() {
        ReceiverManager.getInstance().stopTracking(ReceiverManager.CHANGE_TYPE_NETWORK, mStateChangedListener);
        disconnect(true);
    }

    /**
     * 判断长连接的状态，是否为以建立连接
     *
     * @return
     */
    public boolean isEstablished() {
        if (mConnectStateMachine.isInState(ConnectStateMachine.ConnectState.ESTABLISH)) {
            return true;
        } else {
            return false;
        }
    }

    public void keepEstablish() {
        synchronized (this) { // ??
            if(!isNetworkConnected()) {
                return;
            }

            switch (mConnectStateMachine.getCurrState()) {
                case BROKEN :
                    connect();
                    break;
                case CONNECTED:
                    if(isNeedLogin) {
                        toLogin();
                    }
                    break;
                case CONNECTING:
                    break;
                case LOGINING:
                    break;
                case ESTABLISH:
                    break;
                case LOGOUTING:
                    break;
                case CLOSING:
                    break;
                case CHAOS:
                    break;
                default:
                    break;
            }
        }
    }

    private void toLogin() {

    }

    private boolean isNetworkConnected() {
        return mCustomNetworkType != -1;
    }

    public void sendMessage(MMessage message) {
        mThreadHandler.sendEmptyMessageDelayed(MSG_SEND, 30000);
    }
}
