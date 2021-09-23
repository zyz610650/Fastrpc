package com.fastrpc.remoting.handler;

import com.fastrpc.remoting.message.PingMessage;
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
public class MessageDuplexHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event= (IdleStateEvent) evt;
        if (event.state()== IdleState.READER_IDLE){
            log.debug("5s no receive data close client");
            ctx.channel().close();
        }
        if (event.state()==IdleState.WRITER_IDLE)
        {
            //发送一个心跳包
            log.debug("send ping");
            ctx.writeAndFlush(new PingMessage());
        }
    }
}
