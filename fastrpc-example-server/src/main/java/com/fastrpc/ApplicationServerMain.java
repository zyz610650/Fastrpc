package com.fastrpc;


import com.fastrpc.annotation.RpcScanner;
import com.fastrpc.transport.netty.server.NettyRpcServerProvider;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: @zyz
 */
@RpcScanner(basePackage = "com.fastrpc")
public class ApplicationServerMain {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx=new AnnotationConfigApplicationContext(ApplicationServerMain.class);
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames ();
        for (String name:beanDefinitionNames)
        {
            System.out.println (name);
        }
        Thread.sleep (9000);
//        NettyRpcServerProvider nettyRpcServerProvider = (NettyRpcServerProvider) ctx.getBean("nettyRpcServerProvider",NettyRpcServerProvider.class);
//        nettyRpcServerProvider.start();
//        System.out.println ("结束");

    }
}
