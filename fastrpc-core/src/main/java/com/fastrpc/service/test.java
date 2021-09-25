package com.fastrpc.service;

/**
 * @author yvioo
 */
public class test {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = test.class.getClassLoader().loadClass("com.fastrpc.service.User");
        Object o=new Object();
        User user=User.class.cast(o);
//        User user = (User) clazz.newInstance();

    //   Class<?> aClass = Class.forName("com.fastrpc.service.User");
//        User user = (User) aClass.newInstance();
//        user.sayhell();

    }
}
