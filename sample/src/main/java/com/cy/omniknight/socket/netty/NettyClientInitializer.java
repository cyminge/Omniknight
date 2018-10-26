package com.cy.omniknight.socket.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;

/**
 *
 * Created by LiuSaibao on 11/23/2016.
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "10")); // 服务器返回消息超时时间??
    private static final int WRITER_TIMEOUT = Integer.parseInt(System.getProperty("writerTimeout", "40")); // 服务器返回消息超时时间??

    private NettyListener listener;
    private boolean mIsSSL;

    private int WRITE_WAIT_SECONDS = 10;

    private int READ_WAIT_SECONDS = 13;

    public NettyClientInitializer(NettyListener listener, boolean isSSL) {
        this.listener = listener;
        mIsSSL = isSSL;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (mIsSSL) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        ChannelPipeline pipeline = ch.pipeline();
        if(mIsSSL) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));    // 开启SSL
        }
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));    // 开启日志，可以设置日志等级
//        pipeline.addLast(new IdleStateHandler(30, 60, 100));

        //字符串解码器
        pipeline.addLast(new StringDecoder());
        //字符串编码器
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new IdleStateHandler(READ_TIMEOUT, WRITER_TIMEOUT, 0));
        //处理类
        pipeline.addLast(new NettyClientHandler(listener));
    }
}
