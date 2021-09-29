package com.fastrpc.loadbalance;

import com.fastrpc.transport.message.RpcRequestMessage;

import java.util.List;

/**
 * @author zyz
 */


public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddress, RpcRequestMessage msg);
}
