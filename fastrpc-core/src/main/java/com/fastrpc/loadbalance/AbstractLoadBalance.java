package com.fastrpc.loadbalance;

import com.fastrpc.transport.netty.message.RpcRequestMessage;

import java.util.List;

/**
 * @author zyz
 */


public abstract class AbstractLoadBalance implements LoadBalance{
    @Override
    public String selectServiceAddress(List<String> serviceAddress, RpcRequestMessage msg) {
        if (serviceAddress==null||serviceAddress.size()==0)
        {
            return null;
        }
        if (serviceAddress.size()==1)
        {
            return serviceAddress.get(0);
        }
        return doSelect(serviceAddress,msg);
    }

     protected abstract   String doSelect(List<String> serviceAddresses, RpcRequestMessage msg);
}
