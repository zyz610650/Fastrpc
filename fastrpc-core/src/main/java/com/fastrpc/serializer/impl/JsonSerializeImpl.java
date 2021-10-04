package com.fastrpc.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.fastrpc.serializer.Serializer;

/**
 * @author: @zyz
 */
public class JsonSerializeImpl implements Serializer  {
    @Override
    public <T> byte[] serialize(T msg) {
        return JSON.toJSONBytes(msg);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes,clazz);
    }
}
