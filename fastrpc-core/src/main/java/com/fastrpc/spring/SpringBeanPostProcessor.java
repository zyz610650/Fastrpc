package com.fastrpc.spring;

import com.fastrpc.Exception.RpcException;
import com.fastrpc.annotation.RpcReference;
import com.fastrpc.annotation.RpcService;
import com.fastrpc.config.RpcServiceConfig;
import com.fastrpc.factory.SingletonFactory;

import com.fastrpc.proxy.ProxyFactory;
import com.fastrpc.proxy.RpcClientProxy;
import com.fastrpc.registry.RegistryService;
import com.fastrpc.registry.impl.RegistryServiceImpl;
import com.fastrpc.transport.RpcRequestTransportService;
import com.fastrpc.transport.impl.RpcRequestTransportServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author: @zyz
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor{

    private final RegistryService registryService = SingletonFactory.getInstance(RegistryServiceImpl.class);
    private final RpcRequestTransportService rpcRequestTransportService=SingletonFactory.getInstance(RpcRequestTransportServiceImpl.class);
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Class<?> clazz=bean.getClass();

        if (clazz.isAnnotationPresent(RpcService.class))
        {
            log.info("[{}] is annotated with  [{}]", clazz.getName(), RpcService.class.getCanonicalName());
            RpcService rpcService = clazz.getAnnotation(RpcService.class);

            RpcServiceConfig rpcServiceConfig=RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean)
                    .build();
            //向注册中心发布服务
            registryService.registerRpcService(rpcServiceConfig.getRpcServcieName());

            //添加Bean缓存
            ProxyFactory.CACHE_BEAN.put(rpcServiceConfig.getRpcServcieName(),bean);
            log.info("refister to zookeeper :[{}]",rpcServiceConfig.getRpcServcieName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz=bean.getClass();
       for(Field field: clazz.getDeclaredFields())
       {
           if (field.isAnnotationPresent(RpcReference.class))
           {
               RpcReference rpcReference=field.getAnnotation(RpcReference.class);

                RpcServiceConfig rpcServiceConfig=RpcServiceConfig.builder()
                        .version(rpcReference.version())
                        .group(rpcReference.group())
                        .service(field.getType ())
                        .build();
               RpcClientProxy rpcClientProxy=new RpcClientProxy(rpcServiceConfig,rpcRequestTransportService);
               Object clientProxy = rpcClientProxy.getProxy(field.getType ());
               field.setAccessible(true);
               try {
                   field.set(bean,clientProxy);
               } catch (IllegalAccessException e) {
                  throw new RpcException(e);
               }
           }
       }
        return bean;
    }


}
