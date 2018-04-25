package com.cy.omniknight.socket.transport;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.cy.omniknight.socket.message.MMessage;
import com.cy.omniknight.socket.netty.NettyClientInitializer;
import com.cy.omniknight.socket.netty.NettyListener;
import com.cy.omniknight.tracer.Tracer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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


    public LongConnectCenter(Context context, String address, int port, boolean isSSL) {
        super(THREAD_NAME);

        mContext = context;
        mIPAddress.mAddress = address;
        mIPAddress.mPort = port;
        mIPAddress.mIsSSL = isSSL;
        mConnectStateMachine = new ConnectStateMachine();
    }

    @Override
    public void onMessageResponse(ByteBuf byteBuf) {

    }

    @Override
    public void onServiceStatusConnectChanged(int statusCode) {

    }

    private class IPAddress {
        private String mAddress;
        private int mPort;
        private boolean mIsSSL;
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
        }
        return false;
    }

    /**
     * 连接服务器
     */
    private void connectInternal() {
        synchronized (mConnectLock) {
            if(!mConnectStateMachine.checkState(ConnectStateMachine.ConnectState.BROKEN)) {
                return;
            }

            mConnectStateMachine.setState(ConnectStateMachine.ConnectState.CONNECTING);

            try {
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
                Channel channel = future.channel();
                // Wait until the connection is closed.
                channel.closeFuture().sync();
                mChannel = channel;
                mConnectStateMachine.setState(ConnectStateMachine.ConnectState.CONNECTED);
            } catch (Exception e) {
                if(null !=  mEventLoopGroup) {
                    mEventLoopGroup.shutdownGracefully();
                }
                mConnectStateMachine.setState(ConnectStateMachine.ConnectState.BROKEN);
                Tracer.w("cyTest", e.getMessage());
            } finally {
            }
        }

    }

    private void disConnectInternal() {
        synchronized (mConnectLock) {
            if (mConnectStateMachine.checkState(ConnectStateMachine.ConnectState.BROKEN)
                    || mConnectStateMachine.checkState(ConnectStateMachine.ConnectState.CLOSING)) {
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


    }

    public void stopWork() {

    }

    /**
     * 判断长连接的状态，是否为以建立连接
     *
     * @return
     */
    public boolean isEstablished() {
        if (mConnectStateMachine.checkState(ConnectStateMachine.ConnectState.ESTABLISH)) {
            return true;
        } else {
            return false;
        }
    }

    public void keepEstablish() {

    }

    public void sendMessage(MMessage message) {

    }
}
