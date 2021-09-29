package com.fastrpc.transport.handler;

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
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client {[]} logout", ctx.channel().remoteAddress());
        ctx.close();
    }

    /**
     * channel 连接异常断开触发
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        log.info("Exception: client  [{}] is closed", ctx.channel());
        ctx.close();
    }
}
