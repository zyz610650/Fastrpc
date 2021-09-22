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
public class RpcResponseMessage extends AbstractResponseMessage {

    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

}
