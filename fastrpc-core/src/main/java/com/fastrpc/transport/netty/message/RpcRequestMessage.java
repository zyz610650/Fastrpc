package com.fastrpc.transport.netty.message;


import lombok.*;

import java.io.Serializable;


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
@Builder
@ToString(callSuper = true)
public class RpcRequestMessage extends Message implements Serializable {

    private String interfaceName;

    private String methodName;

    private Class[] paramTypes;
    private Object[] parameters;
    /**
     * 用于服务器向客户端发送消息,客户端知道服务器返回的是哪次通信的结果
     */
    public int seqId;
    /**
     * 用于区分接口的多实现
     */
    private String group;
    /**
     * 版本
     */
    private String version;


    @Override
    public byte getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }

    /**
     * 获得注册到zk的服务名
     * @return
     */
    public String getRpcServcieName() {
        return interfaceName+"&"+getVersion()+"&"+getGroup();
    }
}
