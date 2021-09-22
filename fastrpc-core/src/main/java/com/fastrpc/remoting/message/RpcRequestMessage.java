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
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
