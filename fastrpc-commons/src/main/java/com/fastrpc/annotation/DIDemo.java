package com.fastrpc.annotation;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author: @zyz
 */
public class DIDemo {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        ScannerUtils.getClass(Controller.class,"com.fastrpc.service");
        Set<Class<?>> aClass = ScannerUtils.getClass(Controller.class);
        for (Class<?> clazz:aClass) {
            Object o = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field:declaredFields)
            {
                Object o1 = field.getType().newInstance();
                field.setAccessible(true);
                field.set(o,01);
            }
        }

    }
}
