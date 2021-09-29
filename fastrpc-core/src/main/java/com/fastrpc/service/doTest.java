package com.fastrpc.service;


import com.fastrpc.transport.netty.client.NettyRpcClient;


import java.io.IOException;

import java.net.URISyntaxException;


/**
 * @author yvioo
 */
public class doTest {
    public static void main(String[] args) throws URISyntaxException, IOException {

        NettyRpcClient client=new NettyRpcClient();
        StudentService studentService = client.getProxyService(StudentService.class);
        System.out.println(   studentService.say("zyz"));
        InfoService infoService = client.getProxyService(InfoService.class);
        System.out.println(   infoService.say("zyz"));
        UserService userService = client.getProxyService(UserService.class);
        System.out.println(   userService.say("zyz"));



    }

}
