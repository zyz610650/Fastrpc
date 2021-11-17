package com.fastrpc.transport.netty.server;

import com.beanbox.beans.annotation.Bean;
import com.fastrpc.config.Config;

import com.fastrpc.factory.SingletonFactory;
import com.fastrpc.proxy.ProxyFactory;
import com.fastrpc.registry.RegistryService;
import com.fastrpc.registry.impl.RegistryServiceImpl;
import com.fastrpc.transport.netty.handler.RpcLogoutHandler;
import com.fastrpc.transport.netty.handler.RpcServerDuplexHandler;
import com.fastrpc.transport.netty.handler.RpcRequestHandler;

import com.fastrpc.transport.netty.protocol.FrameDecoderProtocol;
import com.fastrpc.transport.netty.protocol.MessageCodecProtocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;


import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zyz
 */
@Slf4j
@Bean
public class NettyRpcServerProvider {
    /**
     * 服务器端口号
     */
    static int port= Config.getServerPort();
    /**
     * 服务器Ip地址
     */
    static String host= Config.getServerHost();
    /**
     * 指定worker线程数
     */
    static int threadNums=Config.getServerCpuNum();


    /**
     * 服务注册中心
     */
    private static RegistryService registryService= SingletonFactory.getInstance(RegistryServiceImpl.class);

    public NettyRpcServerProvider()
    {
        log.info("Rpc server startup");
        //netty随rpc框架启动所加载,随项目运行结束
        //netty服务器关闭
        Thread thread=new Thread (()->{
            start();
        },"netty-thread");
        thread.setDaemon (true);
        thread.start ();

    }

    public void start()
    {
        LoggingHandler LOGGING_HANDLER=new LoggingHandler(LogLevel.INFO);
        MessageCodecProtocol MESSAGE_CODEC=new MessageCodecProtocol();
        RpcServerDuplexHandler DUPLEX_HANDLER=new RpcServerDuplexHandler();
        EventLoopGroup boosGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup(2);
        RpcRequestHandler REQUEST_HANDLER=new RpcRequestHandler();
        RpcLogoutHandler LOROUT_HANDLER=new RpcLogoutHandler();
        DefaultEventExecutorGroup serviceHandlerGroup=new DefaultEventExecutorGroup(threadNums);
        try {

            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.group(boosGroup,workerGroup);
            bootstrap.childOption(ChannelOption.TCP_NODELAY,true);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.option(ChannelOption.SO_BACKLOG,128);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch)  {
                   //处理半包粘包
                    ch.pipeline().addLast(new FrameDecoderProtocol());
                    //日志
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    //编码器 消息预处理
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //channel关闭处理
                    ch.pipeline().addLast(LOROUT_HANDLER);
                    //心跳机制
                    ch.pipeline().addLast(new IdleStateHandler(15,0,0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(DUPLEX_HANDLER);
                    // 业务逻辑处理 将该handler交给特定的业务线程处理
                    ch.pipeline().addLast(serviceHandlerGroup,REQUEST_HANDLER);

                }
            });
           Channel channel=bootstrap.bind(port).sync().channel();

            ChannelFuture closeFuture = channel.closeFuture ();
            closeFuture.sync ();
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            log.error("zookeeper: logout [{}] node"+host+port);
            registryService.delRpcServiceNode(new InetSocketAddress(host,port));
        }

    }


}
