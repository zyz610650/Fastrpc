package com.fastrpc.http;


import com.beanbox.context.suppport.ClassPathXmlApplicationContext;
import com.fastrpc.Exception.RpcException;
import com.fastrpc.annotation.RpcReference;
import com.fastrpc.config.RpcServiceConfig;
import com.fastrpc.factory.SingletonFactory;
import com.fastrpc.proxy.RpcClientProxy;
import com.fastrpc.transport.RpcRequestTransportService;
import com.fastrpc.transport.impl.RpcRequestTransportServiceImpl;
import com.fastrpc.transport.netty.message.RpcRequestMessage;
import com.fastrpc.utils.SequenceIdGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
@Data
@NoArgsConstructor
public class GenericService {
    private final RpcRequestTransportService rpcRequestTransportService= SingletonFactory.getInstance(RpcRequestTransportServiceImpl.class);
    private String group;
    private String version;
    private String serviceName;
    Object $invoke(String methodName, List<String> paramTypes,  List<String> params)
    {
        int seqId = SequenceIdGenerator.nextId();
        // 通过方法获取类名，如果通过proxy获取则得到的是代理类的类名
        RpcRequestMessage msg = new RpcRequestMessage(seqId,serviceName
                ,methodName,paramTypes,params
                ,group,version);
        // 表明本次是泛化调用
        msg.setGenericService(true);
        Object res = rpcRequestTransportService.sendRpcRequest(msg);
        StringBuilder sb=new StringBuilder(serviceName);
        sb.append("&").append(methodName);
        log.info("泛化调用[{}]执行成功",sb.toString());
        return res;
    }

}
