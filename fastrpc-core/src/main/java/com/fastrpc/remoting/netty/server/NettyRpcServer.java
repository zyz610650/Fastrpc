package com.fastrpc.remoting.netty.server;

import com.fastrpc.config.Config;

import com.fastrpc.remoting.handler.MessageDuplexHandler;
import com.fastrpc.remoting.protocol.FrameDecoderProtocol;
import com.fastrpc.remoting.protocol.MessageCodecProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@Slf4j
public class NettyRpcServer {
    /**
     * 服务器端口号
     */
    static int port= Config.getServerPort();
    /**
     * 指定worker线程数
     */
    static int threadNums=Config.getServerCpuNum();
    static String host;
    @SneakyThrows
    public void start()
    {
        host= InetAddress.getLocalHost().getHostAddress();
        LoggingHandler LOGGING_HANDLER=new LoggingHandler(LogLevel.DEBUG);
        MessageCodecProtocol MESSAGE_CODEC=new MessageCodecProtocol();
        MessageDuplexHandler DUPLEX_HANDLER=new MessageDuplexHandler();
        EventLoopGroup boosGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup=new DefaultEventExecutorGroup(threadNums);
        try {
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.group(boosGroup,workerGroup);
            bootstrap.childOption(ChannelOption.TCP_NODELAY,true);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.childOption(ChannelOption.SO_BACKLOG,128);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                   //处理半包粘包
                    ch.pipeline().addLast(new FrameDecoderProtocol());
                    //编码器 消息预处理
                    ch.pipeline().addLast(MESSAGE_CODEC);
                   //日志
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    //心跳机制
                    ch.pipeline().addLast(new IdleStateHandler(10,0,0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(DUPLEX_HANDLER);
                    //业务逻辑处理
                }
            });
            Channel channel=bootstrap.bind(host,port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
