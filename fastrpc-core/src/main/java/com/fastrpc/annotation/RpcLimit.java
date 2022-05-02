package com.fastrpc.annotation;

import com.fastrpc.enums.LimitMethod;
import com.fastrpc.flow.LimitRateService;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: @zyz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RpcLimit {

    /**
     * 每 limitTime时间内 允许处理的请求为limitNums个，单位是timeUnit
     * @return
     */
    long interval() default 10;

    int limitNums() default 10;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    LimitMethod limitMethod() default LimitMethod.TOKENBUCKETRATE;
}
