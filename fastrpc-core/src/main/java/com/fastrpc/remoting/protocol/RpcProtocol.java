package com.fastrpc.remoting.protocol;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

import com.fastrpc.utils.SequenceIdGenerator;
import lombok.Data;
import lombok.ToString;

/**
 * 通信协议参数 封装所有协议参数
 */
@Data
public class RpcProtocol {
    /**
     * 魔数 用来验证信息身份呢 4byte
     */
    public static final byte[] MAGIC_NUMBER="frpc".getBytes();
    /**
     * 协议版本 1byte
     */
    private   byte VERSION;
    /**
     *  序列化反射 1byte 0 java 1 json 2 kryo
     */
    private   byte SERIALIZETYPE;
    /**
     * 消息类型 1byte  具体需要用户指定，在初始构造时随便写死了一个
     */
    private   byte MESSAGETYPE;


    /**
     *  压缩类型 1byte
     */
    private   byte COMPRESSTYPE;
    /**
     *  消息序列号 4 byte 用于标识消息
     */
    private  int SEQUENCEID;

    /**
     * 消息最大长度
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    public RpcProtocol(byte VERSION, byte SERIALIZETYPE, byte MESSAGETYPE, byte COMPRESSTYPE, int SEQUENCEID) {
        this.VERSION = VERSION;
        this.SERIALIZETYPE = SERIALIZETYPE;
        this.MESSAGETYPE = MESSAGETYPE;
        this.COMPRESSTYPE = COMPRESSTYPE;
        this.SEQUENCEID = SEQUENCEID;
    }

    // 这点写死了
    public static RpcProtocol getPolProtocol()
    {
        return new RpcProtocol((byte) 1,(byte)2,(byte)101, (byte)0,SequenceIdGenerator.nextId());
    }
}
