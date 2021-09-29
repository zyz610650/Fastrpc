package com.fastrpc.transport.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class RpcResponseMessage extends AbstractResponseMessage {

    /**
     * 返回值
     */
    private Object returnValue;


    public RpcResponseMessage(boolean success,Object returnValue, String exceptionValue) {
        super(success,exceptionValue);
        this.returnValue = returnValue;

    }

    @Override
    public byte getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

}
