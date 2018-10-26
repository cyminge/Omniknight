package com.cy.omniknight.socket.netty;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 * Created by LiuSaibao on 11/23/2016.
 */
public interface NettyListener {

    byte STATUS_CONNECT_SUCCESS = 1;

    byte STATUS_CONNECT_CLOSED = 0;

    byte STATUS_CONNECT_ERROR = 0;


    /**
     * 当接收到系统消息
     */
    void onMessageResponse(String byteBuf);

    /**
     * 当服务状态发生变化时触发
     */
    void onServiceStatusConnectChanged(ChannelHandlerContext ctx, int statusCode);
}
