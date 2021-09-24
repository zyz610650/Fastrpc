package com.fastrpc.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.fastrpc.serializer.Serializer;

import lombok.extern.slf4j.Slf4j;


import java.io.*;


/**
 * @author zyz
 */
@Slf4j
public class SerializeImpl {
    private static final ThreadLocal<Kryo> KRYO_THREADLOCAL=ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        // 禁止类注册 因为在rpc上不同机器注册的Id可能不同
//        kryo.setRegistrationRequired(true);
        //禁止循环引用
        kryo.setReferences(false);
        return kryo;
    });


    /**
     * 序列化算法
     */
    public  enum Algorithm implements Serializer {
        /**
         * jdk自带的序列化
         */
        Java{
           @Override
           public <T> byte[] serialize(T msg) {
               if (msg==null) {
                   throw new RuntimeException("msg is null");
               }
               ByteArrayOutputStream bos=new ByteArrayOutputStream();
               ObjectOutputStream oos;
               try {
                   oos = new ObjectOutputStream(bos);
                   oos.writeObject(msg);
                   return bos.toByteArray();
               } catch (IOException e) {
                   throw new RuntimeException("Deserialization failed"+e);
               }


           }

           @Override
           public <T> T deserialize(Class<T> clazz, byte[] bytes) {
               ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
               ObjectInputStream ois;
               try {
                   ois = new ObjectInputStream(bis);
                   Object o = ois.readObject();
                   return clazz.cast(o);
               } catch (IOException | ClassNotFoundException e) {
                   throw new RuntimeException("Deserialization failed");
               }

           }
       },
        /**
         * fastJson
         */
        Json{
            @Override
            public <T> byte[] serialize(T msg) {
                return JSON.toJSONBytes(msg);
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                return JSON.parseObject(bytes,clazz);
            }
        },
        /**
         * kryo
         */
        Kryo{
            @Override
            public <T> byte[] serialize(T msg) {
                try {
                    Kryo kryo = KRYO_THREADLOCAL.get();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    Output out=new Output(bos);
                    kryo.writeObject(out,msg);
                    return bos.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Deserialization failed");
                }
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    Kryo kryo = KRYO_THREADLOCAL.get();
                    ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
                    Input in=new Input(bis);
                    KRYO_THREADLOCAL.remove();
                    return kryo.readObject(in, clazz);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Deserialization failed");
                }

            }
        }

    }
}
