package com.fastrpc.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: @zyz
 */
public class SingletonFactory {
    private static final Map<String,Object> SINGLETON_MAP=new ConcurrentHashMap<>();
    private static final Map<Class<?>,Object> LOCK_MAP=new ConcurrentHashMap<>();


    public static <T> T getInstance(Class<T> clazz)
    {

        if (clazz==null) {
            throw new IllegalArgumentException("class is null");
        }
        String key=clazz.getCanonicalName() ;
        Object instance=SINGLETON_MAP.get(key);
          if (instance!=null)
          {
              return clazz.cast(instance);
          }
        synchronized (LOCK_MAP.computeIfAbsent(clazz,o->new Object()))
        {
            instance=SINGLETON_MAP.get(key);
            if (instance==null)
            {
                try {
                    instance=clazz.getDeclaredConstructor().newInstance();
                    SINGLETON_MAP.put(key,instance);

                } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    return clazz.cast(instance);

    }
}
