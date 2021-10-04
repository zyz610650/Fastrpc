package com.fastrpc.registry;

import com.fastrpc.transport.message.RpcRequestMessage;

import java.net.InetSocketAddress;

/**
 * @author zyz
 */
public interface ZkService {

    /**
     * 向zk上注册rpc服务
     * @param rpcServiceName
     */
    public void registerRpcService(String rpcServiceName);

    /**
     * 注销rpc服务
     * @param rpcServiceName
     */
    void delRpcService(String rpcServiceName);

    /**
     * 获得rpc服务提供者地址
     * @param rpcRequestMessage
     * @return 服务提供者地址
     */
    public InetSocketAddress getRpcService(RpcRequestMessage rpcRequestMessage);

    /**
     * 删除注册到zk上的rpc服务的提供者
     */
    public void delRpcServiceNode(InetSocketAddress inetSocketAddress);



}
