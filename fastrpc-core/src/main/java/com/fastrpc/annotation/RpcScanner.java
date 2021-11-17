package com.fastrpc.annotation;



import java.lang.annotation.*;

/**
 * @author: @zyz
 */
@Documented
//和@Component等类一样将类加到IOC容器中

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcScanner {
    /**
     * 需要扫描的包 必须放在程序启动的入口类上
     * @return
     */
    String[] basePackage();
}
