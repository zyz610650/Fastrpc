package com.fastrpc.extension;

import java.lang.annotation.*;

@Condition
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface ConditionOnClass {

    Class<?> clazz()  default Object.class;
    String name() default "zyz";
}
