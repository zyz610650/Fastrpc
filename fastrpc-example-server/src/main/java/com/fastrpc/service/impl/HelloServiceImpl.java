package com.fastrpc.service.impl;

import com.fastrpc.annotation.RpcService;
import com.fastrpc.dto.User;
import com.fastrpc.service.HelloService;

/**
 * @author: @zyz
 */
@RpcService(group = "G1",version = "v1.0.1")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHi(User user) {
        return "Hello, I am "+user.getName();
    }
}
