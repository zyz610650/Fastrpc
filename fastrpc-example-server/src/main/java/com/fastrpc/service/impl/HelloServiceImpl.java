package com.fastrpc.service.impl;

import com.fastrpc.annotation.RpcLimit;
import com.fastrpc.annotation.RpcService;
import com.fastrpc.dto.User;
import com.fastrpc.enums.LimitMethod;
import com.fastrpc.service.HelloService;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author: @zyz
 */
@NoArgsConstructor
@RpcService(group = "G1",version = "v1.0.1")
public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl被创建");
    }
    @Override
    @RpcLimit(interval = 10,limitNums = 5,timeUnit = TimeUnit.SECONDS,limitMethod = LimitMethod.SLIDINGWINDOWRATE)
    public String sayHi(User user) {
        System.out.println ("sayHi 方法被执行");
        return "Hello, I am "+user.getName();
    }

}
