package com.fastrpc.utils;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

import com.fastrpc.annotation.RpcService;

/**
 * 获取CPU核心数
 */
public class CpuUtil {

    public static int getCpus()
    {
        return Runtime.getRuntime().availableProcessors();
    }
}
