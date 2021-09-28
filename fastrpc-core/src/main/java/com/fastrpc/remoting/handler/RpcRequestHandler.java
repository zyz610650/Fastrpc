package com.fastrpc.remoting.handler;

import com.fastrpc.proxy.ProxyFactory;
import com.fastrpc.remoting.message.RpcRequestMessage;
import com.fastrpc.remoting.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage responseMessage=new RpcResponseMessage();
        responseMessage.setSeqId(msg.getSeqId());
        try {
            Object res=ProxyFactory.doMethod(msg);
            responseMessage.setReturnValue(res);
            responseMessage.setSuccess(true);


        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setExceptionValue(e.getMessage());

        } finally {
            ctx.writeAndFlush(responseMessage);
        }

    }
}
