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

/**
 *
 * Created by LiuSaibao on 11/23/2016.
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

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

//        //获取管道
//        ChannelPipeline pipeline = socketChannel.pipeline();
//        //字符串解码器
//        pipeline.addLast(new StringDecoder());
//        //字符串编码器
//        pipeline.addLast(new StringEncoder());
//        //处理类
//        pipeline.addLast(new ServerHandler4());

        if(mIsSSL) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));    // 开启SSL
        }
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));    // 开启日志，可以设置日志等级
//        pipeline.addLast(new IdleStateHandler(30, 60, 100));

        //字符串解码器
        pipeline.addLast(new StringDecoder());
        //字符串编码器
        pipeline.addLast(new StringEncoder());
        //处理类
        pipeline.addLast(new NettyClientHandler(listener));
    }

//    @Override
//    protected void initChannel(SocketChannel ch) throws Exception {
//        // Configure SSL.
//        final SslContext sslCtx;
//        if (mIsSSL) {
//            sslCtx = SslContextBuilder.forClient()
//                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
//        } else {
//            sslCtx = null;
//        }
//
//        ChannelPipeline pipeline = ch.pipeline();
//
//        if(mIsSSL) {
//            pipeline.addLast(sslCtx.newHandler(ch.alloc()));    // 开启SSL
//        }
//        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));    // 开启日志，可以设置日志等级
////        pipeline.addLast(new IdleStateHandler(30, 60, 100));
//        pipeline.addLast(new NettyClientHandler(listener));
//    }
}
