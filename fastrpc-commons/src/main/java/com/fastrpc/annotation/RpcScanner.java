package com.fastrpc.annotation;

import org.omg.SendingContext.RunTime;

import java.lang.annotation.*;

/**
 * @author: @zyz
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcScanner {
    /**
     * 需要扫描的包 必须放在程序启动的入口类上
     * @return
     */
    String[] basePackage();
}
