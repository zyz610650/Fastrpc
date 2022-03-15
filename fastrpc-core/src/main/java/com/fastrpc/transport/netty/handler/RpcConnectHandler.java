package com.fastrpc.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcConnectHandler extends ChannelInboundHandlerAdapter  {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channelInactive:客户端[{}]和远程断开连接", ctx.channel().localAddress());
        ctx.pipeline().remove(this);
        ctx.channel().close();
//        reconnection(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.warn("exceptionCaught:客户端[{}]和远程异常断开连接", ctx.channel().localAddress());
        } else {
            log.error(cause.getMessage());
        }
        ctx.pipeline().remove(this);
        //ctx.close();
        ctx.channel().close();
//        reconnection(ctx);
    }




}
