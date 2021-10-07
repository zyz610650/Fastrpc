package com.fastrpc.service;

/**
 * @author yvioo
 */
public class User {
    private static String name="zyz";
    static {
        System.out.println("static"+name);
    }

    public User() {
        System.out.println("dsa");
    }

    public void sayhell()
    {

        System.out.println("hello ");
    }


}
