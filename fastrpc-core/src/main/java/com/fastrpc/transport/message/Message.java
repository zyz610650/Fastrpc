package com.fastrpc.transport.message;

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

    /**
     * 用于服务器向客户端发送消息,客户端知道服务器返回的是哪次通信的结果
     */
    private int seqId;
    /**
     * 用于反射创建对应的消息
     */
    private byte messageType;

    private static final Map<Byte,Class<? extends Message>> messageTypeMap=new ConcurrentHashMap<>();

    public static Class<? extends Message> getMessageType(byte messageType)
    {
        return  messageTypeMap.get(messageType);
    }

    /**
     * 获得消息类型
     * @return 返回消息类型
     */
    public abstract byte getMessageType();

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
