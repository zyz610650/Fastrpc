package com.fastrpc.remoting.netty.client;


import com.fastrpc.remoting.handler.MessageDuplexHandler;
import com.fastrpc.remoting.protocol.FrameDecoderProtocol;
import com.fastrpc.remoting.protocol.MessageCodecProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
public class NettyRpcClient {

    public void start(){
        NioEventLoopGroup group=new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER=new LoggingHandler(LogLevel.DEBUG);
        MessageCodecProtocol MESSAGE_CODEC=new MessageCodecProtocol();
        MessageDuplexHandler DUPLEX_HANDLER=new MessageDuplexHandler();

        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new FrameDecoderProtocol());
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(new IdleStateHandler(0,10,0, TimeUnit.SECONDS));
                ch.pipeline().addLast(DUPLEX_HANDLER);
            }
        });


    }
}
