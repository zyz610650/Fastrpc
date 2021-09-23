package com.fastrpc.remoting.message;


import lombok.Data;
import lombok.ToString;


/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@Data
@ToString(callSuper = true)
public class RpcRequestMessage extends Message {

    private String interfaceName;
    private String methodName;
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
