package com.fastrpc.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fastrpc.remoting.message.PingMessage;
import com.fastrpc.remoting.message.PongMessage;
import com.fastrpc.remoting.message.RpcRequestMessage;
import com.fastrpc.remoting.message.RpcResponseMessage;
import com.fastrpc.serializer.Serializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import sun.security.jgss.krb5.Krb5Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Supplier;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@Slf4j
public class SerializeImpl {
    private static final ThreadLocal<Kryo> threadLocal=ThreadLocal.withInitial(new Supplier<Kryo>() {
        @Override
        public Kryo get() {
            Kryo kryo=new Kryo();
            // 禁止类注册 因为在rpc上不同机器注册的Id可能不同
            kryo.setRegistrationRequired(true);
            //禁止循环引用
            kryo.setReferences(false);
            return kryo;
        }
    });

    public  enum Algorithm implements Serializer {
        Java{
           @SneakyThrows
           @Override
           public <T> byte[] serialize(T msg) {
               if (msg==null) throw new RuntimeException("msg is null");
               ByteArrayOutputStream bos=new ByteArrayOutputStream();
               ObjectOutputStream oos=new ObjectOutputStream(bos);
               oos.writeObject(msg);
               return bos.toByteArray();
           }

           @SneakyThrows
           @Override
           public <T> T deserialize(Class<T> clazz, byte[] bytes) {
               ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
               ObjectInputStream ois=new ObjectInputStream(bis);
               return (T)ois.readObject();

           }
       },
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
        Kryo{
            @SneakyThrows
            @Override
            public <T> byte[] serialize(T msg) {
                Kryo kryo = threadLocal.get();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Output out=new Output(bos);
//                oos.writeObject(msg);
                kryo.writeObject(out,msg);

                return bos.toByteArray();
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Kryo kryo = threadLocal.get();
                ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
                Input in=new Input(bis);
                return kryo.readObject(in, clazz);

            }
        }

    }
}
