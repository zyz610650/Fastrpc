package com.fastrpc.serializer;

import com.fastrpc.extension.SPI;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

@SPI("kyro")
public interface Serializer {
   <T> byte[] serialize(T msg);

   <T> T deserialize(Class<T> clazz,byte[] bytes);
}
