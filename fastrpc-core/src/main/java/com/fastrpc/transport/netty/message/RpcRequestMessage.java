package com.fastrpc.transport.netty.message;


import lombok.*;

import java.io.Serializable;
import java.util.List;


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
    private List<String> paramTypeList;
    private List<String> parameterList;
    // 是否是泛化调用 默认不是
    private boolean isGenericService=false;
    /**
     * 用于区分接口的多实现
     */
    private String group;
    /**
     * 版本
     */
    private String version;

    public RpcRequestMessage(int seqId, String interfaceName, String methodName, Class[] paramTypes, Object[] parameters, String group, String version) {
        super(seqId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.parameters = parameters;
        this.group = group;
        this.version = version;
    }
    public RpcRequestMessage(int seqId, String interfaceName, String methodName, List<String> paramTypeList, List<String> parameterList, String group, String version) {
        super(seqId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.parameters = parameters;
        this.group = group;
        this.version = version;
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
        return interfaceName+"&"+getGroup()+"&"+getVersion();
    }
}
