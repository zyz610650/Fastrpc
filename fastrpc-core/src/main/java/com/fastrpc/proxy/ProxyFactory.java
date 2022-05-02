package com.fastrpc.proxy;

import com.fastrpc.Exception.RpcException;
import com.fastrpc.annotation.RpcLimit;
import com.fastrpc.extension.ExtensionLoader;
import com.fastrpc.flow.*;
import com.fastrpc.transport.netty.message.RpcRequestMessage;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
        private static LimitRateService limitRateService=new LimitRateServiceImpl();

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

            //方法级别限流处理
           if( method.isAnnotationPresent(RpcLimit.class))
           {
               RpcLimit annotation = method.getAnnotation(RpcLimit.class);
               int num= annotation.limitNums();
               long time=annotation.interval();
               TimeUnit timeUnit = annotation.timeUnit();
               String taskName=new StringBuilder(interfaceName).append("$").append(methodName).toString();
               TaskParameter taskParameter =new TaskParameter(taskName,timeUnit,time,num);

              final Class<?> instanceClazzCopy=instanceClass;
               TaskResult taskResult = limitRateService.doMethod(taskParameter, new Task() {
                   @Override
                   public Object doTask() {
                       try {
                           return method.invoke(instanceClazzCopy.newInstance(), parameters);
                       } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                         throw new RuntimeException(e);
                       }

                   }
               });
            if(taskResult.isSuccess()) return taskResult.getRes();
            throw new RpcException("Method execute failure,the reasion is  " + taskName + " is  overload");
           }

           // 没有限流
           Object res = method.invoke(instanceClass.newInstance(), parameters);
            return res;
        } catch (Exception e) {
            log.error("Method execute failure: "+e.getCause().getMessage());
           throw new RpcException(e.getMessage());
        }

    }



}
