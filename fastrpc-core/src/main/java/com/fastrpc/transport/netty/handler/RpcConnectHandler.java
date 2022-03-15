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
        log.warn("channelInactive:{}", ctx.channel().localAddress());
        ctx.pipeline().remove(this);
        ctx.channel().close();
//        reconnection(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.warn("exceptionCaught:客户端[{}]和远程断开连接", ctx.channel().localAddress());
        } else {
            log.error(cause.getMessage());
        }
        ctx.pipeline().remove(this);
        //ctx.close();
        ctx.channel().close();
//        reconnection(ctx);
    }


//    private void reconnection(ChannelHandlerContext ctx) {
//        log.info("5s之后重新建立连接");
//        ctx.channel().eventLoop().schedule(new Runnable() {
//            @Override
//            public void run() {
//                boolean connect = client.connect();
//                if (connect) {
//                    log.info("重新连接成功");
//                } else {
//                    reconnection(ctx);
//                }
//            }
//        }, 5, TimeUnit.SECONDS);
//    }

}
