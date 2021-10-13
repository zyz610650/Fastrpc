package com.fastrpc;


import com.fastrpc.annotation.RpcScanner;
import com.fastrpc.transport.netty.server.NettyRpcServerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: @zyz
 */
@Slf4j
@RpcScanner(basePackage = "com.fastrpc")
public class ApplicationServerMain {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx=new AnnotationConfigApplicationContext(ApplicationServerMain.class);
//        NettyRpcServerProvider server = ctx.getBean (NettyRpcServerProvider.class);
//        server.start ();
        Thread.sleep (10000);

    }
}
