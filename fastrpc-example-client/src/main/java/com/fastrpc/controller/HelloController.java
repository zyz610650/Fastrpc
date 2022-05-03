package com.fastrpc.controller;

import com.beanbox.beans.annotation.Bean;
import com.fastrpc.annotation.RpcReference;
import com.fastrpc.annotation.RpcService;
import com.fastrpc.dto.User;
import com.fastrpc.service.HelloService;


/**
 * @author: @zyz
 */
@Bean
public class HelloController {


    @RpcReference(group = "G2",version = "v1.0.1")
    private static HelloService helloService;
    @RpcReference(group = "G1",version = "v1.0.1")
    private static HelloService hiService;

    public  void sayHi()
    {
        User user=new User();
        user.setName("zyz");
        System.out.println (helloService.sayHi(user));
//        user.setName("mzd");
//        System.out.println (hiService.sayHi(user));

    }


}
