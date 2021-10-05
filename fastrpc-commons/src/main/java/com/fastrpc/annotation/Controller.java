package com.fastrpc.annotation;

import lombok.Getter;

import java.lang.annotation.*;

/**
 * @author: @zyz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Controller {

    String value() default "";
}
