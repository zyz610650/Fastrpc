package com.fastrpc.constants;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

import com.fastrpc.utils.SequenceIdGenerator;
import lombok.Data;

/**
 * 通信协议参数 封装所有协议参数
 */
@Data
public class RpcMessageProtocolConstants {

    /**
     * 消息最大长度
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    /**
     * 魔数 用来验证信息身份呢 4byte
     */
    public static final byte[] MAGIC_NUMBER="frpc".getBytes();
    /**
     * 协议版本 1byte
     */
    public  static byte VERSION=0;
    /**
     *  序列化反射 1byte 0 java 1 json 2 kryo
     */
    public static  byte SERIALIZETYPE=0;

    /**
     *  压缩类型 1byte
     */
    public static  byte COMPRESSTYPE=0;







}
