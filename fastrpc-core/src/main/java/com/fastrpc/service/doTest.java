package com.fastrpc.service;


import com.fastrpc.config.Config;
import com.fastrpc.proxy.ProxyFactory;
import com.fastrpc.remoting.netty.client.NettyRpcClient;


import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author yvioo
 */
public class doTest {
    public static void main(String[] args) throws URISyntaxException, IOException {

//        NettyRpcClient client=new NettyRpcClient();
//        StudentService studentService = client.getProxyService(StudentService.class);
//        System.out.println(   studentService.say("zyz"));
//        InfoService infoService = client.getProxyService(InfoService.class);
//        System.out.println(   infoService.say("zyz"));
//        UserService userService = client.getProxyService(UserService.class);
//        System.out.println(   userService.say("zyz"));

//        String[] keys = {"太阳", "月亮", "星星","asd","wq","12","sad"};
//        List<String> list=new ArrayList<>();
//        list.add("qqq");
//        list.add("ads");
//        System.out.println(System.identityHashCode(list));
//        List<String> list1=new ArrayList<>();
//        list.add("qqq");
//        list.add("ads");
//        System.out.println(System.identityHashCode(list1));
    Map<String,String>map=new ConcurrentHashMap<>();
    map.put("zyz","192.168.1.25:8080");
    map.put("mzd","192.168.1.25:8080");
    map.put("mzqd","192.168.1.25:8280");
    InetSocketAddress inetSocketAddress=new InetSocketAddress("192.168.1.25",8080);
    map.values().removeIf(value->value.equals(inetSocketAddress.toString().split("/")[1]));
        System.out.println(map.size());
    }

}
