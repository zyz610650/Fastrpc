package com.fastrpc.transport.netty.message;


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
    /**
     * 用于服务器向客户端发送消息,客户端知道服务器返回的是哪次通信的结果
     */
    public int seqId;

    public RpcResponseMessage(boolean success,Object returnValue, String exceptionValue) {
        super(success,exceptionValue);
        this.returnValue = returnValue;

    }

    @Override
    public byte getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

}
