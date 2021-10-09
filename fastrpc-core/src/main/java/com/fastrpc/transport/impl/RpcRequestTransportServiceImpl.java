package com.fastrpc.transport.impl;

import com.fastrpc.extension.ExtensionLoader;
import com.fastrpc.transport.RpcRequestTransportService;
import com.fastrpc.transport.netty.message.RpcRequestMessage;
import lombok.NoArgsConstructor;

/**
 * @author: @zyz
 */
@NoArgsConstructor
public class RpcRequestTransportServiceImpl  implements RpcRequestTransportService {
    private static final RpcRequestTransportService transportService= ExtensionLoader.getExtensionLoader(RpcRequestTransportService.class).getExtension();
    @Override
    public Object sendRpcRequest(RpcRequestMessage rpcRequestMessage) {
        return transportService.sendRpcRequest(rpcRequestMessage);
    }
}
