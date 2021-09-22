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
//应答消息父类
@Data
@ToString(callSuper = true)
public abstract class AbstractResponseMessage extends Message{

    private boolean success;
    private String reason;

    public AbstractResponseMessage() {

    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }
}
