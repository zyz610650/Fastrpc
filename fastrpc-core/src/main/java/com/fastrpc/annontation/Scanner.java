package com.fastrpc.annontation;

import java.util.Set;

/**
 * @author: @zyz
 */
public interface Scanner {

    /**
     * 扫描包 并注册服务到zk 注册到IOC
     * @param packageNames
     */
    Set<Class<?>> scanService(String... packageNames);

    /**
     * 查找引用
     * @param packageNames
     */
    Set<Class<?>>  scanReference(String... packageNames);
}

//现在问题:
//   1.RPC服务注册
//   2.引用端怎么自动注入
//        3.扫描包的注解放哪个类上
//        4.怎么启动时自动加载