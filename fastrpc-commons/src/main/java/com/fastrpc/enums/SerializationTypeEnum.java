package com.fastrpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author: @zyz
 */
@AllArgsConstructor
@Getter
public enum  SerializationTypeEnum {


    JDK((byte) 0x01,"jdk"),
    JSON((byte) 0x02,"json"),
    KYRO ((byte) 0x03,"kyro");

    /**
     * 编码
     */
    private final byte code;
    /**
     * 对应SPI指定具体实现
     */
    private final String name;

    public static String getName(byte code){
        for (SerializationTypeEnum c: SerializationTypeEnum.values())
        {
            if (c.getCode()==code)
            {
                return c.getName();
            }
        }
        return null;
    }


    }
