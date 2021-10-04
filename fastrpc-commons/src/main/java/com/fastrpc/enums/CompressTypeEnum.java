package com.fastrpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: @zyz
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01,"gzip");
    private final byte code;
    private final String name;

    public static String getName(byte code)
    {
        for (CompressTypeEnum c:CompressTypeEnum.values())
        {
            if (c.code==code)
            {
                return c.getName();
            }
        }
        return null;
    }



}
