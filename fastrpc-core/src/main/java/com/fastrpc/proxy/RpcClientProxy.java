package com.fastrpc.proxy;

import com.fastrpc.config.RpcServiceConfig;
import com.fastrpc.transport.RpcRequestTransportService;
import com.fastrpc.transport.impl.RpcRequestTransportServiceImpl;
import com.fastrpc.transport.netty.message.RpcRequestMessage;
import com.fastrpc.utils.SequenceIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: @zyz
 */
@Data
@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {
    private RpcServiceConfig rpcServiceConfig;
    private  RpcRequestTransportService rpcRequestTransportService;


    /**
     * 获得代理类对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> clazz)
    {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    /**
     * RpcClient的动态代理类
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        int seqId = SequenceIdGenerator.nextId();

        RpcRequestMessage msg = new RpcRequestMessage(seqId,proxy.getClass().getCanonicalName()
                ,method.getName(),method.getParameterTypes(),args
                ,rpcServiceConfig.getGroup(),rpcServiceConfig.getVersion());

        Object o = rpcRequestTransportService.sendRpcRequest(msg);
        return o;

    }
}
