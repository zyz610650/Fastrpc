package com.fastrpc.annotation;

import java.lang.annotation.*;

/**
 * @author: @zyz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcReference {

    /**
     * version
     * @return
     */
    String version() default "";

    /**
     * 选择具体的实现类
     * @return
     */
    String group() default "";
}
