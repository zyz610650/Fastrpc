package com.fastrpc.transport;

import com.fastrpc.extension.SPI;
import com.fastrpc.transport.netty.message.RpcRequestMessage;

/**
 * @author: @zyz
 */
@SPI("netty")
public interface RpcRequestTransportService {
    Object sendRpcRequest(RpcRequestMessage rpcRequestMessage);
}
