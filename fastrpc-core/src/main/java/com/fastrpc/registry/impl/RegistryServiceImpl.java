package com.fastrpc.registry.impl;

import com.fastrpc.config.RpcServiceConfig;
import com.fastrpc.extension.ExtensionLoader;
import com.fastrpc.registry.RegistryService;
import com.fastrpc.transport.message.RpcRequestMessage;

import java.net.InetSocketAddress;

/**
 * @author: @zyz
 */
public class RegistryServiceImpl implements RegistryService {
    private RegistryService registryService= ExtensionLoader.getExtensionLoader(RegistryServiceImpl.class).getExtension();
    @Override
    public void registerRpcService(String rpcServiceName) {
        registryService.registerRpcService(rpcServiceName);
    }

    @Override
    public void delRpcService(String rpcServiceName) {
        registryService.delRpcService(rpcServiceName);
    }

    @Override
    public InetSocketAddress getRpcService(RpcRequestMessage msg)
    {
        return registryService.getRpcService(msg);
    }

    @Override
    public void delRpcServiceNode(InetSocketAddress inetSocketAddress) {
        registryService.delRpcServiceNode(inetSocketAddress);
    }


}
