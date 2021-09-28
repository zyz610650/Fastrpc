package com.fastrpc.remoting.netty.client;


import com.fastrpc.Exception.RpcException;
import com.fastrpc.config.Config;
import com.fastrpc.remoting.handler.RpcClientDuplexHandler;
import com.fastrpc.remoting.handler.RpcServerDuplexHandler;
import com.fastrpc.remoting.handler.RpcResponseHandler;
import com.fastrpc.remoting.message.RpcRequestMessage;
import com.fastrpc.remoting.protocol.FrameDecoderProtocol;
import com.fastrpc.remoting.protocol.MessageCodecProtocol;
import com.fastrpc.utils.SequenceIdGenerator;
import com.fastrpc.zkservice.ZkService;
import com.fastrpc.zkservice.impl.ZkServiceImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
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
    private  static  Bootstrap bootstrap=new Bootstrap();
    private  static  NioEventLoopGroup group=new NioEventLoopGroup();
    private static   final ZkService zkService=new ZkServiceImpl();
    private static final Object lock=new Object();


    public NettyRpcClient() {

        //初始化netty服务器
        init();
    }

    /**
     * get channel 单例
     * @return
     */
    static Channel getChannel(String rpcServiceName)
    {
        synchronized (lock)
        {
            if (channel==null) {
                log.info("Init channel");
                doConnect(rpcServiceName);
            }
            return channel;

        }
    }

    /**
     * 初始化客户端
     */
    public static void init(){

        LoggingHandler LOGGING_HANDLER=new LoggingHandler(LogLevel.DEBUG);
        MessageCodecProtocol MESSAGE_CODEC=new MessageCodecProtocol();
        RpcClientDuplexHandler DUPLEX_HANDLER=new RpcClientDuplexHandler();
        RpcResponseHandler RESPONSE_HANDLER=new RpcResponseHandler();

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        //5s没有连接成功，则连接失败
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new FrameDecoderProtocol());
            ch.pipeline().addLast(LOGGING_HANDLER);
            ch.pipeline().addLast(MESSAGE_CODEC);
            //心跳
            ch.pipeline().addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
            ch.pipeline().addLast(DUPLEX_HANDLER);
            ch.pipeline().addLast(RESPONSE_HANDLER);
            }
        });

    }

    /**
     * connect server and get Channel
     */
    public static void doConnect(String rpcServiceName)
    {
        //从zk 获取服务器IP
        InetSocketAddress inetAddress = zkService.getRpcService(rpcServiceName);

        ChannelFuture future = null;
        try {
            future = bootstrap.connect(inetAddress).sync();
            channel=future.channel();
            log.info("The client has connected [{}] successful!",inetAddress);
        } catch (InterruptedException e) {
            log.error("connection exception");
            throw new RpcException("connection exception");
        }

        channel.closeFuture()
                .addListener((promise)->{
                    log.error("***remoting service close");
                    group.shutdownGracefully();
                });
    }

    /**
     * 方法执行代理方法
     * @param serviceClass
     * @param <T> 接口类型
     * @return 返回接口对象
     */
    public  <T> T getProxyService(Class<T> serviceClass)
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
                // zk中存的时 simpleName 缓存中存的是全类名
                Channel channel = getChannel(serviceClass.getSimpleName());
                if (channel.isActive()) {
                    channel.writeAndFlush(msg);
                    DefaultPromise promise = new DefaultPromise(NettyRpcClient.channel.eventLoop());
                    RpcResponseHandler.PROMISES.put(seqId, promise);
                    promise.await();
                    if (promise.isSuccess()) {
                        log.info("************do method success: [{}]", msg);
                        return promise.getNow();
                    } else {
                        log.error("*************remoting service exception " + promise.cause());
                        throw new RpcException(promise.cause().getMessage());
                    }
                }else{
                    channel.close();
                    throw new IllegalStateException();
                }
            }
        });
        return (T)o;
    }

    public static void main(String[] args) {
        NettyRpcClient.init();
    }
}