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
//应答消息父类
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public abstract class AbstractResponseMessage extends Message{

    private boolean success;
    /**
     * 异常值
     */
    private String exceptionValue;


}
