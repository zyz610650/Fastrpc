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
public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PING_REQUEST;
    }
}
