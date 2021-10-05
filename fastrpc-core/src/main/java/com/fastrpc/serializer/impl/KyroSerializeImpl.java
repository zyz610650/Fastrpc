package com.fastrpc.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fastrpc.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author: @zyz
 */
public class KyroSerializeImpl implements Serializer {
    private static final ThreadLocal<Kryo> KRYO_THREADLOCAL=ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        // 禁止类注册 因为在rpc上不同机器注册的Id可能不同
        kryo.setRegistrationRequired(false);
        //禁止循环引用
        kryo.setReferences(false);

        return kryo;
    });

    @Override
    public <T> byte[] serialize(T msg) {
        try( ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output out=new Output(bos)) {
            Kryo kryo = KRYO_THREADLOCAL.get();
            kryo.writeObject(out,msg);
            KRYO_THREADLOCAL.remove();
            return out.toBytes();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("***********************************************");
            throw new RuntimeException("Deserialization failed");
        }
    }


    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try (ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
             Input in=new Input(bis)){
            Kryo kryo = KRYO_THREADLOCAL.get();
            KRYO_THREADLOCAL.remove();
            return kryo.readObject(in, clazz);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Deserialization failed");
        }

    }

}
