package com.fastrpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: @zyz
 */
@AllArgsConstructor
@Getter
public enum VersionEnum {

    v1((byte)0x01,"V1.0.0");
    private byte code;
    private String name;
    public static String getName(byte code)
    {
        for (VersionEnum c:VersionEnum.values())
        {
            if (c.code==code)
            {
                return c.getName();
            }
        }
        return null;
    }
}
