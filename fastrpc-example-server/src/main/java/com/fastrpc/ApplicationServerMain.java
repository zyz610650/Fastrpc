package com.fastrpc;

import com.fastrpc.annotation.RpcScanner;
import com.fastrpc.transport.netty.client.NettyRpcClientProvider;
import com.fastrpc.transport.netty.server.NettyRpcServerProvider;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: @zyz
 */
@RpcScanner(basePackage = "com.fastrpc")
public class ApplicationServerMain {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx=new AnnotationConfigApplicationContext(ApplicationServerMain.class);
        NettyRpcServerProvider nettyRpcServerProvider = (NettyRpcServerProvider) ctx.getBean("nettyRpcServerProvider");
        nettyRpcServerProvider.start();
    }
}
