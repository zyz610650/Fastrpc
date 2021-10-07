package com.fastrpc.service.impl;

import com.fastrpc.annotation.RpcService;
import com.fastrpc.dto.User;
import com.fastrpc.service.HelloService;

/**
 * @author: @zyz
 */
@RpcService(group = "G2",version = "v1.0.1")
public class HiServiceImpl implements HelloService {

    @Override
    public String sayHi(User user) {
        return "Hi, I am "+user.getName();
    }
}
