package com.fastrpc.proxy;

import com.fastrpc.remoting.message.RpcRequestMessage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
public class ProxyFactory {
        private static Properties properties;
        private static Map<Class<?>,Class<?>> map=new ConcurrentHashMap<>();
        static {
            try(InputStream in=ProxyFactory.class.getResourceAsStream("/application.properties")){
                properties=new Properties();
                properties.load(in);
                Set<String> proNames = properties.stringPropertyNames();
                for (String name: proNames)
                {
                    if (name.endsWith("Service"))
                    {
                        Class<?> interfaceClass=Class.forName(name);
                        Class<?> instanceClass=Class.forName(properties.getProperty(name));
                        map.put(interfaceClass,instanceClass);
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
                return map.get(Class.forName(interfaceName));
            } catch (ClassNotFoundException e) {
               throw new RuntimeException("no corresponding instance");
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

        Class<?> instanceClass = ProxyFactory.getInstanceClass(interfaceName);
        Method method = null;
        try {
            method = instanceClass.getMethod(methodName, paramTypes);
            Object res = method.invoke(instanceClass.newInstance(), parameters);
            return res;
        } catch (Exception e) {
            log.error("Method execute failure: "+e.getCause().getMessage());
           throw new RuntimeException("Method execute failure");
        }

    }
}
