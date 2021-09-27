package com.fastrpc.zkservice.impl;

import com.fastrpc.config.Config;
import com.fastrpc.utils.CuratorUtils;
import com.fastrpc.zkservice.ZkService;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yvioo
 */
public class ZkServiceImpl implements ZkService {

//    项目初始话就需要将所有配置的类和服务器注册到zk里
    @Override
    public void registerRpcService(String rpcServiceName) {
        InetSocketAddress inetSocketAddress= new InetSocketAddress(Config.getServerHost(),Config.getServerPort());
        // example: /fastrpc/serviceName/192.168.0.121:8899
        //处理本机地址为InetSocketAddress.toString() localhost/127.0.0.1:8800"的情况
        String[] socket=inetSocketAddress.toString().split("/");
        String path = CuratorUtils.getPath(rpcServiceName)  +"/"+socket[socket.length-1];
        CuratorUtils.createPersistentNode(path);
    }

    /**
     * 删除注册到zk上的rpc服务
     * @param rpcServiceName
     */
    @Override
    public void delRpcService(String rpcServiceName) {
        CuratorUtils.delRpcServiceName(rpcServiceName);
    }

    @Override
    public InetSocketAddress getRpcService(String rpcServiceName) {
        List<String> childNodes = CuratorUtils.getChildNodes(rpcServiceName);
        String host = childNodes.get(0);
        String[] split = host.split(":");
        return new InetSocketAddress(split[0],Integer.parseInt(split[1]));
    }
    /**
     * 删除注册到zk上的rpc服务的提供者
     * @param
     */
    @Override
    public void delRpcServiceNode(InetSocketAddress inetSocketAddress) {
        CuratorUtils.delNode(inetSocketAddress);
    }





}
