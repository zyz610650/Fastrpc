package com.fastrpc.service;

import com.fastrpc.annotation.RpcLimit;
import com.fastrpc.dto.User;
import com.fastrpc.enums.LimitMethod;

import java.util.concurrent.TimeUnit;

/**
 * @author: @zyz
 */


public interface HelloService {


    String sayHi(User user);
}
//    long interval() default 10;
//
//        int limitNums() default 10;
//
//        TimeUnit timeUnit() default TimeUnit.SECONDS;
//
//        LimitMethod limitMethod() default LimitMethod.TOKENBUCKETRATE;