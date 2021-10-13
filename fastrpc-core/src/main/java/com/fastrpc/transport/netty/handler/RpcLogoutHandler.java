package com.fastrpc.transport.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zyz
 */

@ChannelHandler.Sharable
@Slf4j
public class RpcLogoutHandler extends ChannelInboundHandlerAdapter {

    /**
     * channel 连接正常断开触发
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("client {[]} logout", ctx.channel().remoteAddress());
        ctx.close();
    }

    /**
     * channel 连接异常断开触发
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        log.info("Exception: client  [{}] is closed", ctx.channel());
        ctx.close();
    }
}
