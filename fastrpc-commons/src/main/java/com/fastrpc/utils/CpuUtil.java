package com.fastrpc.utils;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

/**
 * 获取CPU核心数
 */
public class CpuUtil {

    public static int getCpus()
    {
        return Runtime.getRuntime().availableProcessors();
    }
}
