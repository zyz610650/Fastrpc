package com.fastrpc.proxy;

import com.fastrpc.Exception.RpcException;
import com.fastrpc.config.RpcServiceConfig;
import com.fastrpc.extension.ExtensionLoader;
import com.fastrpc.transport.RpcRequestTransportService;
import com.fastrpc.transport.impl.RpcRequestTransportServiceImpl;
import com.fastrpc.transport.netty.message.RpcRequestMessage;

import com.fastrpc.utils.SequenceIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyz
 * 获得代理类实例
 */
@Getter
@Slf4j
@Data
public class ProxyFactory   {
        private static Properties properties;
        public static Map<String,Object> CACHE_BEAN=new ConcurrentHashMap<>();


    /**
     * 获得实现类class
     * @param rpcServiceName
     * @return
     */
    public static Object getInstanceClass(String rpcServiceName )
        {
            try {
                return CACHE_BEAN.get(rpcServiceName);
            } catch (Exception e) {
               throw new RpcException("no corresponding instance",e);
            }
        }

    /**
     * RpcServer通过反射执行方法
     * @param msg
     * @return
     */
    public static Object invokeMethod(RpcRequestMessage msg)
    {

        String interfaceName=msg.getInterfaceName();
        String methodName=msg.getMethodName();
        Class[] paramTypes=msg.getParamTypes();
        Object[] parameters=msg.getParameters();

        Class<?> instanceClass = ProxyFactory.getInstanceClass(msg.getRpcServcieName()).getClass();
        try {
            if (instanceClass==null)
            {
                instanceClass=ExtensionLoader.getExtensionLoader(Class.forName(interfaceName)).getExtension(msg.getGroup()).getClass();
                CACHE_BEAN.put(msg.getRpcServcieName(),instanceClass.newInstance());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RpcException(e);
     }
        Method method ;
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
