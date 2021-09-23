package com.fastrpc.utils;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 序列号生成器
 */
public class SequenceIdGenerator {
    private static final AtomicInteger id=new AtomicInteger(1001);
    public static int nextId(){ return id.getAndIncrement();}
}
