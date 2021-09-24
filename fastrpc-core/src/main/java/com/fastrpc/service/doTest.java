package com.fastrpc.service;

import com.fastrpc.remoting.netty.client.NettyRpcClient;

/**
 * @author yvioo
 */
public class doTest {
    public static void main(String[] args) {
       UserService service = NettyRpcClient.getProxyService(UserService.class);
        System.out.println(service.say("zyz"));
        System.out.println(service.say("zyz"));
        System.out.println(service.say("zyz"));
        System.out.println(service.say("zyz"));
        System.out.println(service.say("zyz"));


    }
}
