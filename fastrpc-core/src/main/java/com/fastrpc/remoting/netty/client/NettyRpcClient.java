package com.fastrpc.remoting.netty.client;


import com.fastrpc.config.Config;
import com.fastrpc.remoting.handler.MessageDuplexHandler;
import com.fastrpc.remoting.handler.RpcResponseHandler;
import com.fastrpc.remoting.message.RpcRequestMessage;
import com.fastrpc.remoting.message.RpcResponseMessage;
import com.fastrpc.remoting.protocol.FrameDecoderProtocol;
import com.fastrpc.remoting.protocol.MessageCodecProtocol;
import com.fastrpc.utils.SequenceIdGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

import java.util.concurrent.TimeUnit;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@Slf4j
public class NettyRpcClient {

    private  static Channel channel;
    private  static String host= Config.getServerHost();
    private  static int port=Config.getServerPort();
    private static final Object lock=new Object();

    /**
     * 获得channel 单例
     * @return
     */
    static Channel getChannel()
    {
        synchronized (lock)
        {
            if (channel==null) {
                log.info("Init channel");
                init();
            }
            return channel;

        }
    }

    /**
     * 初始化客户端
     */
    public static void init(){
        NioEventLoopGroup group=new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER=new LoggingHandler(LogLevel.DEBUG);
        MessageCodecProtocol MESSAGE_CODEC=new MessageCodecProtocol();
        MessageDuplexHandler DUPLEX_HANDLER=new MessageDuplexHandler();
        RpcResponseHandler RESPONSE_HANDLER=new RpcResponseHandler();
        try {
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new FrameDecoderProtocol());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(new IdleStateHandler(0,10,0, TimeUnit.SECONDS));
                ch.pipeline().addLast(DUPLEX_HANDLER);
                ch.pipeline().addLast(RESPONSE_HANDLER);
                }
            });
            channel = bootstrap.connect(host, port).sync().channel();
            channel.closeFuture()
                    .addListener((promise)->{
                    log.info("==============remoting service close");
                    group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("remoting service exception:[{}]",e.getCause().getMessage());
        }
    }


    public static void main(String[] args) {
        NettyRpcClient.init();
    }

public static <T> T getProxyService(Class<T> serviceClass)
{
    ClassLoader loader=serviceClass.getClassLoader();
    Class<?>[] interfaces=new Class[]{serviceClass};
    Object o = Proxy.newProxyInstance(loader, interfaces, new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            int seqId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    seqId,
                    serviceClass.getName()
                    , method.getName()
                    , method.getParameterTypes()
                    , args
            );
            getChannel().writeAndFlush(msg);

            DefaultPromise promise = new DefaultPromise(channel.eventLoop());
            RpcResponseHandler.PROMISES.put(seqId, promise);
            promise.await();
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                log.error("remoting service exception " + promise.cause());
                throw new RuntimeException(promise.cause().getMessage());
            }

        }
    });
    return (T)o;
}
}