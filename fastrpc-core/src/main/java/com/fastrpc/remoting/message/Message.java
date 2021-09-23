package com.fastrpc.remoting.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@Data
public abstract class Message implements Serializable {

    private int seqId;
    private byte messageType;

    private static final Map<Byte,Class<? extends Message>> messageTypeMap=new ConcurrentHashMap<>();

    public static Class<? extends Message> getMessageType(byte messageType)
    {
        return  messageTypeMap.get(messageType);
    }
    public abstract int getMessageType();

    /**
     * 请求类型
     */
    public static final byte RPC_MESSAGE_TYPE_REQUEST = 1;
    /**
     * 响应类型
     */
    public static final byte  RPC_MESSAGE_TYPE_RESPONSE = 2;

    /**
     * 心跳请求
     */
    public static final byte PING_REQUEST=3;

    /**
     * 心跳响应
     */
    public static final byte PONG_Message=4;

    static {
        messageTypeMap.put(RPC_MESSAGE_TYPE_REQUEST,RpcRequestMessage.class);
        messageTypeMap.put(RPC_MESSAGE_TYPE_RESPONSE,RpcResponseMessage.class);
        messageTypeMap.put(PING_REQUEST,PingMessage.class);
        messageTypeMap.put(PONG_Message,PongMessage.class);

    }



}
