package com.fastrpc.proxy;

import com.fastrpc.Exception.RpcException;
import com.fastrpc.factory.SingletonFactory;
import com.fastrpc.registry.RegistryService;
import com.fastrpc.registry.impl.RegistryServiceImpl;
import com.fastrpc.transport.message.RpcRequestMessage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyz
 * 获得代理类实例
 */
@Getter
@Slf4j
public class ProxyFactory  {
        private static Properties properties;
        public static Map<Class<?>,Class<?>> SERVICE_MAP=new ConcurrentHashMap<>();

    /**
     * 发布服务到zk
     */
    static {
            try(InputStream in=ProxyFactory.class.getClassLoader().getResourceAsStream("service.properties")){
                log.info("The server is registering services to zookeeper. Please waiting......");
                properties=new Properties();
                properties.load(in);
                Set<String> proNames = properties.stringPropertyNames();
                RegistryService registryService = SingletonFactory.getInstance(RegistryServiceImpl.class);
                for (String name: proNames)
                {
                    if (name.endsWith("Service"))
                    {

                        registryService.registerRpcService(name);
                        log.info("refister to zookeeper :[{}]",name);
                        Class<?> interfaceClass=Class.forName(name);
                        Class<?> instanceClass=Class.forName(properties.getProperty(name));
                        SERVICE_MAP.put(interfaceClass,instanceClass);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    /**
     * 获得实现类class
     * @param interfaceName
     * @return
     */
    public static Class<?> getInstanceClass(String interfaceName )
        {

            try {
                return SERVICE_MAP.get(Class.forName(interfaceName));
            } catch (ClassNotFoundException e) {
               throw new RpcException("no corresponding instance");
            }
        }

    /**
     *
     * @param msg
     * @return
     *  通过反射执行方法
     */
    public static Object doMethod(RpcRequestMessage msg)
    {

        String interfaceName=msg.getInterfaceName();
        String methodName=msg.getMethodName();
        Class[] paramTypes=msg.getParamTypes();
        Object[] parameters=msg.getParameters();
        String group=msg.getGroup();
        Class<?> instanceClass = ProxyFactory.getInstanceClass(interfaceName);
        Method method = null;
        try {
            method = instanceClass.getMethod(methodName, paramTypes);
            Object res = method.invoke(instanceClass.newInstance(), parameters);
            return res;
        } catch (Exception e) {
            log.error("Method execute failure: "+e.getCause().getMessage());
           throw new RpcException(e.getMessage());
        }

    }


}
