package com.fastrpc.transport.netty.handler;

import com.fastrpc.Exception.RpcException;
import com.fastrpc.transport.netty.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    public static Map<Integer,Promise> PROMISES=new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        System.out.println(msg);
        int seqId=msg.getSeqId();
        Promise promise=PROMISES.get(seqId);

        if (promise==null)
        {
            throw new RpcException("***method execute exception");
        }
        /**
         * 异步执行结果返回给等待客户端等待的线程
         */
        if (msg.isSuccess())
        {
            promise.setSuccess(msg.getReturnValue());
        }else{
            promise.setFailure(new RpcException(msg.getExceptionValue()));
        }

    }
}
