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
public class PongMessage extends Message{
    @Override
    public int getMessageType() {
        return PONG_Message;
    }
}
