package com.fastrpc.transport.netty.message;

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
public class PongMessage extends Message{
    @Override
    public byte getMessageType() {
        return PONG_Message;
    }
}
