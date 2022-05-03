package com.fastrpc.transport.netty.handler;

import com.fastrpc.proxy.ProxyFactory;
import com.fastrpc.transport.netty.message.RpcRequestMessage;
import com.fastrpc.transport.netty.message.RpcResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
    Set<Channel> set=new HashSet<>();
    AtomicInteger atomicInteger=new AtomicInteger();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
//        System.out.println("访问的次数为： "+atomicInteger.incrementAndGet());
//        System.out.println("更前:  "+set.size());
//        Channel channel=ctx.channel();
//        set.add(channel);
//        System.out.println("更新后: "+set.size());
        //==================================================================
        RpcResponseMessage responseMessage=new RpcResponseMessage();
        responseMessage.setSeqId(msg.getSeqId());
        try {
            Object res=ProxyFactory.invokeMethod(msg);
            responseMessage.setReturnValue(res);
            responseMessage.setSuccess(true);

        } catch (Exception e) {
            System.out.println("================");
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            responseMessage.setSuccess(false);
            responseMessage.setExceptionValue(e.getMessage());

        } finally {
            ctx.writeAndFlush(responseMessage);
        }

    }
}
