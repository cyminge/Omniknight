package com.cy.omniknight.socket.netty;

import com.cy.omniknight.tracer.Tracer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 *
 * Created by LiuSaibao on 11/23/2016.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    private NettyListener listener;
    public NettyClientHandler(NettyListener listener){
        this.listener = listener;
    }
    
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		NettyClient.getInstance().setConnectStatus(true);
		listener.onServiceStatusConnectChanged(ctx, NettyListener.STATUS_CONNECT_SUCCESS);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		NettyClient.getInstance().setConnectStatus(false);
		listener.onServiceStatusConnectChanged(ctx, NettyListener.STATUS_CONNECT_CLOSED);
//		NettyClient.getInstance().reconnect();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, String byteBuf) throws Exception {
		listener.onMessageResponse(byteBuf);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}

		IdleStateEvent e = (IdleStateEvent) evt;
		if (e.state() == IdleState.READER_IDLE) {
			// The connection was OK but there was no traffic for last period.
//			Tracer.d("cyTest", "read idle");
//			ctx.close();
		} else if (e.state() == IdleState.WRITER_IDLE) {
			Tracer.d("cyTest", "writer idle");
			// TODO 发心跳

		}

//		if (evt instanceof IdleStateEvent) {
//			IdleStateEvent event = (IdleStateEvent) evt;
//			if (event.state() == IdleState.READER_IDLE){
//				ctx.close();
//			}else if (event.state() == IdleState.WRITER_IDLE){
//				try{
//					ctx.channel().writeAndFlush("Chilent-Ping\r\n");
//				} catch (Exception e){
//					Timber.e(e.getMessage());
//				}
//			}
//		}
//		super.userEventTriggered(ctx, evt);
	}
}
