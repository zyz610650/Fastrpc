package com.fastrpc.loadbalance;

import com.fastrpc.extension.SPI;
import com.fastrpc.transport.netty.message.RpcRequestMessage;

import java.util.List;

/**
 * @author zyz
 */

@SPI("loadBalance")
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddress, RpcRequestMessage msg);
}
