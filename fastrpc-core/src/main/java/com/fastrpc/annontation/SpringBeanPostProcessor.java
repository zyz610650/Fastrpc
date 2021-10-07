package com.fastrpc.annontation;

import com.fastrpc.annotation.RpcService;
import com.fastrpc.config.RpcServiceConfig;
import com.fastrpc.factory.SingletonFactory;

import com.fastrpc.registry.RegistryService;
import com.fastrpc.registry.impl.RegistryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author: @zyz
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final RegistryService registryService = SingletonFactory.getInstance(RegistryServiceImpl.class);
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz=bean.getClass();

        if (clazz.isAnnotationPresent(RpcService.class))
        {
            RpcService annotation = clazz.getAnnotation(RpcService.class);

            String group=annotation.group();
            String version=annotation.version();
            RpcServiceConfig rpcServiceConfig=new RpcServiceConfig(version,group,bean);
            //向注册中心发布服务
            registryService.registerRpcService(rpcServiceConfig.getRpcServcieName());
            log.info("refister to zookeeper :[{}]",rpcServiceConfig.getRpcServcieName());
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return null;
    }
}
