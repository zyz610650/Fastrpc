package com.fastrpc.transport.netty.handler;

import com.fastrpc.transport.netty.message.PingMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * 用于处理假死现象
 * @author zhang
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcClientDuplexHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event= (IdleStateEvent) evt;
        if (event.state()==IdleState.WRITER_IDLE)
        {
            //发送一个心跳包
            log.debug("per 5s send ping");
            ctx.writeAndFlush(new PingMessage());
        }

    }
}
