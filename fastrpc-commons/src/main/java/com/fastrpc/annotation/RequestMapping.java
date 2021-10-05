package com.fastrpc.annotation;

import java.lang.annotation.*;

/**
 * @author: @zyz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RequestMapping {

    String value() default "";
}
