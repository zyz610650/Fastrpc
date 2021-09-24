package com.fastrpc.remoting.message;


import com.fastrpc.serializer.Serializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.lang.reflect.Parameter;


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

    public RpcRequestMessage(int seqId,String interfaceName, String methodName,Class[] paramTypes, Object[] parameters) {
        super.setSeqId(seqId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.parameters = parameters;
    }


    @Override
    public byte getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
