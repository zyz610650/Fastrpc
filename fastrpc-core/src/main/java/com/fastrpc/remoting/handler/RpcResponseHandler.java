package com.fastrpc.remoting.handler;

import com.fastrpc.remoting.message.RpcResponseMessage;
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

        int seqId=msg.getSeqId();
        Promise promise=PROMISES.get(seqId);
        if (promise==null)
        {
            throw new RuntimeException("*** method execute exception");
        }
        /**
         * 异步执行结果返回给等待客户端等待的线程
         */
        if (msg.isSuccess())
        {
            promise.setSuccess(msg.getReturnValue());
        }else{
            promise.setFailure(msg.getExceptionValue());
        }

    }
}
