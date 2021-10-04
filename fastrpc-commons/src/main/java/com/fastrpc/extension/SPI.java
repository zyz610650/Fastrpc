package com.fastrpc.extension;

import java.lang.annotation.*;

/**
 * @author: @zyz
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
    /**
     * default extension name
     */
    String value() default "";
}
