package com.fastrpc.serializer.impl;

import com.fastrpc.serializer.Serializer;

import java.io.*;

/**
 * @author: @zyz
 */
public class JdkSerializeImpl  implements Serializer {

    @Override
    public <T> byte[] serialize(T msg) {
        if (msg==null) {
            throw new RuntimeException("msg is null");
        }

        try (ByteArrayOutputStream bos=new ByteArrayOutputStream();
             ObjectOutputStream oos= new ObjectOutputStream(bos)){
            oos.writeObject(msg);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Deserialization failed"+e);
        }


    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {

        try (ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
             ObjectInputStream ois= new ObjectInputStream(bis);){
            Object o = ois.readObject();
            return clazz.cast(o);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization failed");
        }

    }
}
