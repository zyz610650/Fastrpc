package com.fastrpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Documented;

/**
 * @author: @zyz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcServiceConfig {

    /**
     * service version
     */
    private String version = "";
    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group = "";

    /**
     * target service
     */
    private Object service;

    /**
     * 向服务注册中心注册的服务名
     * @return
     */
    public String getRpcServcieName()
    {
        return this.getInterfaceName()+"&"+group+"&"+version;
    }

    /**
     * 获取实现类的接口名
     * @return
     */
    public String getInterfaceName()
    {
        return service.getClass().getInterfaces()[0].getCanonicalName();
    }


}
