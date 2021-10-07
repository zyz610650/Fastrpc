package com.fastrpc.transport.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@ToString(callSuper = true)
public class RpcRequestMessage extends Message implements Serializable {

    private String interfaceName;


    private String methodName;

    private Class[] paramTypes;
    private Object[] parameters;
    /**
     * 用于区分接口的多实现
     */
    private String group;
    /**
     * 版本
     */
    private String version;
    public RpcRequestMessage(int seqId,String interfaceName, String methodName,String group,String version,Class[] paramTypes, Object[] parameters) {
        super.setSeqId(seqId);
        this.group=group;
        this.version=version;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.parameters = parameters;
    }


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
