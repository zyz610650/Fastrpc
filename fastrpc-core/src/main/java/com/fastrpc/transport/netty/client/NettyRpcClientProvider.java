package com.fastrpc.transport.netty.client;


import com.fastrpc.Exception.RpcException;
import com.fastrpc.factory.SingletonFactory;
import com.fastrpc.registry.RegistryService;
import com.fastrpc.registry.impl.RegistryServiceImpl;
import com.fastrpc.transport.RpcRequestTransportService;
import com.fastrpc.transport.netty.handler.RpcClientDuplexHandler;
import com.fastrpc.transport.netty.handler.RpcResponseHandler;
import com.fastrpc.transport.netty.message.RpcRequestMessage;
import com.fastrpc.transport.netty.protocol.FrameDecoderProtocol;
import com.fastrpc.transport.netty.protocol.MessageCodecProtocol;

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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author zyz
 */
@Slf4j
public class NettyRpcClientProvider implements RpcRequestTransportService {

    private  static Channel channel;
    private  static  Bootstrap bootstrap=new Bootstrap();
    private  static  NioEventLoopGroup group=new NioEventLoopGroup();
    private  static RegistryService ZK_REGISTRY_SERVICE= SingletonFactory.getInstance(RegistryServiceImpl.class);
    private  static final Object lock=new Object();


    public NettyRpcClientProvider() {
        //初始化netty服务器
        init();
    }

    /**
     * get channel 单例
     * @return
     */
    static Channel getChannel(RpcRequestMessage rpcRequestMessage)
    {
        synchronized (lock)
        {
            if (channel==null) {
                log.info("Init channel");
                doConnect(rpcRequestMessage);
            }
            return channel;

        }
    }

    /**
     * 初始化客户端
     */
    public static void init(){

        LoggingHandler LOGGING_HANDLER=new LoggingHandler(LogLevel.INFO);
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
    public static void doConnect(RpcRequestMessage rpcRequestMessage)
    {
        //从zk 获取服务器IP
        InetSocketAddress inetAddress = ZK_REGISTRY_SERVICE.getRpcService(rpcRequestMessage);

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
                    log.error("The remoting service has been closed!");
                    group.shutdownGracefully();
                });
    }



    /**
     * 方法执行代理方法
     * @param msg
     * @return
     */
    @Override
    public  Object sendRpcRequest(RpcRequestMessage msg)
    {
        Channel channel = getChannel(msg);
        try {
        if (channel.isActive()) {
            channel.writeAndFlush(msg);
            DefaultPromise promise = new DefaultPromise(NettyRpcClientProvider.channel.eventLoop());
            RpcResponseHandler.PROMISES.put(msg.getSeqId(), promise);
            // 服务器2s内没有返回结果 则默认执行失败
            promise.await(2000,TimeUnit.MILLISECONDS);

            if (promise.isSuccess()) {
                log.info("do method success: [{}]", msg);
                return promise.getNow();
            } else {
                log.error("remoting service exception " + promise.cause());
                throw new RpcException(promise.cause().getMessage());

            }
        }else{
            channel.close();
            throw new IllegalStateException("connection is closed");
        }
        } catch (InterruptedException e) {
            log.error("service exception " + e);
             throw new IllegalStateException(e);
     }
    }

    public static void main(String[] args) {
        NettyRpcClientProvider.init();
    }
}






