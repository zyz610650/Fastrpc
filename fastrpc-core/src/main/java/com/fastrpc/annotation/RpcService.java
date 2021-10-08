package com.fastrpc.annotation;

import java.lang.annotation.*;

/**
 * @author: @zyz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

    /**
     * version
     * @return
     */
    String version() default "v1.0.0";

    /**
     * 选择具体的实现类
     * @return
     */
    String group() default "";
}
