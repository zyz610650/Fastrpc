package com.fastrpc.serializer;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
public interface Serializer {
   <T> byte[] serialize(T msg);

   <T> T deserialize(Class<T> clazz,byte[] bytes);
}
