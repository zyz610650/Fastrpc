package com.fastrpc.remoting.netty.server;

import com.fastrpc.config.Config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.SneakyThrows;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
public class NettyRpcServer {
    //服务器端口号
    static int port= Config.getServerPort();
    // 指定worker线程数
    static int threadNums=Config.getServerCpuNum();
    @SneakyThrows
    public void start()
    {
        EventLoopGroup boos=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup(threadNums);
        Bootstrap bootstrap=new Bootstrap();
    }

}
