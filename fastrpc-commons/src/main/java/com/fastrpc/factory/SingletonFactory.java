package com.fastrpc.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: @zyz
 */
public class SingletonFactory {
    private static final Map<String,Object> OBJECT_MAP=new ConcurrentHashMap<>();
    public static <T> T getInstance(Class<T> clazz)
    {
        if (clazz==null) {
            throw new IllegalArgumentException("class is null");
        }
        String key=clazz.getCanonicalName() ;
        if (OBJECT_MAP.containsKey(key))
        {
            return clazz.cast(OBJECT_MAP.get(key));
        }

           return clazz.cast(OBJECT_MAP.computeIfAbsent(key,k->{
               try {
                   return clazz.getDeclaredConstructor().newInstance();
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }

           }));


    }
}
